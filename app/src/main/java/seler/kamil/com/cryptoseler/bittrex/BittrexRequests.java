package seler.kamil.com.cryptoseler.bittrex;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class BittrexRequests {

	private Map<String, String> methods;
	private BittrexConnector bittrex;
	private List<String> currencies;


	public BittrexRequests(String key, String secret) {
		this.bittrex = new BittrexConnector(key, secret);
		this.methods = new TreeMap<String, String>();

		methods.put("balance", "/account/getbalance");
		methods.put("getOrder", "/account/getorder");
		methods.put("buy", "/market/buylimit");
		methods.put("sell", "/market/selllimit");
		methods.put("ticker", "/public/getticker");
		methods.put("altcoins", "/public/getmarkets");
		methods.put("openOrders", "/market/getopenorders");
		methods.put("balance", "/account/getbalance");
		methods.put("balances", "/account/getbalances");
	}

	public void getBalance() {
		Map<String, String> params = new LinkedHashMap<String, String>();

		params.put("currency", "btc");
		JSONObject res = null;
		try {
			res = bittrex.makeRequest(methods.get("balance"), params).getJSONObject("result");
		} catch (JSONException e) {
			e.printStackTrace();
		}

	}

	public void getCurrencies() {
		this.currencies = new ArrayList<String>();
		JSONArray request = null;
		try {
			request = bittrex.makeSimpleRequest(methods.get("altcoins"), null).getJSONArray("result");
			for (int i = 0; i < request.length(); i++)
				this.currencies.add(request.getJSONObject(i).getString("MarketCurrency").toLowerCase());

		} catch (JSONException e) {
			e.printStackTrace();
		}


	}

	public double getSellPrice(String token) {
		String coin = "btc-" + token;
		Map<String, String> params = new LinkedHashMap<String, String>();

		params.put("market", coin);
		JSONObject request = null;
		try {
			request = bittrex.makeSimpleRequest(methods.get("ticker"), params);
			if(request.getBoolean("success")){
				double ask = request.getJSONObject("result").getDouble("Ask");
				return ask;
			}
			return 0.00;


		} catch (JSONException e) {
			e.printStackTrace();
			return 0.00;
		}

	}



	private double getBalance(String token) {

		Map<String, String> params = new LinkedHashMap<String, String>();

		params.put("currency", token);
		JSONObject request = null;
		try {
			request = bittrex.makeSimpleRequest(methods.get("balance"), params).getJSONObject("result");
			double availableTokens = request.getDouble("avaliable");
			return availableTokens;
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return -1.0;
	}

	public JSONArray getBalances() {

				JSONArray result = null;
		try {
			result = bittrex.makeSimpleRequest(methods.get("balances"), null).getJSONArray("result");
			return result;

		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}


	public JSONObject sellOrder(String token, double avaliableToken, double rate) {


		Map<String, String> params = new LinkedHashMap<String, String>();

		params.put("market", "btc-"+token);
		params.put("quantity", avaliableToken + "");
		params.put("rate", rate + "");
		JSONObject request = null;
		request = bittrex.makeRequest(methods.get("sell"), params);
		System.out.println(request);
		return request;
	}
}
