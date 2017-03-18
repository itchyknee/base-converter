package uk.co.ichini.baseconverter;

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Test;

public class BaseConverterTest {

	BaseConverter converter;
	
	@After
	public void teardown() {
		//prevent leaks from tests
		converter = null;
	}
	
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
		
		converter = new BaseConverter("0123456789");
		checkEncoding(0,  "0");
		checkEncoding(1,  "1");
		checkEncoding(9,  "9");
		checkEncoding(10, "10");
	}

	@Test
	public void testIntegerDecoding_base10() {
		
		converter = new BaseConverter("0123456789");
		checkDecoding("0",  0);
		checkDecoding("1",  1);
		checkDecoding("9",  9);
		checkDecoding("10", 10);
	}

	private void checkDecoding(String encoded, long expected) {
		assertEquals(expected, converter.decode(encoded));
	}

	private void checkEncoding(Number n, String expected) {
		assertEquals(expected, converter.encode(n));
	}

//	//@Test
//	public void testRoundtripBase10() {
//		
//		check("0123456789", 1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
//	}

//	private <N extends Number> void check(String encoding, N... fixture) {
//		
//		BaseConverter<N> converter = new BaseConverter<N>(encoding);
//		for (N expected : fixture) {
//			String encodedNumber = converter.encode(expected);
//			N actual = converter.decode(encodedNumber);
//			assertEquals(expected, actual);
//		}
//	}
}
