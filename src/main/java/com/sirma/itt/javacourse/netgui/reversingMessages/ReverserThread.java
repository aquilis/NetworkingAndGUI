package com.sirma.itt.javacourse.netgui.reversingMessages;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Deals with each client separately. Reverses the messages recieved from the client and re-sends
 * them. Updates the server GUI.
 */
public class ReverserThread implements Runnable {

	private Socket clientSocket = null;
	private final int clientNumber;
	private final Server.ServerGUI gui;
	private PrintWriter out = null;
	private BufferedReader in = null;

	/**
	 * Constructs the reverser thread with a client socket.
	 * 
	 * @param clientSocket
	 *            is the socket of the connected client to serve.
	 * @param clientNumber
	 *            is the number of the current client
	 * @param gui
	 *            is the server GUI to log all actions in
	 */
	public ReverserThread(Socket clientSocket, int clientNumber, Server.ServerGUI gui) {
		this.clientSocket = clientSocket;
		this.clientNumber = clientNumber;
		this.gui = gui;
		new Thread(this).start();
	}

	/**
	 * Reads the message from the client.
	 * 
	 * @return the meassage read from the client
	 * @throws IOException
	 *             if a problem with the input stream occurs
	 */
	private String readClientMessage() throws IOException {
		return in.readLine();
	}

	/**
	 * Sends the given message to the client.
	 * 
	 * @param msg
	 *            is the message to send
	 */
	private void writeClientMessage(String msg) {
		out.println(msg);
		gui.log("Reversed message sent to client #" + clientNumber);
	}

	/**
	 * Reverses the string backwards.
	 * 
	 * @param input
	 *            is the string to be reversed
	 * @return the reversed input string
	 */
	private String reverseString(String input) {
		String reversed = "";
		for (int i = input.length() - 1; i >= 0; i--) {
			reversed += input.charAt(i);
		}
		return reversed;
	}

	@Override
	public void run() {
		try {
			out = new PrintWriter(clientSocket.getOutputStream(), true);
			in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			while (true) {
				String input = readClientMessage();
				if (input == null) {
					gui.log("Client #" + clientNumber + " disconnected");
					break;
				} else {
					writeClientMessage("The reverse of [" + input + "] is [" + reverseString(input)
							+ "]");
				}
			}
		} catch (IOException e) {
		}
	}
}
