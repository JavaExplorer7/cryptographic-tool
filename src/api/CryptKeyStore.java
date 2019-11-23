package api;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.security.KeyStore;
import java.security.KeyStore.PasswordProtection;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import javax.crypto.SecretKey;


public class CryptKeyStore {

	private static final String KEY_STORE_TYPE = "JCEKS";
	private static boolean isOK = true;

	private String path;
	private String password;
	private KeyStore store;
	private PasswordProtection protection;
	// the password protection for the key store

	public CryptKeyStore(String path, String password) {
		this.path 		= path;
		this.password = password;
		this.store 		= loadKeyStore();
		this.protection = new PasswordProtection(password.toCharArray());
	}

	public boolean isOK() {
		return isOK;
	}

	/** Load the key store with the given pathname and password */
	private KeyStore loadKeyStore() {
		KeyStore ks = null;
		try {
			ks = KeyStore.getInstance(KEY_STORE_TYPE);
			FileInputStream fis = new FileInputStream(path);
			ks.load(fis, password.toCharArray());
			fis.close();
		} catch (Exception e) {
			System.out.println(e);
			isOK = false;
		}
		return ks;
	}

	/** Create a new key store */
	public static CryptKeyStore createKeyStore(String path, String password) {
		KeyStore ks = null;
		try {
			ks = KeyStore.getInstance(KEY_STORE_TYPE);
			ks.load(null, password.toCharArray());

			FileOutputStream fos = new FileOutputStream(path);
			ks.store(fos, password.toCharArray());
			fos.close();
		} catch (Exception e) {
			System.out.println(e);
		}
		return new CryptKeyStore(path, password);
	}

	/** Add a key to a key store */
	public boolean addKey(String alias, SecretKey sKey) {
		try {
			isOK = true;
			KeyStore.SecretKeyEntry skEntry = new KeyStore.SecretKeyEntry(sKey);
			store.setEntry(alias, skEntry, protection);

			FileOutputStream fos = new FileOutputStream(path);
			store.store(fos, password.toCharArray());
			fos.close();
		} catch (Exception e) {
			System.out.println(e);
			isOK = false;
		}

		return isOK;
	}

	/** Delete a key from a key store */
	public boolean deleteKey(String alias) {
		try {
			isOK = true;
			store.deleteEntry(alias);

			FileOutputStream fos = new FileOutputStream(path);
			store.store(fos, password.toCharArray());
			fos.close();
		} catch (Exception e) {
			System.out.println(e);
			isOK = false;
		}

		return isOK;
	}

	/** Retrieve a key from a key store */
	public SecretKey getKey(String alias) {
		SecretKey sKey = null;
		try {
			KeyStore.SecretKeyEntry skEntry =
					(KeyStore.SecretKeyEntry) store.getEntry(alias, protection);
			sKey = skEntry.getSecretKey();
		} catch (Exception e) {
			System.out.println(e);
		}
		return sKey;
	}

	/** Retrieve all aliases from a key store */
	public List<String> getAliases() {
		List<String> names = new ArrayList<>();
		Enumeration<String> aliases = null;

		try {
			aliases = store.aliases();
		} catch (Exception e) {
			System.out.println(e);
		}

		while (aliases != null && aliases.hasMoreElements())
			names.add(aliases.nextElement());

		return names;
	}

}
