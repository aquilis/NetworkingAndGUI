package com.sirma.itt.javacourse.netgui.downloadAgent;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.swing.JProgressBar;

/**
 * Transfers the content of an input stream into an output stream.
 */
public class TransferObject {
	private final InputStream input;
	private final OutputStream output;

	/**
	 * Constructs the transferrer instance with 2 I/O streams to work with.
	 * 
	 * @param input
	 *            is the input stream to read from.
	 * @param output
	 *            is the output stream to write to
	 */
	public TransferObject(InputStream input, OutputStream output) {
		this.input = input;
		this.output = output;
	}

	/**
	 * Transfer the content of the input stream to the output.
	 * 
	 * @param numberOfBytes
	 *            is the number of bytes to be transferred.
	 * @param offset
	 *            is the front offset of the bytes to be transferred
	 * @param pBar
	 *            is the progress bar that will indicate what part of the data
	 *            has been transferred at each moment
	 * @return the number of bytes taht have been read from the Input stream
	 * @throws IOException
	 *             if a problem with the I/O occurs
	 */
	public int transfer(int numberOfBytes, int offset, JProgressBar pBar)
			throws IOException {
		// Skip the given offset from the input stream
		input.skip(offset);
		byte[] buffer = new byte[256];
		int bytesRead = 0;
		int temp = 0;
		while ((bytesRead = input.read(buffer)) != -1) {
			output.write(buffer, 0, bytesRead);
			temp += bytesRead;
			// set the progress bar's value in percents
			if (pBar != null) {
				float tempVal = (float) temp / (float) numberOfBytes;
				pBar.setValue((int) (tempVal * 100));
			}
		}
		return temp;
	}
}
