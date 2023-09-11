import util.CircularBuffer;
import util.Converter;

import java.io.*;
import java.util.Random;

public class Test {
	private static final File FILE = new File("C:\\Zips\\test\\io.dat");
	private static final String STRING_A = "ff 90 1d 53 a0";
	private static final byte[] ARRAY_A = Converter.stringToData(STRING_A);

	public static void main(String[] args) {
		writeFile();
		readFile();
	}

	@SuppressWarnings("unused")
	private static void misc() {
		try(FileOutputStream fileOut = new FileOutputStream(FILE, true);
			FileInputStream fileIn = new FileInputStream(FILE);
			DataOutputStream dataOut = new DataOutputStream(fileOut);
			DataInputStream dataIn = new DataInputStream(fileIn)){

			for (int i = 1; i <= 100; i++) {
				dataOut.writeInt((int)(Math.random() * 256 * 256));
			}
			int sum = 0;
			try {
				while(true)
					sum += dataIn.readInt();
			}
			catch (EOFException e){
				System.out.println("Sum of integers in file: " + sum);
			}

		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}

	private static void writeFile() {
		try(FileOutputStream fileOut = new FileOutputStream(FILE, true);
			BufferedOutputStream dataOut = new BufferedOutputStream(fileOut)){

			byte[] arr = new byte[100];
			Random random = new Random(3);
			random.nextBytes(arr);
			dataOut.write(arr);

			dataOut.write(ARRAY_A);

			random.nextBytes(arr);
			dataOut.write(arr);

			dataOut.write(ARRAY_A);
			dataOut.write(ARRAY_A);

			byte[] arr1 = new byte[53];
			random.nextBytes(arr1);
			dataOut.write(arr1);
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}

	/*
	---------------------
	Circular buffer tests
	---------------------
	 */

	@SuppressWarnings("InfiniteLoopStatement")
	private static void readFile() {
		try(FileInputStream fileIn = new FileInputStream(FILE);
			DataInputStream dataIn = new DataInputStream(fileIn)){

			long i = 0;
			CircularBuffer buffer = new CircularBuffer(ARRAY_A.length);
			try {
				while(true) {
					buffer.push(dataIn.readByte());
					i++;
					if (buffer.equals(ARRAY_A)) {
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
