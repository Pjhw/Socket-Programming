package Server;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import java.awt.GridBagLayout;
import java.net.*;
import java.util.*;
import java.awt.GridBagConstraints;
import javax.swing.JTextArea;
import java.awt.Font;
import java.awt.Insets;

public class ServerWindow {

	
	//message prefixes
	private final String CP = "/c/";
	private final String EP = "/e/";
	private final String DP = "/d/";
	private final String MP = "/m/";
	private final String UP = "/u/";
	private final String LP = "/l/";
	
	private JFrame frmChatServer;
	private JTextArea serverHistory;

	private Server server;
	
	
	private Thread listen, userListThread;
	
	private List<User> users = new ArrayList <User>();
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ServerWindow window = new ServerWindow();
					window.frmChatServer.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public ServerWindow() {
		initialize();
		
		console("Server is booting up...");
		server = new Server();
		if(!server.isRunning()) console(server.failedToBootUp());
		else{
			console(server.bootedUpSuccessfully());
			bootThreads();
		}
		
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frmChatServer = new JFrame();
		frmChatServer.setTitle("Chat Server");
		frmChatServer.setResizable(false);
		frmChatServer.setBounds(100, 100, 824, 523);
		frmChatServer.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{0, 0, 0, 0};
		gridBagLayout.rowHeights = new int[]{0, 0, 0, 0};
		gridBagLayout.columnWeights = new double[]{0.0, 1.0, 0.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{0.0, 1.0, 0.0, Double.MIN_VALUE};
		frmChatServer.getContentPane().setLayout(gridBagLayout);
		
		JScrollPane scrollPane = new JScrollPane();
		GridBagConstraints gbc_scrollPane = new GridBagConstraints();
		gbc_scrollPane.insets = new Insets(0, 0, 5, 5);
		gbc_scrollPane.fill = GridBagConstraints.BOTH;
		gbc_scrollPane.gridx = 1;
		gbc_scrollPane.gridy = 1;
		frmChatServer.getContentPane().add(scrollPane, gbc_scrollPane);
		
		serverHistory = new JTextArea();
		serverHistory.setFont(new Font("Monospaced", Font.PLAIN, 18));
		serverHistory.setEditable(false);
		scrollPane.setViewportView(serverHistory);
	}
	
	private void bootThreads() {
		listen();
		userUpdate();
	}
	
	/**
	 * Runs the listener thread that will continuously listen for
	 * DatagramPackets
	 */
	private void listen() {
		
		listen = new Thread("Server Listener") {
			public void run() {
				while(server.isRunning()) {
					process(server.receive());				
				}
			}
		};
		
		listen.start();
	}
	
	/**
	 * Runs the listener thread that will continuously listen for
	 * DatagramPackets
	 */
	private void userUpdate() {
		
		userListThread = new Thread("User List Thread") {
			public void run() {
				while(server.isRunning()) {
					try {
						Thread.sleep(2000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					sendUpdatedUserList();				
				}
			}
		};
		
		userListThread.start();
	}

	
	private void process(DatagramPacket packet) {
		String message = new String(packet.getData());
		
		if(message.startsWith(CP)) processConnection(message, packet.getAddress(), packet.getPort());
		else if(message.startsWith(DP)) processDisconnect(message, packet.getAddress(), packet.getPort());
		else if(message.startsWith(MP)) processMessage(message, packet.getAddress(), packet.getPort());
	}
	
	private void processConnection(String message, InetAddress address, int port) {
		String name = getMessage(message, CP);
		
		User user = new User(name, address, port);
		
		if(users.contains(user)) {
			console("User tried to connect with username \"" + name + "\", but "+ 
					"user already exists.");
			server.send(UP + name + EP, address, port);
		}
		
		else {
			users.add(user);
			console(getMessage(message,CP) + " has connected to the server from " + address + " - " + port);
			server.send(CP + EP, address, port);
		}
	}
	
	private void processDisconnect(String message, InetAddress address, int port) {
		User user = new User(getMessage(message,DP), address, port);
		users.remove(user);
		console(getMessage(message, DP) + " has disconnected from the server.");
	}
	
	private void processMessage(String message, InetAddress address, int port) {
		sendToAllUsers(getMessage(message, MP), MP);
	}
	
	private void sendUpdatedUserList() {
		String userList = "";
		
		for(int i = 0; i < users.size(); i++) {
			userList += users.get(i).name + "\n\r"; 
		}
		
		sendToAllUsers(userList, LP);
	}
	
	private void sendToAllUsers(String message, String prefix) {
		InetAddress address;
		int port;
		
		for(int i = 0; i < users.size(); i++) {
			address = users.get(i).getAddress();
			port = users.get(i).getPort();
			
			server.send(prefix + message + EP, address, port);
		}
	}
	
	private void console(String message) {
		serverHistory.append(message + "\n\r");
	}

	
	private String getMessage(String message, String prefix) {
		return message.split(prefix+"|"+EP)[1];
	}
}
