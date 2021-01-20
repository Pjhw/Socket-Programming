package Client;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

public class Client {
	
	private final String SERVERADDRESS = "192.168.1.31";
	private final int SERVERPORT = 5000;
	
	private DatagramSocket socket;
	
	private String address;
	private int port;
	
	private boolean clientIsRunning;
	
	private Thread send;
	
	public Client() {
		openSocket();
	}
	
	
	/**
	 * Opens a DatagramSocket to be used to receive packets from other clients
	 */
	private void openSocket() {
		int attempts = 0;
		clientIsRunning = false;
		InetAddress ipAddress = null;
		
		
		//Gets the local ip address for printing purposes.
		try {ipAddress = InetAddress.getLocalHost();
		} catch (UnknownHostException e1) {e1.printStackTrace();}
		
		
		//Attempts to open a socket to any available port
		while(socket == null && attempts++ <= 10) {
			try {socket = new DatagramSocket();}
			catch (SocketException e) {}
		}
		
		//After 10 attempts, stop trying.
		if(attempts >= 10) {return;}
		
		//Show the address and port the server is connected to.
		port = socket.getLocalPort();
		address = ipAddress.getHostAddress();
		
		clientIsRunning = true;
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
	
	public void send(String message) {
		send = new Thread("Sending Thread") {
			public void run() {
				byte[] data = message.getBytes();

				try {
					socket.send(new DatagramPacket(data, data.length, InetAddress.getByName(SERVERADDRESS), SERVERPORT));
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		};
		
		send.start();
	}
	
	
	public boolean isRunning() {
		return clientIsRunning;
	}
	
	public String bootedUpSuccessfully() {
		return "Client booting up from " + address + " on port: " + port;
	}
	
	public String failedToBootUp() {
		return "Could not find an empty port... Client could not boot up";
	}
}
