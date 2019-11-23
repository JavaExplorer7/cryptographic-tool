package application;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Utility {

	private static final char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();

	public static final List<String> HASH_FUNCTION_NAMES =
			Arrays.asList("MD5", "SHA-1", "SHA-256", "SHA-512");

	public static final Map<String, String> KEY_TYPES =
			Stream.of(new String[][] {
				{"DES", "64"},
				{"AES", "128"},
				{"TripleDES", "192"},
				{"HmacSHA1", "512"},
				{"HmacSHA256", "256"},
				{"HmacSHA512", "512"}})
			.collect(Collectors.toMap(data -> data[0], data -> data[1]));

	private static final List<String> ALGORITHMS = Arrays.asList("DES", "AES", "TripleDES");
	private static final List<String> MODES     = Arrays.asList("ECB"); // CBC mode requires IV
	private static final List<String> PADDINGS 	= Arrays.asList("NoPadding", "PKCS5Padding");

	public static List<String> getAlgorithms() {
		List<String> trans = new ArrayList<>();

		for (String a : ALGORITHMS)
			for (String m : MODES)
				for (String p : PADDINGS)
					trans.add(a + "/" + m + "/" + p);

		return trans;
	}

 	public static String bytesToHex(byte[] bytes) {
 	    char[] hexChars = new char[bytes.length * 2];
 	    for (int j = 0; j < bytes.length; j++) {
 	        int v = bytes[j] & 0xFF;
 	        hexChars[j * 2] = HEX_ARRAY[v >>> 4];
 	        hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
 	    }
 	    return new String(hexChars);
 	}

}
