package com.sirma.itt.javacourse.netgui.clientServerConversation;

import java.awt.Dimension;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;

/**
 * The client side application.
 */
public final class Client {
	private static JLabel infoLabel = null;
	private static Socket clientSocket = null;
	/**
	 * A private constrcutor.
	 */
	private Client() {
	}
	
	/**
	 * Returns the current clock time at format HH:mm:ss.
	 * 
	 * @return the current clock time on the server.
	 */
	private static String currentTime() {
		return new SimpleDateFormat("HH:mm:ss").format(new Date());
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

	/**
	 * Entry point for the client application. Excetutes when the app starts.
	 * 
	 * @param args
	 *            are the cmd args.
	 * @throws IOException
	 *             if a problem with the I/O streams occurs
	 * @throws InterruptedException
	 *             if a client thread is interrupted.
	 */
	public static void main(String[] args) throws IOException,
			InterruptedException {
		new ClientGUI();
		BufferedReader in = null;
		int port = getAvailablePortRange(7000, 7020);
		try {
			infoLabel.setText(infoLabel.getText() + "<br>|" + currentTime()
					+ "| Connected to server at port " + port);
			in = new BufferedReader(new InputStreamReader(
					clientSocket.getInputStream()));
		} catch (UnknownHostException e) {
			System.err.println("Unknown host");
			System.exit(1);
		} catch (IOException e) {
			System.err
					.println("Couldn't get I/O for the connection to the host");
			System.exit(1);
		}
		String serverResponse = in.readLine();
		if (serverResponse != null) {
			infoLabel.setText(infoLabel.getText() + "<br>|" + currentTime()
					+ "|  Message from server recieved");
		}
		System.out.println("Server responded: " + serverResponse);

		// clean up
		in.close();
		clientSocket.close();
		infoLabel.setText(infoLabel.getText() + "<br>|" + currentTime()
				+ "|  Connection with server closed");
	}

	/**
	 * The GUI for the client application.
	 */
	private static class ClientGUI extends JFrame {
		/**
		 * Comment for serialVersionUID.
		 */
		private static final long serialVersionUID = 1L;

		/**
		 * Constructs the GUI.
		 */
		public ClientGUI() {
			setTitle("Client application");
			setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			setLayout(null);
			setSize(new Dimension(512, 200));
			setResizable(false);
			createInfoField();
			this.getContentPane().add(infoLabel);
			setVisible(true);
		}

		/**
		 * Creates the text label that will show the sevrer activity.
		 */
		private void createInfoField() {
			infoLabel = new JLabel("<html>Client activity log:");
			infoLabel.setBounds(0, 0, 300, 200);
			infoLabel.setBorder(BorderFactory.createLoweredBevelBorder());
			infoLabel.setVerticalAlignment(JLabel.TOP);
		}
	}
}
