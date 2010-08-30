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
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.drawable.Drawable;

public final class Utils {

	private static final Utils instance = new Utils();

	private final DefaultHttpClient httpclient;

	private final Map<String, Drawable> imageCache;

	private Utils() {
		httpclient = new DefaultHttpClient();
		this.imageCache = new WeakHashMap<String, Drawable>();
	}

	public static Utils getInstance() {
		return instance;
	}

	public String getUrlContents(String url) throws IOException, NotLoggedInException {
		HttpGet req = new HttpGet(url);
		HttpResponse response = httpclient.execute(req);

	    InputStream content = response.getEntity().getContent();

	    if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
	    	BufferedReader buf = new BufferedReader(new InputStreamReader(content));
		    String r = "";
		    while (true) {
		    	String l = buf.readLine();
		    	if (l == null) {
		    		break;
		    	}
		    	r += l;
		    }
		    return r;
	    } else {
	    	//throw new RuntimeException("Failed to get url: " + url);

	    	// HACK HACK - we should look at the response to figure out what exception is being thrown, not just assume it's facebook
	    	// not logged in.
	    	throw new NotLoggedInException();
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
