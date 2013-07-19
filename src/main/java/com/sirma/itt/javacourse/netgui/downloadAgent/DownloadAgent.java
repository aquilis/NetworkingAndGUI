package com.sirma.itt.javacourse.netgui.downloadAgent;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.JTextField;

/**
 * Downloads files from the network to the user machine.
 */
public final class DownloadAgent {
	/**
	 * Constrcuts the GUI part of the download agent.
	 */
	public DownloadAgent() {
		new DownloadAgentGUI();
	}

	private File sourceFile;
	private String sourceURL;
	private JProgressBar pBar;
	private JButton dButton;

	/**
	 * Downloads the content from the source url to the source file specified.
	 * 
	 * @param sourceURL
	 *            is the URL of the resource that has to be written to the file.
	 * @param sourceFile
	 *            is the source file where the content from the URl will be
	 *            written
	 * @throws IOException
	 *             if there's a problem with the I/O
	 */
	private void downloadFile(String sourceURL, File sourceFile)
			throws IOException {
		URL inputURL = new URL(sourceURL);
		URLConnection connection = inputURL.openConnection();
		new Downloader(connection, sourceFile, pBar, dButton);
	}

	/**
	 * The inner class that draws the GUI part of the download agent has to have
	 * access to its internal methods and fields.
	 */
	private class DownloadAgentGUI extends JFrame {
		/**
		 * Comment for serialVersionUID.
		 */
		private static final long serialVersionUID = 1L;

		// a reference to the current frame
		private final JFrame frame;
		private JTextField urlField;
		private JTextField filenameField;
		private JLabel urlLabel;
		private JLabel filenameLabel;


		/**
		 * Constructs and draws the GUI components of download agent.
		 */
		public DownloadAgentGUI() {
			frame = this;
			setTitle("Download agent");
			setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			setLayout(null);
			setSize(new Dimension(512, 512));
			setResizable(false);
			createDownloadButton();
			createURLField();
			createURLlabel();
			createFilenameField();
			createFilenameLabel();
			createProgressBar();
			this.getContentPane().add(pBar);
			this.getContentPane().add(dButton);
			this.getContentPane().add(urlField);
			this.getContentPane().add(urlLabel);
			this.getContentPane().add(filenameField);
			this.getContentPane().add(filenameLabel);
			setVisible(true);
		}

		/**
		 * Creates and draws the download button ontro the screen. Creates
		 * itsevent listener.
		 */
		public void createDownloadButton() {
			dButton = new JButton("Download");
			dButton.setSize(new Dimension(100, 100));
			dButton.setLocation(200, 250);
			dButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					if ((urlField.getText().length() == 0)
							|| (filenameField.getText().length() == 0)) {
						JOptionPane.showMessageDialog(frame,
								"Empty URL or filename.",
								"Formatting error", JOptionPane.ERROR_MESSAGE);
						return;
					}
					sourceURL = urlField.getText();
					sourceFile = new File(filenameField.getText());
					try {
						downloadFile(sourceURL, sourceFile);
					} catch (IOException e1) {
						System.out.println("IO problem:");
						e1.printStackTrace();
					}
				}
			});
		}

		/**
		 * Creates the progress bar and paint it on the screen.
		 */
		public void createProgressBar() {
			pBar = new JProgressBar(0, 100);
			pBar.setValue(50);
			pBar.setStringPainted(true);
			pBar.setBounds(115, 180, 256, 32);
			pBar.setVisible(false);
		}

		/**
		 * Creates and draws the text field where the user enters the source
		 * URl.
		 */
		public void createURLField() {
			urlField = new JTextField(20);
			urlField.setSize(256, 24);
			urlField.setLocation(116, 64);
		}

		/**
		 * Creates and draws the url label for the user.
		 */
		public void createURLlabel() {
			urlLabel = new JLabel("Input your URL:");
			urlLabel.setSize(new Dimension(100, 24));
			urlLabel.setLocation(8, 64);
		}

		/**
		 * Creates and draws the text field where the user enters the path for
		 * the source file.
		 */
		public void createFilenameField() {
			filenameField = new JTextField(20);
			filenameField.setSize(256, 24);
			filenameField.setLocation(116, 100);
		}

		/**
		 * Creates and draws the filename label for the user.
		 */
		public void createFilenameLabel() {
			filenameLabel = new JLabel("Input a filename:");
			filenameLabel.setSize(new Dimension(100, 24));
			filenameLabel.setLocation(8, 100);
		}
	}
}
