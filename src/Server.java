import java.io.*;
import java.sql.*;
import java.util.*;
import java.net.*;

public class Server{
  
public static void main(String[] args) 
{
	new Server ();
}
public Server ()// constructor
{ 
	try 
	{
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
        	    		ResultSet rset = stmt.executeQuery(strSelect);
						boolean get_authed = false;
						String login = inputFromClient.readUTF();
						System.out.println("We have received login value "+ login);
						String password = inputFromClient.readUTF();
						System.out.println("We have received password value "+ password);
						while(rset.next()) 
        	    		{  
        	    		    if(login.equals(rset.getString("login")) && password.equals(rset.getString("password"))) {
        	    		        get_authed = true;
        	    		    } 
        	    		} 
						outputToClient.writeBoolean(get_authed); 
						break;
					case 2:
						String Reguser = inputFromClient.readUTF();
						System.out.println("We received login value "+ Reguser);
						String Regpassword = inputFromClient.readUTF();
						System.out.println("We received password value "+ Regpassword);
						String strInsert = "insert into users values (\"" + Reguser + "\", \"" + Regpassword + "\")"; 
						int count = stmt.executeUpdate(strInsert);
						outputToClient.writeBoolean(count == 1); 
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
						System.out.println("We received " + Text + " from " + Sender);
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
			}
		} catch(IOException ex) {
			System.err.println(ex);
		}
  	}
}
