package org.openqa;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;

import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.ProtocolException;
import org.apache.http.client.RedirectHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHttpRequest;
import org.apache.http.protocol.HttpContext;
import org.json.JSONException;
import org.json.JSONObject;

public class SafariTmp {


	public static void main(String[] args) throws IOException, JSONException {
		HttpHost host = new HttpHost("localhost", 9999);

		BasicHttpRequest r = new BasicHttpRequest("POST", "/wd/hub/session");


		DefaultHttpClient client = new DefaultHttpClient();
		client.setRedirectHandler(new RedirectHandler() {
			public boolean isRedirectRequested(HttpResponse response, HttpContext context) {
				return false;
			}

			public URI getLocationURI(HttpResponse response, HttpContext context) throws ProtocolException {
				return null;
			}
		});
		HttpResponse response = client.execute(host, r);
		System.out.println(response);
		
		Header redirect = response.getFirstHeader("location");
		System.out.println("H"+redirect.getName());
		System.out.println("V"+redirect.getValue());
		
		//JSONObject o = extractObject(response);
	}

	private static JSONObject extractObject(HttpResponse resp) throws IOException, JSONException {
		BufferedReader rd = new BufferedReader(new InputStreamReader(resp.getEntity().getContent()));
		StringBuffer s = new StringBuffer();
		String line;
		while ((line = rd.readLine()) != null) {
			s.append(line);
		}
		rd.close();
		return new JSONObject(s.toString());
	}
}
