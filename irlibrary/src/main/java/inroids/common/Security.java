/*
 * Encryption and DEcryption 
 * 
 * Created by sealy on 2013-07-01. 
 * Copyright 2013 Sealy, Inc. All rights reserved.
 */

package inroids.common;

import java.security.MessageDigest;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import android.util.Base64;

/**
* Encryption and DEcryption 
* @author Sealy
*/

public abstract class Security {
	private static final String strTag="IRLibrary";
	/**
	 * Encryption with MD5
	 * @param sData The string of Special encrypted.
	 * @return the string with MD5, or null 
	 */
	public static String encryptByMD5(String sData){
		try{
			byte[] bTemp = encryptByMD5(sData.getBytes("UTF-8"));
			StringBuffer sBuffer = new StringBuffer();
			for (int intI = 0; intI < bTemp.length; intI++) {
				if (Integer.toHexString(0xFF & bTemp[intI]).length() == 1)
					sBuffer.append("0").append(Integer.toHexString(0xFF & bTemp[intI]));
				else
					sBuffer.append(Integer.toHexString(0xFF & bTemp[intI]));
				}
			return sBuffer.toString();
		}catch (Exception e) {
			MyLog.e(strTag, "Security.encryptByMD5:"+e.toString());
	    }
		return null;
	}
	
	/** 
     * Encryption with MD5
     * @param data  the byte data
     * @return the byte data
     */  
    public static byte[] encryptByMD5(byte[] data){
    	try{
	        MessageDigest md5 = MessageDigest.getInstance("MD5");  
	        md5.reset();			
	        md5.update(data);    
	        return md5.digest(); 
	    }catch (Exception e) {
	    	MyLog.e(strTag, "Security.encryptByMD5:"+e.toString());
	    }
		return null;
    }
    
	/**
	 * Encryption by Base64
	 * @param data The string of Special encrypted.
	 * @return the string, or null 
	 */
	public static String encryptByBase64(String data){
		try {
			return Base64.encodeToString(data.getBytes(), Base64.DEFAULT);
		}catch (Exception e) {
	    	MyLog.e(strTag, "Security.encryptByBase64:"+e.toString());
	    }
		return null;
	}
	
	/**
	 * Encryption by Base64
	 * @param data the byte data.
	 * @return the string, or null 
	 */
	public static String encryptByBase64(byte[] data){
		try {
			return Base64.encodeToString(data, Base64.DEFAULT);
		}catch (Exception e) {
	    	MyLog.e(strTag, "Security.encryptByBase64:"+e.toString());
	    }
		return null;
	}
	
	/**
	 * DEcryption by Base64
	 * @param data The string of Special DEcrypted.
	 * @return the string, or null 
	 */
	public static String decryptByBase64(String data){
		try {
			return new String(Base64.decode(data, Base64.DEFAULT));
		}catch (Exception e) {
	    	MyLog.e(strTag, "Security.decryptByBase64:"+e.toString());
	    }
		return null;
	}
	
	/**
	 * DEcryption by Base64
	 * @param data The string of Special DEcrypted.
	 * @return the byte data, or null 
	 */
	public static byte[] decryptByBase64_byte(String data){
		try {
			return Base64.decode(data, Base64.DEFAULT);
		}catch (Exception e) {
	    	MyLog.e(strTag, "Security.decryptByBase64_byte:"+e.toString());
	    }
		return null;
	}
	
	/**
	 * Encryption by AES256
	 * @param aString The string of Special encrypted.
	 * @return the string, or null 
	 */
	public static String encryptByAES256(String seed, String clearText){
		try {
			byte[] rawKey = getRawKey(seed.getBytes());    
			byte[] result = encrypt(rawKey, clearText.getBytes());    
			return inroids.common.Convert.byteToHexString(result); 
		}catch (Exception e) {
	    	MyLog.e(strTag, "Security.encryptByAES256:"+e.toString());
	    }
		return null;
  }    
  
	/**
	 * DEcryption by AES256
	 * @param aString The string of Special DEcrypted.
	 * @return the string, or null 
	 */
	public static String decryptByAES256(String seed, String encrypted) {
		try {
			byte[] rawKey = getRawKey(seed.getBytes());
			byte[] enc = inroids.common.Convert.stringToByte(encrypted);
			byte[] result = decrypt(rawKey, enc); 
			return new String(result); 
		}catch (Exception e) {
	    	MyLog.e(strTag, "Security.decryptByAES256:"+e.toString());
	    }
		return null;
	}
  
	private static byte[] getRawKey(byte[] seed) {
		try {
			KeyGenerator kgen = KeyGenerator.getInstance("AES");    
		    SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");    
		    sr.setSeed(seed);    
		    kgen.init(128, sr); // 192 and 256 bits may not be available    
		    SecretKey skey = kgen.generateKey();    
		    byte[] raw = skey.getEncoded();    
		    return raw;    
		} catch (Exception e) {
			MyLog.e(strTag, "Security.getRawKey:"+e.toString());
		}
		return null;
	}    
 
	private static byte[] encrypt(byte[] raw, byte[] clear){
		try {
			SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");    
			Cipher cipher = Cipher.getInstance("AES");    
			cipher.init(Cipher.ENCRYPT_MODE, skeySpec);    
			byte[] encrypted = cipher.doFinal(clear);    
			return encrypted;
		} catch (Exception e) {
			MyLog.e(strTag, "Security.encrypt:"+e.toString());
		}
		return null;
	}    
 
	private static byte[] decrypt(byte[] raw, byte[] encrypted){
		try {
			SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");    
			Cipher cipher = Cipher.getInstance("AES");    
			cipher.init(Cipher.DECRYPT_MODE, skeySpec);    
			byte[] decrypted = cipher.doFinal(encrypted);    
			return decrypted; 
		} catch (Exception e) {
			MyLog.e(strTag, "Security.decrypt:"+e.toString());
		}
		return null;
	}    

}
