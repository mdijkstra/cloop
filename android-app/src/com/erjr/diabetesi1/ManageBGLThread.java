package com.erjr.diabetesi1;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.Socket;
import java.net.URI;
import java.net.URL;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.security.KeyStore;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import android.content.Context;
import android.util.Log;

import com.erjr.cloop.dao.SGVDataSource;
import com.erjr.cloop.entities.SGV;

public class ManageBGLThread extends Thread {

	Context context;
	private String baseUrl = "https://www.ManageBGL.com/api/1.0/";
	private String token = null;
	private String userName = "edward.r.robinson%40gmail.com";
	private String password = "";
	public static final String ManageBGLTimeFormat = "yyyy-MM-dd%20HH:mm:ss";
	private final String USER_AGENT = "Mozilla/5.0";

	XPathFactory xpathFactory = XPathFactory.newInstance();
	XPath xpath = xpathFactory.newXPath();
	private static String TAG = "ManageBGLCom";

	public ManageBGLThread(Context context) {
		this.context = context;
	}

	public void run() {
		try {
			login();
			if (token != null) {
				SGVDataSource sgvDS = new SGVDataSource(context);
				SGV[] sgvs = sgvDS.getSGVToSendToCloud();
				if(sgvs == null) {
					return;
				}
				for (int i = 0; i < sgvs.length; i++) {
					if (sgvs[i] != null) {
						sendSGVToManageBGL(sgvs[i]);
					}
				}
			} else {
				Log.e(TAG, "Could not login :(");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void login() throws XPathExpressionException, IOException,
			JSONException {
		if (token != null) {
			return;
		}
		String urlExtension = "login?email=" + userName + "&password="
				+ password + "&debug=";
		Document result = sendGet(urlExtension);
		if (result != null) {
			if (xpath.evaluate("/response/result", result).equals("1")) {
				token = xpath.evaluate("/response/token", result);
			}
		}
	}

	public boolean sendSGVToManageBGL(SGV sgv) throws XPathExpressionException,
			IOException, JSONException {
		String urlExtension = "add.json?token=" + token + "&value="
				+ sgv.getSg().toString() + "&other=&notes=&log_type=22&time="
				+ Util.convertDateToManageBGLString(sgv.getDatetimeRecorded());
		JSONObject result = readJsonFromUrl(baseUrl + urlExtension);

		if (result.getBoolean("result")) {
			Log.i(TAG, "Sucessfully added SGV " + sgv.getCgmDataID()
					+ " by id: " + result.getInt("id"));
			return true;
		} else {
			Log.e(TAG, "Could not add SGV: " + result.getString("message"));
			return false;
		}
	}

	private static String readAll(Reader rd) throws IOException {
		StringBuilder sb = new StringBuilder();
		int cp;
		while ((cp = rd.read()) != -1) {
			sb.append((char) cp);
		}
		return sb.toString();
	}

	public static JSONObject readJsonFromUrl(String url) throws IOException,
			JSONException {
		InputStream is = new URL(url).openStream();
		try {
			BufferedReader rd = new BufferedReader(new InputStreamReader(is,
					Charset.forName("UTF-8")));
			String jsonText = readAll(rd);
			JSONObject json = new JSONObject(jsonText);
			Log.i(TAG, json.toString());
			return json;
		} finally {
			is.close();
		}
	}

	private Document stringToDocument(String xml) {
		try {
			InputSource source = new InputSource(new StringReader(xml));

			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document document = db.parse(source);
			return document;
		} catch (Exception e) {
			return null;
		}
	}

	// HTTP GET request
	private Document sendGet(String urlExtension) {
		String responseString;
		try {
			// String url = "http://www.google.com/search?q=mkyong";
			String url = baseUrl + urlExtension;
			URL obj = new URL(url);
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();

			// optional default is GET
			con.setRequestMethod("GET");

			// add request header
			con.setRequestProperty("User-Agent", USER_AGENT);

			int responseCode = con.getResponseCode();
			System.out.println("\nSending 'GET' request to URL : " + url);
			System.out.println("Response Code : " + responseCode);

			BufferedReader in = new BufferedReader(new InputStreamReader(
					con.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();
			responseString = response.toString();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		// print result
		// System.out.println(responseString.toString());
		Log.i(TAG, responseString);
		return stringToDocument(responseString);
		// return responseString;
	}

	// HTTP POST request
	private void sendPost() throws Exception {

		String url = "https://selfsolve.apple.com/wcResults.do";
		URL obj = new URL(url);
		HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();

		// add reuqest header
		con.setRequestMethod("POST");
		con.setRequestProperty("User-Agent", USER_AGENT);
		con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");

		String urlParameters = "sn=C02G8416DRJM&cn=&locale=&caller=&num=12345";

		// Send post request
		con.setDoOutput(true);
		DataOutputStream wr = new DataOutputStream(con.getOutputStream());
		wr.writeBytes(urlParameters);
		wr.flush();
		wr.close();

		int responseCode = con.getResponseCode();
		System.out.println("\nSending 'POST' request to URL : " + url);
		System.out.println("Post parameters : " + urlParameters);
		System.out.println("Response Code : " + responseCode);

		BufferedReader in = new BufferedReader(new InputStreamReader(
				con.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();

		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();

		// print result
		System.out.println(response.toString());

	}

	public String getInternetData(String urlExtension) {
		BufferedReader in = null;
		String data = null;

		try {
			HttpClient client = new DefaultHttpClient();
			client.getConnectionManager().getSchemeRegistry()
					.register(getMockedScheme());

			URI website = new URI(baseUrl + urlExtension);
			HttpGet request = new HttpGet();
			request.setURI(website);
			HttpResponse response = client.execute(request);
			response.getStatusLine().getStatusCode();

			in = new BufferedReader(new InputStreamReader(response.getEntity()
					.getContent()));
			StringBuffer sb = new StringBuffer("");
			String l = "";
			String nl = System.getProperty("line.separator");
			while ((l = in.readLine()) != null) {
				sb.append(l + nl);
			}
			in.close();
			data = sb.toString();
			return data;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			if (in != null) {
				try {
					in.close();
					return data;
				} catch (Exception e) {
					Log.e("GetMethodEx", e.getMessage());
				}
			}
		}
	}

	public Scheme getMockedScheme() throws Exception {
		MySSLSocketFactory mySSLSocketFactory = new MySSLSocketFactory();
		return new Scheme("https", (SocketFactory) mySSLSocketFactory, 443);
	}

	class MySSLSocketFactory extends SSLSocketFactory {
		javax.net.ssl.SSLSocketFactory socketFactory = null;

		public MySSLSocketFactory(KeyStore truststore) throws Exception {
			super();
			socketFactory = getSSLSocketFactory();
		}

		public MySSLSocketFactory() throws Exception {
			this(null);
		}

		@Override
		public Socket createSocket(Socket socket, String host, int port,
				boolean autoClose) throws IOException, UnknownHostException {
			return socketFactory.createSocket(socket, host, port, autoClose);
		}

		@Override
		public Socket createSocket() throws IOException {
			return socketFactory.createSocket();
		}

		javax.net.ssl.SSLSocketFactory getSSLSocketFactory() throws Exception {
			SSLContext sslContext = SSLContext.getInstance("TLS");

			TrustManager tm = new X509TrustManager() {
				public void checkClientTrusted(X509Certificate[] chain,
						String authType) throws CertificateException {
				}

				public void checkServerTrusted(X509Certificate[] chain,
						String authType) throws CertificateException {
				}

				public X509Certificate[] getAcceptedIssuers() {
					return null;
				}
			};
			sslContext.init(null, new TrustManager[] { tm }, null);
			return sslContext.getSocketFactory();
		}

		@Override
		public String[] getDefaultCipherSuites() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public String[] getSupportedCipherSuites() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Socket createSocket(String host, int port) throws IOException,
				UnknownHostException {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Socket createSocket(String host, int port,
				InetAddress localHost, int localPort) throws IOException,
				UnknownHostException {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Socket createSocket(InetAddress host, int port)
				throws IOException {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Socket createSocket(InetAddress address, int port,
				InetAddress localAddress, int localPort) throws IOException {
			// TODO Auto-generated method stub
			return null;
		}
	}

	public void cancel() {
		// TODO Auto-generated method stub
		
	}
}
