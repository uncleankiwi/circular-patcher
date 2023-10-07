package util;

import util.trie.BulkQuery;
import util.trie.Sequence;

import java.io.DataInputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;

public class BulkPatchHelper {
	private File fileIn;
	private File fileOut;
	private BulkQuery bulkQuery;
	private CircularBuffer buffer;

	@SuppressWarnings("InfiniteLoopStatement")
	public void run() {
		try(FileInputStream fileInStream = new FileInputStream(fileIn);
		DataInputStream dataInStream = new DataInputStream(fileInStream)){

			if (bulkQuery.getSequences().size() == 0) {
				throw new RuntimeException("No sequences to search");
			}

			int maxQueryLength = 0;
			for (Sequence sequence : bulkQuery.getSequences()) {
				if (sequence.getQuery().length > maxQueryLength) {
					maxQueryLength = sequence.getQuery().length;
				}
			}

			long offsetCounter = 0;
			buffer = new CircularBuffer(maxQueryLength);
			try {
				while(true) {
					buffer.push(dataInStream.readByte());
					offsetCounter++;
					if (buffer.queryWithWildcards(arr)) {
						System.out.println(offsetCounter + ": " + Converter.dataToString(buffer.contents()));
					}
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
}


