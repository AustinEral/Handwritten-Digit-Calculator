/*
Austin Eral
austineral@ymail.com
5/24/27
Digit Recognition Final Project
CS 17.11

This image recognizer is an extension of a fixed topology feed-forward neural net providing a
backpropagation method for training. It provides functionality for only one hidden layer.
 */

package edu.srjc.af.austin.eral.handwritten_calculator;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Created by austi_000 on 5/6/2017.
 */
public class DigitImageRecognizer extends NeuralNetwork
{
    private final int IMAGE_WIDTH;
    private final int IMAGE_HEIGHT;
    private final int NUM_OUTPUTS = 10;
    
    private double fitnessPercentage = 0;
    
    
    //--------------------------------Constructors--------------------------------//
    
    
    
    
    
    /**
     * Creates a neural network with an input neuron for every pixel. Twenty hidden neurons are
     * created and ten output neurons. Each output neuron represents one digit.
     *
     * @param pixelWidth is the pixel width of the picture.
     * @param pixelHeight is the pixel height of the picture.
     */
    public DigitImageRecognizer(int pixelWidth, int pixelHeight)
    {
        super(pixelWidth * pixelHeight, 20, 10);
        
        IMAGE_WIDTH = pixelWidth;
        IMAGE_HEIGHT = pixelHeight;
    }
    
    
    
    
    
    /**
     * Creates a neural network with an input neuron for every pixel. Ten output neurons are
     * created. Each output neuron represents one digit.
     *
     * @param pixelWidth is the pixel width of the picture.
     * @param pixelHeight is the pixel height of the picture.
     * @param inNumHiddens is the number of hidden neurons to be created in the network.
     */
    public DigitImageRecognizer(int pixelWidth, int pixelHeight, int inNumHiddens)
    {
        super(pixelWidth * pixelHeight, inNumHiddens, 10);
        
        IMAGE_WIDTH = pixelWidth;
        IMAGE_HEIGHT = pixelHeight;
    }
    
    
    //----------------------------------Helpers-----------------------------------//
    
    
    
    
    
    /**
     * Trains the network from a csv file of images.
     *
     * @param inFile is a csv file of images. Each line must represent one image, and each
     * csv must represent one pixel in that image. The first csv of each line must represent the
     * digit of that image. This value will not be used in constructing the images. Pixel values
     * should be 0 to 255 greyscale.
     * @throws FileNotFoundException if the file not found.
     * @throws IllegalArgumentException if the file was improperly formatted.
     */
    public void trainFromCSV(File inFile) throws FileNotFoundException
    {
        Scanner fileIn;
        fileIn = new Scanner(inFile);
        
        
        while (fileIn.hasNextLine())
        {
            String[] image = fileIn.nextLine().split(",");
            int label;
            try
            {
                label = Integer.parseInt(image[0]);
            }
            catch (NumberFormatException e)
            {
                throw new IllegalArgumentException("File was improperly formatted. Label can only be a number.");
            }
            
            if (label < 0 || label > 9)
            {
                throw new IllegalArgumentException("File was improperly formatted. Image labels can only " +
                    "be from 0 to 9.");
            }
            computeImageOutputs(image);
            trainNumber(label, image);
        }
    }
    
    
    
    
    
    /**
     * Gets the values of the output neurons and converts them to a integer. The index of the
     * highest value output neuron is the determined value.
     *
     * @return the supposed integer value of the image.
     */
    public int convertOutputsToInt()
    {
        int highestValueIndex = 0;
        double highestValue = getOutputs().get(0).getNeuralValue();
        for (int i = 1; i < getOutputs().size(); i++)
        {
            if (getOutputs().get(i).getNeuralValue() > highestValue)
            {
                highestValueIndex = i;
                highestValue = getOutputs().get(i).getNeuralValue();
            }
        }
        return highestValueIndex;
    }
    
    
    
    
    
    /**
     * Converts an integer to an ArrayList of Neurons. All the neuron values are 0.0 except the
     * index of the number argument, which is 1.0;
     *
     * @param number is the number to convert to Neuron objects.
     * @return an ArrayList of outputs corresponding to the input number.
     */
    public ArrayList<Neuron> convertIntToOutputs(int number)
    {
        ArrayList<Neuron> outs = new ArrayList<>();
        for (int count = 0; count < NUM_OUTPUTS; count++)
        {
            outs.add(new Neuron(0.0));
        }
        outs.get(number).setNeuralValue(1.0);
        return outs;
    }
    
    
    
    
    
    /**
     * Computes the network outputs by putting a pixel value in every input neuron from the passed
     * in image.
     *
     * @param image is a string of greyscale pixel values.
     * @exception IllegalArgumentException if image has invalid pixel values.
     */
    public void computeImageOutputs(String[] image)
    {
        for (int pixelIndex = 0; pixelIndex < (IMAGE_WIDTH * IMAGE_HEIGHT); pixelIndex++)
        {
            double pixelVal;
            try
            {
                pixelVal = Double.parseDouble(image[pixelIndex + 1]) / 255;
            }
            catch (NumberFormatException e)
            {
                throw new IllegalArgumentException("Image's pixel value was not a number.");
            }
            if (pixelVal < 0.0 || pixelVal > 1.0)
            {
                throw new IllegalArgumentException("Image's pixel value was out of range. Must be 0 to 255.");
            }
            getInputs().get(pixelIndex).setNeuralValue(pixelVal);
        }
        computeOutputs();
    }
    
    
    
    
    
    /**
     * Gets the digit that the image truly is.
     *
     * @param image is an array of strings containing greyscle pixel values.
     * @return the digit of the image.
     */
    public int getImageDigit(String[] image)
    {
        computeImageOutputs(image);
        return convertOutputsToInt();
    }
    
    
    
    
    
    /**
     * Scans a csv file to test the network. Every time an image is correctly recognized, the
     * fitness score is incremented. The final score is stored as a ratio of successful
     * attempts divided by all attempts. The score is rounded to hundredths.
     *
     * @param inFile is a csv file of images. Each line must represent one image, and each
     * csv must represent one pixel in that image. The first csv of each line must represent the
     * digit of that image. This value will not be used in constructing the image. Pixel values
     * should be 0 to 255 greyscale.
     * @throws FileNotFoundException if the file not found.
     * @return the number of successful image recognitions.
     */
    public void computeFitness(File inFile) throws FileNotFoundException
    {
        Scanner fileIn = new Scanner(inFile);
        
        double fitness = 0;
        double totalPasses = 0;
        while (fileIn.hasNextLine())
        {
            String[] image = fileIn.nextLine().split(",");
            
            if (getImageDigit(image) == Integer.parseInt(image[0]))
            {
                fitness++;
            }
            totalPasses++;
        }
        if (totalPasses != 0.0)
        {
            fitnessPercentage = (Math.round((fitness / totalPasses) * 100.0)) / 100.0;
        }
    }
    
    
    
    
    
    /**
     * Turns a String Array into a png Image and outputs it to the output path. The image's
     * colors are inverted because the training data used is inverted. This inversion helps
     * the network learn images better because the white areas will have no value and those
     * neurons will not send signal.
     *
     * @param image is an array of strings containing greyscale pixel values from 0 to 255.
     * @param outputPath is the location defined for the image to be outputted.
     */
    public void makePngFromStringArray(String[] image, File outputPath)
    {
        BufferedImage img = new BufferedImage(IMAGE_WIDTH, IMAGE_HEIGHT, BufferedImage.TYPE_BYTE_GRAY);
        int pixelIndex = 1;
        for (int y = 0; y < IMAGE_HEIGHT; y++)
        {
            for (int x = 0; x < IMAGE_WIDTH; x++)
            {
                int pixelVal;
                try
                {
                    pixelVal = Integer.parseInt(image[pixelIndex]);
                }
                catch (IllegalArgumentException e)
                {
                    throw new IllegalArgumentException("Image's pixel value was not an integer.");
                }
                pixelVal = (pixelVal * -1) + 255;
                Color col = new Color(pixelVal, pixelVal, pixelVal);
                img.setRGB(x, y, col.getRGB());
                pixelIndex++;
            }
        }
        try
        {
            ImageIO.write(img, "png", outputPath);
        }
        catch (IOException e)
        {
            System.out.println("Error: " + e);
        }
    }
    
    
    
    
    
    /**
     * Takes a String Array of pixel values and returns a new version shifted to its center of
     * mass. This is useful for the network to only read images that have the same center of mass.
     * All of the digits in the training data csv file have been centered by their mass.
     *
     * @param image is an array of strings containing greyscale pixel values from 0 to 255.
     * @return a new image array centered by its mass.
     */
    public String[] centerImage(String[] image)
    {
        String[][] smallerImage = new String[IMAGE_WIDTH][IMAGE_HEIGHT];
        
        int sumX = 0;
        int sumY = 0;
        int totalMass = 0;
        
        int pixelIndex = 1;
        for (int y = 0; y < IMAGE_HEIGHT; y++)
        {
            for (int x = 0; x < IMAGE_WIDTH; x++)
            {
                int pixelValue = Integer.parseInt(image[pixelIndex]);
                sumX += x * pixelValue;
                sumY += y * pixelValue;
                totalMass += + pixelValue;
                smallerImage[x][y] = image[pixelIndex];
                pixelIndex++;
            }
        }
        
        sumX = sumX / totalMass;
        sumY = sumY / totalMass;
        
        int xOffset = (IMAGE_WIDTH / 2) - sumX;
        int yOffset = (IMAGE_HEIGHT / 2) - sumY;
        
        
        String[][] largerImage = new String[3 * IMAGE_WIDTH][3 * IMAGE_HEIGHT];
        
        for (int y = 0; y < largerImage.length; y++)
        {
            for (int x = 0; x < largerImage[0].length; x++)
            {
                largerImage[x][y] = "0";
            }
        }
        
        int xStaringPoint = (largerImage.length) / 3;
        int yStaringPoint = (largerImage[0].length) / 3;
        for (int y = 0; y < smallerImage.length; y++)
        {
            for (int x = 0; x < smallerImage[0].length; x++)
            {
                largerImage[xStaringPoint + x][yStaringPoint + y] = smallerImage[x][y];
            }
        }
        
        String[][] newImage = new String[IMAGE_WIDTH][IMAGE_HEIGHT];
        for (int y = 0; y < newImage.length; y++)
        {
            for (int x = 0; x < newImage[0].length; x++)
            {
                newImage[x][y] = largerImage[xStaringPoint + x - xOffset][yStaringPoint + y - yOffset];
            }
        }
        
        String[] stringImage = new String[IMAGE_WIDTH * IMAGE_HEIGHT + 1];
        stringImage[0] = "0";
        pixelIndex = 1;
        for (int y = 0; y < IMAGE_HEIGHT; y++)
        {
            for (int x = 0; x < IMAGE_WIDTH; x++)
            {
                stringImage[pixelIndex] = newImage[x][y];
                pixelIndex++;
            }
        }
        return stringImage;
    }
    
    
    
    
    
    /**
     * Converts a buffered image into a String array for this class to perform other operations
     * on the image.
     * @param image is a Buffered image.
     * @return an array of strings containing greyscale pixel values from 0 to 255.
     */
    public String[] bufferedImageToStringArray(BufferedImage image)
    {
        // http://stackoverflow.com/questions/17278829/grayscale-bitmap-into-2d-array
        // Provided raster info.
        Raster raster = image.getData();
        String[] imageArray = new String[IMAGE_WIDTH * IMAGE_HEIGHT + 1];
        int count = 1;
        for (int i = 0; i < IMAGE_HEIGHT; i++)
        {
            for (int j = 0; j < IMAGE_WIDTH; j++)
            {
                imageArray[count] = Integer.toString((raster.getSample(j, i, 0) * -1) + 255);
                count++;
            }
        }
        return imageArray;
    }
    
    
    
    
    
    /**
     * Trains the network on an array of strings containing greyscale pixel values from 0 to 255.
     * These values are passed into the input neurons of the network.
     * @param inNumber is the digit that the image is of.
     * @param imageArray is an array of strings containing greyscale pixel values from 0 to 255.
     */
    public void trainNumber(int inNumber, String[] imageArray)
    {
        computeImageOutputs(imageArray);
        ArrayList<Neuron> idealOutputs = convertIntToOutputs(inNumber);
        backpropagate(idealOutputs);
    }
    
    
    //-----------------------------Getters & Setters------------------------------//
    
    
    
    
    
    public int getImageWidth()
    {
        return IMAGE_WIDTH;
    }
    
    
    
    
    
    public int getImageHeight()
    {
        return IMAGE_HEIGHT;
    }
    
    
    
    
    
    public double getFitnessPercentage()
    {
        return fitnessPercentage;
    }
    
    
    
    
    
    public void resetFitness()
    {
        fitnessPercentage = 0.0;
    }
}
