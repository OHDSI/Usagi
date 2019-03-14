package org.ohdsi.usagi;

import java.io.File;

import org.ohdsi.usagi.ui.UsagiMain;

public class Launcher {
	private final static int	MIN_HEAP	= 1000;

	public static void main(String[] args) throws Exception {

		float heapSizeMegs = (Runtime.getRuntime().maxMemory() / 1024) / 1024;

		if (heapSizeMegs > MIN_HEAP) {
			System.out.println("Launching with current VM");
			UsagiMain.main(args);
		} else {
			System.out.println("Starting new VM");
			String pathToJar = UsagiMain.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath();
			ProcessBuilder pb = new ProcessBuilder("java", "-Xmx" + MIN_HEAP + "m", "-classpath", pathToJar, "org.ohdsi.usagi.ui.UsagiMain");
			pb.inheritIO();
			pb.redirectError(new File("ErrorStream.txt"));
			pb.start();
		}
	}
}
