package util;

import java.io.*;

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
	public void run() {
		if (bulkQuery.getSequences().size() == 0) {
			throw new RuntimeException("No sequences to search");
		}
		if (fileIn == null) {
			throw new RuntimeException("No input file specified");
		}

		try(FileInputStream fileInStream = new FileInputStream(fileIn);
		DataInputStream dataInStream = new DataInputStream(fileInStream)){
			int maxQueryLength = 0;
			for (Sequence sequence : bulkQuery.getSequences()) {
				if (sequence.getQuery().length > maxQueryLength) {
					maxQueryLength = sequence.getQuery().length;
				}
			}

			CircularBuffer buffer = new CircularBuffer(maxQueryLength);
			try {
				while(true) {
					buffer.push(dataInStream.readByte());
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

	public void addQuery(String description, String query) {
		addQuery(description, Converter.wildcardStringToData(query));
	}

	public void addQuery(String description, Byte[] query) {
		if (bulkQuery == null) {
			bulkQuery = new BulkQuery();
		}
		bulkQuery.add(description, query);
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
}


