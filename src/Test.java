import util.BulkPatchHelper;
import util.Converter;

import java.io.*;
import java.util.Random;

public class Test {
	private static final File FILE = new File("C:\\Zips\\test\\io.dat");
	private static final File FILE_AFTER = new File("C:\\Zips\\test\\io_after.dat");

	private static final String A = "aa bb cc";
	private static final String B1 = "ff bb cc 00";
	private static final String B2 = "ff bb cc";
	private static final String AB = "?? bb cc";
	private static final String C = "11 22 33 44 55";
	private static final String D = "dd dd 01 02 03";
	private static final String E = "00 00 00";

	public static void main(String[] args) {
//		writeFile();
		bulkFindTester(FILE);
		System.out.println("===========================");
		BulkPatchHelper.clearFile(FILE_AFTER);
		bulkReplaceTester(FILE, FILE_AFTER);
		System.out.println("===========================");
		bulkFindTester(FILE_AFTER);
	}

	@SuppressWarnings("unused")
	private static void bulkFindTester(File f) {
		System.out.println("Searching file, length " + f.length());
		BulkPatchHelper bulkPatchHelper = new BulkPatchHelper();
		bulkPatchHelper.setFileIn(f);
		bulkPatchHelper.addQuery("a", A);
		bulkPatchHelper.addQuery("b1", B1);
		bulkPatchHelper.addQuery("b2", B2);
		bulkPatchHelper.addQuery("ab", AB);
		bulkPatchHelper.addQuery("c", C);
		bulkPatchHelper.addQuery("d", D);
		bulkPatchHelper.addQuery("e", E);
		bulkPatchHelper.executeFind();
		bulkPatchHelper.printResults();
	}

	@SuppressWarnings("SameParameterValue")
	private static void bulkReplaceTester(File fIn, File fOut) {
		System.out.println("Replacing file, length " + fIn.length());
		BulkPatchHelper bulkPatchHelper = new BulkPatchHelper();
		bulkPatchHelper.setFileIn(fIn);
		bulkPatchHelper.setFileOut(fOut);
		bulkPatchHelper.addReplace("ab -> d", AB, D);
		bulkPatchHelper.addReplace("c -> e", C, E);
		bulkPatchHelper.executeReplace();
		bulkPatchHelper.printResults();
	}

	//writes a segment of 'length' random bytes to the data output
	private static void writeRandomSegment(BufferedOutputStream dataOut, Random random, int length) throws IOException {
		byte[] arr = new byte[length];
		random.nextBytes(arr);
		dataOut.write(arr);
	}

	//will perform a check to make sure none of the Bytes are null
	private static void write(BufferedOutputStream dataOut, String s) throws IOException {
		Byte[] arr1 = Converter.wildcardStringToData(s);
		for (Byte b : arr1 ) {
			if (b == null) {
				throw new RuntimeException("Cannot write a Byte[] array that contains a null value");
			}
		}
		byte[] arr2 = Converter.stringToData(s);
		dataOut.write(arr2);
	}

	@SuppressWarnings("unused")
	private static void writeFile() {
		try(FileOutputStream fileOut = new FileOutputStream(FILE, true);
			BufferedOutputStream dataOut = new BufferedOutputStream(fileOut)){

			Random random = new Random(3);
			writeRandomSegment(dataOut, random, 23);
			write(dataOut, A);
			write(dataOut, B1);
			write(dataOut, B2);
			write(dataOut, C);
			writeRandomSegment(dataOut, random, 12);
			write(dataOut, C);
			write(dataOut, C);
			writeRandomSegment(dataOut, random, 12);
			write(dataOut, B1);
			write(dataOut, C);
			write(dataOut, A);
			writeRandomSegment(dataOut, random, 2);
			write(dataOut, B2);
			write(dataOut, B1);
			writeRandomSegment(dataOut, random, 3);
			write(dataOut, B2);
			writeRandomSegment(dataOut, random, 120);
			write(dataOut, A);
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
}
