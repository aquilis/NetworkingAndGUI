package com.sirma.itt.javacourse.netgui.clientServerConversation;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;

/**
 * The server side application.
 */
public final class Server {
	private static JLabel infoLabel = null;
	private static ServerSocket serverSocket = null;
	private static Socket clientSocket = null;
	private static PrintWriter out = null;

	/**
	 * A private constrcutor.
	 */
	private Server() {
	}

	/**
	 * Checks if the given port is available to use.
	 * 
	 * @param port
	 *            is the port to check
	 * @return true if the port is available to use
	 * @throws IOException
	 *             if problem with the I/O
	 */
	private static boolean isPortAvailable(int port) throws IOException {
		ServerSocket socket = null;
		try{
			socket = new ServerSocket(port);
			socket.setReuseAddress(true);
			return true;
		} catch (IOException e) {
		} finally {
			if (socket != null) {
				socket.close();
			}
		}
		return false;
	}

	/**
	 * Gets the first available port in the given range.
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
	 * Returns the current clock time at format HH:mm:ss.
	 * 
	 * @return the current clock time on the server.
	 */
	private static String currentTime() {
		return new SimpleDateFormat("HH:mm:ss").format(new Date());
	}

	/**
	 * The entry point for the server application. Executed when the app starts.
	 * 
	 * @param args
	 *            are the cmd args
	 * @throws IOException
	 *             if a problem with the IO occurs
	 */
	public static void main(String[] args) throws IOException {
		new ServerGUI();
		int port = getAvailablePortRange(7000, 7020);
		try {
			serverSocket = new ServerSocket(port, 0,
					InetAddress.getByName("localhost"));
			infoLabel.setText(infoLabel.getText() + "<br>|" + currentTime()
					+ "|  Server is listeninig at port " + port);
		} catch (IOException e) {
			System.err
					.println("The specified port is already used by another process");
			System.exit(1);
		}

		try {
			clientSocket = serverSocket.accept();
			infoLabel.setText(infoLabel.getText() + "<br>|" + currentTime()
					+ "| Client accepted succesfully");
		} catch (IOException e) {
			System.exit(1);
		}

		out = new PrintWriter(clientSocket.getOutputStream(), true);
		Date currentDate = new Date();
		out.println("Hello! The current date is: " + currentDate);
		infoLabel.setText(infoLabel.getText() + "<br>|" + currentTime()
				+ "| Message sent to client");
		// clean up
		out.close();
		clientSocket.close();
		serverSocket.close();
	}

	/**
	 * The GUI part of the server application.
	 */
	private static class ServerGUI extends JFrame {
		/**
		 * Comment for serialVersionUID.
		 */
		private static final long serialVersionUID = 1L;
		private JButton stopButton;

		/**
		 * Constrcucts the interface.
		 */
		public ServerGUI() {
			setTitle("Server application");
			setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			setLayout(null);
			setSize(new Dimension(512, 200));
			setResizable(false);
			createInfoField();
			createStopButton();
			this.getContentPane().add(infoLabel);
			this.getContentPane().add(stopButton);
			setVisible(true);
		}

		/**
		 * Creates the text label that will show the sevrer activity.
		 */
		private void createInfoField() {
			infoLabel = new JLabel("<html>Server activity log:");
			infoLabel.setBounds(0, 0, 300, 200);
			infoLabel.setBorder(BorderFactory.createLoweredBevelBorder());
			infoLabel.setVerticalAlignment(JLabel.TOP);
		}

		/**
		 * Creates the stop button that will stop the server.
		 */
		private void createStopButton() {
			stopButton = new JButton("Stop server");
			stopButton.setBounds(350, 64, 100, 50);
			stopButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					try {
						if (clientSocket != null) {
							clientSocket.close();
						}
						if (serverSocket != null) {
							serverSocket.close();
						}
						if (out != null) {
							out.close();
						}
						System.exit(ABORT);
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
			});
		}
	}
}
