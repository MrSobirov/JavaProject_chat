import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.io.IOException;

public class Client extends Application {

    private Stage stage;

    Scene RegScene;
    TextField RegLogin;
    PasswordField RegPassword;
    Label Reg_u =  new Label("User Name");
    Label Reg_p = new Label("Password ");
    Button signup, back;
    HBox Reg_1 = new HBox();
    HBox Reg_2 = new HBox(); 
    HBox Reg_3 = new HBox(); 
    VBox Reg_Vertical = new VBox();


    Scene LoginScene;
    TextField LogLogin;
    PasswordField LogPassword;
    Label Log_u =  new Label("User Name");
    Label Log_p = new Label("Password ");
    Button submit, register;
    HBox Log_1 = new HBox();
    HBox Log_2 = new HBox(); 
    HBox Log_3 = new HBox();
    VBox Log_Vertical = new VBox();
    
    Scene ChatScene;
    public static final ObservableList data = FXCollections.observableArrayList();
    final ListView listViewMsg = new ListView(data);
    TextArea Message;
    Button Send, SeeUsers, Logout;
    HBox Chat_1 = new HBox();
    HBox Chat_2 = new HBox();
    VBox Chat_Vertical = new VBox();

    Scene UsersScene;
    public static final ObservableList Users = FXCollections.observableArrayList();
    final ListView listViewUsers = new ListView(Users);
    Button back_main;
    Label title = new Label("All registered users");
    VBox User_Vertical = new VBox();
    

    @Override
    public void start(Stage start_stage) throws IOException, ClassNotFoundException {    
        Socket socket = new Socket("192.168.100.40", 8000);
        DataOutputStream toServer = new DataOutputStream(socket.getOutputStream());
        DataInputStream fromServer = new DataInputStream(socket.getInputStream());
        stage = start_stage;
        stage.setTitle("Chat Screen");
        
        LoginScene = createLoginScene(toServer,  fromServer);
        RegScene = createRegisterScene(toServer,  fromServer);
        
        UsersScene = createUserScene(toServer, fromServer);

        stage.setScene(LoginScene);
        stage.show();
    }

    private Scene createRegisterScene(DataOutputStream to, DataInputStream from) {
        RegLogin = new TextField();
        Reg_1.getChildren().addAll(Reg_u, RegLogin);
        Reg_1.setSpacing(30);

        RegPassword= new PasswordField();
        Reg_2.getChildren().addAll(Reg_p, RegPassword);
        Reg_2.setSpacing(30);

        signup = new Button("Sign up");
        signup.setStyle("-fx-background-color: #00FFFF");
        signup.setOnAction(p->{
            try {            
                to.writeInt(2);            
				to.writeUTF(RegLogin.getText());
				to.flush();
                System.out.println(RegLogin.getText() + "sent");
				to.writeUTF(RegPassword.getText());
				to.flush();
                System.out.println(RegPassword.getText() + "sent");
                Boolean authed = from.readBoolean();
                System.out.println(authed + " got");
                if(authed) {
                    switchScenes(ChatScene);
                }
            } catch (IOException ex) 
            {
                System.out.println(ex.toString() + '\n');
            }
        });

        back = new Button("Back");
        back.setStyle("-fx-background-color: #00FFFF");
        back.setOnAction(p->{
            switchScenes(LoginScene);
        });
        Reg_3.getChildren().addAll(signup, back);
        Reg_3.setSpacing(30);
        
        Reg_Vertical.getChildren().addAll(Reg_1, Reg_2, Reg_3);
        Reg_Vertical.setSpacing(15);
        Reg_Vertical.setPadding(new Insets(10, 30, 30, 30));
        Scene temp_scene = new Scene(Reg_Vertical, 300,300);
        return temp_scene;
    }

    private Scene createLoginScene(DataOutputStream to, DataInputStream from) throws IOException{
       
        LogLogin = new TextField();
        Log_1.getChildren().addAll(Log_u, LogLogin);
        Log_1.setSpacing(30);

        LogPassword= new PasswordField();
        Log_2.getChildren().addAll(Log_p, LogPassword);
        Log_2.setSpacing(30);

        submit = new Button("Submit");
        submit.setStyle("-fx-background-color: #00FFFF");
        submit.setOnAction(p->{
            try {        
                to.writeInt(1);        
				to.writeUTF(LogLogin.getText());
				to.flush();
                System.out.println(LogLogin.getText() + " sent");
				to.writeUTF(LogPassword.getText());
				to.flush();
                System.out.println(LogPassword.getText() + " sent");
				Boolean authed = from.readBoolean();
                System.out.println(authed + " got");
                if(authed) {
                    ChatScene = createChatScene(to, from);
                    switchScenes(ChatScene);
                } else {
                    switchScenes(RegScene);
                }
                
            } catch (IOException | ClassNotFoundException ex) 
            {
                System.out.println(ex.toString() + '\n');
            }
        });

        register = new Button("Register");
        register.setStyle("-fx-background-color: #00FFFF");
        register.setOnAction(actionEvent -> {            
            switchScenes(RegScene);
        });
        Log_3.getChildren().addAll(submit, register);
        Log_3.setSpacing(70);
        
        Log_Vertical.getChildren().addAll(Log_1, Log_2, Log_3);
        Log_Vertical.setSpacing(15);
        Log_Vertical.setPadding(new Insets(10, 30, 30, 30));
        Scene temp_scene = new Scene(Log_Vertical, 300,300);
        return temp_scene;
    }

    private Scene createChatScene(DataOutputStream to, DataInputStream from) throws IOException, ClassNotFoundException {
        getMessages(to, from);
        Message = new TextArea();
        Message.setPrefHeight(40);
        Message.setPrefWidth(150);
        Send = new Button("Send");
        Send.setPrefHeight(40);
        Send.setPrefWidth(50);
        Send.setOnAction(p-> {
            try {            
                to.writeInt(4); 
                String text = LogLogin.getText() != "" ? LogLogin.getText() : RegLogin.getText();           
				to.writeUTF(text);
				to.flush();
				to.writeUTF(Message.getText());
				to.flush();
                Message.clear();
                data.clear();
                getMessages(to, from);
                switchScenes(ChatScene);
            } catch (IOException | ClassNotFoundException ex) 
            {
                System.out.println(ex.toString() + '\n');
            }
        });

        SeeUsers = new Button("Users");
        SeeUsers.setPrefHeight(40);
        SeeUsers.setPrefWidth(50);
        SeeUsers.setOnAction(p-> {
            switchScenes(UsersScene);
        });

        Logout = new Button("Logout");
        Logout.setPrefHeight(40);
        Logout.setPrefWidth(70);
        Logout.setOnAction(p-> {
            switchScenes(LoginScene);
        });

        Chat_1.getChildren().addAll(SeeUsers, Logout);
        Chat_1.setSpacing(87);

        Chat_2.getChildren().addAll(Message, Send);
        Chat_2.setSpacing(20);
        Chat_Vertical.getChildren().addAll(Chat_1, listViewMsg, Chat_2);
        Chat_Vertical.setSpacing(20);
        Chat_Vertical.setPadding(new Insets(20, 20, 20, 20));
        Scene temp_scene = new Scene(Chat_Vertical, 250, 300);
        return temp_scene;

    }


    private Scene createUserScene(DataOutputStream to, DataInputStream from) throws IOException, ClassNotFoundException {
        to.writeInt(5);
        ObjectInputStream ois = new ObjectInputStream(from);  
        ArrayList users = (ArrayList) ois.readObject();
        System.out.println(users);
        listViewUsers.setPrefSize(190, 210);
        listViewUsers.setEditable(true);            
        for (int i = 0; i < users.size(); i++) {
            String user = (String) users.get(i);
            Users.add(user);
        }          
        listViewUsers.setItems(Users);
        back_main = new Button("Back to main");
        back_main.setOnAction(p-> {
            switchScenes(ChatScene);
        });
        User_Vertical.getChildren().addAll(title, listViewUsers, back_main);
        User_Vertical.setSpacing(10);
        User_Vertical.setPadding(new Insets(15, 15, 15, 15));
        Scene temp_scene = new Scene(User_Vertical, 200, 200);
        return temp_scene; 
    }

    private void getMessages(DataOutputStream to, DataInputStream from) throws IOException, ClassNotFoundException {
        to.writeInt(3);
        ObjectInputStream ois = new ObjectInputStream(from);  
        ArrayList messages = (ArrayList) ois.readObject();
        System.out.println(messages);
        listViewMsg.setPrefSize(190, 210);
        listViewMsg.setEditable(true);
        for (int i = 0; i < messages.size(); i++) {
            HashMap one_m = (HashMap) messages.get(i);
            System.out.println(one_m);
            String sender = (String) one_m.get("sender");
            String text = (String) one_m.get("text");
            data.add(sender.toUpperCase() + ": " + text);
        }          
        listViewMsg.setItems(data);   
    }
    
    public void switchScenes(Scene scene) {
		stage.setScene(scene);
	}

    public static void main(String[] args) {
        launch();
    }
}