package util;

public class CircularBuffer {
	private Node first;		//becomes the head when the buffer is empty
	private Node head;
	private Node tail;
	private final int maxSize;
	private int size;		//size of buffer that's actually filled
	private long offset;

	public CircularBuffer(int maxSize) {
		offset = -1;
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
		offset++;
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

	public long getOffset() {
		//the stored offset is based on the end of the buffer, so we subtract the current buffer size
		//to obtain the offset of the beginning
		return offset - size + 1;
	}

	/*
	Gives the trie in the bulkQuery the contents in the window until no more are requested
	(because there are no more possible branches that match or a leaf has been reached).
	 */
	public void bulkQueryWithWildcards(BulkQuery bulkQuery) {
		bulkQuery.setBuffer(this);
		Node n = head;
		bulkQuery.reset();
		while (bulkQuery.hasNext()) {
			bulkQuery.pushQuery(n.b);
			n = n.next;
		}
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
	public void clear() {
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
