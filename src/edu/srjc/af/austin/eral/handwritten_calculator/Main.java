/*
Austin Eral
austineral@ymail.com
5/24/27
Digit Recognition Final Project
CS 17.11

Sets the GUI stage for the digit image recognizer.
*/
package edu.srjc.af.austin.eral.handwritten_calculator;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application
{
    
    @Override
    public void start(Stage stage) throws Exception
    {
        Scene scene = new Scene(FXMLLoader.load(getClass().getResource("FXMLDocument.fxml")));
        
        stage.setScene(scene);
        stage.setTitle("Digit Recognizer");
        stage.show();
    }

    public static void main(String[] args)
    {
        launch(args);
    }
    
}
