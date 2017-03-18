package uk.co.ichini.baseconverter;

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Test;

public class BaseConverterTest {

	private BaseConverter converter;
	
	@Test(expected=IllegalArgumentException.class)
	public void testIllegalArg_repeatedEncoding() {
		new BaseConverter("11");
	}

	@Test(expected=IllegalArgumentException.class)
	public void testIllegalArg_EncodedCharNotFound() {
		converter = new BaseConverter("0123456789");
		converter.decode("A0");
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
		assertEquals(expected, converter.decode(encoded).longValue());
	}

	private void checkEncoding(Number n, String expected) {
		assertEquals(expected, converter.encode(n));
	}

	@After
	public void teardown() {
		//prevent leaks from tests
		converter = null;
	}
}
