package com.sirma.itt.javacourse.netgui.reversingMessages;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.KeyStroke;

/**
 * The client-side application.
 */
public final class Client extends Thread {
	private static BufferedReader in;
	private static PrintWriter out = null;
	private static Socket clientSocket = null;
	private static JTextArea logBox;
	private static JButton sendButton;
	private static ClientGUI gui = null;
	private Input currentInput = new Input();
	private ArrayList<InputMemento> inputHistory = new ArrayList<InputMemento>();

	/**
	 * Starts the client-side app.
	 */
	private Client() {
		gui = new ClientGUI(this);
	}

	/**
	 * Checks if the given port is available to use and temporarily set the client socket to it.
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
	 * Gets the first available port in the given range and set the client socket to it.
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

	@Override
	public void run() {
		try {
			// bind the client socket to the first available port in the range
			getAvailablePortRange(7000, 7020);
			in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			out = new PrintWriter(clientSocket.getOutputStream(), true);
			sendButton.setEnabled(true);
		} catch (IOException e) {
			gui.log("I/O problem occured");
		}
		try {
			// wait for a response from the server
			String input;
			while ((input = in.readLine()) != null) {
				gui.log("server: " + input);
			}
		} catch (IOException e) {
		} catch (NullPointerException npe) {
			gui.log("Server not found");
			return;
		} finally {
			try {
				gui.log("Connection to server lost");
				sendButton.setEnabled(false);
				clientSocket.close();
				in.close();
				out.close();
			} catch (IOException e) {
			} catch (NullPointerException npe) {
				gui.log("Server not found");
			}
		}
	}

	/**
	 * Entry point for the app.
	 * 
	 * @param args
	 *            are the cmd args
	 */
	public static void main(String[] args) {
		new Client();
	}

	/**
	 * Memento class that saves the states of the input. When history of the inputs is needed, the
	 * original input class is reconstructed.
	 */
	private class InputMemento {
		private String message;

		/**
		 * constructs the class with a content string.
		 * 
		 * @param content
		 *            is the content string to save
		 */
		public InputMemento(String content) {
			this.message = content;
		}

		/**
		 * Gets the saved state of the input class.
		 * 
		 * @return the saved message
		 */
		public String getSavedMessage() {
			return this.message;
		}
	}

	/**
	 * The input class that wraps the user input sent to the server and saves itself to make history
	 * of the input strings.
	 */
	private class Input {
		private String message;

		/**
		 * sets the content string of the input class.
		 * 
		 * @param message
		 *            is the content string to set.
		 */
		public void setMessage(String message) {
			this.message = message;
		}
		/**
		 * Gets the message.
		 * 
		 * @return the content message
		 */
		public String getMessage() {
			return this.message;
		}
		/**
		 * saves the input class.
		 * 
		 * @return a memento class of the input
		 */
		public InputMemento save() {
			return new InputMemento(message);
		}
	}

	/**
	 * The GUI for the client application.
	 */
	private class ClientGUI extends JFrame {
		/**
		 * Comment for serialVersionUID.
		 */
		private static final long serialVersionUID = 1L;
		private JButton joinButton;
		private Thread parent = null;
		private JTextField textBox = null;
		private JScrollPane scrollPane = null;
		private ActionListener arrowUpListener = null;
		private ActionListener arrowDownListener = null;
		private int scrollIndex = 0;

		/**
		 * Constructs the GUI.
		 * 
		 * @param parent
		 *            is the parent thread that the GUI button has to start when clicked.
		 */
		public ClientGUI(Thread parent) {
			this.parent = parent;
			arrowUpListener = new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					if (inputHistory.get(scrollIndex) != null) {
						currentInput.setMessage(inputHistory.get(scrollIndex).getSavedMessage());
						textBox.setText(currentInput.getMessage());
						if (scrollIndex > 0) {
							scrollIndex--;
						}
					}
				}
			};
			KeyStroke strokeUp = KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0);
			arrowDownListener = new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					if ((scrollIndex < inputHistory.size() - 1)
							&& (inputHistory.get(scrollIndex + 1) != null)) {
						scrollIndex++;
						currentInput.setMessage(inputHistory.get(scrollIndex).getSavedMessage());
						textBox.setText(currentInput.getMessage());
					}
				}
			};
			KeyStroke strokeDown = KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0);

			setTitle("Client application");
			setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			setLayout(new FlowLayout(FlowLayout.LEADING, 6, 6));
			setSize(new Dimension(550, 300));
			setResizable(false);
			createScrollPane();
			createJoinButton();
			createSendButton();
			createTextField();
			this.getRootPane().registerKeyboardAction(arrowUpListener, strokeUp,
					JComponent.WHEN_IN_FOCUSED_WINDOW);
			this.getRootPane().registerKeyboardAction(arrowDownListener, strokeDown,
					JComponent.WHEN_IN_FOCUSED_WINDOW);
			this.getContentPane().add(scrollPane);
			this.getContentPane().add(joinButton);
			this.getContentPane().add(textBox);
			this.getContentPane().add(sendButton);
			setVisible(true);
		}

		/**
		 * Logs the given message into the client GUI label. Includes the current time in the log.
		 * 
		 * @param msg
		 *            is the message to log
		 */
		public void log(String msg) {
			logBox.append("\n|" + new SimpleDateFormat("HH:mm:ss").format(new Date()) + "| " + msg);
		}

		/**
		 * Creates the text label that will show the client activity.
		 */
		private void createScrollPane() {
			logBox = new JTextArea(5, 30);
			logBox.setBorder(BorderFactory.createLoweredBevelBorder());
			logBox.setEditable(false);
			logBox.append("Client activity log:");
			scrollPane = new JScrollPane(logBox);
			scrollPane.setPreferredSize(new Dimension(400, 200));
		}

		/**
		 * Creates the send button that sends the string from the text field to the server.
		 */
		private void createSendButton() {
			sendButton = new JButton("Send");
			sendButton.setEnabled(false);
			sendButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					if (textBox.getText().length() == 0) {
						return;
					}
					if (".".equals(textBox.getText())) {
						gui.log("Disconnected from server.");
						sendButton.setEnabled(false);
						joinButton.setEnabled(false);
						try {
							clientSocket.close();
							in.close();
							out.close();
						} catch (IOException e1) {
							gui.log("Failed to close connection");
						}
					}
					currentInput.setMessage(textBox.getText());
					inputHistory.add(currentInput.save());
					scrollIndex = inputHistory.size() - 1;
					out.println(currentInput.getMessage());
					textBox.setText("");
				}
			});
		}

		/**
		 * Creates the join button that connects to the server.
		 */
		private void createJoinButton() {
			joinButton = new JButton("Join");
			joinButton.setPreferredSize(new Dimension(100, 50));
			joinButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					parent.start();
					joinButton.setEnabled(false);
				}
			});
		}

		/**
		 * Creates the text field where the user inputs the text to be sent to the server.
		 */
		private void createTextField() {
			textBox = new JTextField();
			textBox.setPreferredSize(new Dimension(400, 24));
		}
	}
}
