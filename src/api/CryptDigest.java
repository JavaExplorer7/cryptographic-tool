package api;

import java.security.MessageDigest;

import application.Utility;

import java.io.*;

public class CryptDigest {

	private static final int BLOCK_SIZE = 1024;

	/**
	 * Compute the digest(hash) of a specific file
	 * @param inFile - file to be computed
	 * @param algorithm - hash function (MD5,SHA-1,SHA-256,SHA-512)
	 * @return digest of the file in hexadecimal
	 */
	public static String computeDigest(String inFile, String algorithm) {
		File datafile = new File(inFile);
		String digest = new String();
		
		try {
			MessageDigest md = MessageDigest.getInstance(algorithm);
			FileInputStream inStream = new FileInputStream(datafile);
			
			byte[] dataBytes = new byte[BLOCK_SIZE];
			int nread = inStream.read(dataBytes);
			
			while (nread > 0) {
				md.update(dataBytes, 0, nread);
				nread = inStream.read(dataBytes);
			}

			byte[] mdbytes = md.digest();
			digest = Utility.bytesToHex(mdbytes);
			
			inStream.close();
		} catch (Exception e) {
			System.out.println(e);
		}
		
		return digest;
	}

}
