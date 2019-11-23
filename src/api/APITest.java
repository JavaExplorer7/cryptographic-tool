package api;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

import application.Utility;

public class APITest {
	
	private static final String BASE_DIR = "/home/jacob/Documents/cipher/";
	private static final String IN_FILE  = BASE_DIR + "J-ubuntu";
	private static final String ENCRYPTED_FILE = BASE_DIR + "encrypted";
	private static final String DECRYPTED_FILE = BASE_DIR + "decrypted";
	
	private static final String KEY_STORE = BASE_DIR + "key-store";
	private static final String PASSWORD  = "Which witch watch which Swatch watch?";

	public static void main(String[] args) {
		//hashTest();
		//keyGenTest();
		//cipherTest();
		keyStoreTest();
	}
	
	private static void hashTest() {
		System.out.println(IN_FILE);
		System.out.println("MD5\t" + CryptDigest.computeDigest(IN_FILE, "MD5"));
		System.out.println("SHA-1\t" + CryptDigest.computeDigest(IN_FILE, "SHA-1"));
		System.out.println("SHA-256\t" + CryptDigest.computeDigest(IN_FILE, "SHA-256"));
		System.out.println("SHA-512\t" + CryptDigest.computeDigest(IN_FILE, "SHA-512"));
	}

	private static HashMap<String, SecretKey> keyGenTest() {
		/* 	+	-----------------------------	+
		 				Algorithm		|		Key Size		+
		  	+	-----------------------------	+
				+		DES					-		64				 	+
				+		AES					-		128				 	+
				+		TripleDES		-		192				 	+
				+		HmacSHA1		-		512				 	+
				+		HmacSHA256	-		256				 	+
				+		HmacSHA512	-		512				 	+
				+	----------------------------- +
		*/
		List<String> algorithm = Arrays.asList("DES", "AES", "TripleDES", 
				"HmacSHA1", "HmacSHA256", "HmacSHA512");
		HashMap<String, SecretKey> keys = new HashMap<>();

		try {
			for (String algo : algorithm) {
				SecretKey sKey = KeyGenerator.getInstance(algo).generateKey();
				keys.put(algo, sKey);
				
				//String key = Utility.bytesToHex(sKey.getEncoded());
				
				//System.out.println(algo + "(" + (key.length() * 4) + ")\t" + key);
			}
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		
		return keys;
	}
	
	private static void cipherTest() {
		/*  -------------------------------------------
	 		+	Transformation						|		Key Size
	  		-------------------------------------------
	 		+	DES/CBC/NoPadding					-	64						+
			+	DES/CBC/PKCS5Padding			-	64						+
			+	DES/ECB/NoPadding					-	64						+
			+	DES/ECB/PKCS5Padding			-	64						+
			+	AES/CBC/NoPadding					-	128						+
			+	AES/CBC/PKCS5Padding			-	128						+
			+	AES/ECB/NoPadding					-	128						+
			+	AES/ECB/PKCS5Padding			-	128						+
			+	TripleDES/CBC/NoPadding		-	192						+
			+	TripleDES/CBC/PKCS5Padding	-	192					+
			+	TripleDES/ECB/NoPadding			-	192					+
			+	TripleDES/ECB/PKCS5Padding	-	192					+
			+	RSA/ECB/PKCS1Padding				-	1024, 2048	+
			+	RSA/ECB/OAEPWithSHA-1AndMGF1Padding				+
			+	RSA/ECB/OAEPWithSHA-256AndMGF1Padding			+
			---------------------------------------------
		*/
		List<String> algorithms = Arrays.asList("DES", "AES", "TripleDES");
		List<String> modes 			= Arrays.asList("ECB", "CBC");
		List<String> paddings 	= Arrays.asList("NoPadding", "PKCS5Padding");
		
		List<String> trans = new ArrayList<>();
		
		for (String a : algorithms)
			for (String m : modes)
				for (String p : paddings)
					trans.add(a + "/" + m + "/" + p);
		
		encrypt(algorithms,  trans);
	}

	private static void encrypt(List<String> algorithm, List<String> transformations) {
		try {
			for (String algo : algorithm) {
				SecretKey sKey = KeyGenerator.getInstance(algo).generateKey();
				System.out.println(algo + " key:\t" + Utility.bytesToHex(sKey.getEncoded()));
				
				for (String trans : transformations)
					if (trans.startsWith(algo)) {
						System.out.print(trans + "\t");
						String encryptedfile = ENCRYPTED_FILE + "." + trans.replaceAll("/", "-");
						String decryptedfile = DECRYPTED_FILE + "." + trans.replaceAll("/", "-");
						
						if (CryptCipher.encrypt(IN_FILE, encryptedfile, trans, sKey))
							System.out.print("Encryption\tOK\t");
						else
							System.out.print("Encryption\t!!\t");
						
						if (CryptCipher.decrypt(encryptedfile, decryptedfile, trans, sKey))
							System.out.println("Decryption\tOK\t");
						else
							System.out.println("Decryption\t!!\t");
					}
				System.out.println();
			}
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
	}

	private static void keyStoreTest() {
		CryptKeyStore keyStore = CryptKeyStore.createKeyStore(KEY_STORE, PASSWORD);
		
		HashMap<String, SecretKey> keys = keyGenTest();
		
		for (String name : keys.keySet())
			keyStore.addKey(name, keys.get(name));
		
		
		List<String> aliases = keyStore.getAliases();
		
		for (String alias : aliases) {
			SecretKey key = keyStore.getKey(alias);
			System.out.println(alias + "\t" + Utility.bytesToHex(key.getEncoded()));
		}
		
		System.out.println("\n");
		
		Collections.shuffle(aliases);
		keyStore.deleteKey(aliases.get(0));
		
		for (String alias : aliases) {
			SecretKey key = keyStore.getKey(alias);
			if (key != null)
				System.out.println(alias + "\t" + Utility.bytesToHex(key.getEncoded()));
			else
				System.out.println(alias + " not found");
		}
	}
	
}
