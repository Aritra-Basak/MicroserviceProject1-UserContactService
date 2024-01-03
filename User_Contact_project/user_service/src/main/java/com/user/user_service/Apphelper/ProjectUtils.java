package com.user.user_service.Apphelper;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;
import java.util.regex.*;

import org.springframework.stereotype.Component;

@Component
public class ProjectUtils {
	
	private final static String SECRET_KEY = "asha_secret_key";

	private final static String SALT = "MySecureSalt";

	// This method use to encrypt to string
	public String encryptPassword(String strToEncrypt) {
		try {

			// Create default byte array
			byte[] iv = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
			IvParameterSpec ivspec = new IvParameterSpec(iv);

			// Create SecretKeyFactory object
			SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");

			// Create KeySpec object and assign with
			KeySpec spec = new PBEKeySpec(SECRET_KEY.toCharArray(), SALT.getBytes(), 10, 256);
			SecretKey tmp = factory.generateSecret(spec);
			SecretKeySpec secretKey = new SecretKeySpec(tmp.getEncoded(), "AES");

			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
			cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivspec);
			return Base64.getEncoder().encodeToString(cipher.doFinal(strToEncrypt.getBytes(StandardCharsets.UTF_8)));
		} catch (Exception e) {
			System.out.println("Error while encrypting: " + e.toString());
		}
		return null;
	}

	// This method use to decrypt to string
	public String decrypt(String strToDecrypt) {
		try {
			strToDecrypt = strToDecrypt.replace(' ', '+');
			// Default byte array
			byte[] iv = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
			// Create IvParameterSpec object and assign with
			// constructor
			IvParameterSpec ivspec = new IvParameterSpec(iv);

			// Create SecretKeyFactory Object
			SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");

			// Create KeySpec object and assign with

			KeySpec spec = new PBEKeySpec(SECRET_KEY.toCharArray(), SALT.getBytes(), 10, 256);
			SecretKey tmp = factory.generateSecret(spec);
			SecretKeySpec secretKey = new SecretKeySpec(tmp.getEncoded(), "AES");

			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
			cipher.init(Cipher.DECRYPT_MODE, secretKey, ivspec);

			return new String(cipher.doFinal(Base64.getDecoder().decode(strToDecrypt)));
		} catch (Exception e) {
			System.out.println("Error while decrypting: " + e.toString());
		}
		return null;
	}
	
	
	public String removeUnwantedSpaces(String passedName){
		String name="";
		passedName.trim();
		name =passedName.replaceAll("\\s+"," ");
		//System.out.println(name);
		return name;	
	}
	
	public String createPassword(String passedName) {
		Random random = new Random();
		String password = passedName.replace(" ","")+"@"+random.nextInt(999);
		return password;
		
	}
	public boolean checkMyName(String inputStr) {
		boolean res = false;
		 Pattern pattern = Pattern.compile(new String ("^[a-zA-Z\\s]*$"));
		    Matcher matcher = pattern.matcher(inputStr);
		    if(matcher.matches()){
		        res =true;
		    }
		    return res;
		 
	}
	
	public boolean checkMyNumber(String inputStr) {
		if(inputStr.length()==10) {
			Pattern pattern = Pattern.compile(new String ("^[6-9]\\d{9}$"));//Meaning-> /^[6-9]-> means the starting digit must be 6,7,8,9 and the rest 9 digits can be any number
		    Matcher matcher = pattern.matcher(inputStr);
		    if(matcher.matches()){
		        return true;
		    }
		    return false;
		}else {
			return false;
		}
	}
	

}
