package seler.kamil.com.cryptoseler.bittrex;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;
import java.util.Map;
import java.util.Set;

import org.json.JSONException;
import org.json.JSONObject;

import seler.kamil.com.cryptoseler.encryption.ComputeKey;

public class BittrexConnector extends Connector{

	private String urlBase = "https://bittrex.com/api/v1.1";

	public BittrexConnector(String key, String secret){
		initKeys(key, secret);
	}
	
	public JSONObject makeSimpleRequest(String method, Map<String, String> params) {

		JSONObject json = null;

		try {

			URL url = new URL(makeURL(method, params));
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("GET");
			connection.setDoOutput(true);

			connection.setConnectTimeout(5000);
			connection.setReadTimeout(5000);
			connection.connect();
			BufferedReader rd = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			String content = "", line="";
			while ((line = rd.readLine()) != null) {
				content += line + "\n";
			}

			json = new JSONObject(content.toString());
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return json;

	}

	public JSONObject makeRequest(String method, Map<String, String> params) {

		String urlText = makeURL(method, params);
		JSONObject json = null;

		try {
			URL url = new URL(urlText);
			System.out.println(urlText);
			String apisign = ComputeKey.calculateHMAC(urlText, secret);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("GET");
			connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			connection.setRequestProperty("apisign", apisign);
			connection.setDoOutput(true);
			connection.setConnectTimeout(5000);
			connection.setReadTimeout(5000);


			connection.connect();
			BufferedReader rd = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			String content = "", line="";
			while ((line = rd.readLine()) != null) {
				content += line + "\n";
			}
			System.out.println("content: "+content.toString());
			json = new JSONObject(content.toString());
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return json;

	}

	private String makeURL(String method, Map<String, String> params) {


		String nonce = new Date().getTime() / 1000 + "";
		String auth = "?apikey=" + this.key + "&nonce=" + nonce;
		String url = urlBase + "" + method;

		if (params != null) {
			url +=  "/" + auth;
			Set<String> keySet = params.keySet();
			for (String key : keySet) {
				String value = params.get(key);
				url += "&" + key + "=" + value;
			}
		}

		System.out.println("urk: "+url);
		return url;
	}



}
