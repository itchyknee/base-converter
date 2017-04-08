package uk.co.ichini.baseconverter;

import java.math.BigInteger;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;

public class BaseConverter {

	/**
	 * Default encoding for bases 10, 16, 36, 62, 66.
	 */
	public static final String DEFAULT_ENCODING = "0123456789"
			+ "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
			+ "abcdefghijklmnopqrstuvwxyz"
			+ "-._~";
	
	private static final int ZERO = 0;
	
	private static BaseConverter sBase10;
	private static BaseConverter sBase16;
	private static BaseConverter sBase36;
	private static BaseConverter sBase62;
	
	private BigInteger base;
	private char[] encoding;
	private Map<Character, Integer> encodingTable;
	
	public static BaseConverter base10() {
		if (sBase10 == null) {
			sBase10 = new BaseConverter(10);
		}
		return sBase10;
	}
	
	public static BaseConverter base16() {
		if (sBase16 == null) {
			sBase16 = new BaseConverter(16);
		}
		return sBase16;
	}
	
	public static BaseConverter base36() {
		if (sBase36 == null) {
			sBase36 = new BaseConverter(36);
		}
		return sBase36;
	}
	
	public static BaseConverter base62() {
		if (sBase62 == null) {
			sBase62 = new BaseConverter(62);
		}
		return sBase62;
	}
	
	public BaseConverter(int base) {
		if (base > DEFAULT_ENCODING.length()) {
			throw new IllegalArgumentException("Maximum base for standard mapping limited to " 
					+ DEFAULT_ENCODING.length()
					+ ". Use constructor with encoding String instead.");
		}
		this.encoding = DEFAULT_ENCODING.substring(0, base).toCharArray();
		this.base = BigInteger.valueOf(base);
		this.encodingTable = createTable(this.encoding);
	}
	public BaseConverter(String encoding) {
		this.encoding = encoding.toCharArray();
		this.base = BigInteger.valueOf(this.encoding.length);
		this.encodingTable = createTable(this.encoding);
	}

	public String encode(byte[] bytes) {
		return encode(toBigInteger(bytes));
	}
	
	public String encode(Number number) {
		BigInteger input;
		if (BigInteger.class.isAssignableFrom(number.getClass())) {
			input = (BigInteger)number;
		} else if (isLongCompatible(number)) {
			input = BigInteger.valueOf(number.longValue());
		} else {
			throw new IllegalArgumentException("Only integral numbers supported.");
		}
		return encode(input);
	}

	public byte[] decodeBytes(String number) {
		BigInteger n = BigInteger.ZERO;
		Integer[] working = decodeToBase(number);
		for (Integer digit : working) {
			n = base.multiply(n).add(BigInteger.valueOf(digit));
		}
		return n.toByteArray();
	}

	public Number decodeNumber(String number) {
		BigInteger n = BigInteger.ZERO;
		Integer[] working = decodeToBase(number);
		for (Integer digit : working) {
			n = base.multiply(n).add(BigInteger.valueOf(digit));
		}
		return n;
	}

	private BigInteger toBigInteger(byte[] bytes) {
		byte[] numberBytes = new byte[bytes.length + 1];
		System.arraycopy(bytes, 0, numberBytes, 1, bytes.length);
		BigInteger result = new BigInteger(numberBytes);
		return result;
	}

	private Integer[] decodeToBase(String number) {
		char[] chars = number.toCharArray();
		Integer[] working = new Integer[chars.length];
		for (int i = 0; i < chars.length; i++) {
			working[i] = encodingTable.get(chars[i]);
			if (working[i] == null) {
				throw new IllegalArgumentException("Encoded character ["
						+ chars[i] 
						+ "] not found in encoding: "
						+ Arrays.toString(encoding));
			}
		}
		return working;
	}

	private String encode(BigInteger number) {
		Integer[] working = encodeToBase(number);
		return encodeFromBase(working);
	}

	private String encodeFromBase(Integer[] working) {
		StringBuilder result = new StringBuilder();
		for (int i = 0; i < working.length; i++) {
			Integer index = working[i];
			result.append(encoding[index]);
		}
	    return result.toString();
	}

	private Integer[] encodeToBase(BigInteger number) {
		Deque<Integer> working = new ArrayDeque<Integer>();
		if (!number.equals(BigInteger.ZERO)) {
			BigInteger n = number;
		    while (!n.equals(BigInteger.ZERO)) {
		        working.push(n.mod(base).intValue());
		        n  = n.divide(base);
		    }
		} else {
			working.add(ZERO);
		}
		return working.toArray(new Integer[working.size()]);
	}

	private boolean isLongCompatible(Number number) {
		return Long.class.isAssignableFrom(number.getClass())
				|| Integer.class.isAssignableFrom(number.getClass())
				|| Short.class.isAssignableFrom(number.getClass());
	}

	/**
	 * Create a map of encoding character to 'base' value.
	 * @param encoding
	 * @return
	 */
	private Map<Character, Integer> createTable(char[] encoding) {
		Map<Character, Integer> result = new HashMap<Character, Integer>();
		for (int i = 0; i < encoding.length; i++) {
			char c = encoding[i];
			if (result.containsKey(c)) {
				throw new IllegalArgumentException("Duplicate encoding caharacter: " + c);
			}
			result.put(c, i);
		}
		return result;
	}

	
}
