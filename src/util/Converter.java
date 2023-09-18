package util;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class Converter {
	private static Map<Character, Byte> hexToHalfByteMap;
	private static Map<Byte, Character> halfByteToHexMap;

	public static void main(String[] args) {
		test("1d 53 a0 a0 a0 a0");
		test("0001FFb8");
		test("00010203121212ff");
		test("ff 90 1d 53 a0");
	}

	private static void test(String s) {
		byte[] data = stringToData(s);
		System.out.println("byte[]:" + Arrays.toString(data));
		System.out.println("String:" + dataToString(data));
	}

	private static Map<Character, Byte> getHexToHalfByteMap() {
		if (hexToHalfByteMap == null) {
			hexToHalfByteMap = new HashMap<>();
			for (int i = 0; i <= 9; i++) {
				hexToHalfByteMap.put((char) (i + '0'), (byte) i);
			}
			for (int i = 0; i <= 5; i++) {
				hexToHalfByteMap.put((char) (i + 'a'), (byte) (i + 10));
			}
		}
		return hexToHalfByteMap;
	}

	private static Map<Byte, Character> getHalfBytetoHexMap() {
		if (halfByteToHexMap == null) {
			halfByteToHexMap = new HashMap<>();
			for (Map.Entry<Character, Byte> entry : getHexToHalfByteMap().entrySet()) {
				halfByteToHexMap.put(entry.getValue(), entry.getKey());
			}
		}
		return halfByteToHexMap;
	}

	public static byte[] stringToData(String bitString) {
		char[] bitStringArr = bitString
				.toLowerCase()
				.toCharArray();
		int length = 0;
		for (char c : bitStringArr) {
			if (getHexToHalfByteMap().containsKey(c)) {
				length++;
			}
		}

		//make sure there the length is an even number, as two characters make a byte
		if (length % 2 != 0) {
			throw new RuntimeException("Expected an even number of letters in byte string. Given length: " + length);
		}
		byte[] data = new byte[length / 2];
		int i = 0;
		boolean left = true;	//reading the left part of the byte first
		for (char c : bitStringArr) {
			if (getHexToHalfByteMap().containsKey(c)) {
				byte val = getHexToHalfByteMap().get(c);
				if (left) {
					data[i] = (byte) (val << 4);

				}
				else {
					data[i] += val;
					i++;
				}
				left = !left;
			}
		}
		return data;
	}

	@SuppressWarnings("GrazieInspection")
	public static String dataToString(byte[] data) {
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < data.length; i++)
		{
			if (i != 0) {
				builder.append(' ');
			}
			char right = getHalfBytetoHexMap().get((byte) (data[i] & 15));

			/* Note that we cannot simply use data[i] >> 4 to get the left part.
			Byte data type cannot be bit-shifted, so it gets implicitly cast into int.
			However, when the value of the byte is ff (or -1), that get casted into
			-1 in int, which is ff ff ff ff.
			When the bit shift is performed with that, we end up with 00 ff ff ff...
			which is cast into byte, resulting in ff ff (-1) again.
			That will fetch a null from the map (because there's no such key),
			and which will then cause a null pointer exception to be thrown when
			attempting to assign it to the char variable.
			 */
			char left = getHalfBytetoHexMap().get((byte) (data[i] >> 4 & 15));
			builder.append(left)
					.append(right);

		}
		return builder.toString();
	}
}
