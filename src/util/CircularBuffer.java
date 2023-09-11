package util;

import java.util.Objects;

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

	public boolean equals(byte[] arr) {
		if (size != arr.length) {
			return false;
		}
		Node n = head;
		for (byte b : arr) {
			if (n.b != b) {
				return false;
			}
			n = n.next;
		}
		return true;
	}

	//support for query arrays that may have wildcards
	public boolean equals(Byte[] arr) {
		if (size != arr.length) {
			return false;
		}
		Node n = head;
		for (Byte b : arr) {
			if (b!= null && n.b != b) {
				return false;
			}
			n = n.next;
		}
		return true;
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
	public void flush() {
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

		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (o == null || getClass() != o.getClass()) return false;
			Node node = (Node) o;
			return this.b == node.b;
		}

		@Override
		public int hashCode() {
			return Objects.hash(b);
		}
	}
}
