import java.io.*;
import java.sql.*;
import java.util.*;
import java.net.*;

public class Server extends Thread{
	public static void main(String[] args) {
		new Server ();
	}

	public Server () { // constructor 
	Thread thread = new Thread();
	thread.start();
	try {
		// Create a server socket
		ServerSocket serverSocket = new ServerSocket(8000);
		System.out.println("Server started at \n");
		// Listen for a connection request
		Socket socket = serverSocket.accept();
		// Create data input and output streams
		DataInputStream inputFromClient = new DataInputStream(socket.getInputStream());
		DataOutputStream outputToClient = new DataOutputStream(socket.getOutputStream());
		// Receive radius from the client
		while(true) {
			try {	
				Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/chat?autoReconnect=true&useSSL=false", "root", "mypassword");            
        	    Statement stmt = conn.createStatement();
				int type = inputFromClient.readInt();
				switch(type) {
					case 1: 
						String strSelect = "select login, password from users";        	    		
						String login = inputFromClient.readUTF();
						String password = inputFromClient.readUTF();
						System.out.println("Server have received existing user " + login + "\npassword: " + password);
						ResultSet rset = stmt.executeQuery(strSelect);
						boolean get_authed = false;
						while(rset.next()) 
        	    		{  
        	    		    if(login.equals(rset.getString("login")) && password.equals(rset.getString("password"))) {
        	    		        get_authed = true;
        	    		    } 
        	    		} 
						System.out.println("Authentication: " + get_authed);
						outputToClient.writeBoolean(get_authed); 
						break;
					case 2:
						String Reguser = inputFromClient.readUTF();
						String Regpassword = inputFromClient.readUTF();
						System.out.println("Server received new user "+Reguser+" with a password " + Regpassword);
						String strInsert = "insert into users values (\"" + Reguser + "\", \"" + Regpassword + "\")"; 
						int count = stmt.executeUpdate(strInsert);
						outputToClient.writeBoolean(count == 1); 
						System.out.println("Successfully added");
						break;
					case 3:
						ArrayList list = new ArrayList();
						String strSelectChat = "select sender, text from messages";
        	    		ResultSet rsetChat = stmt.executeQuery(strSelectChat);
						while(rsetChat.next()) 
        	    		{  
							HashMap mMap = new HashMap();
							mMap.put("sender", rsetChat.getString("sender"));
							mMap.put("text",rsetChat.getString("text"));
							list.add(mMap);
        	    		} 
						ObjectOutputStream oos = new ObjectOutputStream(outputToClient);  
						oos.writeObject(list);
						break;
					case 4:
						String Sender = inputFromClient.readUTF();
						String Text = inputFromClient.readUTF();
						System.out.println("Server received new messages (" + Text + ") from " + Sender);
						String MSGInsert = "insert into messages values (\"" + Sender + "\", \"" + Text + "\")"; 
						stmt.executeUpdate(MSGInsert);
						break;
					case 5:
						ArrayList users = new ArrayList();
						String strSelectUsers = "select login, password from users";
						ResultSet rsetUsers = stmt.executeQuery(strSelectUsers);
						while(rsetUsers.next()) {  
							users.add(rsetUsers.getString("login"));
						} 
						ObjectOutputStream oostr = new ObjectOutputStream(outputToClient);  
						oostr.writeObject(users);
						break;
					}
				} catch (SQLException e) {
					System.out.println(e);
				} 
				thread.sleep(2000); 	
			}
		} catch(IOException | InterruptedException ex) {
			System.err.println(ex);
		}
  	}
}
