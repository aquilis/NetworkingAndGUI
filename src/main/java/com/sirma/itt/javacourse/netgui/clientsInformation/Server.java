package com.sirma.itt.javacourse.netgui.clientsInformation;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;

/**
 * The server-side application maintains a list with all connected clients and
 * notifies each one when a new client connects. Supports time-coded activity
 * log for each significatnt change that occurs with the server.
 */
@SuppressWarnings("rawtypes")
public final class Server {

	private static List listClients = null;
	private ServerSocket serverSocket = null;
	private Socket lastClient = null;
	private PrintWriter out = null;
	private int numberOfClients;
	private String message;
	private static JLabel infoLabel = null;
	private ServerGUI gui;

	/**
	 * Constrcuts the server and runs it.
	 * 
	 * @throws IOException
	 *             if a problem with the IO occurs
	 */
	private Server() throws IOException {
		runServer();
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
		try {
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
	 * Runs the main sevrer logic.
	 * 
	 * @throws IOException
	 *             if problem with the I/O
	 */
	@SuppressWarnings("unchecked")
	private void runServer() throws IOException {
		gui = new ServerGUI();
		numberOfClients = 1;
		listClients = new ArrayList<Socket>();
		boolean listenning = true;
		int port = getAvailablePortRange(7000, 7020);
		try {
			serverSocket = new ServerSocket(port, 1,
					InetAddress.getByName("localhost"));
			gui.log("Server started connection");
		} catch (IOException e) {
			gui.log("could not listen to port " + port);
		}
		while (listenning) {
			try{
				lastClient = serverSocket.accept();
				out = new PrintWriter(lastClient.getOutputStream(), true);
				out.println("You've succesfully joined as client #"
						+ numberOfClients);
				gui.log("Client " + numberOfClients + " joined");
			} catch (SocketException e) {
				gui.log("bad socket");
				break;
			} catch (IOException ioe) {
				gui.log("I/O problem");
				break;
			}
			if (numberOfClients > 1) {
				message = "Client #" + numberOfClients + " connected";
				new ClientsNotifier(listClients, message);
			}
			listClients.add(lastClient);
			numberOfClients++;
		}
	}

	/**
	 * The entry point for the server application.
	 * 
	 * @param args
	 *            are the cmd args.
	 * @throws IOException
	 *             if problem with the I/O
	 */
	public static void main(String[] args) throws IOException {
		new Server();
	}

	/**
	 * The GUI part of the server application.
	 */
	private class ServerGUI extends JFrame {
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
		 * Logs the given message in the server GUI panel. Includes the current
		 * time in the log.
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
						if (lastClient != null) {
							lastClient.close();
						}
						if (serverSocket != null) {
							serverSocket.close();
						}
						if (out != null) {
							out.close();
						}
						System.exit(ABORT);
					} catch (IOException e1) {
					}
				}
			});
		}
	}
}
