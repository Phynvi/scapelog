import junit.framework.TestCase;

public class StringReverse extends TestCase {

	public void testStringReverse() {
		int generator = 1503630405;

		System.out.println(encrypt(generator, "i.K5^OH"));
		assertEquals("key", encrypt(generator, "q.Q"));
	}

	private String encrypt(int generator, String str) {
		char[] chars = str.toCharArray();
		for (int i = 0; i < chars.length; i++) {
			generator = generator * 1103515245 + 12345;
			chars[i] ^= generator & 0x7F;
		}
		return new String(chars);
	}

}