package api;

import java.security.Provider;
import java.security.Security;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class OtherTest {
	
	private static final String ENCODE_MODE = "UTF-8";

	private static final String PLAIN_TEXT = "Java Code Geeks Rock!\0\0\0\0\0\0\0\0\0\0\0";
	private static final String SECRET_KEY = "0123456789abcdef";
	
	private static final String IV = "HelloKitty123456";
	
	
	public static byte[] encrypt(String plainText, String encryptionKey) throws Exception {
	  Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding", "SunJCE");
	  SecretKeySpec key = new SecretKeySpec(encryptionKey.getBytes(ENCODE_MODE), "AES");
	  cipher.init(Cipher.ENCRYPT_MODE, key, new IvParameterSpec(IV.getBytes(ENCODE_MODE)));
	  return cipher.doFinal(plainText.getBytes(ENCODE_MODE));
	}
	
	public static String decrypt(byte[] cipherText, String decryptionKey) throws Exception{
	  Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding", "SunJCE");
	  SecretKeySpec key = new SecretKeySpec(decryptionKey.getBytes(ENCODE_MODE), "AES");
	  cipher.init(Cipher.DECRYPT_MODE, key,new IvParameterSpec(IV.getBytes(ENCODE_MODE)));
	  return new String(cipher.doFinal(cipherText), ENCODE_MODE);
	}
	

	public static void main(String [] args) {
		try {
 
			System.out.println("Plain Text\t" + PLAIN_TEXT);
 
			byte[] cipherText = encrypt(PLAIN_TEXT, SECRET_KEY);
 
			System.out.print("Cipher Text\t");
			for (int i = 0; i < cipherText.length; i++)
				System.out.print(String.format("%02X ",cipherText[i]));
 
			System.out.println("");
 
			String decrypted = decrypt(cipherText, SECRET_KEY);

			System.out.println("Decrypted Text\t" + decrypted);
 
			
			for (Provider p : Security.getProviders())
				System.out.println(p.getName());
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
