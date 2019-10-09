/*
Austin Eral
austineral@ymail.com
5/24/27
Digit Recognition Final Project
CS 17.11

This neuron is used to store a neural value in a lager neural network structure. Each neuron
stores each previous connection and each next connection in the network structure.
 */

package edu.srjc.af.austin.eral.handwritten_calculator;

import java.util.ArrayList;

/**
 * Created by austi_000 on 4/29/2017.
 */
public class Neuron
{
    private double neuralValue = 0.0;
    
    private ArrayList<Connection> prevConnections = new ArrayList<>();
    private ArrayList<Connection> nextConnections = new ArrayList<>();
    
    
    //--------------------------------Constructors--------------------------------//
    
    
    
    
    
    public Neuron()
    {
    }
    
    
    
    
    
    public Neuron(double inNeuralValue)
    {
        neuralValue = inNeuralValue;
    }
    
    
    //----------------------------------Helpers-----------------------------------//
    
    
    
    
    
    /**
     * Computes the neural value which is the sum of the each previous neuron's
     * neural value multiplied by the connection weight linking that neuron to this neuron.
     */
    public void computeNeuralValue()
    {
        if (getPrevConnections().size() > 0)
        {
            neuralValue = 0.0;
            for (Connection prevConnection : prevConnections)
            {
                neuralValue += prevConnection.getPrevNeuron().neuralValue * prevConnection.getWeight();
            }
            neuralValue = sigmoidFunction(neuralValue);
        }
    }
    
    
    
    
    
    /**
     * Activation function for the neural value that returns a value between 0 and 1.
     *
     * @param neuralValue is the neural value before activation.
     * @return the new activated neural value.
     */
    public static double sigmoidFunction(double neuralValue)
    {
        return 1.0 / (1.0 + Math.pow(Math.E, (-neuralValue)));
    }
    
    
    
    
    
    @Override
    public String toString()
    {
        return "Neural Value: " + neuralValue
            + "\nNum Prev Neurons: " + prevConnections.size()
            + "\nNum Next Neurons: " + nextConnections.size()
            + "\n\n";
    }
    
    
    //-----------------------------Getters & Setters------------------------------//
    
    
    
    
    
    public double getNeuralValue()
    {
        return neuralValue;
    }
    
    
    
    
    
    public void setNeuralValue(double inNeuralValue)
    {
        neuralValue = inNeuralValue;
    }
    
    
    
    
    
    public ArrayList<Connection> getPrevConnections()
    {
        return prevConnections;
    }
    
    
    
    
    
    public ArrayList<Connection> getNextConnections()
    {
        return nextConnections;
    }
}
