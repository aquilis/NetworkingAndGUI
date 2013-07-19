package com.sirma.itt.javacourse.netgui.clientsInformation;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;

/**
 * A thread class that notifies all clients from the list that another client
 * has connected.
 */
@SuppressWarnings("rawtypes")
public class ClientsNotifier implements Runnable {

	private List listClients = null;
	private String message = null;
	/**
	 * @param listClients
	 *            is the list of clients to notify
	 * @param msg
	 *            is the message to send to each one of them
	 */
	public ClientsNotifier(List listClients, String msg) {
		this.listClients = listClients;
		this.message = msg;
		new Thread(this).start();
	}

	@Override
	public void run() {
		for (int i = 0; i < listClients.size() - 1; i++) {
			Socket tempClient = (Socket) listClients.get(i);
			try {
				PrintWriter out = new PrintWriter(tempClient.getOutputStream(),
						true);
				out.println(message);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
