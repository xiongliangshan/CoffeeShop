package com.lyancafe.coffeeshop.utils;

import android.content.Context;
import android.text.TextUtils;
import android.util.Base64;

import org.apache.commons.io.IOUtils;

import java.io.FileInputStream;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;

public class RsaEncryptor {
	private String publicKeyFile;
	private Cipher cipher = null;

	public RsaEncryptor(Context context, String publicKeyFile) throws Exception {
		// set the default value
		if (TextUtils.isEmpty(publicKeyFile)) {
			publicKeyFile = "public.key";
		}

		String publicKeyContent = null;

		try {
			publicKeyContent = IOUtils.toString(context.getAssets().open(
					publicKeyFile));
		} catch (Exception e) {
			publicKeyContent = IOUtils.toString(new FileInputStream(
					publicKeyFile));
		}

		byte[] publicKeyByte = Base64.decode(publicKeyContent, Base64.DEFAULT);

		X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(publicKeyByte);
		KeyFactory rsaKeyFactory = KeyFactory.getInstance("RSA");
		PublicKey publicKey = rsaKeyFactory.generatePublic(x509KeySpec);
		cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
		cipher.init(Cipher.ENCRYPT_MODE, publicKey);
	}

	public String encrypt(String text) throws Exception {
		//byte[] clear = Base64.decode(text, Base64.DEFAULT);
		byte[] clear = text.getBytes("UTF8");
		byte[] encrypted = cipher.doFinal(clear);
		String encryptedText = Base64.encodeToString(encrypted, Base64.DEFAULT);

		return encryptedText;
	}

	public String getPublicKeyFile() {
		return publicKeyFile;
	}

}

