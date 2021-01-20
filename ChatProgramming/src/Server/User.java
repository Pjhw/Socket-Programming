package Server;

import java.net.*;

public class User {
	
	private InetAddress address;
	private int port;
	public String name;

	public User(String name, InetAddress address, int port) {
		this.name = name;
		this.address = address;
		this.port = port;
	}

	public InetAddress getAddress() {
		return address;
	}

	public int getPort() {
		return port;
	}
	
	public boolean equals(Object o) {
		if(!(o instanceof User)) return false;
		
		User user = (User) o;
		
		if(!user.name.equals(this.name)) return false;
		
		return true;
	}
	
}
