package com.emarinel.common;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.params.ConnManagerPNames;
import org.apache.http.conn.params.ConnPerRouteBean;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.drawable.Drawable;

public final class Utils {

	private static final Utils instance = new Utils();

	private final DefaultHttpClient httpclient;

	private final Map<String, Drawable> imageCache;







/*


	private static class CustomSSLSocketFactory extends org.apache.http.conn.ssl.SSLSocketFactory
	{
	private SSLSocketFactory FACTORY = HttpsURLConnection.getDefaultSSLSocketFactory ();

	public CustomSSLSocketFactory () throws Exception
	    {
	    super(null);
	    try
	        {
	        SSLContext context = SSLContext.getInstance ("TLS");
	        TrustManager[] tm = new TrustManager[] { new X509TrustManager() {

	            @Override
	            public void checkClientTrusted(
	                    X509Certificate[] chain,
	                    String authType) throws CertificateException {
	                // Oh, I am easy!
	            }

	            @Override
	            public void checkServerTrusted(
	                    X509Certificate[] chain,
	                    String authType) throws CertificateException {
	                // Oh, I am easy!
	            }

	            @Override
	            public X509Certificate[] getAcceptedIssuers() {
	                return null;
	            }

	        }};
	        context.init (null, tm, new SecureRandom ());

	        FACTORY = context.getSocketFactory();
	        }
	    catch (Exception e)
	        {
	        e.printStackTrace();
	        }
	    }

	@Override
	public Socket createSocket() throws IOException {
	    return FACTORY.createSocket();
	}

	 // TODO: add other methods like createSocket() and getDefaultCipherSuites().
	 // Hint: they all just make a call to member FACTORY
	}
	*/


	private Utils() {
		// Hack to avoid SSL cert. http://stackoverflow.com/questions/1217141/self-signed-ssl-acceptance-android
		SchemeRegistry schemeRegistry = new SchemeRegistry();
		schemeRegistry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
		//schemeRegistry.register(new Scheme("https", new EasySSLSocketFactory(), 443));
		schemeRegistry.register(new Scheme("https", new EasySSLSocketFactory(), 8443));

		HttpParams params = new BasicHttpParams();
		params.setParameter(ConnManagerPNames.MAX_TOTAL_CONNECTIONS, 30);
		params.setParameter(ConnManagerPNames.MAX_CONNECTIONS_PER_ROUTE, new ConnPerRouteBean(30));
		params.setParameter(HttpProtocolParams.USE_EXPECT_CONTINUE, false);
		HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);

		ClientConnectionManager cm = new ThreadSafeClientConnManager(params, schemeRegistry);
		httpclient = new DefaultHttpClient(cm, new DefaultHttpClient().getParams());







		this.imageCache = new WeakHashMap<String, Drawable>();
	}

	public static Utils getInstance() {
		return instance;
	}

	public DefaultHttpClient getHttpClient() {
		return this.httpclient;
	}

	public String getUrlContents(String url) {
		System.out.println(url);

		HttpGet req = new HttpGet(url);
		try {
			HttpResponse response = httpclient.execute(req);
		    InputStream content = response.getEntity().getContent();

		    BufferedReader buf = new BufferedReader(new InputStreamReader(content));
	    	StringBuffer sb = new StringBuffer();
		    while (true) {
		    	String l = buf.readLine();
		    	if (l == null) {
		    		break;
		    	}
		    	sb.append(l);
		    }
		    String responseString = sb.toString();

		    if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
		    	return responseString;
		    } else {
		    	throw new RuntimeException(String.format("Failed to get url: %s. Status code was %d instead of %d. Response was %s.",
		    		url, response.getStatusLine().getStatusCode(), HttpStatus.SC_OK, responseString));
		    }
		} catch (IOException ex) {
			throw new RuntimeException("Failed to get url: " + url, ex);
		}
	}

	public HttpResponse postToUrl(String url, List<NameValuePair> nameValuePairs) throws ClientProtocolException, IOException {
		HttpPost post = new HttpPost(url);
        post.setEntity(new UrlEncodedFormEntity(nameValuePairs));
        return httpclient.execute(post);
	}

	public static String formatDate(Date d) {
		return new SimpleDateFormat("EEE h:mm a").format(d);
	}

	public static void handleConnectionFailure(Context context) {
		showAlertDialog(context, "Connection failed.");
	}

	public static void showAlertDialog(Context context, String message) {
		new AlertDialog.Builder(context).setMessage(message).setNeutralButton("OK", null).create().show();
	}

	public Drawable getImageAtUrl(String url) throws IOException {
		if (imageCache.containsKey(url)) {
			return imageCache.get(url);
		}

		try {
			Drawable result = Drawable.createFromStream((InputStream) fetch(url), "src");
			imageCache.put(url, result);
			return result;
		} catch (MalformedURLException e) {
			e.printStackTrace();
			throw new IllegalArgumentException("Invalid url: " + url, e);
		} catch (IOException e) {
			e.printStackTrace();
			throw new IOException("Failed to load image at url: " + url);
		}
	}

	private static Object fetch(String address) throws MalformedURLException,IOException {
		URL url = new URL(address);
		return url.getContent();
	}
}
