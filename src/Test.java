import util.BulkPatchHelper;
import util.Converter;

import java.io.*;
import java.util.Random;

@SuppressWarnings("unused")
public class Test {
	private static final File FILE = new File("C:\\Zips\\test\\io.dat");
	private static final File FILE_AFTER = new File("C:\\Zips\\test\\io_after.dat");
	private static final String STRING_A = "ff 90 1d 53 a0";
	private static final byte[] ARRAY_A = Converter.stringToData(STRING_A);

	private static final byte[] B_1 = Converter.stringToData("ff 33 10 53 a0");
	private static final byte[] B_2 = Converter.stringToData("ff 22 01 53 a0");
	private static final Byte[] B_FIND = Converter.wildcardStringToData("ff ?? ?? 53 a0");
	private static final Byte[] B_REPLACE = Converter.wildcardStringToData("ff ?? ?? dd 0e");

	private static final String SECTION1 = "00 01 02 03 ee";
	private static final String SECTION2 = "ff 90 1d 53 a0";
	private static final byte[] SECTION1_ARRAY = Converter.stringToData(SECTION1);
	private static final byte[] SECTION2_ARRAY = Converter.stringToData(SECTION2);

	public static void main(String[] args) {
		bulkFindTester();
	}

	@SuppressWarnings("unused")
	private static void bulkFindTester() {
		BulkPatchHelper.clearFile(FILE);
		Byte[] a = Converter.wildcardStringToData("aa bb cc");
		Byte[] b2 = Converter.wildcardStringToData("ff bb cc");
		Byte[] b1 = Converter.wildcardStringToData("ff bb cc 00");
		Byte[] ab = Converter.wildcardStringToData("?? bb cc");
		Byte[] c = Converter.wildcardStringToData("11 22 33 44 55");


		BulkPatchHelper bulkPatchHelper = new BulkPatchHelper();
		bulkPatchHelper.setFileIn(FILE);
		bulkPatchHelper.addQuery("a", a);
		bulkPatchHelper.addQuery("b1", b1);
		bulkPatchHelper.addQuery("b2", b2);
		bulkPatchHelper.addQuery("ab", ab);
		bulkPatchHelper.addQuery("c", c);
		bulkPatchHelper.run();
		bulkPatchHelper.printResults();
	}

	//writes a segment of 'length' random bytes to the data output
	private static void writeRandomSegment(BufferedOutputStream dataOut, Random random, int length) throws IOException {
		byte[] arr = new byte[length];
		random.nextBytes(arr);
		dataOut.write(arr);
	}

	private static void writeFile() {
		try(FileOutputStream fileOut = new FileOutputStream(FILE, true);
			BufferedOutputStream dataOut = new BufferedOutputStream(fileOut)){

			Random random = new Random(3);
			writeRandomSegment(dataOut, random, 100);
			dataOut.write(ARRAY_A);
			writeRandomSegment(dataOut, random, 37);
			dataOut.write(ARRAY_A);
			dataOut.write(ARRAY_A);
			writeRandomSegment(dataOut, random, 120);
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
}
