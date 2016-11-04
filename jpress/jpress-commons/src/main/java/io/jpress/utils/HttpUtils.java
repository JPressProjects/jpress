/**
 * Copyright (c) 2015-2016, Michael Yang 杨福海 (fuhai999@gmail.com).
 *
 * Licensed under the GNU Lesser General Public License (LGPL) ,Version 3.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.gnu.org/licenses/lgpl-3.0.txt
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.jpress.utils;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.security.cert.X509Certificate;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import com.jfinal.core.JFinal;

public class HttpUtils {

	private static final String TAG = "HttpUtils";
	private static final int mReadTimeOut = 1000 * 10; // 10秒
	private static final int mConnectTimeOut = 1000 * 5; // 5秒
	private static final String CHAR_SET = JFinal.me().getConstants().getEncoding();
	private static final int mRetry = 2; // 默认尝试访问次数

	public static String get(String url) throws Exception {
		return get(url, null);
	}

	public static String get(String url, Map<String, ? extends Object> params) throws Exception {
		return get(url, params, null);
	}

	public static String get(String url, Map<String, ? extends Object> params, Map<String, String> headers)
			throws Exception {
		if (url == null || url.trim().length() == 0) {
			throw new Exception(TAG + ": url is null or empty!");
		}

		if (params != null && params.size() > 0) {
			if (!url.contains("?")) {
				url += "?";
			}

			if (url.charAt(url.length() - 1) != '?') {
				url += "&";
			}

			url += buildParams(params);
		}

		return tryToGet(url, headers);
	}

	public static String buildParams(Map<String, ? extends Object> params) throws UnsupportedEncodingException {
		if (params == null || params.isEmpty()) {
			return null;
		}

		StringBuilder builder = new StringBuilder();
		for (Map.Entry<String, ? extends Object> entry : params.entrySet()) {
			if (entry.getKey() != null && entry.getValue() != null)
				builder.append(entry.getKey().trim()).append("=")
						.append(URLEncoder.encode(entry.getValue().toString(), CHAR_SET)).append("&");
		}

		if (builder.charAt(builder.length() - 1) == '&') {
			builder.deleteCharAt(builder.length() - 1);
		}

		return builder.toString();
	}

	private static String tryToGet(String url, Map<String, String> headers) throws Exception {
		int tryTime = 0;
		Exception ex = null;
		while (tryTime < mRetry) {
			try {
				return doGet(url, headers);
			} catch (Exception e) {
				if (e != null)
					ex = e;
				tryTime++;
			}
		}
		if (ex != null)
			throw ex;
		else
			throw new Exception("未知网络错误 ");
	}

	private static String doGet(String strUrl, Map<String, String> headers) throws Exception {
		HttpURLConnection connection = null;
		InputStream stream = null;
		try {

			connection = getConnection(strUrl);
			configConnection(connection);
			if (headers != null && headers.size() > 0) {
				for (Map.Entry<String, String> entry : headers.entrySet()) {
					connection.setRequestProperty(entry.getKey(), entry.getValue());
				}
			}

			connection.setInstanceFollowRedirects(true);
			connection.connect();

			stream = connection.getInputStream();
			ByteArrayOutputStream obs = new ByteArrayOutputStream();
			byte[] buffer = new byte[1024];
			for (int len = 0; (len = stream.read(buffer)) > 0;) {
				obs.write(buffer, 0, len);
			}
			obs.flush();
			obs.close();
			stream.close();

			return new String(obs.toByteArray());
		} finally {
			if (connection != null) {
				connection.disconnect();
			}
			if (stream != null) {
				stream.close();
			}
		}
	}

	public static String post(String url) throws Exception {
		return post(url, null);
	}

	public static String post(String url, Map<String, ? extends Object> params) throws Exception {
		return post(url, params, null);
	}

	public static String post(String url, Map<String, ? extends Object> params, Map<String, String> headers)
			throws Exception {
		if (url == null || url.trim().length() == 0) {
			throw new Exception(TAG + ":url is null or empty!");
		}

		if (params != null && params.size() > 0) {
			return tryToPost(url, buildParams(params), headers);
		} else {
			return tryToPost(url, null, headers);
		}
	}

	public static String post(String url, String content, Map<String, String> headers) throws Exception {
		return tryToPost(url, content, headers);
	}

	private static String tryToPost(String url, String postContent, Map<String, String> headers) throws Exception {
		int tryTime = 0;
		Exception ex = null;
		while (tryTime < mRetry) {
			try {
				return doPost(url, postContent, headers);
			} catch (Exception e) {
				if (e != null)
					ex = e;
				tryTime++;
			}
		}
		if (ex != null)
			throw ex;
		else
			throw new Exception("未知网络错误 ");
	}

	private static String doPost(String strUrl, String postContent, Map<String, String> headers) throws Exception {
		HttpURLConnection connection = null;
		InputStream stream = null;
		try {
			connection = getConnection(strUrl);
			configConnection(connection);
			if (headers != null && headers.size() > 0) {
				for (Map.Entry<String, String> entry : headers.entrySet()) {
					connection.setRequestProperty(entry.getKey(), entry.getValue());
				}
			}

			connection.setRequestMethod("POST");
			connection.setDoOutput(true);

			if (null != postContent && !"".equals(postContent)) {
				DataOutputStream dos = new DataOutputStream(connection.getOutputStream());
				dos.write(postContent.getBytes(CHAR_SET));
				dos.flush();
				dos.close();
			}
			stream = connection.getInputStream();
			ByteArrayOutputStream obs = new ByteArrayOutputStream();

			byte[] buffer = new byte[1024];
			for (int len = 0; (len = stream.read(buffer)) > 0;) {
				obs.write(buffer, 0, len);
			}
			obs.flush();
			obs.close();

			return new String(obs.toByteArray());

		} finally {
			if (connection != null) {
				connection.disconnect();
			}
			if (stream != null) {
				stream.close();
			}
		}

	}

	private static void configConnection(HttpURLConnection connection) {
		if (connection == null)
			return;
		connection.setReadTimeout(mReadTimeOut);
		connection.setConnectTimeout(mConnectTimeOut);

		connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
		connection.setRequestProperty("User-Agent",
				"Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/33.0.1750.146 Safari/537.36");
	}

	private static HttpURLConnection getConnection(String strUrl) throws Exception {
		if (strUrl == null) {
			return null;
		}
		if (strUrl.toLowerCase().startsWith("https")) {
			return getHttpsConnection(strUrl);
		} else {
			return getHttpConnection(strUrl);
		}
	}

	private static HttpURLConnection getHttpConnection(String urlStr) throws Exception {
		URL url = new URL(urlStr);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		return conn;
	}

	private static HttpsURLConnection getHttpsConnection(String urlStr) throws Exception {
		URL url = new URL(urlStr);
		HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
		conn.setHostnameVerifier(hnv);
		SSLContext sslContext = SSLContext.getInstance("SSL", "SunJSSE");
		if (sslContext != null) {
			TrustManager[] tm = { xtm };
			sslContext.init(null, tm, null);
			SSLSocketFactory ssf = sslContext.getSocketFactory();
			conn.setSSLSocketFactory(ssf);
		}

		return conn;
	}

	private static X509TrustManager xtm = new X509TrustManager() {
		public void checkClientTrusted(X509Certificate[] chain, String authType) {
		}

		public void checkServerTrusted(X509Certificate[] chain, String authType) {
		}

		public X509Certificate[] getAcceptedIssuers() {
			return null;
		}
	};

	private static HostnameVerifier hnv = new HostnameVerifier() {
		public boolean verify(String hostname, SSLSession session) {
			return true;
		}
	};

}
