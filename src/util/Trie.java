package util;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

class Trie {
	private Trie wildcard;						//contains node that has a wildcard at its position
	private final Map<Byte, Set<Trie>> map;		//contains all non-wildcard nodes
	private Sequence sequence;					//stores a sequence IF this is the leaf for that sequence
	private final Set<Trie> resultSet;			//created once and reused
	private final BulkQuery bulkQuery;

	Trie(BulkQuery bulkQuery) {
		map = new HashMap<>();
		resultSet = new HashSet<>();
		this.bulkQuery = bulkQuery;
	}

	//get all children that match this byte
	Set<Trie> pushQuery(byte b) {
		resultSet.clear();
		if (wildcard != null) {
			resultSet.add(wildcard);
		}
		if (map.containsKey(b)) {
			resultSet.addAll(map.get(b));
		}

		//marking each leaf node in the result set as seen
		for (Trie child : resultSet) {
			if (child.sequence != null) {
				//todo entire sequence + offset number is put in. could modify this.
				child.sequence.getResults().add(new Result(bulkQuery.getOffset(), bulkQuery.buffer.contents()));
			}
		}

		return resultSet;
	}

	//insert the given Sequence's sequence as a child, starting at the offset element
	void populate(Sequence sequence, int offset) {
		Byte b = sequence.getQuery()[offset];
		Trie child = new Trie(this.bulkQuery);
		if (b == null) {
			this.wildcard = child;
		}
		else {
			addToKey(b, child);
		}
		//adding the sequence only if this child is a leaf
		if (offset >= sequence.getQuery().length - 1) {
			child.sequence = sequence;
		}
		else {
			child.populate(sequence, offset + 1);
		}
	}

	private void addToKey(byte b, Trie child) {
		if (map.containsKey(b)) {
			map.get(b).add(child);
		}
		else {
			Set<Trie> set = new HashSet<>();
			set.add(child);
			map.put(b, set);
		}
	}

}
