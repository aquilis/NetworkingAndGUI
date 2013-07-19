package com.sirma.itt.javacourse.netgui.broadcaster;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;

/**
 * The client-side application.
 */
public class Client {
	/**
	 * A temp method.
	 */
	public void temp() {
	}

	/**
	 * The entry point for the app.
	 * 
	 * @param args
	 *            are the cmd args
	 * @throws IOException
	 *             asdsad
	 * @throws UnknownHostException
	 *             asdad
	 */
	public static void main(String[] args) throws UnknownHostException,
			IOException {
		byte[] b = new byte[256];
		DatagramPacket dgram = new DatagramPacket(b, b.length);
		MulticastSocket socket = new MulticastSocket(7000);
		socket.joinGroup(InetAddress.getByName("225.4.5.6"));
		while (true) {
			socket.receive(dgram);
			System.err.println("Received " + dgram.getLength() + " bytes from "
					+ dgram.getAddress());
			dgram.setLength(b.length);
		}
	}
}
