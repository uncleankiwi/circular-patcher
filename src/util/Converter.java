package util;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class Converter {
	private static Map<Character, Byte> hexToByteMap;
	private static Map<Byte, Character> byteToHexMap;

	public static void main(String[] args) {
		byte[] data = stringToData("0001FFb");
		System.out.println(Arrays.toString(data));
		System.out.println(dataToString(data));
	}

	private static Map<Character, Byte> getHexToByteMap() {
		if (hexToByteMap == null) {
			hexToByteMap = new HashMap<>();
			for (int i = 0; i <= 9; i++) {
				hexToByteMap.put((char) (i + '0'), (byte) i);
			}
			for (int i = 0; i <= 5; i++) {
				hexToByteMap.put((char) (i + 'a'), (byte) (i + 10));
			}
		}
		return hexToByteMap;
	}

	private static Map<Byte, Character> getBytetoHexMap() {
		if (byteToHexMap == null) {
			byteToHexMap = new HashMap<>();
			for (Map.Entry<Character, Byte> entry : getHexToByteMap().entrySet()) {
				byteToHexMap.put(entry.getValue(), entry.getKey());
			}
		}
		return byteToHexMap;
	}

	public static byte[] stringToData(String bitString) {
		char[] bitStringArr = bitString
				.toLowerCase()
				.toCharArray();
		int length = 0;
		for (char c : bitStringArr) {
			if (getHexToByteMap().containsKey(c)) {
				length++;
			}
		}
		byte[] data = new byte[length];

		for (int inIndex = 0, outIndex = 0; inIndex < length; inIndex++) {
			char c = bitStringArr[inIndex];
			if (getHexToByteMap().containsKey(c)) {
				data[outIndex] = getHexToByteMap().get(c);
				outIndex++;
			}
		}
		return data;
	}

	public static String dataToString(byte[] data) {
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < data.length; i++) {
			if (i % 2 == 0 && i != 0) {
				builder.append(' ');
			}
			builder.append(getBytetoHexMap().get(data[i]));
		}
		return builder.toString();
	}
}
