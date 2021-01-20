package Client;

import java.awt.EventQueue;

import javax.swing.JFrame;
import java.awt.GridBagLayout;
import javax.swing.JTextField;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.Font;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import java.awt.event.ActionListener;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class ClientWindow {

	private final String CP = "/c/";
	private final String EP = "/e/";
	private final String DP = "/d/";
	private final String MP = "/m/";
	private final String UP = "/u/";
	private final String LP = "/l/";
	
	private JFrame frame;
	private JTextField nameField;
	private JTextField messageField;
	private JTextArea clientHistory;
	private JTextArea userArea;
	private JButton sendButton;
	private JButton connectButton;
	
	
	private Client client;

	
	private Thread listen;
	private JScrollPane scrollPane_1;
	private JLabel UsersLabel;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ClientWindow window = new ClientWindow();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public ClientWindow() {
		initialize();
		
		client = new Client();
		
		if(!client.isRunning()) console(client.failedToBootUp());
		else{
			console(client.bootedUpSuccessfully());
			listen();
		}
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				disconnectClient();
			}
		});
		frame.setBounds(100, 100, 912, 545);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{36, 0, 359, 192, 162, 0};
		gridBagLayout.rowHeights = new int[]{-4, 45, 0, 0, 0, 0, 0};
		gridBagLayout.columnWeights = new double[]{0.0, 1.0, 1.0, 0.0, 1.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{0.0, 0.0, 0.0, 1.0, 0.0, 0.0, Double.MIN_VALUE};
		frame.getContentPane().setLayout(gridBagLayout);
		
		JLabel nameLabel = new JLabel("Name:");
		nameLabel.setFont(new Font("Tahoma", Font.PLAIN, 14));
		GridBagConstraints gbc_nameLabel = new GridBagConstraints();
		gbc_nameLabel.insets = new Insets(0, 0, 5, 5);
		gbc_nameLabel.anchor = GridBagConstraints.EAST;
		gbc_nameLabel.gridx = 1;
		gbc_nameLabel.gridy = 1;
		frame.getContentPane().add(nameLabel, gbc_nameLabel);
		
		nameField = new JTextField();
		nameField.setFont(new Font("Tahoma", Font.PLAIN, 14));
		GridBagConstraints gbc_nameField = new GridBagConstraints();
		gbc_nameField.insets = new Insets(0, 0, 5, 5);
		gbc_nameField.fill = GridBagConstraints.HORIZONTAL;
		gbc_nameField.gridx = 2;
		gbc_nameField.gridy = 1;
		frame.getContentPane().add(nameField, gbc_nameField);
		nameField.setColumns(10);
		
		connectButton = new JButton("Connect");
		connectButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(connectButton.getText().equals("Connect")) connectClient();
				else disconnectClient();
			}
		});
		GridBagConstraints gbc_connectButton = new GridBagConstraints();
		gbc_connectButton.insets = new Insets(0, 0, 5, 5);
		gbc_connectButton.gridx = 3;
		gbc_connectButton.gridy = 1;
		frame.getContentPane().add(connectButton, gbc_connectButton);
		
		UsersLabel = new JLabel("Users Online:");
		UsersLabel.setFont(new Font("Tahoma", Font.PLAIN, 14));
		GridBagConstraints gbc_UsersLabel = new GridBagConstraints();
		gbc_UsersLabel.insets = new Insets(0, 0, 5, 0);
		gbc_UsersLabel.gridx = 4;
		gbc_UsersLabel.gridy = 1;
		frame.getContentPane().add(UsersLabel, gbc_UsersLabel);
		
		JScrollPane scrollPane = new JScrollPane();
		GridBagConstraints gbc_scrollPane = new GridBagConstraints();
		gbc_scrollPane.gridwidth = 3;
		gbc_scrollPane.insets = new Insets(0, 0, 5, 5);
		gbc_scrollPane.fill = GridBagConstraints.BOTH;
		gbc_scrollPane.gridx = 1;
		gbc_scrollPane.gridy = 3;
		frame.getContentPane().add(scrollPane, gbc_scrollPane);
		
		clientHistory = new JTextArea();
		clientHistory.setEditable(false);
		scrollPane.setViewportView(clientHistory);
		
		messageField = new JTextField();
		messageField.addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent e) {
				if(e.getKeyCode() == KeyEvent.VK_ENTER) {
					sendMessage();
				}
			}
		});
		
		scrollPane_1 = new JScrollPane();
		GridBagConstraints gbc_scrollPane_1 = new GridBagConstraints();
		gbc_scrollPane_1.gridheight = 2;
		gbc_scrollPane_1.insets = new Insets(0, 0, 5, 0);
		gbc_scrollPane_1.fill = GridBagConstraints.BOTH;
		gbc_scrollPane_1.gridx = 4;
		gbc_scrollPane_1.gridy = 2;
		frame.getContentPane().add(scrollPane_1, gbc_scrollPane_1);
		
		userArea = new JTextArea();
		userArea.setEditable(false);
		scrollPane_1.setViewportView(userArea);
		GridBagConstraints gbc_messageField = new GridBagConstraints();
		gbc_messageField.gridwidth = 2;
		gbc_messageField.insets = new Insets(0, 0, 5, 5);
		gbc_messageField.fill = GridBagConstraints.HORIZONTAL;
		gbc_messageField.gridx = 1;
		gbc_messageField.gridy = 4;
		frame.getContentPane().add(messageField, gbc_messageField);
		messageField.setColumns(10);
		
		sendButton = new JButton("Send");
		sendButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				sendMessage();
			}
		});
		GridBagConstraints gbc_sendButton = new GridBagConstraints();
		gbc_sendButton.insets = new Insets(0, 0, 5, 5);
		gbc_sendButton.gridx = 3;
		gbc_sendButton.gridy = 4;
		frame.getContentPane().add(sendButton, gbc_sendButton);
	}
	
	/**
	 * Runs the listener thread that will continuously listen for
	 * DatagramPackets
	 */
	private void listen() {
		
		listen = new Thread("Server Listener") {
			public void run() {
				while(client.isRunning()) {
					process(client.receive());				
				}
			}
		};
		
		listen.start();
	}
	
	private void process(DatagramPacket packet) {
		String message = new String(packet.getData());
		
		if(message.startsWith(CP)) processConnection(message, packet.getAddress(), packet.getPort());
		else if(message.startsWith(UP)) processUsername(message);
		else if(message.startsWith(MP)) processMessage(message);
		else if(message.startsWith(LP)) processUserList(message);
	}
	
	private void processConnection(String message, InetAddress address, int port) {
		console("You have successfully connected to the server at " + address + " - " + port + "!");
		
		connectButton.setText("Disconnect");
		nameField.setEditable(false);
	}
	
	private void processUsername(String message) {
		console("Username \"" + getMessage(message,UP) + "\" is taken already.");
	}
	
	private void processMessage(String message) {
		console(getMessage(message, MP));
	}
	
	private void processUserList(String message) {
		userArea.setText(getMessage(message, LP));
	}
	
	private void sendMessage() {
		if(messageField.getText().equals("")) return;
		client.send(MP + nameField.getText() + ": " +messageField.getText() + EP);
		messageField.setText("");
	}
	
	
	private void connectClient() {
		if(nameField.getText().equals("")) {
			console("Please enter a valid name");
			return;
		}
		
		client.send(CP + nameField.getText() + EP);
	}
	
	private void disconnectClient() {
		if(connectButton.getText().equals("Connect")) return;
		client.send(DP + nameField.getText() + EP);
		connectButton.setText("Connect");
		nameField.setEditable(true);
		userArea.setText("");
	}
	
	private void console(String message) {
		clientHistory.append(message + "\n\r");
	}
	
	private String getMessage(String message, String prefix) {
		if(prefix.equals(MP)) {
			System.out.println("Message");
			String text = message.substring(3).split(EP)[0];
			return text;
		}
		return message.split(prefix+"|"+EP)[1];
	}

}
