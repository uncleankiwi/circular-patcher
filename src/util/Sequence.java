package util;

import java.util.ArrayList;
import java.util.List;

public class Sequence {
	private final String description;
	private final Byte[] query;
	private final List<Result> results;

	public Sequence(String description, Byte[] query) {
		this.description = description;
		this.query = query;
		this.results = new ArrayList<>();
	}

	public Byte[] getQuery() {
		return query;
	}

	public List<Result> getResults() {
		return results;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder(description);
		builder.append("[").append(Converter.dataToString(query)).append("]").append("\n");
		for (Result result : results) {
			builder.append(result);
		}
		return builder.toString();
	}
}
