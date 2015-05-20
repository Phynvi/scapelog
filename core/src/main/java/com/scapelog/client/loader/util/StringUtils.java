package com.scapelog.client.loader.util;

/**
 * @author Sean
 */
public final class StringUtils {

	/**
	 * The {@link StringUtils} private constructor to stop the class
	 * from being created.
	 */
	private StringUtils() {

	}

	/**
	 * A method that gets a class based on its file name.
	 * @param str The string.
	 * @return The modified string.
	 */
	public static String classToFileName(String str) {
		str = str.replace('.', '/');
		if (!str.endsWith(".class")) {
			str = str + ".class";
		}
		return str;
	}

	/**
	 * Replaces all characters that equal to a certain character with another.
	 * @param str The string to check.
	 * @param toReplace The character to replace with.
	 * @param replaceWith The character to replace with.
	 * @return The new modified string.
	 */
	public static String replaceAllWith(String str, char toReplace, char replaceWith) {
		char[] chars = str.toCharArray();
		for (int i = 0; i < chars.length; i++) {
			if (chars[i] == toReplace) {
				chars[i] = replaceWith;
			}
		}
		return new String(chars);
	}
}