package com.sirma.itt.javacourse.netgui.broadcaster;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

/**
 * The echo server.
 */
public class Server extends Thread {

	private final DatagramSocket socket;

	/**
	 * Construct the server.
	 * 
	 * @throws SocketException
	 *             sadasdasd.
	 */
	public Server() throws SocketException {
		// construct the socket with only a port to wait on
		socket = new DatagramSocket(7000);
	}

	/**
	 * Entry point for teh server.
	 * 
	 * @param args
	 *            are the cmd args
	 * @throws SocketException
	 *             sdasdas
	 */
	public static void main(String[] args) throws SocketException {
		new Server().start();
	}

	@Override
	public void run() {
		try {
			DatagramSocket socket = new DatagramSocket();
			byte[] b = new byte[256];
			DatagramPacket dgram;
			dgram = new DatagramPacket(b, b.length,
					InetAddress.getByName("225.4.5.6"), 7000);
			System.err.println("Sending " + b.length + " bytes to "
					+ dgram.getAddress() + ':' + dgram.getPort());
			while (true) {
				System.err.print(".");
				socket.send(dgram);
				Thread.sleep(1000);
			}
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}
	}

}
