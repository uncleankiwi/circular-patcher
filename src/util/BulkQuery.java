package util;

import java.util.HashSet;
import java.util.Set;

/*
Requirements:
	Each BulkQuery contains only one trie.
	The head Trie node won't correspond to any bytes; the first byte of all Sequences correspond to
	the children of this head node. That way, we don't need to have multiple Tries stored in this wrapper object.

	Each sequence should have:
		1. descriptive text
		2. a list of results containing
			A. offset and
			B. actual result

	For use in CircularBuffer:
		public void bulkQueryWithWildcards(BulkQuery bulkQuery) {...}
		- whenever one or more sequence in the trie match the buffer contents,
		the sequence encountered and the offset are noted.

 */
class BulkQuery {
	private final Trie head;
	//stores all sequences encountered, will be iterated through and printed after search
	private final Set<Sequence> sequences;
	private Set<Trie> currentSet;
	private Set<Trie> nextSet;
	CircularBuffer buffer;

	public BulkQuery() {
		head = new Trie(this);
		currentSet = new HashSet<>();
		nextSet = new HashSet<>();
		sequences = new HashSet<>();
	}

	public Set<Sequence> getSequences() {
		return this.sequences;
	}

	public void setBuffer(CircularBuffer buffer) {
		this.buffer = buffer;
	}

	public boolean hasNext() {
		return !currentSet.isEmpty();
	}

	//for every Trie in current set, put their downstream Tries into nextSet
	public void pushQuery(byte b) {
		for (Trie trie : currentSet) {
			nextSet.addAll(trie.pushQuery(b));
		}
		Set<Trie> temp = currentSet;
		currentSet = nextSet;
		nextSet = temp;
		nextSet.clear();
	}

	//resets the search, and sets the scope back to the heads of the Tries stored
	public void reset() {
		currentSet.clear();
		currentSet.add(head);
	}

	long getOffset() {
		return buffer.getOffset();
	}

	void add(String description, Byte[] query) {
		Sequence sequence = new Sequence(description, query);
		sequences.add(sequence);
		head.populate(sequence, 0);
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder("Bytes searched: " + getOffset());
		for (Sequence sequence : sequences) {
			builder.append("\n")
					.append(sequence);
		}
		return builder.toString();
	}
}
