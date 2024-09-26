package com.lostmc.core.utils;

import java.security.Key;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;

public class EncryptionUtil {

	static String algorithm = "DESede";
	static Key symKey;
	static Cipher cipher;

	static void init() {
		if (symKey == null || cipher == null) {
			try {
				symKey = KeyGenerator.getInstance(algorithm).generateKey();
				cipher = Cipher.getInstance(algorithm);
			} catch (Exception error) {
				throw new RuntimeException(error);
			}
		}
	}

	public static byte[] encrypt(String input) {
		try {
			init();
			cipher.init(Cipher.ENCRYPT_MODE, symKey);
			byte[] inputBytes = input.getBytes();
			return cipher.doFinal(inputBytes);
		} catch (Exception error) {
			error.printStackTrace();
			return null;
		}
	}

	public static String decrypt(byte[] encryption) {
		try {
			init();
			cipher.init(Cipher.DECRYPT_MODE, symKey);
			byte[] decrypt = cipher.doFinal(encryption);
			String decrypted = new String(decrypt);
			return decrypted;
		} catch (Exception error) {
			error.printStackTrace();
			return null;
		}
	}
}
