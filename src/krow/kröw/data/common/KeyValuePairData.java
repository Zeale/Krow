package kröw.data.common;

import java.util.HashMap;

public class KeyValuePairData {

	public final HashMap<String, Object> mapping = new HashMap<>();

	public void put(String key, Object value) {
		mapping.put(key, value);
	}

}
