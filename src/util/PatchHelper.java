package util;


import java.io.DataInputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;

public class PatchHelper {

	@SuppressWarnings("InfiniteLoopStatement")
	public static void readFile(File file, byte[] arr) {
		try(FileInputStream fileIn = new FileInputStream(file);
			DataInputStream dataIn = new DataInputStream(fileIn)){

			long i = 0;
			CircularBuffer buffer = new CircularBuffer(arr.length);
			try {
				while(true) {
					buffer.push(dataIn.readByte());
					i++;
					if (buffer.query(arr)) {
						System.out.println(i + ": " + Converter.dataToString(buffer.contents()));
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


	@SuppressWarnings({"InfiniteLoopStatement", "unused"})
	public static void readHeadTailFile(File file, byte[] section1, int minWildcards, int maxWildcards, byte[] section2) {
		try(FileInputStream fileIn = new FileInputStream(file);
			DataInputStream dataIn = new DataInputStream(fileIn)){

			long i = 0;
			CircularBuffer buffer = new CircularBuffer(section1.length + maxWildcards + section2.length);
			try {
				while(true) {
					buffer.push(dataIn.readByte());
					i++;
					if (buffer.headTail1Query(section1, minWildcards, maxWildcards, section2)) {
						System.out.println(i + ": " + Converter.dataToString(buffer.contents()));
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
