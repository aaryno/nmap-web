package aaryn.nmap.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Singleton class used to read properties only once for the whole application
 * 
 * @author aaryno1
 *
 */
public class NmapWebProperties {

	private static NmapWebProperties instance;
	private static Properties properties;

	private NmapWebProperties() throws IOException {
		loadProperties();
	}

	public Properties getProperties() {
		return properties;
	}

	public static void setProperties(Properties properties) {
		NmapWebProperties.properties = properties;
	}

	public static synchronized NmapWebProperties getInstance()
			throws IOException {
		if (instance == null) {
			instance = new NmapWebProperties();
		}
		return instance;
	}

	private static Properties loadProperties() throws IOException {
		properties = new Properties();
		InputStream inputStream = null;
		String resource = "nmapweb.properties";
		inputStream = NmapWebProperties.class.getClassLoader()
				.getResourceAsStream(resource);
		properties.load(inputStream);

		if (inputStream != null) {
			try {
				inputStream.close();
			} catch (IOException e) {
				System.err
						.println("Unable to close resource file: " + resource);
				e.printStackTrace();
				// todo: what's most appropriate here. Do we have our properties
				// or not?
			}
		}
		return properties;
	}
}
