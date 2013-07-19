package com.sirma.itt.javacourse.netgui.downloadAgent;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URLConnection;

import javax.swing.JButton;
import javax.swing.JProgressBar;

/**
 * Starts a new thread to read from the source URL and write to the file
 * specified.
 */
@SuppressWarnings("unused")
public class Downloader extends Thread {
	private final BufferedInputStream in;
	private final BufferedOutputStream out;
	private final URLConnection connection;
	private final File sourceFile;
	private TransferObject transferrer;
	private final JProgressBar pBar;
	private final JButton dButton;

	/**
	 * Constrcuts the downloader with an initial URL source connection to read
	 * from, a filename to write to, and GUI elements to handle.
	 * 
	 * @param connection
	 *            is the source connection where the downloader will read the
	 *            data from
	 * @param sourceFile
	 *            is the file where the data will be written
	 * @param pBar
	 *            is the progress bar to indicate what part of the file has been
	 *            downlaoded so far
	 * @param dButton
	 *            is the download button that the downloader thread will set to
	 *            unactive and then active when the download is done
	 * @throws IOException
	 *             if there is a problem with the IO
	 */
	public Downloader(URLConnection connection, File sourceFile,
			JProgressBar pBar, JButton dButton)
			throws IOException {
		this.connection = connection;
		this.sourceFile = sourceFile;
		this.pBar = pBar;
		this.dButton = dButton;
		pBar.setVisible(true);
		this.in = new BufferedInputStream(connection.getInputStream());
		this.out = new BufferedOutputStream(new FileOutputStream(sourceFile));
		start();
	}

	/**
	 * The main logic of the downloader thread is contained within this method.
	 */
	@Override
	public void run() {
		int transferredBytes = 0;
		// Will not work if the connection source doesn't have header fields for
		// content-length
		int bytesToTransfer = connection.getContentLength();
		dButton.setEnabled(false);
		transferrer = new TransferObject(in, out);
		try {
			transferredBytes = transferrer.transfer(bytesToTransfer, 0, pBar);
		} catch (IOException e) {
		} finally {
			try {
				in.close();
				out.close();
			} catch (IOException e) {
			}
		}
		dButton.setEnabled(true);
		if (transferredBytes == bytesToTransfer) {
		pBar.setString("Done");
		} else {
			pBar.setString("Unable to retrieve source size");
		}
	}
}
