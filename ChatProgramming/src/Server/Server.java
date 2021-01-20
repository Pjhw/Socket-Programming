package Server;

import java.io.IOException;
import java.net.*;



public class Server {
	
	private final int SERVERPORT = 5000;
	
	private String address;
	private int port;

	private boolean serverIsRunning;

	private DatagramSocket socket;

	private Thread send;
	
	public Server() {
		openSocket();
	}
	
	/**
	 * Opens a DatagramSocket to be used to receive packets from other clients
	 */
	private void openSocket() {
		int attempts = 0;
		serverIsRunning = false;
		InetAddress ipAddress = null;
		
		
		//Gets the local ip address for printing purposes.
		try {ipAddress = InetAddress.getLocalHost();
		} catch (UnknownHostException e1) {e1.printStackTrace();}
		
		
		//Attempts to open a socket to any available port
		while(socket == null && attempts++ <= 10) {
			try {socket = new DatagramSocket(SERVERPORT);}
			catch (SocketException e) {}
		}
		
		//After 10 attempts, stop trying.
		if(attempts >= 10) {return;}
		
		//Show the address and port the server is connected to.
		port = socket.getLocalPort();
		address = ipAddress.getHostAddress();
		
		serverIsRunning = true;
		//listen();
	}
	
	
	public DatagramPacket receive() {
		byte[] dataBuffer = new byte[1024];
		DatagramPacket packet = new DatagramPacket(dataBuffer, dataBuffer.length);
		
		try {
			socket.receive(packet);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return packet;
	}
	
	public void send(String message, final InetAddress address, final int port) {
		send = new Thread("Sending Thread") {
			public void run() {
				byte[] data = message.getBytes();

				try {
					socket.send(new DatagramPacket(data, data.length, address, port));
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		};
		
		send.start();
	}
	
	
	public boolean isRunning() {
		return serverIsRunning;
	}
	
	public String bootedUpSuccessfully() {
		return "Connected to " + address + " on port: " + port;
	}
	
	public String failedToBootUp() {
		return "Could not find an empty port... Disconnected";
	}
}
