import util.CircularBuffer;
import util.Converter;
import util.PatchHelper;

import java.io.*;
import java.util.Random;

public class Test {
	private static final File FILE = new File("C:\\Zips\\test\\io.dat");
	private static final String STRING_A = "ff 90 1d 53 a0";	//ff 90 1d 53 a0
	private static final byte[] ARRAY_A = Converter.stringToData(STRING_A);

	private static final String SECTION1 = "00 01 02 03 ee";
	private static final String SECTION2 = "ff 90 1d 53 a0";
	private static final byte[] SECTION1_ARRAY = Converter.stringToData(SECTION1);
	private static final byte[] SECTION2_ARRAY = Converter.stringToData(SECTION2);

	public static void main(String[] args) {
		clearFile();
		writeFile();
		writeHeadTailSequence();
		PatchHelper.readFile(FILE, ARRAY_A);
		PatchHelper.readHeadTailFile(FILE, SECTION1_ARRAY, 0, 20, SECTION2_ARRAY);
	}

	@SuppressWarnings("unused")
	private static void misc() {
		try(FileOutputStream fileOut = new FileOutputStream(FILE, false);
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

	@SuppressWarnings("unused")
	private static void readBytes(File file) {
		try(FileInputStream fileIn = new FileInputStream(FILE);
			DataInputStream dataIn = new DataInputStream(fileIn)){
			try {
				while(true)
					System.out.println(dataIn.readByte());
			}
			catch (EOFException e){
				System.out.println("EOF");
			}

		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings("unused")
	private static void writeHeadTailSequence() {

		try(FileOutputStream fileOut = new FileOutputStream(FILE, true);
			BufferedOutputStream dataOut = new BufferedOutputStream(fileOut)){


			//100 random, section1, 10 bytes, section2, 120 random,
			//section1, 50 random, section2, 100 random, section1, 5 bytes, section2, 200 random
			Random random = new Random(3);

			writeRandomSegment(dataOut, random, 100);
			dataOut.write(SECTION1_ARRAY);
			writeRandomSegment(dataOut, random, 10);
			dataOut.write(SECTION2_ARRAY);
			writeRandomSegment(dataOut, random, 120);
			dataOut.write(SECTION1_ARRAY);
			writeRandomSegment(dataOut, random, 50);
			dataOut.write(SECTION2_ARRAY);
			writeRandomSegment(dataOut, random, 100);
			dataOut.write(SECTION1_ARRAY);
			writeRandomSegment(dataOut, random, 5);
			dataOut.write(SECTION2_ARRAY);
			writeRandomSegment(dataOut, random, 200);
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}

	//writes a segment of 'length' random bytes to the data output
	private static void writeRandomSegment(BufferedOutputStream dataOut, Random random, int length) throws IOException {
		byte[] arr = new byte[length];
		random.nextBytes(arr);
		dataOut.write(arr);
	}

	//writes a sequence of 00-ff bytes
	private static byte[] getArr() {
		byte[] b = new byte[256];
		for (int i = 0; i < 256; i++) {
			b[i] = (byte) i;
		}
		return b;
	}

	@SuppressWarnings("unused")
	private static void writeRange() {
		try(FileOutputStream fileOut = new FileOutputStream(FILE, true);
			BufferedOutputStream dataOut = new BufferedOutputStream(fileOut)){
			dataOut.write(getArr());
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings({"unused"})
	private static void readRange() {
		try(FileInputStream fileIn = new FileInputStream(FILE);
			DataInputStream dataIn = new DataInputStream(fileIn)){

			long i = 0;
			CircularBuffer buffer = new CircularBuffer(256);
			try {
				while(true) {
					buffer.push(dataIn.readByte());
					i++;
					if (buffer.query(getArr())) {
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

	@SuppressWarnings("unused")
	private static void writeFile() {
		try(FileOutputStream fileOut = new FileOutputStream(FILE, true);
			BufferedOutputStream dataOut = new BufferedOutputStream(fileOut)){

			Random random = new Random(3);
			writeRandomSegment(dataOut, random, 100);
			dataOut.write(ARRAY_A);
			writeRandomSegment(dataOut, random, 53);
			dataOut.write(ARRAY_A);
			dataOut.write(ARRAY_A);
			writeRandomSegment(dataOut, random, 121);
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}

	private static void clearFile() {
		try {
			FileOutputStream fileOut = new FileOutputStream(FILE, false);
			fileOut.close();
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}

}
