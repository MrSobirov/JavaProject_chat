import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import java.io.*;
import java.net.*;

import java.io.IOException;

public class Client extends Application {

    Button submit, clear;
    TextField login;
    PasswordField password;
    @Override
    public void start(Stage stage) throws IOException {
        stage.setTitle("Lab8");
        Label l=new Label("          ");


        HBox layout1 = new HBox();
        Label l1=new Label("User Name");
        login= new TextField();
        layout1.getChildren().addAll(l1, l, login);


        HBox layout2 = new HBox();
        Label l2=new Label("Password ");
        password=new PasswordField();
        layout2.getChildren().addAll(l2, l, password);


        HBox layout3 = new HBox();
        submit = new Button("Submit");
        submit.setStyle("-fx-background-color: #00FFFF");
        submit.setOnAction(p->{
            try {
                Socket socket = new Socket("localhost", 8000);
                DataOutputStream toServer = new DataOutputStream(socket.getOutputStream());
                DataInputStream fromServer = new DataInputStream(socket.getInputStream());;
				toServer.writeUTF(login.getText());
				toServer.flush();
                System.out.println(login.getText() + "sent");
				toServer.writeUTF(password.getText());
				toServer.flush();
                System.out.println(password.getText() + "sent");
				Boolean authed = fromServer.readBoolean();
                System.out.println(authed + "got");
                socket.close();
            } catch (IOException ex) 
            {
                System.out.println(ex.toString() + '\n');
            }
        });

        clear =new Button("Clear");
        clear.setStyle("-fx-background-color: #00FFFF");
        clear.setOnAction(actionEvent -> {            
            login.clear();
            password.clear();
        });
        layout3.getChildren().addAll(submit,l,clear);


        VBox G = new VBox();
        G.getChildren().addAll(layout1, layout2, layout3);
        Scene scene = new Scene(G, 250,250);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}