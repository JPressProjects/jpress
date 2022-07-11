/**
 * Copyright (c) 2015-2022, Michael Yang 杨福海 (fuhai999@gmail.com).
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.jpress.commons.utils;

import com.jfinal.kit.LogKit;
import com.jfinal.log.Log;
import io.jboot.components.gateway.JbootGatewayConfig;
import io.jboot.exception.JbootException;
import io.jboot.utils.StrUtil;

import javax.net.ssl.*;
import javax.servlet.http.HttpServletResponse;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.zip.GZIPInputStream;

public class HttpProxy {

    private static final Log LOG = Log.getLog(HttpProxy.class);

    private int readTimeOut = 10000; //10s
    private int connectTimeOut = 5000; //5s
    private int retries = 2;
    private String contentType = JbootGatewayConfig.DEFAULT_PROXY_CONTENT_TYPE;
    private String method = "GET";


    private boolean instanceFollowRedirects = false;
    private boolean useCaches = false;

    private Map<String, String> headers;

    private Exception exception;

    private InputStream uploadStream;



    public void start(String url, HttpServletResponse resp) {
        int triesCount = Math.max(retries, 0);
        Exception exception = null;

        do {
            try {
                exception = null;
                doSendRequest(url, resp);
            } catch (Exception ex) {
                exception = ex;
            }
        } while (exception != null && triesCount-- > 0);

        if (exception != null) {
            this.exception = exception;
            LOG.error(exception.toString(), exception);
        }
    }


    protected void doSendRequest(String url, HttpServletResponse resp) throws Exception {

        HttpURLConnection conn = null;
        try {
            conn = getConnection(url);

            /**
             * 配置 HttpURLConnection 的 http 请求头
             */
            configConnection(conn);


            // get 请求
            if ("GET".equalsIgnoreCase(method)) {
                conn.connect();
            }
            // post 请求
            else {
                conn.setDoOutput(true);
                conn.setDoInput(true);
                copyRequestStreamToConnection(conn);
            }


            /**
             * 配置 HttpServletResponse 的 http 响应头
             */
            configResponse(resp, conn);

            /**
             * 复制链接的 inputStream 流到 Response
             */
            copyConnStreamToResponse(conn, resp);

        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
    }


    protected void copyRequestStreamToConnection(HttpURLConnection conn) throws IOException {
        if (this.uploadStream == null) {
            return;
        }
        OutputStream outStream = null;
        InputStream inStream = null;
        try {
            outStream = conn.getOutputStream();
            inStream = uploadStream;
            int len;
            byte[] buffer = new byte[1024];
            while ((len = inStream.read(buffer)) != -1) {
                outStream.write(buffer, 0, len);
            }
        } finally {
            quetlyClose(outStream, inStream);
        }
    }


    protected void copyConnStreamToResponse(HttpURLConnection conn, HttpServletResponse resp) throws IOException {
        if (resp.isCommitted()) {
            return;
        }

        InputStream inStream = null;
        OutputStream outStream = null;
        try {
            inStream = getInputStream(conn);
            outStream = resp.getOutputStream();
            byte[] buffer = new byte[1024];
            for (int len; (len = inStream.read(buffer)) != -1; ) {
                outStream.write(buffer, 0, len);
            }
//            outStream.flush();
        } finally {
            quetlyClose(inStream);
        }
    }


    protected void quetlyClose(Closeable... closeables) {
        for (Closeable closeable : closeables) {
            if (closeable != null) {
                try {
                    closeable.close();
                } catch (IOException e) {
                    LogKit.logNothing(e);
                }
            }
        }
    }


    protected void configResponse(HttpServletResponse resp, HttpURLConnection conn) throws IOException {

        if (resp.isCommitted()) {
            return;
        }

        resp.setStatus(conn.getResponseCode());

        //conn 是否已经指定了 contentType，如果指定了，就用 conn 的，否则就用自己配置的
        boolean isContentTypeSetted = false;

        Map<String, List<String>> headerFields = conn.getHeaderFields();
        if (headerFields != null && !headerFields.isEmpty()) {
            Set<String> headerNames = headerFields.keySet();
            for (String headerName : headerNames) {
                //需要排除 Content-Encoding，因为 Server 可能已经使用 gzip 压缩，但是此代理已经对 gzip 内容进行解压了
                if (StrUtil.isBlank(headerName) || "Content-Encoding".equalsIgnoreCase(headerName)) {
                    continue;
                }

                String headerFieldValue = conn.getHeaderField(headerName);
                if (StrUtil.isNotBlank(headerFieldValue)) {
                    resp.setHeader(headerName, headerFieldValue);
                    if ("Content-Type".equalsIgnoreCase(headerName)) {
                        isContentTypeSetted = true;
                    }
                }
            }
        }

        //conn 没有 Content-Type，需要设置为手动配置的内容
        if (!isContentTypeSetted) {
            resp.setContentType(contentType);
        }
    }

    protected InputStream getInputStream(HttpURLConnection conn) throws IOException {
        InputStream stream = conn.getResponseCode() >= 400
                ? conn.getErrorStream()
                : conn.getInputStream();

        if ("gzip".equalsIgnoreCase(conn.getContentEncoding())) {
            return new GZIPInputStream(stream);
        } else {
            return stream;
        }
    }


    protected void configConnection(HttpURLConnection conn) throws ProtocolException {

        conn.setReadTimeout(readTimeOut);
        conn.setConnectTimeout(connectTimeOut);
        conn.setInstanceFollowRedirects(instanceFollowRedirects);
        conn.setUseCaches(useCaches);

        conn.setRequestMethod(method);

        if (this.headers != null) {
            for (Map.Entry<String, String> entry : this.headers.entrySet()) {
                conn.setRequestProperty(entry.getKey(), entry.getValue());
            }
        }
    }

    protected HttpURLConnection getConnection(String urlString) {
        try {
            if (urlString.toLowerCase().startsWith("https")) {
                return getHttpsConnection(urlString);
            } else {
                return getHttpConnection(urlString);
            }
        } catch (Throwable ex) {
            throw new JbootException(ex);
        }
    }

    protected HttpURLConnection getHttpConnection(String urlString) throws Exception {
        URL url = new URL(urlString);
        return (HttpURLConnection) url.openConnection();
    }

    protected HttpsURLConnection getHttpsConnection(String urlString) throws Exception {

        SSLContext sslContext = SSLContext.getInstance("SSL", "SunJSSE");
        TrustManager[] tm = {trustAnyTrustManager};
        sslContext.init(null, tm, null);
        SSLSocketFactory ssf = sslContext.getSocketFactory();

        URL url = new URL(urlString);
        HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
        conn.setHostnameVerifier(hnv);
        conn.setSSLSocketFactory(ssf);
        return conn;
    }

    protected static X509TrustManager trustAnyTrustManager = new X509TrustManager() {
        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType) {
        }

        @Override
        public void checkServerTrusted(X509Certificate[] chain, String authType) {
        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return null;
        }
    };

    protected static HostnameVerifier hnv = (hostname, session) -> true;


    public Exception getException() {
        return exception;
    }

    public void setException(Exception exception) {
        this.exception = exception;
    }

    public int getReadTimeOut() {
        return readTimeOut;
    }

    public void setReadTimeOut(int readTimeOut) {
        this.readTimeOut = readTimeOut;
    }

    public int getConnectTimeOut() {
        return connectTimeOut;
    }

    public void setConnectTimeOut(int connectTimeOut) {
        this.connectTimeOut = connectTimeOut;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public int getRetries() {
        return retries;
    }

    public void setRetries(int retries) {
        this.retries = retries;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public boolean isInstanceFollowRedirects() {
        return instanceFollowRedirects;
    }

    public void setInstanceFollowRedirects(boolean instanceFollowRedirects) {
        this.instanceFollowRedirects = instanceFollowRedirects;
    }

    public boolean isUseCaches() {
        return useCaches;
    }

    public void setUseCaches(boolean useCaches) {
        this.useCaches = useCaches;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    public HttpProxy addHeader(String key, String value) {
        if (this.headers == null) {
            this.headers = new HashMap<>();
        }
        this.headers.put(key, value);
        return this;
    }

    public HttpProxy addHeaders(Map<String, String> headers) {
        if (this.headers == null) {
            this.headers = new HashMap<>();
        }
        this.headers.putAll(headers);
        return this;
    }

    public InputStream getUploadStream() {
        return uploadStream;
    }

    public void setUploadStream(InputStream uploadStream) {
        this.uploadStream = uploadStream;
    }
}
