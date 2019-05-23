package org.cysecurity.cspf.jvl.model;

import java.util.Random;

public class Salt {

	private Salt() {}

	public static String generateSalt() {
		StringBuilder builder = new StringBuilder();
		Random rand = new Random();
		byte[] bytes = new byte[32];
		rand.nextBytes(bytes);
		for(byte b : bytes) {
			builder.append(String.format("%02x", b));
		}
		return builder.toString();
	}
}
