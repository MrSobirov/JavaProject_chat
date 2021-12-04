import java.io.*;
import java.sql.*;
import java.net.*;
public class Server
{
  
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
			String login = inputFromClient.readUTF();
			System.out.println("We have received login value "+ login);
			String password = inputFromClient.readUTF();
			System.out.println("We have received password value "+ password);
			// Send area back to the client
            try {
                Connection conn = DriverManager.getConnection( "jdbc:mysql://localhost:3306/fast_food?autoReconnect=true&useSSL=false", "root", "mypassword");            
                Statement stmt = conn.createStatement();
                String strSelect = "select login, password from users";
                ResultSet rset = stmt.executeQuery(strSelect);
                while(rset.next()) 
                {  
                    if(login.equals(rset.getString("login")) && password.equals(rset.getString("password"))) {
                        outputToClient.writeBoolean(true); 
                    } else {
                        outputToClient.writeBoolean(false); 
                    }
                } 
            } catch (SQLException e) {
                System.out.println(e);
            }
			serverSocket.close();
		} catch(IOException ex) {
			System.err.println(ex);
		}
  	}
}
