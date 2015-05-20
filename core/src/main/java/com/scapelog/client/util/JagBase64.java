package com.scapelog.client.util;

import java.util.prefs.Preferences;

/**
 * Static methods for translating Base64 encoded strings to byte arrays
 * and vice-versa.
 *
 * @author  Josh Bloch
 * @author Sean
 * @version 1.4, 01/23/03
 * @see     Preferences
 * @since   1.4
 */
public final class JagBase64 {

	public static String encode(byte[] a) {
		int aLen = a.length;
		int numFullGroups = aLen/3;
		int numBytesInPartialGroup = aLen - 3*numFullGroups;
		int resultLen = 4*((aLen + 2)/3);
		StringBuffer result = new StringBuffer(resultLen);
		char[] intToAlpha = intToBase64;
		int inCursor = 0;
		for (int i=0; i<numFullGroups; i++) {
			int byte0 = a[inCursor++] & 0xff;
			int byte1 = a[inCursor++] & 0xff;
			int byte2 = a[inCursor++] & 0xff;
			result.append(intToAlpha[byte0 >> 2]);
			result.append(intToAlpha[(byte0 << 4)&0x3f | (byte1 >> 4)]);
			result.append(intToAlpha[(byte1 << 2)&0x3f | (byte2 >> 6)]);
			result.append(intToAlpha[byte2 & 0x3f]);
		}
		if (numBytesInPartialGroup != 0) {
			int byte0 = a[inCursor++] & 0xff;
			result.append(intToAlpha[byte0 >> 2]);
			if (numBytesInPartialGroup == 1) {
				result.append(intToAlpha[(byte0 << 4) & 0x3f]);
			}
		}
		return result.toString();
	}

	private static final char intToBase64[] = {
			'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M',
			'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z',
			'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm',
			'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
			'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '*', '/'
	};

	public static byte[] decode(String s) {
		byte[] buffer = new byte[s.length()*3 / 4];
		if(buffer.length != 16) {
			throw new IllegalArgumentException("Sorry but only 16 bytes is supported in jagex's base64 implemention.");
		}
		int mask = 0xFF;
		int index = 0;
		for(int i = 0; i < s.length(); i += 4){
			int c0 = base64ToInt[s.charAt( i )];
			int c1 = base64ToInt[s.charAt( i + 1)];
			buffer[index++]= (byte)(((c0 << 2) | (c1 >> 4)) & mask);
			if(index >= buffer.length){
				return buffer;
			}
			int c2 = base64ToInt[s.charAt( i + 2)];
			buffer[index++]= (byte)(((c1 << 4) | (c2 >> 2)) & mask);
			if(index >= buffer.length){
				return buffer;
			}
			int c3 = base64ToInt[s.charAt( i + 3 )];
			buffer[index++]= (byte)(((c2 << 6) | c3) & mask);
		}
		return buffer;
	}

	private static final byte base64ToInt[] = { -1, -1, -1, -1, -1, -1, -1, -1,
			-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
			-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
			62, 62, -1, 63, -1, 63, 52, 53, 54, 55, 56, 57, 58, 59, 60, 61, -1,
			-1, -1, -1, -1, -1, -1, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12,
			13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, -1, -1, -1, -1,
			-1, -1, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40,
			41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, -1, -1, -1, -1, -1 };

}