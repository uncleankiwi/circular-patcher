package util;


import java.io.*;

public class PatchHelper {

	@SuppressWarnings({"InfiniteLoopStatement", "unused"})
	public static void find(File file, byte[] arr) {
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

	@SuppressWarnings("InfiniteLoopStatement")
	public static void findWithWildcard(File file, Byte[] arr) {
		try(FileInputStream fileIn = new FileInputStream(file);
			DataInputStream dataIn = new DataInputStream(fileIn)){

			long i = 0;
			CircularBuffer buffer = new CircularBuffer(arr.length);
			try {
				while(true) {
					buffer.push(dataIn.readByte());
					i++;
					if (buffer.queryWithWildcards(arr)) {
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
	public static void replaceWithWildcard(File fileIn, File fileOut, Byte[] arrFind, Byte[] arrReplace) {
		try(FileInputStream streamIn = new FileInputStream(fileIn);
			DataInputStream dataIn = new DataInputStream(streamIn);
			FileOutputStream streamOut = new FileOutputStream(fileOut);
			BufferedOutputStream dataOut = new BufferedOutputStream(streamOut)){

			long byteNumber = 0;
			CircularBuffer buffer = new CircularBuffer(arrFind.length);
			try {
				while(true) {
					Byte overflow = buffer.pushAndReturn(dataIn.readByte());
					if (overflow != null) {
						dataOut.write(overflow);
					}
					byteNumber++;
					if (buffer.queryWithWildcards(arrFind)) {
						byte[] result = buffer.contents();
						System.out.println(byteNumber + ": " + Converter.dataToString(result));
						//substituting bytes in the result array with non-null bytes in arrReplace
						for (int i = 0; i < arrReplace.length; i++) {
							if (arrReplace[i] != null) {
								result[i] = arrReplace[i];
							}
						}

						//write the edited array to file and dump the buffer contents
						dataOut.write(result);
						buffer.clear();

					}
				}
			}
			catch (EOFException e){
				//write whatever was remaining in the buffer to file
				dataOut.write(buffer.contents());
				System.out.println("End of file");
			}

		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings({"InfiniteLoopStatement", "unused"})
	public static void findHeadTail(File file, byte[] section1, int minWildcards, int maxWildcards, byte[] section2) {
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

	public static void clearFile(File file) {
		try {
			FileOutputStream fileOut = new FileOutputStream(file, false);
			fileOut.close();
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
}
