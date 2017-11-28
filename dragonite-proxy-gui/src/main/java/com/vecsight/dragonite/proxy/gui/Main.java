package com.vecsight.dragonite.proxy.gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("DragoniteController.fxml"));
        primaryStage.setTitle("DragoniteX");
        primaryStage.setScene(new Scene(root, 860, 567));
        // 禁止调整窗口大小
        primaryStage.setResizable(false);



        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
