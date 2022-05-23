package com.example.kafka.demo;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.Security;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class Test {

	
	private static final String ALGORITHM = "AES"; 

	
	
	
	public String encrypt(final String valueEnc, final String secKey) { 

		
		
		
	    String encryptedVal = null;

	    try {
	        final Key key = generateKeyFromString(secKey);
	        final Cipher c = Cipher.getInstance(ALGORITHM);
	        c.init(Cipher.ENCRYPT_MODE, key);
	        final byte[] encValue = c.doFinal(valueEnc.getBytes());
	        
	        encryptedVal = new String(Base64.getEncoder().encode(encValue), StandardCharsets.UTF_8);
	        
	    } catch(Exception ex) {
	        System.out.println("The Exception is=" + ex);
	    }

	    return encryptedVal;
	}
	
	public static String decrypt(final String encryptedValue, final String secretKey) {

	    String decryptedValue = null;

	    try {

	        final Key key = generateKeyFromString(secretKey);
	        final Cipher c = Cipher.getInstance(ALGORITHM);
	        c.init(Cipher.DECRYPT_MODE, key);
	        final byte[] decorVal = Base64.getDecoder().decode(encryptedValue);
	        final byte[] decValue = c.doFinal(decorVal);
	        decryptedValue = new String(decValue);
	    } catch(Exception ex) {
	        System.out.println("The Exception is=" + ex);
	    }

	    return decryptedValue;
	}
	
	
	public static Key generateKeyFromString(final String secKey) throws Exception {
	    final byte[] keyVal = Base64.getDecoder().decode(secKey);
	    final Key key = new SecretKeySpec(keyVal, ALGORITHM);
	    return key;
	}
	
	public static SecretKey generateKey(int n) throws NoSuchAlgorithmException {
	    KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
	    keyGenerator.init(n);
	    SecretKey key = keyGenerator.generateKey();
	    return key;
	}
	
	public static IvParameterSpec generateIv() {
	    byte[] iv = new byte[16];
	    new SecureRandom().nextBytes(iv);
	    return new IvParameterSpec(iv);
	}
	
	public static void main(String[] args) {
		
		String order = "xbcd";
		String valor = "x-x--b--c--d--";
		int lastPosition = 0;
		
		Map<Character, Integer> orderMap = new HashMap<>();
		for(int i=0; i<order.length(); i++) {
			orderMap.put(order.charAt(i), i);
		}
		
		boolean valid = true;
		for (int i=0; i<valor.length(); i++) {
			
			Integer charIndex = orderMap.get(valor.charAt(i));
			
			if (charIndex != null) {
				if (charIndex < lastPosition) {
					valid = false;
					break;
				} else if (charIndex > lastPosition) {
					lastPosition = charIndex;
				}
			}
			
		}
		
		System.out.println(valid ? "valid" : "not valid");
	}
	
//	public static void main(String[] args) throws NoSuchAlgorithmException, NoSuchPaddingException, 
//		InvalidKeyException, IllegalBlockSizeException, BadPaddingException, UnsupportedEncodingException, InvalidAlgorithmParameterException {
//	
//		
//		
////		byte []key = "bs)n39vb.e&@Q2dWK;)n`k2rb`A)9\\;WKn[Wg%($By$6XyW]'@LUX+j".getBytes("UTF-8");
////		MessageDigest sha = MessageDigest.getInstance("SHA-256");
////        key = sha.digest(key);
//
//		Key secretKeySpec = new SecretKeySpec(
//			Base64.getDecoder().decode("RVIOnsSPMsqG6bh4lTLQMIbeEWXg09+/Z5U7tGyvQ6A="), "AES");
//
//
//		System.out.println("chave: " + new String(Base64.getEncoder().encode(secretKeySpec.getEncoded())));
////		System.out.println("chave: " + new String(key, "UTF-8"));
//		
//		
//		Cipher cipher = Cipher.getInstance("AES");
//		cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);
//		
//		byte[] criptografado = cipher.doFinal("Testo é criptografado".getBytes());
//		
//		System.out.println("Criptografado: " + new String(criptografado, StandardCharsets.UTF_8));
//		
//		cipher = Cipher.getInstance("AES");
//		cipher.init(Cipher.DECRYPT_MODE, secretKeySpec);
//		//cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, new IvParameterSpec("1234567890qwerty".getBytes()));
//		
//		criptografado = cipher.doFinal(criptografado);
//		
//		System.out.println("Descriptografado: " + new String(criptografado, StandardCharsets.UTF_8));
//		
//		
//		System.out.println(Security.getAlgorithms("MessageDigest"));
//		
//	}
}
