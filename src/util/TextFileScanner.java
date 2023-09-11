package util;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

/*
Grabs all text files from the given directory and all subdirectories,
then searches all lines in them.
All the lines that match the regex pattern will be printed in the log.
Note that before printing, the results are also stripped of tab characters (to remove indentation).
 */
@SuppressWarnings("unused")
public class TextFileScanner {
	/**
	 * Searches every line in .txt files in the given directory and all subdirectories,
	 * then prints only the lines that match the regex given.
	 * Tab characters are stripped from the matching lines before printing.
	 *
	 * @param directory Search this directory and all subdirectories for
	 *                  .txt files.
	 *                  e.g. "C:\Files\"
	 *
	 * @param regex e.g. ".+stuff.+" fetches all lines with the word "stuff" in it.
	 *
	 */
	public static void getMatchingLines(String directory, String regex) {
		Set<String> set = new HashSet<>();
		Pattern pattern = Pattern.compile(regex);
		Path dir = Paths.get(directory);
		try (Stream<Path> files = Files.walk(dir)) {
			files.filter(TextFileScanner::isTxtFile).forEach(x -> getMatches(x, set, pattern));
		}
		catch (IOException e) {
			System.out.println(e.getMessage());
		}

		System.out.println("Matches: " + set.size());
		for (String s : set) {
			System.out.println(s);
		}
	}

	static void getMatches(Path file, Set<String> matches, Pattern pattern) {
		try (Stream<String> lines = Files.lines(file, StandardCharsets.UTF_8)) {
			lines.forEach(x -> {
				Matcher matcher = pattern.matcher(x);
				if (matcher.find()) {
					matches.add(x.replaceAll("\\t",""));
				}
			});
		}
		catch (IOException e) {
			System.out.println(e.getMessage());
		}
	}

	static boolean isTxtFile(Path file) {
		String[] splitName = file.getFileName().toString().split("\\.");
		return splitName[splitName.length - 1].equals("txt");
	}
}
