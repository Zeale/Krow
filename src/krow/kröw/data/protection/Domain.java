package kröw.data.protection;

import java.util.WeakHashMap;

public class Domain {

	private final String path;

	private static final WeakHashMap<String, Domain> loadedDomains = new WeakHashMap<>();

	static Domain getDomain(String path) {
		if (loadedDomains.containsKey(path))
			return loadedDomains.get(path);
		else
			return new Domain(path);
	}

	Domain(String fullName) {
		path = fullName;
		loadedDomains.put(fullName, this);
	}

	@Override
	public String toString() {
		return super.toString() + "[path=\"" + getPath() + "\"]";
	}

	public String getPath() {
		return path;
	}

}
