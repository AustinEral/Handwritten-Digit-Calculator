/*
Austin Eral
austineral@ymail.com
5/24/27
Digit Recognition Final Project
CS 17.11

This class is a connection to be used in a neural network to connect neuron classes.
731782543
 */

package edu.srjc.af.austin.eral.handwritten_calculator;

import java.util.Random;

/**
 * Created by austi_000 on 4/29/2017.
 */
public class Connection
{
    private static long randSeed = 731782543;
    private static Random gen = new Random(randSeed);
    private static double startingWeightRange = 0.6;
    
    private double weight = gen.nextDouble() * startingWeightRange - (startingWeightRange / 2);
    private double prevDeltaWeight = 0;
    private double deltaWeight = 0;
    
    final Neuron prevNeuron;
    private final Neuron nextNeuron;
    
    
    //--------------------------------Constructors--------------------------------//
    
    
    
    
    
    /**
     * Creates a connection of two neurons based of the flow of data between them.
     *
     * @param inPrevNeuron is the previous neuron in the flow of data.
     * @param inNextNeuron is the next neuron in the flow of data.
     */
    public Connection(Neuron inPrevNeuron, Neuron inNextNeuron)
    {
        prevNeuron = inPrevNeuron;
        nextNeuron = inNextNeuron;
    }
    
    
    //----------------------------------Helpers-----------------------------------//
    
    
    
    
    
    public void resetWeight()
    {
        weight = gen.nextDouble() * startingWeightRange - (startingWeightRange / 2);
        prevDeltaWeight = 0;
        deltaWeight = 0;
    }
    
    
    //-----------------------------Getters & Setters------------------------------//
    
    
    
    
    
    public double getWeight()
    {
        return weight;
    }
    
    
    
    
    
    public void setWeight(double inWeight)
    {
        weight = inWeight;
    }
    
    
    
    
    
    public double getPrevDeltaWeight()
    {
        return prevDeltaWeight;
    }
    
    
    
    
    
    public void setPrevDeltaWeight(double inPrevDeltaWeight)
    {
        prevDeltaWeight = inPrevDeltaWeight;
    }
    
    
    
    
    
    public double getDeltaWeight()
    {
        return deltaWeight;
    }
    
    
    
    
    
    public void setDeltaWeight(double inDeltaWeight)
    {
        deltaWeight = inDeltaWeight;
    }
    
    
    
    
    
    public Neuron getPrevNeuron()
    {
        return prevNeuron;
    }
    
    
    
    
    
    public Neuron getNextNeuron()
    {
        return nextNeuron;
    }
    
    
    
    
    
    public static Random getGen()
    {
        return gen;
    }
    
    
    
    
    
    public static void setGen(Random inGen)
    {
        gen = inGen;
    }
    
    
    
    
    
    public static double getStartingWeightRange()
    {
        return startingWeightRange;
    }
    
    
    
    
    
    public static void setStartingWeightRange(double inStartingWeightRange)
    {
        startingWeightRange = inStartingWeightRange;
    }
    
    
    
    
    
    public static long getRandSeed()
    {
        return randSeed;
    }
    
    
    
    
    
    public static void setRandSeed(long inRandSeed)
    {
        randSeed = inRandSeed;
    }
}
