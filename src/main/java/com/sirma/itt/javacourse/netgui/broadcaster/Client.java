package com.sirma.itt.javacourse.netgui.broadcaster;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

/**
 * The client side app.
 */
public class Client extends Thread {
	private static JTextArea logBox;
	private static ClientGUI gui = null;
	private MulticastSocket socket = null;
	private InetAddress address;
	private final int port = 8888;
	private final Map<Integer, InetAddress> availableChannels = new HashMap<Integer, InetAddress>();

	/**
	 * Constructs the client class and the GUI. Adds 3 available channels.
	 */
	public Client() {
		try {
			availableChannels.put(1, InetAddress.getByName("224.2.2.3"));
			availableChannels.put(2, InetAddress.getByName("224.1.2.3"));
			availableChannels.put(3, InetAddress.getByName("224.1.3.3"));
			// connect to channel 1 by default
			address = availableChannels.get(1);
			socket = new MulticastSocket(port);
		} catch (IOException e) {
			e.printStackTrace();
		}
		gui = new ClientGUI(this);
	}

	/**
	 * The entry point for the app.
	 */
	@Override
	public void run() {
		DatagramPacket inPacket = null;
		byte[] inBuf = new byte[256];
		try {
			socket.joinGroup(address);
			while (true) {
				inPacket = new DatagramPacket(inBuf, inBuf.length);
				socket.receive(inPacket);
				String msg = new String(inBuf, 0, inPacket.getLength());
				gui.log(msg);
			}
		} catch (IOException ioe) {
			System.out.println(ioe);
		}
	}

	/**
	 * Entry point for the client app.
	 * 
	 * @param args
	 *            are the cmdm args.
	 */
	public static void main(String[] args) {
		new Client();
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
		private JScrollPane scrollPane = null;
		private ButtonGroup buttonGroup = null;
		private JRadioButton buttonChannel1;
		private JRadioButton buttonChannel2;
		private JRadioButton buttonChannel3;

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
			setLayout(new FlowLayout(FlowLayout.LEADING, 6, 6));
			setSize(new Dimension(550, 300));
			setResizable(false);
			createScrollPane();
			createJoinButton();
			createChannelRadioButtons();
			buttonGroup = new ButtonGroup();
			buttonGroup.add(buttonChannel1);
			buttonGroup.add(buttonChannel2);
			buttonGroup.add(buttonChannel3);
			this.getContentPane().add(scrollPane);
			this.getContentPane().add(joinButton);
			this.getContentPane().add(buttonChannel1);
			this.getContentPane().add(buttonChannel2);
			this.getContentPane().add(buttonChannel3);
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
			logBox.append("\n|"
					+ new SimpleDateFormat("HH:mm:ss").format(new Date())
					+ "| " + msg);
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
		 * Creates the radio buttons for channel selection.
		 */
		private void createChannelRadioButtons() {
			buttonChannel1 = new JRadioButton("Channel 1");
			buttonChannel1.setSelected(true);
			buttonChannel1.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					address = availableChannels.get(1);
				}
			});
			buttonChannel2 = new JRadioButton("Channel 2");
			buttonChannel2.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					address = availableChannels.get(2);
				}
			});
			buttonChannel3 = new JRadioButton("Channel 3");
			buttonChannel3.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					address = availableChannels.get(3);
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
					buttonChannel1.setEnabled(false);
					buttonChannel2.setEnabled(false);
					buttonChannel3.setEnabled(false);
				}
			});
		}
	}
}
