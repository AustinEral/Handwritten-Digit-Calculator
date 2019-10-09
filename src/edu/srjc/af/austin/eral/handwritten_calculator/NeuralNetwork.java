/*
Austin Eral
austineral@ymail.com
5/24/27
Digit Recognition Final Project
CS 17.11

This neural network is a fixed topology feed-forward neural net providing a backpropagation method
for training. It provides functionality for only one hidden layer.
 */

package edu.srjc.af.austin.eral.handwritten_calculator;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by austi_000 on 4/29/2017.
 */
public class NeuralNetwork
{
    private ArrayList<Neuron> inputs = new ArrayList<>();
    private ArrayList<Neuron> hiddens = new ArrayList<>();
    private ArrayList<Neuron> outputs = new ArrayList<>();
    
    private double learningRate = 0.4;
    private double decay = 1.0;
    private double momentum = 0.1;
    
    
    //--------------------------------Constructors--------------------------------//
    
    
    
    
    
    /**
     * Creates a network with the structure of one input layer, one hidden layer, and one output
     * layer.
     *
     * @param numInputs  is the number of Neurons to be created in the input layer.
     * @param numHiddens is the number of Neurons to be created in the hidden layer.
     * @param numOutputs is the number of Neurons to be created in the output layer.
     */
    public NeuralNetwork(int numInputs, int numHiddens, int numOutputs)
    {
        initializeNetwork(numInputs, numHiddens, numOutputs);
    }
    
    
    //----------------------------------Helpers-----------------------------------//
    
    
    
    
    
    /**
     * Recreates the network structure with a new hidden layer size.
     *
     * @param numHiddens is the number of Neurons to be created in the hidden layer.
     */
    public void resetNumHiddens(int numHiddens)
    {
        int numInputs = inputs.size() - 1;
        int numOutputs = outputs.size();
        inputs = new ArrayList<>();
        hiddens = new ArrayList<>();
        outputs = new ArrayList<>();
        
        initializeNetwork(numInputs, numHiddens, numOutputs);
    }
    
    
    
    
    
    /**
     * Creates a network structure by adding an input layer, a hidden layer, and a output layer
     * of neurons connected by connection objects. A bias neuron is added to the input layer and
     * the hidden layer.
     *
     * @param numInputs  is the number of Neurons to be created in the input layer.
     * @param numHiddens is the number of Neurons to be created in the hidden layer.
     * @param numOutputs is the number of Neurons to be created in the output layer.
     */
    private void initializeNetwork(int numInputs, int numHiddens, int numOutputs)
    {
        for (int count = 0; count < numInputs; count++)
        {
            inputs.add(new Neuron());
        }
        inputs.add(createBiasNeuron());
        
        for (int count = 0; count < numHiddens; count++)
        {
            hiddens.add(new Neuron());
            for (Neuron input : inputs)
            {
                Connection con = new Connection(input, hiddens.get(count));
                input.getNextConnections().add(con);
                hiddens.get(count).getPrevConnections().add(con);
            }
        }
        hiddens.add(createBiasNeuron());
        
        for (int count = 0; count < numOutputs; count++)
        {
            
            outputs.add(new Neuron());
            for (Neuron hidden : hiddens)
            {
                Connection con = new Connection(hidden, outputs.get(count));
                hidden.getNextConnections().add(con);
                outputs.get(count).getPrevConnections().add(con);
            }
        }
    }
    
    
    
    
    
    /**
     * Creates a bias neuron. Bias neurons must not have preceding connections.
     *
     * @return a new Neuron with neural value of 1;
     */
    private static Neuron createBiasNeuron()
    {
        Neuron neuron = new Neuron();
        neuron.setNeuralValue(1.0);
        return neuron;
    }
    
    
    
    
    
    /**
     * Computes all of the network neuron values, but most importantly the output neurons.
     *
     * @param inputValues are new input values to be assigned to the network before computing output neurons.
     * @throws IllegalArgumentException if inputValues size does not match this class's input neurons size.
     */
    public void computeOutputs(ArrayList<Neuron> inputValues)
    {
        if (inputValues.size() != inputs.size())
        {
            throw new IllegalArgumentException("inputValues parameter size does not mach the size of this " +
                "classes inputs.");
        }
        for (int index = 0; index < inputs.size() - 1; index++)
        {
            inputs.get(index).setNeuralValue(inputValues.get(index).getNeuralValue());
        }
        for (Neuron neuron : hiddens)
        {
            neuron.computeNeuralValue();
        }
        for (Neuron neuron : outputs)
        {
            neuron.computeNeuralValue();
        }
    }
    
    
    
    
    
    /**
     * Computes all of the network neuron values, but most importantly the output neurons.
     * Input neuron values should be set before calling this method.
     */
    public void computeOutputs()
    {
        for (Neuron neuron : hiddens)
        {
            neuron.computeNeuralValue();
        }
        for (Neuron neuron : outputs)
        {
            neuron.computeNeuralValue();
        }
    }
    
    
    
    
    
    /**
     * Corrects the connection values in the network so that the next time the output values are
     * calculated with the current input values, they will be closer to the ideal outputs.
     *
     * @param idealOutputs The the outputs the network is supposed to output from the current inputs.
     */
    public void backpropagate(ArrayList<Neuron> idealOutputs)
    {
        int outputsIndex = 0;
        for (Neuron output : outputs)
        {
            for (Connection con : output.getPrevConnections())
            {
                double derRespectsToOutput = output.getNeuralValue() - idealOutputs.get(outputsIndex).getNeuralValue();
                double derRespectsToNet = output.getNeuralValue() * (1.0 - output.getNeuralValue());
                double derRespectsToWeight = con.getPrevNeuron().getNeuralValue();
                double partialDerivative = derRespectsToOutput * derRespectsToNet * derRespectsToWeight;
                
                double deltaWeight = -learningRate * partialDerivative;
                double newWeight = con.getWeight() + deltaWeight;
                con.setPrevDeltaWeight(deltaWeight);
                con.setDeltaWeight(deltaWeight);
                con.setWeight(newWeight + momentum * con.getPrevDeltaWeight());
            }
            outputsIndex++;
        }
        
        // https://kunuk.wordpress.com/2010/10/11/neural-network-backpropagation-with-java/
        // The next for loop was taken from kunk.wordpress.com and modified to fit this class.
        for (Neuron n : hiddens)
        {
            ArrayList<Connection> connections = n.getPrevConnections();
            for (Connection con : connections)
            {
                double aj = n.getNeuralValue();
                double ai = con.prevNeuron.getNeuralValue();
                double sumKoutputs = 0;
                int j = 0;
                int index = 0;
                
                for (Neuron out_neu : outputs)
                {
                    //TODO hidden neurons cant go below outputs
                    double wjk = out_neu.getPrevConnections().get(index).getWeight();
                    double desiredOutput = idealOutputs.get(index).getNeuralValue();
                    double ak = out_neu.getNeuralValue();
                    j++;
                    sumKoutputs = sumKoutputs
                        + (-(desiredOutput - ak) * ak * (1 - ak) * wjk);
                    index++;
                }
                
                double partialDerivative = aj * (1 - aj) * ai * sumKoutputs;
                double deltaWeight = -learningRate * partialDerivative;
                double newWeight = con.getWeight() + deltaWeight;
                con.setPrevDeltaWeight(deltaWeight);
                con.setDeltaWeight(deltaWeight);
                con.setWeight(newWeight + momentum * con.getPrevDeltaWeight());
            }
        }
        momentum = momentum * decay;
    }
    
    
    
    
    
    public void resetWeights()
    {
        Connection.setGen(new Random(Connection.getRandSeed()));
        for (Neuron neuron : hiddens)
        {
            for (Connection con : neuron.getPrevConnections())
            {
                con.resetWeight();
            }
            for (Connection con : neuron.getNextConnections())
            {
                con.resetWeight();
            }
        }
    }
    
    
    
    
    
    @Override
    public String toString()
    {
        StringBuilder inputString = new StringBuilder();
        
        inputString.append("Input Layer: \n\n");
        for (Neuron neuron : inputs)
        {
            inputString.append(neuron);
        }
        
        inputString.append("Hidden Layer: \n\n");
        for (Neuron neuron : hiddens)
        {
            inputString.append(neuron);
        }
        
        inputString.append("Output Layer: \n\n");
        for (Neuron neuron : outputs)
        {
            inputString.append(neuron);
        }
        
        return inputString.toString();
    }
    
    
    //-----------------------------Getters & Setters------------------------------//
    
    
    
    
    
    public ArrayList<Neuron> getInputs()
    {
        return inputs;
    }
    
    
    
    
    
    public ArrayList<Neuron> getHiddens()
    {
        return hiddens;
    }
    
    
    
    
    
    public ArrayList<Neuron> getOutputs()
    {
        return outputs;
    }
    
    
    
    
    
    public double getLearningRate()
    {
        return learningRate;
    }
    
    
    
    
    
    public void setLearningRate(double inLearningRate)
    {
        learningRate = inLearningRate;
    }
    
    
    
    
    
    public double getDecay()
    {
        return decay;
    }
    
    
    
    
    
    public void setDecay(double inDecay)
    {
        decay = inDecay;
    }
    
    
    
    
    
    public double getMomentum()
    {
        return momentum;
    }
    
    
    
    
    
    public void setMomentum(double inMomentum)
    {
        momentum = inMomentum;
    }
}
