/*
Austin Eral
austineral@ymail.com
5/24/27
Digit Recognition Final Project
CS 17.11

Controller for the network parameters window. Various parameters in the neural network can be
altered in this window to change the behavior or performance of the digit recognizer.
 */

package edu.srjc.af.austin.eral.handwritten_calculator;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by austi_000 on 5/9/2017.
 */
public class NetworkParametersController implements Initializable
{
    @FXML
    public Text hiddenNeuronsText;
    
    @FXML
    public TextField learningRate;
    
    @FXML
    public TextField momentum;
    
    @FXML
    public TextField decay;
    
    @FXML
    public TextField numHiddenNeurons;
    
    @FXML
    public TextField randomSeed;
    
    @FXML
    public TextField weightRange;
    
    @FXML
    public Text fitnessResult;
    
    @FXML
    public Button okButton;
    
    DigitImageRecognizer network;
    
    
    
    
    
    @Override
    public void initialize(URL url, ResourceBundle rb)
    {
    }
    
    
    
    
    public void setNetwork(DigitImageRecognizer inNetwork)
    {
        network = inNetwork;
    
        learningRate.setText(Double.toString(network.getLearningRate()));
        momentum.setText(Double.toString(network.getMomentum()));
        decay.setText(Double.toString(network.getDecay()));
        numHiddenNeurons.setText(Integer.toString(network.getHiddens().size() - 1));
        randomSeed.setText(Long.toString(Connection.getRandSeed()));
        weightRange.setText(Double.toString(Connection.getStartingWeightRange()));
        
        fitnessResult.setText(Double.toString(network.getFitnessPercentage()));
    }
    
    
    
    
    
    @FXML
    public void onChangeLearningRate()
    {
        try
        {
            network.setLearningRate(Double.parseDouble(learningRate.getText()));
        }
        catch (NumberFormatException e)
        {
            learningRate.setText("");
        }
    }
    
    
    
    
    
    @FXML
    public void onChangeMomentum()
    {
        try
        {
            network.setMomentum(Double.parseDouble(momentum.getText()));
        }
        catch (NumberFormatException e)
        {
            momentum.setText("");
        }
    }
    
    
    
    
    
    @FXML
    public void onChangeDecay()
    {
        try
        {
            network.setDecay(Double.parseDouble(decay.getText()));
        }
        catch (NumberFormatException e)
        {
            decay.setText("");
        }
    }
    
    
    
    
    
    @FXML
    public void onChangeNumHiddenNeurons()
    {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Network Training Loss");
        alert.setHeaderText(null);
        alert.setContentText("Changing the number of hidden neurons will result in network " +
            "training loss. The network will need to be trained again.\n\nA higher number of " +
            "hidden neurons will lengthen the time it takes to train the network.");
        alert.showAndWait();
        if (alert.getResult().equals(ButtonType.OK))
        {
            try
            {
                if (Integer.parseInt(numHiddenNeurons.getText()) >= 10)
                {
                    network.resetNumHiddens(Integer.parseInt(numHiddenNeurons.getText()));
                }
                else
                {
                    Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                    errorAlert.setTitle("Invalid Number of Neurons");
                    errorAlert.setHeaderText(null);
                    errorAlert.setContentText("The number of hidden layer neurons must be 10 " +
                        "or higher. This network does not provide functionality for less hidden " +
                        "neurons than output neurons.");
                    errorAlert.showAndWait();
                }
            }
            catch (NumberFormatException e)
            {
                numHiddenNeurons.setText("");
            }
        }
    }
    
    
    
    
    
    @FXML
    public void onChangeRandomSeed()
    {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Network Training Information");
        alert.setHeaderText(null);
        alert.setContentText("Changing this parameter will have no effect until network is reset.");
        alert.showAndWait();
            try
            {
                Connection.setRandSeed(Long.parseLong(randomSeed.getText()));
            }
            catch (NumberFormatException e)
            {
                randomSeed.setText("");
            }
    }
    
    
    
    
    
    @FXML
    public void onChangeWeightRange()
    {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Network Training Information");
        alert.setHeaderText(null);
        alert.setContentText("Changing this parameter will have no effect until network is reset.");
        alert.showAndWait();
        try
        {
            Connection.setStartingWeightRange(Double.parseDouble(weightRange.getText()));
        }
        catch (NumberFormatException e)
        {
            weightRange.setText("");
        }
    }
    
    
    
    
    
    @FXML
    public void resetNetwork()
    {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Network Training Loss");
        alert.setHeaderText(null);
        alert.setContentText("Resetting the network weights will result in network training " +
            "loss. The network will need to be trained again.");
        alert.showAndWait();
        if (alert.getResult().equals(ButtonType.OK))
        {
            network.resetWeights();
            network.resetFitness();
            fitnessResult.setText(Double.toString(network.getFitnessPercentage()));
        }
    }
    
    
    
    
    
    @FXML
    public void closeWindow()
    {
        Stage stage = (Stage)okButton.getScene().getWindow();
        stage.close();
    }
    
    
    
    
    
    public void onGetNetworkHelp(ActionEvent inActionEvent)
    {
        Alert information = new Alert(Alert.AlertType.INFORMATION);
        information.setTitle("Get Started");
        information.setHeaderText("Need Help?");
        information.setContentText("Before using this calculator be sure to train " +
            "the network via the \"Neural Network\" tab.\n\nThe training file can be changed " +
            "and network parameters can be altered via this tab.\n\nLastly enter training " +
            "mode to experiment with training the network single handwritten digits written " +
            "by you.");
        information.showAndWait();
    }
}
