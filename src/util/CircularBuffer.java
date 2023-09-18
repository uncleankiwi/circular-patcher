package util;

public class CircularBuffer {
	private Node first;		//becomes the head when the buffer is empty
	private Node head;
	private Node tail;
	private final int maxSize;
	private int size;		//size of buffer that's actually filled

	public CircularBuffer(int maxSize) {
		size = 0;
		this.maxSize = maxSize;
		Node last = null;
		for (int i = 1; i <= maxSize; i++) {
			Node n = new Node();
			if (first == null) {
				first = n;
			}

			if (last != null) {
				last.next = n;
			}
			last = n;

			if (i == maxSize) {
				last.next = first;
			}
		}
	}

	/*
	If it is empty, set first as head and tail and write to it.
	If it isn't empty, write to tail's next. If the new tail is head, set head as head's next.
	 */
	public void push(byte b) {
		if (size < maxSize) {
			size++;
		}
		if (head == null) {
			head = first;
			tail = first;
			first.b = b;
		}
		else {
			tail = tail.next;
			tail.b = b;
			if (tail == head) {
				head = head.next;
			}
		}
	}

	/**
	 * A simple search that tests to see if the buffer contents equal a given array,
	 * starting at the head position of the buffer.
	 * @param arr Search query.
	 * @return Whether the query is an exact match.
	 */
	public boolean query(byte[] arr) {
		if (size < arr.length) {
			return false;
		}
		return query(arr, head);
	}


	/**
	 * A simple search that tests to see if the buffer contents equal a given array,
	 * starting at the given position of the array.
	 * @param arr Search query.
	 * @param start The node position in the array to start checking.
	 * @return Whether the query is a match.
	 */
	public boolean query(byte[] arr, Node start) {
		Node n = start;
		for (byte b : arr) {
			if (b != n.b) {
				return false;
			}
			n = n.next;
		}
		return true;
	}

	/**
	 * Returns whether the buffer contains the given section1 sequence starting at the head position,
	 * followed some bytes in the middle (this section is minWildcard to maxWildcard in length),
	 * and then finally ending with the 'section2' sequence of the query.
	 * @param section1 The starting sequence of the search query.
	 * @param minWildcards Number of wildcard bytes in the middle.
	 * @param maxWildcards Number of wildcard bytes in the middle.
	 * @param section2 The ending sequence of the search query.
	 * @return Search result.
	 */
	public boolean headTail1Query(byte[] section1, int minWildcards, int maxWildcards, byte[] section2) {
		Node n = head;
		for (byte value : section1) {
			if (n.b != value) {
				return false;
			}

			n = n.next;
		}

		//skipping minWildcard nodes
		Node section2Start = n;
		for (int i = 0; i < minWildcards; i++) {
			n = n.next;
		}

		//trying to match the remaining section, with an additional 0 to (maxWildcards - minWildcards) in between
		boolean match;
		for (int i = 0; i < maxWildcards - minWildcards; i++) {
			match = true;
			//moving the head one step at the start of the second loop onwards
			if (i != 0) {
				section2Start = section2Start.next;
			}

			n = section2Start;
			for (byte b : section2) {
				if (n.b != b) {
					match = false;
					break;
				}
				n = n.next;
			}

			if (match) {
				return true;
			}
		}
		return false;
	}

	//copy whatever was in the buffer into a byte[]
	public byte[] contents() {
		byte[] arr = new byte[size];
		Node n = head;
		for (int i = 0; i < size; i++) {
			arr[i] = n.b;
			n = n.next;
		}
		return arr;
	}

	//dump the contents of the buffer.
	//to be used when replacing the search query match with something else.
	@SuppressWarnings("unused")
	public void dump() {
		size = 0;
		head = null;
		tail = null;
	}

	@SuppressWarnings("unused")
	public int maxSize() {
		return maxSize;
	}

	private static class Node {
		Node next;
		byte b;
	}
}
