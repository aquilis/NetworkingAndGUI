package com.sirma.itt.javacourse.netgui.clientsInformation;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;

/**
 * The client-side application.
 */
public final class Client extends Thread {
	private static BufferedReader in;
	private static Socket clientSocket = null;
	private static JLabel infoLabel = null;
	private static ClientGUI gui = null;

	/**
	 * Starts the client-side app.
	 */
	private Client() {
		gui = new ClientGUI(this);
	}

	/**
	 * Checks if the given port is available to use and temporarily set the
	 * client socket to it.
	 * 
	 * @param port
	 *            is the port to check
	 * @return true if the port is available to use
	 * @throws IOException
	 *             if problem with the I/O
	 */
	private static boolean isPortAvailable(int port) throws IOException {
		try {
			clientSocket = new Socket(InetAddress.getByName("localhost"), port);
			clientSocket.setReuseAddress(false);
			return true;
		} catch (IOException e) {
		}
		return false;
	}

	/**
	 * Gets the first available port in the given range and set the client
	 * socket to it.
	 * 
	 * @param min
	 *            is the minimal port range
	 * @param max
	 *            is the maximal port range
	 * @return the first available port in the given range, or -1 if non
	 * @throws IOException
	 *             if problem wit hte I/O
	 */
	private static int getAvailablePortRange(int min, int max)
			throws IOException {
		for (int i = min; i <= max; i++) {
			if (isPortAvailable(i)) {
				return i;
			}
		}
		return -1;
	}

	@Override
	public void run() {
		try {
			// bind the client socket to the first available port in the range
			getAvailablePortRange(7000, 7020);
			in = new BufferedReader(new InputStreamReader(
					clientSocket.getInputStream()));
		} catch (IOException e) {
			gui.log("I/O problem occured");
		}
		try {
			while (true) {
				String input = in.readLine();
				if (input == null) {
					break;
				} else {
					gui.log("server: " + input);
				}
			}
		} catch (IOException e) {
			gui.log("Server stopped the connection");
		} catch (NullPointerException npe) {
			gui.log("Server not found");
			return;
		} finally {
			try {
				clientSocket.close();
				in.close();
			} catch (IOException e) {
			} catch (NullPointerException npe) {
				gui.log("Server not found");
			}
		}
	}

	/**
	 * Entry point for tha app.
	 * 
	 * @param args
	 *            are the cmd args
	 */
	public static void main(String[] args) {
		new Client();
	}

	/**
	 * The GUI for the client application.
	 */
	private static class ClientGUI extends JFrame {
		/**
		 * Comment for serialVersionUID.
		 */
		private static final long serialVersionUID = 1L;
		private JButton joinButton;
		private Thread parent = null;

		/**
		 * Constructs the GUI.
		 * 
		 * @param parent
		 *            is the parent thread that the GUI button has to start when
		 *            clicked.
		 */
		public ClientGUI(Thread parent) {
			this.parent = parent;
			setTitle("Client application");
			setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			setLayout(null);
			setSize(new Dimension(600, 200));
			setResizable(false);
			createInfoField();
			createJoinButton();
			this.getContentPane().add(infoLabel);
			this.getContentPane().add(joinButton);
			setVisible(true);
		}

		/**
		 * Logs the given message into the client GUI label. Includes the
		 * current time in the log.
		 * 
		 * @param msg
		 *            is the message to log
		 */
		public void log(String msg) {
			infoLabel.setText(infoLabel.getText() + "<br>|"
					+ new SimpleDateFormat("HH:mm:ss").format(new Date())
					+ "| " + msg);
		}

		/**
		 * Creates the text label that will show the client activity.
		 */
		private void createInfoField() {
			infoLabel = new JLabel("<html>Client activity log:");
			infoLabel.setBounds(0, 0, 400, 200);
			infoLabel.setBorder(BorderFactory.createLoweredBevelBorder());
			infoLabel.setVerticalAlignment(JLabel.TOP);
		}

		/**
		 * Creates the join button that connects to the server.
		 */
		private void createJoinButton() {
			joinButton = new JButton("Join");
			joinButton.setBounds(450, 64, 100, 50);
			joinButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					parent.start();
					joinButton.setEnabled(false);
				}
			});
		}
	}
}
