/*
Austin Eral
austineral@ymail.com
5/24/27
Digit Recognition Final Project
CS 17.11

Sets up a GUI for a simple calculator that allows the user to draw the digits for calculation.
A neural network is used to determine the digits drawn. The network must be trained on a data set
before it can recognize digits.

The network's default parameters are not as optimal as when I was presenting this project in class.
I lost the original values so the network does not recognize each digit as well as I would like.
 */
package edu.srjc.af.austin.eral.handwritten_calculator;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ResourceBundle;

import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.effect.*;
import javafx.scene.image.Image;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.*;


public class ImageCalculatorController implements Initializable
{
    private final int IMAGE_WIDTH = 28;
    private final int IMAGE_HEIGHT = 28;
    private final String trainingFileName = "60,000 Digits.csv";
    
    private DigitImageRecognizer network;
    
    private GraphicsContext gc1;
    private GraphicsContext gc2;
    
    private File trainingFile;
    private FileChooser fileChooser;
    
    private boolean isLineDrawnCanvas1 = false;
    private boolean isLineDrawnCanvas2 = false;
    private boolean isTrainingMode = false;
    
    private Stage networkStage;
    
    private FXMLLoader loader;
    
    private enum Operator
    {
        Add,
        Subtract,
        Multiply,
        Divide
    }
    
    private Operator operator;
    
    @FXML
    public AnchorPane anchor;
    
    @FXML
    private Canvas canvas1;
    
    @FXML
    public Canvas canvas2;
    
    @FXML
    public Pane pane1;
    @FXML
    public Pane pane2;
    
    @FXML
    public Button clear1;
    
    @FXML
    public Button clear2;
    
    @FXML
    public Button teachNum;
    
    @FXML
    public TextField numberField;
    
    @FXML
    public Text result1;
    
    @FXML
    public Text result2;
    
    @FXML
    public MenuItem trainNetworkButton;
    
    @FXML
    public Text answer;
    
    @FXML
    public Button btnAdd;
    
    @FXML
    public Button btnSubtract;
    
    @FXML
    public Button btnMultiply;
    
    @FXML
    public Button btnDivide;
    
    
    
    
    
    @Override
    public void initialize(URL url, ResourceBundle rb)
    {
        network = new DigitImageRecognizer(IMAGE_WIDTH, IMAGE_HEIGHT);
        
        // Prepares the canvases
        gc1 = canvas1.getGraphicsContext2D();
        gc1.setLineWidth(20);
        gc1.setStroke(Color.BLACK);
        gc2 = canvas2.getGraphicsContext2D();
        gc2.setLineWidth(20);
        gc2.setStroke(Color.BLACK);
        
        BoxBlur blur = new BoxBlur();
        blur.setWidth(2);
        blur.setHeight(2);
        blur.setIterations(10);
        gc1.setEffect(blur);
        gc2.setEffect(blur);
        
        // Sets the training file for the network.
        fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(new File("./"));
        fileChooser.setTitle("Open Training File");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV File", "*.csv"));
        
        trainingFile = new File(trainingFileName);
        if (!trainingFile.isFile())
        {
            trainNetworkButton.setDisable(true);
        }
        
        // Creates another stage for changing the network parameters.
        try
        {
            loader = new FXMLLoader(getClass().getResource("NetworkFXML.fxml"));
            Scene scene = new Scene(loader.load());
            networkStage = new Stage();
            networkStage.setScene(scene);
            networkStage.setTitle("Network Parameters");
        }
        catch (IOException e)
        {
            Alert errorAlert = new Alert(Alert.AlertType.ERROR);
            errorAlert.setTitle("Loader Error");
            errorAlert.setHeaderText(null);
            errorAlert.setContentText("Could not load new parameter scene.");
            errorAlert.showAndWait();
        }
        
        canvas1.addEventHandler(MouseEvent.MOUSE_PRESSED,
            new EventHandler<MouseEvent>()
            {
                
                @Override
                public void handle(MouseEvent event)
                {
                    gc1.beginPath();
                    gc1.moveTo(event.getX(), event.getY());
                    gc1.stroke();
                }
            });
        canvas1.addEventHandler(MouseEvent.MOUSE_DRAGGED,
            new EventHandler<MouseEvent>()
            {
                
                @Override
                public void handle(MouseEvent event)
                {
                    gc1.lineTo(event.getX(), event.getY());
                    gc1.stroke();
                    isLineDrawnCanvas1 = true;
                }
            });
        canvas1.addEventHandler(MouseEvent.MOUSE_RELEASED,
            new EventHandler<MouseEvent>()
            {
                
                @Override
                public void handle(MouseEvent event)
                {
                    if (isLineDrawnCanvas1)
                    {
                        onScanImage(canvas1, result1);
                        solve();
                    }
                }
            });
        
        canvas2.addEventHandler(MouseEvent.MOUSE_PRESSED,
            new EventHandler<MouseEvent>()
            {
                
                @Override
                public void handle(MouseEvent event)
                {
                    gc2.beginPath();
                    gc2.moveTo(event.getX(), event.getY());
                    gc2.stroke();
                }
            });
        
        canvas2.addEventHandler(MouseEvent.MOUSE_DRAGGED,
            new EventHandler<MouseEvent>()
            {
                
                @Override
                public void handle(MouseEvent event)
                {
                    gc2.lineTo(event.getX(), event.getY());
                    gc2.stroke();
                    isLineDrawnCanvas2 = true;
                }
            });
        
        canvas2.addEventHandler(MouseEvent.MOUSE_RELEASED,
            new EventHandler<MouseEvent>()
            {
                
                @Override
                public void handle(MouseEvent event)
                {
                    if (isLineDrawnCanvas2)
                    {
                        onScanImage(canvas2, result2);
                        solve();
                    }
                }
            });
    }
    
    
    
    
    
    @FXML
    public void onGetHelp()
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
    
    
    
    
    
    @FXML
    public void onChooseFile(ActionEvent inActionEvent)
    {
        File selectedFile = fileChooser.showOpenDialog(null);
        {
            if (selectedFile != null)
            {
                trainingFile = selectedFile;
                trainNetworkButton.setDisable(false);
            }
        }
    }
    
    
    
    
    
    @FXML
    private void onTrain(ActionEvent event)
    {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Train Network From File");
        alert.setHeaderText(null);
        alert.setContentText("Training the neural network will make this window unusable for a " +
            "period of time. It should take no more than a minute or two.");
        alert.showAndWait();
        
        if (alert.getResult().equals(ButtonType.OK))
        {
            if (trainingFile != null)
            {
                try
                {
                    network.trainFromCSV(trainingFile);
                    network.computeFitness(trainingFile);
                }
                catch (FileNotFoundException e)
                {
                    Alert alertError = new Alert(Alert.AlertType.ERROR);
                    alertError.setTitle("File Not Found");
                    alertError.setHeaderText(null);
                    alertError.setContentText("The File Could Not be found.");
                    alertError.showAndWait();
                }
                catch (IllegalArgumentException e)
                {
                    Alert alertError = new Alert(Alert.AlertType.ERROR);
                    alertError.setTitle("Improper File Format");
                    alertError.setHeaderText(null);
                    alertError.setContentText(e.getMessage()
                        + "\nPlease select a different training file or correct the file format.");
                    alertError.showAndWait();
                }
            }
        }
    }
    
    
    
    
    
    @FXML
    public void onClear1()
    {
        result1.setText("");
        isLineDrawnCanvas1 = false;
        answer.setText("");
        gc1.setEffect(null);
        gc1.clearRect(0, 0, canvas1.getWidth(), canvas1.getHeight());
        BoxBlur blur = new BoxBlur();
        blur.setWidth(2);
        blur.setHeight(2);
        blur.setIterations(10);
        gc1.setEffect(blur);
    }
    
    
    
    
    
    @FXML
    public void onClear2()
    {
        result2.setText("");
        isLineDrawnCanvas2 = false;
        answer.setText("");
        gc2.setEffect(null);
        gc2.clearRect(0, 0, canvas2.getWidth(), canvas2.getHeight());
        BoxBlur blur = new BoxBlur();
        blur.setWidth(3);
        blur.setHeight(3);
        blur.setIterations(10);
        gc2.setEffect(blur);
    }
    
    
    
    
    
    @FXML
    public void onScanImage(Canvas inCanvas, Text result)
    {
        Image snapshot = inCanvas.snapshot(null, null);
        BufferedImage image = SwingFXUtils.fromFXImage(snapshot, null);
        BufferedImage resizedImage = new BufferedImage(IMAGE_WIDTH, IMAGE_HEIGHT, BufferedImage.TYPE_BYTE_GRAY);
        
        // http://www.journaldev.com/615/java-image-resize-program-using-graphics2d-example
        // This site provided information on Graphics2D objects.
        Graphics2D graphics2D = resizedImage.createGraphics();
        graphics2D.setComposite(AlphaComposite.Src);
        graphics2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        graphics2D.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        graphics2D.drawImage(image, 0, 0, IMAGE_WIDTH, IMAGE_HEIGHT, null);
        graphics2D.dispose();
        
        String[] centeredImage = network.centerImage(network.bufferedImageToStringArray(resizedImage));
        
        int number = network.getImageDigit(centeredImage);
        
        result.setText(Integer.toString(number));
        
    }
    
    
    
    
    
    @FXML
    public void onTeachNum(MouseEvent inMouseEvent)
    {
        if (isLineDrawnCanvas1)
        {
            Image snapshot = canvas1.snapshot(null, null);
            BufferedImage image = SwingFXUtils.fromFXImage(snapshot, null);
            BufferedImage resizedImage = new BufferedImage(IMAGE_WIDTH, IMAGE_HEIGHT, BufferedImage.TYPE_BYTE_GRAY);
            
            // http://www.journaldev.com/615/java-image-resize-program-using-graphics2d-example
            // This site provided information on Graphics2D objects.
            Graphics2D graphics2D = resizedImage.createGraphics();
            graphics2D.setComposite(AlphaComposite.Src);
            graphics2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            graphics2D.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            graphics2D.drawImage(image, 0, 0, IMAGE_WIDTH, IMAGE_HEIGHT, null);
            graphics2D.dispose();
            
            String[] centeredImage = network.centerImage(network.bufferedImageToStringArray(resizedImage));
            network.trainNumber(Integer.parseInt(numberField.getText()), centeredImage);
            result1.setText(Integer.toString(network.getImageDigit(centeredImage)));
        }
        else
        {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Canvas Empty");
            alert.setHeaderText(null);
            alert.setContentText("Canvas must be drawn on before training network.");
            alert.showAndWait();
        }
    }
    
    
    
    
    
    @FXML
    public void onToggleTrainingMode()
    {
        isTrainingMode = !isTrainingMode;
        
        pane2.setVisible(!isTrainingMode);
        pane2.setDisable(isTrainingMode);
        
        onClear1();
        onClear2();
        clear2.setDisable(isTrainingMode);
        clear2.setVisible(!isTrainingMode);
        btnAdd.setDisable(isTrainingMode);
        btnAdd.setVisible(!isTrainingMode);
        btnSubtract.setVisible(!isTrainingMode);
        btnMultiply.setVisible(!isTrainingMode);
        btnDivide.setVisible(!isTrainingMode);
        btnSubtract.setDisable(isTrainingMode);
        btnMultiply.setDisable(isTrainingMode);
        btnDivide.setDisable(isTrainingMode);
        
        pane1.setLayoutX(isTrainingMode ? 300 : 50);
        clear1.setLayoutX(isTrainingMode ? 450 : 200);
        result1.setLayoutX(isTrainingMode ? 412 : 162);
        numberField.setDisable(!isTrainingMode);
        numberField.setVisible(isTrainingMode);
        teachNum.setVisible(isTrainingMode);
        numberField.clear();
        
        if (isTrainingMode)
        {
            numberField.requestFocus();
        }
        else
        {
            teachNum.setDisable(true);
        }
    }
    
    
    
    
    
    @FXML
    public void onNumberTyped(KeyEvent inKeyEvent)
    {
        if (numberField.getText().length() > 0)
        {
            numberField.setText(numberField.getText(1, 1));
        }
        if (!Character.isDigit(inKeyEvent.getCharacter().charAt(0)))
        {
            inKeyEvent.consume();
            teachNum.setDisable(true);
        }
        else
        {
            teachNum.setDisable(false);
        }
    }
    
    
    
    
    
    @FXML
    public void onChangeNetworkParameters(ActionEvent inActionEvent)
    {
        NetworkParametersController controller = loader.getController();
        controller.setNetwork(network);
        networkStage.show();
    }
    
    
    
    
    
    @FXML
    public void add(ActionEvent inActionEvent)
    {
        InnerShadow shadow = new InnerShadow();
        shadow.setRadius(20.0);
        resetAllButtonEffects();
        btnAdd.setEffect(shadow);
        operator = Operator.Add;
        solve();
    }
    
    
    
    
    
    @FXML
    public void subtract(ActionEvent inActionEvent)
    {
        InnerShadow shadow = new InnerShadow();
        shadow.setRadius(20.0);
        resetAllButtonEffects();
        btnSubtract.setEffect(shadow);
        operator = Operator.Subtract;
        solve();
    }
    
    
    
    
    
    @FXML
    public void multiply(ActionEvent inActionEvent)
    {
        InnerShadow shadow = new InnerShadow();
        shadow.setRadius(20.0);
        resetAllButtonEffects();
        btnMultiply.setEffect(shadow);
        operator = Operator.Multiply;
        solve();
    }
    
    
    
    
    
    @FXML
    public void divide(ActionEvent inActionEvent)
    {
        InnerShadow shadow = new InnerShadow();
        shadow.setRadius(20.0);
        resetAllButtonEffects();
        btnDivide.setEffect(shadow);
        operator = Operator.Divide;
        solve();
    }
    
    
    
    
    
    private void solve()
    {
        if (!result1.getText().equals("") && !result2.getText().equals("") && operator != null)
        {
            int operator1 = Integer.parseInt(result1.getText());
            int operator2 = Integer.parseInt(result2.getText());
            
            switch (operator)
            {
                case Add:
                    answer.setText(Integer.toString(operator1 + operator2));
                    break;
                case Subtract:
                    answer.setText(Integer.toString(operator1 - operator2));
                    break;
                case Multiply:
                    answer.setText(Integer.toString(operator1 * operator2));
                    break;
                case Divide:
                    if (result2.getText().equals("0"))
                    {
                        answer.setText("Undefined");
                    }
                    else
                    {
                        answer.setText(new DecimalFormat("#.########").format((double) operator1 / (double) operator2));
                    }
            }
        }
    }
    
    
    
    
    
    private void resetAllButtonEffects()
    {
        btnAdd.setEffect(null);
        btnSubtract.setEffect(null);
        btnMultiply.setEffect(null);
        btnDivide.setEffect(null);
    }
    
    
    
    
    
    public DigitImageRecognizer getNetwork()
    {
        return network;
    }
}