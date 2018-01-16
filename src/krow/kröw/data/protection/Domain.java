package kröw.data.protection;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.WeakHashMap;

//Until the workings of this class are sorted out, I'm gonna keep it final.
public final class Domain {

	private static final String DOMAIN_CONFIGURATION_FILE_NAME = "DomainConfiguration.kdc";

	private final String path;
	private final File directory, configurationFile;
	private final DomainConfigData configuration;

	private static final WeakHashMap<String, Domain> loadedDomains = new WeakHashMap<>();

	static Domain getDomain(String path) throws DomainInitializeException, UnavailableException {

		// Availability checkup
		if (!Protection.isAvailable()) {
			if (!Protection.tryEnable())
				throw new UnavailableException();
		}

		if (loadedDomains.containsKey(path))
			return loadedDomains.get(path);
		else
			return new Domain(path);
	}

	static Domain getDomain(Class<?> owner) throws DomainInitializeException, UnavailableException {

		// Availability checkup
		if (!Protection.isAvailable()) {
			if (!Protection.tryEnable())
				throw new UnavailableException();
		}

		String name = pkgToDom(owner.getName(), owner.getCanonicalName());
		return getDomain(name);
	}

	private Domain(String fullName) throws DomainInitializeException {

		// Set the directory field, so the initialization methods and such can use it.
		directory = new File(Protection.DOMAINS_DIRECTORY, path = fullName);

		// The config's directory will be used
		configurationFile = new File(directory, DOMAIN_CONFIGURATION_FILE_NAME);

		// Make the directory represented by this Domain. If there's already a folder
		// there and there is no DomainConfiguration file, then we throw an Exception
		// and s
		if (directory.exists())
			if (!(directory.isDirectory() || configurationFile.isFile()))
				throw new DomainInitializeException(
						"The file/directory denoted by this domain exists but is not a domain (it has no configuration file or something). The process of creating this domain has failed. This problem can usually be solved by deleting whatever exists where this domain does; delete this file: "
								+ directory.getAbsolutePath());
			else
				;// The directory exists and checkDirectory() returned true. This is a valid
		// directory. Let's get a configuration object that represents this Domain's
		// config (done below) and we are done constructing this Domain object :D
		else if (!directory.mkdirs())
			throw new DomainInitializeException(
					"For some reason, there was an error when trying to make the directory (folder) for a Domain. The location (where the folder was supposed to go) might be protected, or something, by admin privileges etc., but I'm not really sure how those things work... The location, ("
							+ directory.getPath()
							+ "), shouldn't exist, but its parent folder might be protected. Or something... Gl m8. (By the way, some of the parent folders might have been created.)");

		// Then set the configuration field, so it can be verified in initialize and
		// such.
		configuration = new DomainConfigData();

		initialize();
		loadedDomains.put(path, this);
	}

	private void initialize() throws DomainInitializeException {
		// Initial file check.
	}

	@Override
	public String toString() {
		return super.toString() + "[path=\"" + getPath() + "\"]";
	}

	public String getPath() {
		return path;
	}

	static String pkgToDom(String clsName, String canonicalName) {
		return clsName.contains("$") ? clsName.replaceAll("\\.", "/").replaceAll("\\$", "#")
				: canonicalName.replaceAll("\\.", "/");
	}

	static String domToPkg(String fullPath) {
		return fullPath.replaceAll("\\#", "\\$").replaceAll("\\/", ".");
	}

	public int getBuildNumber() {
		return configuration.getBuildNumber();
	}

	private class DomainConfigData {

		private int buildNumber = -1;
		private InputStreamReader configReader;
		private OutputStreamWriter configWriter;

		public int getBuildNumber() {
			return buildNumber;
		}

		private HashMap<String, Object> data = new HashMap<>();

		private DomainConfigData() throws DomainInitializeException {

			try {

				boolean newFile = configurationFile.createNewFile();

				configReader = new InputStreamReader(new FileInputStream(configurationFile));

				int numb;
				if (newFile) {
					InputStreamReader reader = new InputStreamReader(
							getClass().getResourceAsStream("/krow/resources/build.data"));
					if (!reader.ready())
						throw new DomainInitializeException(
								"The build.data file, (which is nested inside the program), was empty...");
					String buildNumb = "";
					while (reader.ready())
						buildNumb += (char) reader.read();
					numb = Integer.parseInt(buildNumb);
					this.buildNumber = numb;
				} else {
					if (!configReader.ready())
						throw new DomainInitializeException("The configuration file was empty...");
					String buildNumb = "";
					while (configReader.ready())
						buildNumb += (char) configReader.read();
					numb = Integer.parseInt(buildNumb);
					this.buildNumber = numb;
				}

				// The writer erases the file, so we make it here. After we read the file.
				configWriter = new OutputStreamWriter(new FileOutputStream(configurationFile));

				configWriter.write(numb + "");
				configWriter.flush();

			} catch (IOException e) {
				throw new DomainInitializeException(e);
			}

			if (buildNumber == -1)
				throw new DomainInitializeException(
						"Couldn't read the build number of this program that was stored in this Domain. The file may be corrupt or something, or maybe the build number couldn't be read from an internal location, if you were making a new Domain.");

		}

	}

}
