package kröw.libs;

import java.util.Locale;

public enum OperatingSystem {
	WINDOWS, MAC_OS, LINUX, OTHER, GENERIC;

	public static final OperatingSystem CURRENT_OPERATING_SYSTEM;

	static {
		CURRENT_OPERATING_SYSTEM = getCurrentOperatingSystem();
	}

	public static OperatingSystem getCurrentOperatingSystem() {
		final String os = System.getProperty("os.name", "generic").toLowerCase(Locale.ENGLISH);
		if (os.startsWith("windows"))
			return OperatingSystem.WINDOWS;
		else if (os.startsWith("mac") || os.startsWith("darwin"))
			return OperatingSystem.MAC_OS;
		else if (os.startsWith("linux"))
			return LINUX;
		else if (os.equals("generic"))
			return GENERIC;
		else
			return OTHER;
	}

	public static OperatingSystem getCurrentOperatingSystemFromCache() {
		return CURRENT_OPERATING_SYSTEM;
	}
}
