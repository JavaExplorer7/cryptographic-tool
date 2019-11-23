package api;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.SecretKey;


public class CryptCipher {
	
	/**
	 * Encrypt a file and write to another file
	 * @param inFile	- file in plaintext
	 * @param outFile	- file in ciphertext
	 * @param transformation	- encryption transformation
	 * @param sKey		- secret key (symmetric key)
	 * @return true if everything goes well, false otherwise
	 */
	public static boolean encrypt(String inFile, String outFile, 
								String transformation, SecretKey sKey) {
		boolean result = false;
		FileInputStream inputFile;
		FileOutputStream outputFile;
		CipherOutputStream cipherOutputStream;
		Cipher cipher;
		
		try {
			// creating the concrete cipher
			cipher = Cipher.getInstance(transformation);
			// initializing the cipher for encryption
			cipher.init(Cipher.ENCRYPT_MODE, sKey);
			
			inputFile = new FileInputStream(inFile);
			outputFile = new FileOutputStream(outFile);
			
			// create output cipher stream
			cipherOutputStream = new CipherOutputStream(outputFile, cipher);
			
			int i = 0;
			while ((i = inputFile.read()) != -1)
				cipherOutputStream.write(i);
			
			cipherOutputStream.close();
			outputFile.close();
			inputFile.close();
			result = true;
		} catch (Exception e) {
			System.out.println(e);
		}

		return result;
	}

	/**
	 * Decrypt a file and write to another file
	 * @param inFile	- file in ciphertext
	 * @param outFile	- file in plaintext
	 * @param transformation	- encryption transformation
	 * @param sKey		- secret key (symmetric key)
	 * @return true if everything goes well, false otherwise
	 */
	public static boolean decrypt(String inFile, String outFile, 
								String transformation, SecretKey sKey) {
		boolean result = false;
		FileInputStream inputFile;
		FileOutputStream outputFile;
		CipherInputStream cipherInputStream;
		Cipher cipher;
		
		try {
			//creating the concrete cipher
			cipher = Cipher.getInstance(transformation);
			//initializing the cipher for decryption
			cipher.init(Cipher.DECRYPT_MODE, sKey);
		
			inputFile = new FileInputStream(inFile);
			outputFile = new FileOutputStream(outFile);

			//create input cipher stream
			cipherInputStream = new CipherInputStream(inputFile, cipher);
		
			int i = 0;
			while ((i = cipherInputStream.read()) != -1)
				outputFile.write(i);
			
			cipherInputStream.close();
			outputFile.close();
			inputFile.close();
			result = true;
		} catch (Exception e) {
			System.out.println(e);
		}

		return result;
	}
	
}
