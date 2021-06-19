package de.pfannekuchen.preprocessor.loader;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

import de.pfannekuchen.preprocessor.gradle.GradleSubproject;

/**
 * Fabric Environment Preparer
 * @author Pancake
 */
public class FabricSetup {
	
	/**
	 * Fabric Minecraft Versions
	 * @author Pancake
	 */
	public static enum Fabric {
		MC1_14_4, MC1_15_2, MC1_16_1, MC1_16_5, MC1_17;
	}
	
	/**
	 * Sets up a Fabric Environment
	 * @param directory Gradle Subproject
	 * @param version Fabric MC Version
	 * @param mappings Mappings Version
	 * @throws IOException Throws whenever Files couldn't be created
	 * @throws MalformedURLException Throws whenever an Invalid Version is being used
	 */
	public static void setupFabric(GradleSubproject directory, Fabric version, String yarn, String modname, String modgroup, String modversion, String loaderversion, String fabricapiversion, String... javaHome) throws MalformedURLException, IOException {
		// Remove Build/Bin Folder
		new File(directory.getLocation(), "bin").delete();
		new File(directory.getLocation(), "build").delete();
		// Create important Files
		new File(directory.getLocation(), "src/main/java").mkdirs();
		new File(directory.getLocation(), "src/main/resources").mkdirs();
		new File(directory.getLocation(), "gradle.properties").createNewFile();
		Files.copy(new URL("http://mgnet.work/cfg/" + version.toString()).openStream(), new File(directory.getLocation(), "build.gradle").toPath(), StandardCopyOption.REPLACE_EXISTING);
		Files.copy(new URL("http://mgnet.work/cfg/fabric.gradle").openStream(), new File(directory.getLocation(), "settings.gradle").toPath(), StandardCopyOption.REPLACE_EXISTING);
		// Download gradle.properties, but change values in it
		BufferedReader reader = new BufferedReader(new InputStreamReader(new URL("http://mgnet.work/cfg/" + version.toString() + ".properties").openStream()));
		PrintWriter writer = new PrintWriter(new File(directory.getLocation(), "gradle.properties"));
		String _line;
		while ((_line = reader.readLine()) != null) {
			writer.println(_line
				.replaceAll("%MODNAME%", modname)
				.replaceAll("%MODGROUP%", modgroup)
				.replaceAll("%MODVERSION%", modversion)
				.replaceAll("%MAPPINGSVERSION%", yarn)
				.replaceAll("%FABRICAPI%", fabricapiversion)
				.replaceAll("%LOADER%", loaderversion)
			);
		}
		writer.println("org.gradle.jvmargs=-Xmx1250M");
		writer.println("org.gradle.daemon=false");
		if (javaHome.length == 1) writer.println("org.gradle.java.home=" + javaHome[0]);
		writer.close();
		reader.close();
		// Note: you don't have to run genSources ever. <3 Fabric
	}
	
}
