package util;

import java.io.*;
import java.util.Set;

import static util.Converter.byteToByte;

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
		if (bulkQuery == null || bulkQuery.getSequences().size() == 0) {
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
		if (bulkQuery == null || bulkQuery.getSequences().size() == 0) {
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
						writeReplaceWithWildcards(dataOut, buffer, sequence);
					}
				}
			}
			catch (EOFException e){
				//search whatever remains in the buffer
				while (buffer.size() > 0) {
					Sequence sequence = buffer.bulkReplaceWithWildcards(bulkQuery);
					if (sequence != null) {
						writeReplaceWithWildcards(dataOut, buffer, sequence);
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

	//write the replacement sequence.
	//if there are any null bytes (and replacement/query sequences have the same length, use the output's bytes)
	private void writeReplaceWithWildcards(DataOutputStream dataOut, CircularBuffer buffer, Sequence sequence)
			throws IOException {
		//if the replacement sequence and query are of the same length,
		//replace it byte by byte. When encountering a null Byte, use the original's.
		Byte[] query = sequence.getQuery();
		Byte[] replace = sequence.getReplace();
		if (query.length == replace.length) {
			byte[] bufferContents = buffer.contents();
			for (int i = 0; i < sequence.length(); i++) {
				if (replace[i] == null) {
					dataOut.writeByte(bufferContents[i]);
				}
				else {
					dataOut.writeByte(replace[i]);
				}
			}
		}
		else {
			//write the replacement sequence
			dataOut.write(byteToByte(sequence.getReplace()));
		}
		//purge the query sequence from the buffer
		buffer.purge(sequence.length());
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

	@SuppressWarnings({"SpellCheckingInspection", "unused"})
	public void addQuery(String description, Byte[] queryStart, int minMidsegment, int maxMidsegment,
						 Byte[] queryEnd) {
		if (bulkQuery == null) {
			bulkQuery = new BulkQuery();
		}
		bulkQuery.add(description, queryStart, minMidsegment, maxMidsegment, queryEnd);
	}

	@SuppressWarnings({"SpellCheckingInspection", "unused"})
	public void addQuery(String description, String queryStart, int minMidsegment, int maxMidsegment,
						 String queryEnd) {
		if (bulkQuery == null) {
			bulkQuery = new BulkQuery();
		}
		bulkQuery.add(description, Converter.wildcardStringToData(queryStart),
				minMidsegment, maxMidsegment, Converter.wildcardStringToData(queryEnd));
	}

	public void addReplace(String description, String query, String replace) {
		addReplace(description, Converter.wildcardStringToData(query), Converter.wildcardStringToData(replace));
	}

	public void addReplace(String description, Byte[] query, Byte[] replace) {
		if (bulkQuery == null) {
			bulkQuery = new BulkQuery();
		}
		bulkQuery.add(description, query, replace);
	}

	@SuppressWarnings({"SpellCheckingInspection", "unused"})
	public void addReplace(String description, Byte[] queryStart, int minMidsegment, int maxMidsegment,
						   Byte[] queryEnd, Byte[] replaceStart, Byte[] replaceEnd) {
		if (bulkQuery == null) {
			bulkQuery = new BulkQuery();
		}
		bulkQuery.add(description, queryStart, minMidsegment, maxMidsegment, queryEnd,
				replaceStart, replaceEnd);
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

	@SuppressWarnings("unused")
	public Set<Sequence> getSequences() {
		return bulkQuery.getSequences();
	}
}


