import util.BulkPatchHelper;
import util.Converter;

import java.io.*;
import java.util.Random;

public class Test {
	private static final File FILE = new File("C:\\Zips\\test\\io.dat");
	private static final File FILE_AFTER = new File("C:\\Zips\\test\\io_after.dat");

	private static final Byte[] A = Converter.wildcardStringToData("aa bb cc");
	private static final Byte[] B1 = Converter.wildcardStringToData("ff bb cc 00");
	private static final Byte[] B2 = Converter.wildcardStringToData("ff bb cc");
	private static final Byte[] AB = Converter.wildcardStringToData("?? bb cc");
	private static final Byte[] C = Converter.wildcardStringToData("11 22 33 44 55");

	public static void main(String[] args) {
//		BulkPatchHelper.clearFile(FILE);
//		writeFile();
		bulkFindTester();
	}

	@SuppressWarnings("unused")
	private static void bulkFindTester() {

		BulkPatchHelper bulkPatchHelper = new BulkPatchHelper();
		bulkPatchHelper.setFileIn(FILE);
		bulkPatchHelper.addQuery("a", A);
		bulkPatchHelper.addQuery("b1", B1);
		bulkPatchHelper.addQuery("b2", B2);
		bulkPatchHelper.addQuery("ab", AB);
		bulkPatchHelper.addQuery("c", C);
		bulkPatchHelper.run();
		bulkPatchHelper.printResults();
	}

	//writes a segment of 'length' random bytes to the data output
	private static void writeRandomSegment(BufferedOutputStream dataOut, Random random, int length) throws IOException {
		byte[] arr = new byte[length];
		random.nextBytes(arr);
		dataOut.write(arr);
	}

	private static void write(BufferedOutputStream dataOut, Byte[] arr) throws IOException {
		byte[] a = new byte[arr.length];
		for (int i = 0; i < arr.length; i++) {
			Byte b = arr[i];
			if (b == null) {
				throw new RuntimeException("Cannot write a Byte[] array that contains a null value");
			}
			else {
				a[i] = b;
			}
		}
		dataOut.write(a);
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
