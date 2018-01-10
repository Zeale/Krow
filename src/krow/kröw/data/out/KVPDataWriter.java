package kr�w.data.out;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.Writer;

import kr�w.data.common.KeyValuePairData;

// TODO Add more convenience setters for os.
public class KVPDataWriter {
	private final Writer os;

	public void write(KeyValuePairData data) {
		writeHeader();
		writeData(data);
	}

	private void writeHeader() {
		// TODO Implement
	}

	private void writeData(KeyValuePairData data) {
		// TODO Implement
	}

	public void writeWithoutHeader(KeyValuePairData data) {
		writeData(data);
	}

	public KVPDataWriter(Writer writer) {
		os = writer;
	}

	public KVPDataWriter(String filepath) throws FileNotFoundException {
		os = new PrintWriter(filepath);
	}

	public KVPDataWriter(File file) throws FileNotFoundException {
		os = new PrintWriter(file);
	}

}
