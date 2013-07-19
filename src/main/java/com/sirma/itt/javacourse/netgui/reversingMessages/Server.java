package com.sirma.itt.javacourse.netgui.reversingMessages;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

/**
 * The server-side application.
 */
public final class Server {

	private ServerSocket serverSocket = null;
	private Socket lastClient = null;
	private PrintWriter out = null;
	private static JTextArea logBox = null;
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
	private static int getAvailablePortRange(int min, int max) throws IOException {
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
	private void runServer() throws IOException {
		gui = new ServerGUI();
		boolean listenning = true;
		int clientNumber = 1;
		int port = getAvailablePortRange(7000, 7020);
		try {
			serverSocket = new ServerSocket(port, 1, InetAddress.getByName("localhost"));
			gui.log("Server started connection");
		} catch (IOException e) {
			gui.log("could not listen to port " + port);
		}
		while (listenning) {
			try {
				lastClient = serverSocket.accept();
				out = new PrintWriter(lastClient.getOutputStream(), true);
				gui.log("Client #" + clientNumber + " connected");
				out.println("Successfully connencted as client #" + clientNumber
						+ ". Welcome to Message reverser.");
				new ReverserThread(lastClient, clientNumber, gui);
				clientNumber++;
			} catch (SocketException e) {
				gui.log("bad socket");
				break;
			} catch (IOException ioe) {
				gui.log("I/O problem");
				break;
			}
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
	class ServerGUI extends JFrame {
		/**
		 * Comment for serialVersionUID.
		 */
		private static final long serialVersionUID = 1L;
		private JButton stopButton;
		private JScrollPane scrollPane = null;

		/**
		 * Constructs the interface.
		 */
		public ServerGUI() {
			setTitle("Server application");
			setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			setLayout(new FlowLayout(FlowLayout.LEADING, 6, 6));
			setSize(new Dimension(550, 250));
			setResizable(false);
			createScrollPane();
			createStopButton();
			this.getContentPane().add(scrollPane);
			this.getContentPane().add(stopButton);
			setVisible(true);
		}

		/**
		 * Logs the given message in the server GUI panel. Includes the current time in the log.
		 * 
		 * @param msg
		 *            is the message to log
		 */
		public void log(String msg) {
			logBox.append("\n|" + new SimpleDateFormat("HH:mm:ss").format(new Date()) + "| " + msg);
		}

		/**
		 * Creates the text label that will show the server activity.
		 */
		private void createScrollPane() {
			logBox = new JTextArea(5, 30);
			logBox.setBorder(BorderFactory.createLoweredBevelBorder());
			logBox.setEditable(false);
			logBox.append("Server activity log:");
			scrollPane = new JScrollPane(logBox);
			scrollPane.setPreferredSize(new Dimension(400, 200));
		}

		/**
		 * Creates the stop button that will stop the server.
		 */
		private void createStopButton() {
			stopButton = new JButton("Stop server");
			stopButton.setPreferredSize(new Dimension(100, 50));
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
