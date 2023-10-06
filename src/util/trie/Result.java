package util.trie;

import util.Converter;

public class Result {
	public final long offset;
	public final byte[] actual;

	public Result(long offset, byte[] actual) {
		this.offset = offset;
		this.actual = actual;
	}

	@Override
	public String toString() {
		return "Byte " + offset + ": " + Converter.dataToString(actual);
	}
}
