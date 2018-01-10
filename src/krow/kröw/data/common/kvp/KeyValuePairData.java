package kröw.data.common.kvp;

import java.util.HashMap;

import kröw.data.common.Convertible;

public class KeyValuePairData extends HashMap<String, Object> implements Convertible<String> {

	@Override
	public String convertTo() {
		String text = "";
		for (Entry<String, Object> e : entrySet()) {
			text += e.getKey() + "=" + e.getValue() + "\n";

		}
		return text;
	}

	// This may soon need some private helper methods.
	@Override
	public void convertFrom(String convertedObject) {
		int position = 0;
		while (position < convertedObject.length()) {
			String key = "";
			char c = convertedObject.charAt(position);
			while (c != '=') {
				key += c;
				// Increment pos & then check if it's too large.
				if (!(++position < convertedObject.length()))
					throw new RuntimeException(
							"The data file ended in a key, so this key had no value. Better kill the entire program...");
			}

			String value = "";
			// Cache the position we're on; we'll need to refer back to it if we don't find
			// a '{'.
			int cachepos = position;
			while (Character.isWhitespace(c))
				if (!(++position < convertedObject.length()))
					throw new RuntimeException("The data file ended prematurely. Better kill the entire program...");
			if (c == '{') {
				int nesting = 1;
				while (nesting > 0) {

				}
			} else if (c == '[') {
				// TODO Remodel reading. Entirely. Arrays can store multiple values, including
				// multiple objects, or multiple arrays. For this, parsing keys and values
				// should be separated into a "parseKey()" and a "parseValue()" method. This
				// way, we can call parseValue() multiple times, if needed, for just one key.
			} else {
				position = cachepos;
				while (c != '\n') {
					value += c;
					if (!(++position < convertedObject.length()))
						break;
				}
			}
			if (!(++position < convertedObject.length()))
				break;

		}
	}

}
