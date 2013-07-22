package com.sirma.itt.javacourse.netgui.broadcaster;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JTextField;

/**
 * The server-side application.
 */
public class Server {
	private Sender sender = null;
	private final List<Integer> currentChannels = new ArrayList<Integer>();
	private final int port = 8888;

	/**
	 * Constructs the GUI and starts the server.
	 */
	public Server() {
		// channel 1 is added by default
		currentChannels.add(1);
		// create the sender that manages the availableChannels
		sender = new Sender();
		runServer();
	}

	/**
	 * The entry point for the server app.
	 */
	public void runServer() {
		new ServerGUI();
	}

	/**
	 * The entry point.
	 * 
	 * @param args
	 *            are the cmd args
	 */
	public static void main(String[] args) {
		new Server();
	}

	/**
	 * The class that decides which availableChannels to send the message to.
	 * Contains a hashmap with all available addresses where the message could
	 * be sent.
	 */
	private class Sender {
		private DatagramSocket socket = null;
		private DatagramPacket outPacket;
		private final Map<Integer, InetAddress> availableChannels = new HashMap<Integer, InetAddress>();

		/**
		 * Constructs the sender class with a hashtable containing key-value
		 * pairs representing each channel and its corresponding address.
		 */
		public Sender() {
			try {
				socket = new DatagramSocket();
				availableChannels.put(1, InetAddress.getByName("224.2.2.3"));
				availableChannels.put(2, InetAddress.getByName("224.1.2.3"));
				availableChannels.put(3, InetAddress.getByName("224.1.3.3"));
			} catch (SocketException | UnknownHostException e) {
				e.printStackTrace();
			}
		}

		/**
		 * Sends the message to all currently chosen availableChannels.
		 * 
		 * @param message
		 *            is the message to send.
		 * @throws IOException
		 *             if problem with the I/O
		 */
		public void sendMessage(String message) throws IOException {
			for (int chan : currentChannels) {
				byte[] buff = message.getBytes();
				outPacket = new DatagramPacket(buff, buff.length,
						availableChannels.get(chan), port);
				socket.send(outPacket);
			}
		}
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
		private JTextField textBox = null;
		private JCheckBox checkChannel1;
		private JCheckBox checkChannel2;
		private JCheckBox checkChannel3;

		/**
		 * Constructs the user interface.
		 */
		public ServerGUI() {
			setTitle("Broadcaster application");
			setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			setLayout(new FlowLayout(FlowLayout.LEADING, 6, 6));
			setSize(new Dimension(470, 150));
			setResizable(false);
			createTextBox();
			createSendButton();
			createCheckBoxes();
			this.getContentPane().add(textBox);
			this.getContentPane().add(stopButton);
			this.getContentPane().add(checkChannel1);
			this.getContentPane().add(checkChannel2);
			this.getContentPane().add(checkChannel3);
			setVisible(true);
		}

		/**
		 * Creates the text box where the messages are entered by the user.
		 */
		private void createTextBox() {
			textBox = new JTextField(30);
			textBox.setPreferredSize(new Dimension(300, 24));
		}

		/**
		 * Creates the send button that sends the message to all chosen
		 * channels.
		 */
		private void createSendButton() {
			stopButton = new JButton("send");
			stopButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					try {
						sender.sendMessage(textBox.getText());
						textBox.setText("");
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
			});
		}

		/**
		 * Creates the checkboxes where the user can choose which channels
		 * should receive the message.
		 */
		private void createCheckBoxes() {
			checkChannel1 = new JCheckBox("channel 1");
			checkChannel1.setSelected(true);
			checkChannel1.addItemListener(new ItemListener() {
				@Override
				public void itemStateChanged(ItemEvent e) {
					if (e.getStateChange() == ItemEvent.DESELECTED) {
						currentChannels.remove(new Integer(1));
					} else {
						currentChannels.add(1);
					}
				}
			});
			checkChannel2 = new JCheckBox("channel 2");
			checkChannel2.addItemListener(new ItemListener() {
				@Override
				public void itemStateChanged(ItemEvent e) {
					if (e.getStateChange() == ItemEvent.DESELECTED) {
						currentChannels.remove(new Integer(2));
					} else {
						currentChannels.add(2);
					}
				}
			});
			checkChannel3 = new JCheckBox("channel 3");
			checkChannel3.addItemListener(new ItemListener() {
				@Override
				public void itemStateChanged(ItemEvent e) {
					if (e.getStateChange() == ItemEvent.DESELECTED) {
						currentChannels.remove(new Integer(3));
					} else {
						currentChannels.add(3);
					}
				}
			});
		}
	}
}
