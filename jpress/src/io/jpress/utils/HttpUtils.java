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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class HttpUtils {

	private static final String TAG = "HttpHandler";
	private static final int mReadTimeOut = 1000 * 10; // 10秒
	private static final int mConnectTimeOut = 1000 * 5; // 5秒
	private static final String CHAR_SET = "utf-8";
	private static final int mRetry = 2; // 默认尝试访问次数

	public static String get(String url) throws Exception {
		return get(url, null);
	}

	public static String get(String url, Map<String, ? extends Object> params) throws Exception {
		if (url == null || url.trim().length() == 0) {
			throw new Exception(TAG + ": get url is null or empty!");
		}

		if (params != null && params.size() > 0) {
			if (!url.contains("?"))
				url += "?";

			StringBuilder sbContent = new StringBuilder();
			if (url.charAt(url.length() - 1) == '?') { // 最后一个字符是 ?
				for (Map.Entry<String, ? extends Object> entry : params.entrySet()) {
					if (entry.getKey() != null)
						sbContent.append(entry.getKey().trim())
								 .append("=")
								 .append(entry.getValue())
								 .append("&");
				}

				if (sbContent.charAt(sbContent.length() - 1) == '&') {
					sbContent.deleteCharAt(sbContent.length() - 1);
				}
			} else {
				for (Map.Entry<String, ? extends Object> entry : params.entrySet()) {
					if (entry.getKey() != null)
						sbContent.append("&")
								 .append(entry.getKey().trim())
								 .append("=")
								 .append(entry.getValue());
				}
			}
			url += sbContent.toString();
		}

		return tryToGet(url);
	}
	
	private static String tryToGet(String url) throws Exception {
		int tryTime = 0;
		Exception ex = null;
		while (tryTime < mRetry) {
			try {
				return doGet(url);
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
	
	private static String doGet(String strUrl) throws Exception {
		strUrl = urlEncode(strUrl, CHAR_SET);
		HttpURLConnection connection = null;
		InputStream stream = null;
		try {

			connection = getConnection(strUrl);
			configConnection(connection);
			
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

	public static String post(String url, Map<String, ? extends Object> params)
			throws Exception {
		if (url == null || url.trim().length() == 0) {
			throw new Exception(TAG + ": post url is null or empty!");
		}

		if (params != null && params.size() > 0) {
			StringBuilder sbContent = new StringBuilder();
			for (Map.Entry<String, ? extends Object> entry : params.entrySet()) {
				if (entry.getKey() != null)
					sbContent.append("&").append(entry.getKey().trim())
							.append("=").append(entry.getValue());
			}
			return tryToPost(url, sbContent.substring(1));

		} else {
			return tryToPost(url, null);
		}

	}

	private static String tryToPost(String url, String postContent) throws Exception {
		int tryTime = 0;
		Exception ex = null;
		while (tryTime < mRetry) {
			try {
				return doPost(url, postContent);
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

	
	
	private static String doPost(String strUrl, String postContent) throws Exception {
		HttpURLConnection connection = null;
		InputStream stream = null;
		try {
			connection = getConnection(strUrl);
			configConnection(connection);

			connection.setRequestMethod("POST");
			connection.setDoOutput(true);

			DataOutputStream dos = new DataOutputStream(connection.getOutputStream());
			dos.write(postContent.getBytes(CHAR_SET));
			dos.flush();
			dos.close();
			
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

		connection.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
		connection.setRequestProperty("User-Agent","Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/33.0.1750.146 Safari/537.36");
	}

	private static HttpURLConnection getConnection(String strUrl) throws Exception {
		if (strUrl == null) {
			return null;
		}
		if (strUrl.toLowerCase().startsWith("https")) {
			try {
				return getHttpsConnection(strUrl);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		} else {
			return getHttpConnection(strUrl);
		}
	}

	private static HttpURLConnection getHttpConnection(String urlStr)throws Exception {
		URL url = new URL(urlStr);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		return conn;
	}

	private static HttpsURLConnection getHttpsConnection(String urlStr)throws Exception {
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

	private static HostnameVerifier hnv =  new HostnameVerifier() {
		public boolean verify(String hostname, SSLSession session) {
			return true;
		}
	};

	public static String urlEncode(String str, String charset)throws UnsupportedEncodingException {
		Pattern pattern = Pattern.compile("[\u4e00-\u9fa5]+");
		Matcher mathcer = pattern.matcher(str);
		StringBuffer buffer = new StringBuffer();
		while (mathcer.find()) {
			mathcer.appendReplacement(buffer,
					URLEncoder.encode(mathcer.group(0), charset));
		}
		mathcer.appendTail(buffer);
		return buffer.toString();
	}

}
