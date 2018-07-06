package seler.kamil.com.cryptoseler.encryption;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Formatter;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class ComputeKey {

	private static final String HMAC_SHA512 = "HmacSHA512";

	private static String toHexString(byte[] bytes) {
		Formatter formatter = new Formatter();
		for (byte b : bytes) {
			formatter.format("%02x", b);
		}
		String value = formatter.toString();
		formatter.close();
		return value;
	}

	public static String calculateHMAC(String data, String secret) {

		SecretKeySpec secretKeySpec = new SecretKeySpec(secret.getBytes(), HMAC_SHA512);
		Mac mac;
		try {
			mac = Mac.getInstance(HMAC_SHA512);
			mac.init(secretKeySpec);
			return toHexString(mac.doFinal(data.getBytes()));
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		}
		
		return null;
	}
}
