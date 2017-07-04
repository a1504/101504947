package com.roleandjoin.utils;

import java.util.StringTokenizer;
import javax.xml.bind.DatatypeConverter;

public class Utilidades {
	private static final String AUTHENTICATION_SCHEME = "Basic";

	public static String[] decodeBasicAuth(String basicAuth) {
		String[] arrUsernameAndPassword = new String[2];
		final String encodedUserPassword = basicAuth.replaceFirst(
				AUTHENTICATION_SCHEME + " ", "");
		String usernameAndPassword = "";
		
//		byte[] message = "hello world".getBytes();
//		String encoded = DatatypeConverter.printBase64Binary(message);
//		byte[] decoded = DatatypeConverter.parseBase64Binary(encoded);
		
		usernameAndPassword = new String(DatatypeConverter.parseBase64Binary(encodedUserPassword));
		
//		usernameAndPassword = new String(Base64.getDecoder().decode(
//				encodedUserPassword));

		// Split username and password tokens
		final StringTokenizer tokenizer = new StringTokenizer(
				usernameAndPassword, ":");
		final String username = tokenizer.nextToken();
		final String password = tokenizer.nextToken();

		arrUsernameAndPassword[0] = username;
		arrUsernameAndPassword[1] = password;
		return arrUsernameAndPassword;
	}
}
