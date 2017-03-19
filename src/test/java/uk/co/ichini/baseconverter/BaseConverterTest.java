package uk.co.ichini.baseconverter;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.security.crypto.keygen.BytesKeyGenerator;
import org.springframework.security.crypto.keygen.KeyGenerators;

public class BaseConverterTest {

	private BaseConverter converter;
	
	@Test(expected=IllegalArgumentException.class)
	public void testIllegalArg_repeatedEncoding() {
		new BaseConverter("11");
	}

	@Test(expected=IllegalArgumentException.class)
	public void testIllegalArg_EncodedCharNotFound() {
		converter = new BaseConverter("0123456789");
		converter.decodeNumber("A0");
	}

	@Test
	public void testIntegerEncoding_base10() {
		converter = BaseConverter.base10();
		checkEncoding(0,  "0");
		checkEncoding(1,  "1");
		checkEncoding(9,  "9");
		checkEncoding(10, "10");
	}

	@Test
	public void testIntegerDecoding_base10() {
		converter = BaseConverter.base10();
		checkDecoding("0",  0);
		checkDecoding("1",  1);
		checkDecoding("9",  9);
		checkDecoding("10", 10);
	}

	@Test
	public void testIntegerEncoding_base16() {
		converter = BaseConverter.base16();
		checkEncoding(0,  "0");
		checkEncoding(1,  "1");
		checkEncoding(15,  "F");
		checkEncoding(16, "10");
	}

	@Test
	public void testIntegerDecoding_base16() {
		converter = BaseConverter.base16();
		checkDecoding("0",  0);
		checkDecoding("1",  1);
		checkDecoding("F",  15);
		checkDecoding("10", 16);
	}

	@Test
	public void testIntegerEncoding_base36() {
		converter = BaseConverter.base36();
		checkEncoding(0,  "0");
		checkEncoding(1,  "1");
		checkEncoding(35,  "Z");
		checkEncoding(36, "10");
	}

	@Test
	public void testIntegerDecoding_base36() {
		converter = BaseConverter.base36();
		checkDecoding("0",  0);
		checkDecoding("1",  1);
		checkDecoding("Z",  35);
		checkDecoding("10", 36);
	}

	@Test
	public void testIntegerEncoding_base62() {
		converter = BaseConverter.base62();
		checkEncoding(0,  "0");
		checkEncoding(1,  "1");
		checkEncoding(61,  "z");
		checkEncoding(62, "10");
	}

	@Test
	public void testIntegerDecoding_base62() {
		converter = BaseConverter.base62();
		checkDecoding("0",  0);
		checkDecoding("1",  1);
		checkDecoding("z",  61);
		checkDecoding("10", 62);
	}

	@Test
	public void testIntegerEncoding_baseN() {
		converter = new BaseConverter(62);
		checkEncoding(0,  "0");
		checkEncoding(1,  "1");
		checkEncoding(61,  "z");
		checkEncoding(62, "10");
	}

	@Test(expected=IllegalArgumentException.class)
	public void testIntegerEncoding_baseN_exceedsKnown() {
		new BaseConverter(70);
	}
	
	private void checkDecoding(String encoded, long expected) {
		Number actual = converter.decodeNumber(encoded).longValue();
		assertEquals(expected, actual);
		assertEquals(encoded, converter.encode(actual));
	}

	private void checkEncoding(Number n, String expected) {
		String actual = converter.encode(n);
		assertEquals(expected, actual);
		assertEquals(n.longValue(), converter.decodeNumber(actual).longValue());
	}

	@Test
	public void testEncodingBytes() {
		converter = BaseConverter.base62();
		checkEncoding(new byte[]{
				0, -116, -45, -126, 114, -46, 32, -58, 33, 103,
				-22, -46, 77, -2, -33,-83, 74, 77, 91, 17, -67,
				40, -89, 5, 19, -23, 74, 78, -5, 20, -41, 24, 19
		}, "XORQQbLbgsORu2g7KpTAMGXW7mTQTBWJYWJsFF7ZYDj");
	}
	
	@Test
	public void testDecodingBytes() {
		converter = BaseConverter.base62();
		checkDecoding("XORQQbLbgsORu2g7KpTAMGXW7mTQTBWJYWJsFF7ZYDj", new byte[]{
				0, -116, -45, -126, 114, -46, 32, -58, 33, 103,
				-22, -46, 77, -2, -33,-83, 74, 77, 91, 17, -67,
				40, -89, 5, 19, -23, 74, 78, -5, 20, -41, 24, 19
		});
	}
	

	private void checkDecoding(String encoded, byte[] expected) {
		byte[] actual = converter.decodeBytes(encoded);
		assertArrayEquals(expected, actual);
		assertEquals(encoded, converter.encode(actual));
	}

	private void checkEncoding(byte[] n, String expected) {
		String actual = converter.encode(n);
		assertEquals(expected, actual);
		assertArrayEquals(n, converter.decodeBytes(actual));
	}

	//TODO fix when 2s complement padding changes return byte[]
	@Test
	@Ignore
	public void roudtripMultiple() {
		converter = BaseConverter.base62();
		BytesKeyGenerator generator = KeyGenerators.secureRandom(32);
		for (int i = 0; i < 10; i++) {
			byte[] bytes = generator.generateKey();
			String actual = converter.encode(bytes);
			System.out.println(actual);
			assertArrayEquals(bytes, converter.decodeBytes(actual));
		}
//		System.out.println(BaseConverter.base62().encode(bytes));
//		System.out.println(new BaseConverter(64).encode(bytes));
//		System.out.println(Base64Utils.encodeToString(bytes));
	}
	
	@After
	public void teardown() {
		//prevent leaks from tests
		converter = null;
	}
}
