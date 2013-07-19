package com.sirma.itt.javacourse.netgui;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;

import com.sirma.itt.javacourse.netgui.downloadAgent.DownloadAgent;

/**
 * Tests the functionality of the download agent.
 */
public final class DownloadTester {
	/**
	 * A private constructor.
	 */
	private DownloadTester() {
	}

	/**
	 * Entry point.
	 * 
	 * @param args
	 *            are the cmd args
	 * @throws IOException
	 *             if problem with the IO
	 */
	@SuppressWarnings("unused")
	public static void main(String[] args) throws IOException {
		File sourceFile = new File("output.txt");
		DownloadAgent agent = new DownloadAgent();
		InetAddress addr = InetAddress.getLocalHost();
	}
}
