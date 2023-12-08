package util;

import java.util.ArrayList;
import java.util.List;

/*
A sequence to search for, but with a stretch of bytes in the middle of a variable length.
 */
public class VariableSequence extends Sequence {
	private final List<Byte[]> possibleQueries;
	private List<Byte[]> possibleReplaces;
	private final Byte[] queryStart;
	private final Byte[] queryEnd;
	@SuppressWarnings("SpellCheckingInspection")
	private final int minMidsegment;
	@SuppressWarnings("SpellCheckingInspection")
	private final int maxMidsegment;
	@SuppressWarnings({"FieldCanBeLocal", "unused"})
	private final Byte[] replaceStart;
	@SuppressWarnings({"FieldCanBeLocal", "unused"})
	private final Byte[] replaceEnd;

	@SuppressWarnings("SpellCheckingInspection")
	public VariableSequence(String description, Byte[] queryStart, int minMidsegment, int maxMidsegment,
							Byte[] queryEnd) {
		this(description, queryStart,  minMidsegment,  maxMidsegment, queryEnd, null, null);
	}

	@SuppressWarnings("SpellCheckingInspection")
	public VariableSequence(String description, Byte[] queryStart, int minMidsegment, int maxMidsegment,
							Byte[] queryEnd, Byte[] replaceStart, Byte[] replaceEnd) {
		super(description, null, null);
		this.queryStart = queryStart;
		this.queryEnd = queryEnd;
		this.minMidsegment = minMidsegment;
		this.maxMidsegment = maxMidsegment;
		this.replaceStart = replaceStart;
		this.replaceEnd = replaceEnd;

		possibleQueries = buildPossibleArrays(queryStart, minMidsegment, maxMidsegment, queryEnd);
		if (replaceStart != null && replaceEnd != null) {
			possibleReplaces = buildPossibleArrays(replaceStart, minMidsegment, maxMidsegment, replaceEnd);
		}
	}

	//Sets the query (and replace) arrays to one of the arrays in the possibleQueries/possibleReplace lists.
	public void setIndex(int i) {
		if (i < 0 || i >= possibleQueries.size()) {
			throw new IndexOutOfBoundsException("Index " + i + " is out of range; maximum index is " +
					(possibleQueries.size() - 1));
		}

		query = possibleQueries.get(i);
		if (possibleReplaces != null) {
			replace = possibleReplaces.get(i);
		}
	}

	//a number for looping through all the possible queries
	public int getMaxIndex() {
		return possibleQueries.size() - 1;
	}

	private List<Byte[]> buildPossibleArrays(Byte[] start, int minMidSegment, int maxMidsegment, Byte[] end) {
		List<Byte[]> result = new ArrayList<>();
		for (int i = minMidSegment; i <= maxMidsegment; i++) {
			//building each possible array
			Byte[] arr = new Byte[start.length + i + end.length];
			System.arraycopy(start, 0, arr, 0, start.length);
			System.arraycopy(end, 0, arr, start.length + i, end.length);
			result.add(arr);
		}
		return result;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder(description);
		builder.append("[").append(Converter.dataToString(queryStart))
				.append(" (+ ").append(minMidsegment)
				.append(" - ")
				.append(maxMidsegment)
				.append(" bytes) ")
				.append(Converter.dataToString(queryEnd))
				.append("]").append("\n");
		for (Result result : results) {
			builder.append(result.actual.length)
					.append(result)
					.append("\n");
		}
		return builder.toString();
	}
}
