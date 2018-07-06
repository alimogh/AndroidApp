package seler.kamil.com.cryptoseler.bittrex;

public abstract class Connector {

	protected String key, secret;
	
	protected void initKeys(String key, String secret){
		this.key = key;
		this.secret = secret;
	}
	
	
	
}
