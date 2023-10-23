package util;

import java.io.*;
import java.util.Set;

/*
Holds:
	- input file and output file
	- the BulkQuery object that contains the trie built from the query strings
	- the buffer to be used
 */
public class BulkPatchHelper {
	private File fileIn;
	private File fileOut;
	private BulkQuery bulkQuery;

	@SuppressWarnings("InfiniteLoopStatement")
	public void executeFind() {
		if (bulkQuery.getSequences().size() == 0) {
			throw new RuntimeException("No sequences to search");
		}
		if (fileIn == null) {
			throw new RuntimeException("No input file specified");
		}

		try(FileInputStream fileInStream = new FileInputStream(fileIn);
		DataInputStream dataIn = new DataInputStream(fileInStream)){

			//calculate buffer size
			int maxQueryLength = 0;
			for (Sequence sequence : bulkQuery.getSequences()) {
				if (sequence.getQuery().length > maxQueryLength) {
					maxQueryLength = sequence.getQuery().length;
				}
			}
			CircularBuffer buffer = new CircularBuffer(maxQueryLength);

			try {
				while(true) {
					buffer.push(dataIn.readByte());
					buffer.bulkQueryWithWildcards(bulkQuery);
				}
			}
			catch (EOFException e){
				System.out.println("End of file");
			}

		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}


	@SuppressWarnings("InfiniteLoopStatement")
	public void executeReplace() {
		if (bulkQuery.getSequences().size() == 0) {
			throw new RuntimeException("No sequences to search");
		}
		if (fileIn == null) {
			throw new RuntimeException("No input file specified");
		}
		if (fileOut == null) {
			throw new RuntimeException("No output file specified");
		}

		try(FileInputStream fileInStream = new FileInputStream(fileIn);
			DataInputStream dataIn = new DataInputStream(fileInStream);
			FileOutputStream fileOutStream = new FileOutputStream(fileOut, false);
			DataOutputStream dataOut = new DataOutputStream(fileOutStream)){

			//calculate buffer size
			int maxQueryLength = 0;
			for (Sequence sequence : bulkQuery.getSequences()) {
				if (sequence.getQuery().length > maxQueryLength) {
					maxQueryLength = sequence.getQuery().length;
				}
			}
			CircularBuffer buffer = new CircularBuffer(maxQueryLength);

			try {
				while(true) {
					Byte b = buffer.pushAndReturn(dataIn.readByte());
					if (b != null) {
						dataOut.writeByte(b);
					}
					Sequence sequence = buffer.bulkReplaceWithWildcards(bulkQuery);
					//a result was found that has to be replaced
					if (sequence != null) {
						//write the replacement sequence
						dataOut.write(sequence.getReplace());
						//purge the query sequence from the buffer
						buffer.purge(sequence.length());
					}
				}
			}
			catch (EOFException e){
				//search whatever remains in the buffer
				while (buffer.size() > 0) {
					Sequence sequence = buffer.bulkReplaceWithWildcards(bulkQuery);
					if (sequence != null) {
						dataOut.write(sequence.getReplace());
						buffer.purge(sequence.length());
					}
					else {
						dataOut.write(buffer.pop());
					}
				}
				System.out.println("End of file");
			}

		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}

	public void addQuery(String description, String query) {
		addQuery(description, Converter.wildcardStringToData(query));
	}

	public void addQuery(String description, Byte[] query) {
		if (bulkQuery == null) {
			bulkQuery = new BulkQuery();
		}
		bulkQuery.add(description, query);
	}

	public void addReplace(String description, String query, String replace) {
		addReplace(description, Converter.wildcardStringToData(query), Converter.stringToData(replace));
	}

	public void addReplace(String description, Byte[] query, byte[] replace) {
		if (bulkQuery == null) {
			bulkQuery = new BulkQuery();
		}
		bulkQuery.add(description, query, replace);
	}

	public void setFileIn(File file) {
		this.fileIn = file;
	}

	public void setFileOut(File file) {
		this.fileOut = file;
	}

	@SuppressWarnings("unused")
	public static void clearFile(File file) {
		try {
			FileOutputStream fileOut = new FileOutputStream(file, false);
			fileOut.close();
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}

	public void printResults() {
		System.out.println(bulkQuery);
	}

	public Set<Sequence> getSequences() {
		return bulkQuery.getSequences();
	}
}


