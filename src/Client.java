import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import java.io.*;
import java.net.*;

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
    

    
    

    @Override
    public void start(Stage start_stage) throws IOException {    
        Socket socket = new Socket("localhost", 8000);
        DataOutputStream toServer = new DataOutputStream(socket.getOutputStream());
        DataInputStream fromServer = new DataInputStream(socket.getInputStream());
        stage = start_stage;
        stage.setTitle("Chat Screen");
        
        LoginScene = createLoginScene(toServer,  fromServer);
        RegScene = createRegisterScene(toServer,  fromServer);
        
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
                
            } catch (IOException ex) 
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

    public void switchScenes(Scene scene) {
		stage.setScene(scene);
	}

    public static void main(String[] args) {
        launch();
    }
}