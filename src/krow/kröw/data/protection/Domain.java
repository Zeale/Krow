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

	static Domain getDomain(Class<?> owner) {
		String name = pkgToDom(owner.getName(), owner.getCanonicalName());
		return getDomain(name);
	}

	private Domain(String fullName) {
		// Instead of using this replacement mechanism, domain folders will have files
		// inside them denoting them as domain folders.
		// fullName = fullName.substring(0, fullName.lastIndexOf("/")) + "/$" +
		// fullName.substring(fullName.lastIndexOf("/") + 1);
		loadedDomains.put(path = fullName, this);
	}

	@Override
	public String toString() {
		return super.toString() + "[path=\"" + getPath() + "\"]";
	}

	public String getPath() {
		return path;
	}

	/**
	 * Sets this domain up (in the filesystem,) for use. Use this method before
	 * calling any read/write methods on this domain.
	 */
	public void initialize() {
		// TODO Make folder(s) and add configuration file. The config file will assert
		// that the directory is a valid domain folder, and not a subfolder.
	}

	static String pkgToDom(String clsName, String canonicalName) {
		return clsName.contains("$") ? clsName.replaceAll("\\.", "/").replaceAll("\\$", "#")
				: canonicalName.replaceAll("\\.", "/");
	}

	static String domToPkg(String fullPath) {
		return fullPath.replaceAll("\\#", "\\$").replaceAll("\\/", ".");
	}

}
