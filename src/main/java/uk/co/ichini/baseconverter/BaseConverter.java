package uk.co.ichini.baseconverter;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;

public class BaseConverter {

	private static final int ZERO = 0;
	private int base;
	private char[] encoding;
	private Map<Character, Integer> encodingTable;
	
	public BaseConverter(String encoding) {
		this.encoding = encoding.toCharArray();
		encodingTable = createTable(this.encoding);
		base = this.encoding.length;
	}

	public String encode(Number number) {
		String result = null;
		if (isLongCompatible(number)) {
			result = encode(toLong(number));
		} else {
			throw new IllegalArgumentException("Only integral numbers supported.");
		}
		return result;
	}

	public Number decode(String number) {
//		    """Compute the number given by digits in base b."""
//		    n = 0
//		    for d in digits:
//		        n = b * n + d
//		    return n
		long n = 0;
		Integer[] working = decodeToBase(number);
		for (Integer digit : working) {
			n = base * n + digit;
		}
		return n;
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

	private String encode(long number) {
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

	private Integer[] encodeToBase(long number) {
		Deque<Integer> working = new ArrayDeque<Integer>();
		if (number > 0) {
			long n = number;
		    while (n > 0) {
		        working.push((int)n % base);
		        n  = n / base;
		    }
		} else {
			working.add(ZERO);
		}
		return working.toArray(new Integer[working.size()]);
	}

	private long toLong(Number number) {
		return number.longValue();
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
