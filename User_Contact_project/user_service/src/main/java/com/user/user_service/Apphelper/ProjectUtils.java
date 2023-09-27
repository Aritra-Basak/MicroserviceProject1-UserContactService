package com.user.user_service.Apphelper;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;
import java.util.regex.*;

import org.springframework.stereotype.Component;

@Component
public class ProjectUtils {
	
	public String EncryptPasswd(String input) {
        StringBuilder hash = new StringBuilder();
        try {
        	//This line initializes a MessageDigest object named sha with the SHA-1 algorithm. A MessageDigest is used for calculating hash values of data.
            MessageDigest sha = MessageDigest.getInstance("SHA-1");
            // This line calculates the SHA-1 hash of the input string and stores it in the hashedBytes byte array. input.getBytes() converts the input string into a byte array before hashing.
            byte[] hashedBytes = sha.digest(input.getBytes());
            char[] digits = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
            for (int idx = 0; idx < hashedBytes.length; ++idx) {
                byte b = hashedBytes[idx];
                //This line appends the first 4 bits (the high nibble) of the byte b to the hash StringBuilder by bitwise ANDing it with 0xf0 
                //and then right-shifting the result by 4 bits to get the index in the digits array.
                hash.append(digits[(b & 0xf0) >> 4]);
                //This line appends the last 4 bits (the low nibble) of the byte b to the hash StringBuilder by bitwise ANDing it with 0x0f.
                hash.append(digits[b & 0x0f]);
            }
        } catch (NoSuchAlgorithmException e) {
        	//This exception is thrown if the requested cryptographic algorithm ("SHA-1" in this case) is not available on the system.
        	e.printStackTrace();
        }
        //0xf0 is a hexadecimal literal representing the value 240 in decimal notation. In binary, it is 11110000. It is used as a bitmask to extract the first 4 bits (the high nibble) from a byte.
        return hash.toString();
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
