package util;

import java.util.ArrayList;
import java.util.List;

public class Sequence {
	private final String description;
	private final Byte[] query;
	private final List<Result> results;
	private final Byte[] replace;

	public Sequence(String description, Byte[] query) {
		this(description, query, null);
	}

	public Sequence(String description, Byte[] query, Byte[] replace) {
		this.description = description;
		this.query = query;
		this.results = new ArrayList<>();
		this.replace = replace;
	}

	public Byte[] getQuery() {
		return query;
	}

	public List<Result> getResults() {
		return results;
	}

	public Byte[] getReplace() {
		return replace;
	}

	int length() {
		return query.length;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder(description);
		builder.append("[").append(Converter.dataToString(query)).append("]").append("\n");
		for (Result result : results) {
			builder.append(result)
					.append("\n");
		}
		return builder.toString();
	}
}
