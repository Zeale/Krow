package kr�w.data.in;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import kr�w.data.common.kvp.KeyValuePairData;

public class KVPDataReader {

	private final Scanner scanner;

	public KVPDataReader(Scanner scanner) {
		super();
		this.scanner = scanner;
	}

	public KVPDataReader(File file) throws FileNotFoundException {
		scanner = new Scanner(file);
	}

	public KVPDataReader(String filepath) throws FileNotFoundException {
		scanner = new Scanner(filepath);
	}

	public KeyValuePairData read() {

		// TODO Implement
			// TODO Check for (& parse) header
			// TODO Parse data
		return null;
	}

}
