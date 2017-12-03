package com.vecsight.dragonite.proxy.gui;

import com.vecsight.dragonite.proxy.gui.controller.DragoniteController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class Main extends Application {

    private DragoniteController dragoniteController;

    @Override
    public void start(Stage primaryStage) throws Exception{
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("/DragoniteController.fxml"));
        AnchorPane anchorPane = fxmlLoader.load();
        primaryStage.setTitle("DragoniteX");
        primaryStage.setScene(new Scene(anchorPane, 860, 567));
        primaryStage.setResizable(false);
        dragoniteController = fxmlLoader.getController();
        dragoniteController.initLog();
        dragoniteController.loadConfig();


        primaryStage.show();
    }

    @Override
    public void stop() throws Exception {
        dragoniteController.saveConfig();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
