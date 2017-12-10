package edu.packt.neuralnet.examples.chapter06;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

import javax.swing.JPanel;
import javax.swing.JTextPane;
import javax.swing.text.StyledDocument;

import edu.packt.neuralnet.NeuralException;
import edu.packt.neuralnet.NeuralNet;
import edu.packt.neuralnet.data.DataNormalization;
import edu.packt.neuralnet.data.DataSet;
import edu.packt.neuralnet.data.NeuralDataSet;
import edu.packt.neuralnet.init.UniformInitialization;
import edu.packt.neuralnet.learn.Backpropagation;
import edu.packt.neuralnet.learn.LearningAlgorithm;
import edu.packt.neuralnet.math.ArrayOperations;
import edu.packt.neuralnet.math.HyperTan;
import edu.packt.neuralnet.math.IActivationFunction;
import edu.packt.neuralnet.math.Linear;
import edu.packt.neuralnet.math.RandomNumberGenerator;
import edu.packt.neuralnet.math.Sigmoid;

/**
 *
 * DiagnosisExample This class performs use cases described in chapter 6 of the
 * book: breast cancer and diabetes data. Data are loaded, data normalization is
 * done, neural net is created using parameters defined, Backpropagation
 * algorithm is used to make neural net learn and charts are plotted
 * 
 * @authors Alan de Souza, FÃ¡bio Soares
 * @version 0.1
 * 
 */
public class DiagnosisExample {

	private JTextPane txtRezultat;

	public JTextPane getTxtRezultat() {
		return txtRezultat;
	}

	public void setTxtRezultat(JTextPane txtRezultat) {
		this.txtRezultat = txtRezultat;
	}
	
    private JPanel panelErrorEvolution;
    
    public JPanel getPanelErrorEvolution() {
		return panelErrorEvolution;
	}

	public void setPanelErrorEvolution(JPanel panelErrorEvolution) {
		this.panelErrorEvolution = panelErrorEvolution;
	}

	public static void main(String[] args) {

		
	}

	public void pokreniTest(String CHOSEN_OPTION, int typedEpochs, int typedNumHdnLayer, double typedLearningRate,
			int numberOfInputs, int numberOfOutputs, int[] inputColumns, int[] outputColumns, int brEksperimenta,
			DataSet dataSet) {

		try {
			StyledDocument document = (StyledDocument) txtRezultat.getDocument();

			document.insertString(document.getLength(),
					"*** EKSPERIMENT #" + brEksperimenta + " ***\nBroj epoha: " + typedEpochs, null);
			document.insertString(document.getLength(), "\nBroj neurona u skrivenom sloju: " + typedNumHdnLayer, null);
			document.insertString(document.getLength(), "\nStopa učenja: " + typedLearningRate, null);
			document.insertString(document.getLength(), "\nIzabran je set podataka za: " + CHOSEN_OPTION + "\n", null);
			int[] numberOfHiddenNeurons = { typedNumHdnLayer };

			// HyperTan hl0Fnc = new HyperTan(1.0);
			Sigmoid hl0Fnc = new Sigmoid(1.0);
			Linear outputAcFnc = new Linear(1.0);

			IActivationFunction[] hiddenAcFnc = { hl0Fnc };

			document.insertString(document.getLength(), "\nKreiranje neuronske mreže...", null);
			NeuralNet nn = new NeuralNet(numberOfInputs, numberOfOutputs, numberOfHiddenNeurons, hiddenAcFnc,
					outputAcFnc, new UniformInitialization(-1.0, 1.0));
			document.insertString(document.getLength(), "\nNeuronska mreža je kreirana.\n", null);
			// nn.print();

			// load data
			double[][] _neuralDataSet = dataSet.getData();

			// normalize data
			DataNormalization dn = new DataNormalization(-1.0, 1.0);
			double[][] dataNormalized = new double[_neuralDataSet.length][_neuralDataSet[0].length];
			dataNormalized = dn.normalize(_neuralDataSet);

			double[][] dataNormalizedToTrain = Arrays.copyOfRange(dataNormalized, 0,
					(int) Math.ceil(dataNormalized.length * (0.8)));
			double[][] dataNormalizedToTest = Arrays.copyOfRange(dataNormalized,
					(int) Math.ceil(dataNormalized.length * (0.8)) + 1, dataNormalized.length);

			// normalized data to train ANN:
			NeuralDataSet neuralDataSetToTrain = new NeuralDataSet(dataNormalizedToTrain, inputColumns, outputColumns);

			// normalized data to test ANN:
			NeuralDataSet neuralDataSetToTest = new NeuralDataSet(dataNormalizedToTest, inputColumns, outputColumns);

			// System.out.println("Dataset to train created");
			// neuralDataSetToTrain.printInput();
			// neuralDataSetToTrain.printTargetOutput();

			document.insertString(document.getLength(), "\nPrikaz rezultata neuronske mreže.\n", null); // Getting the
																										// first
																										// output
																										// of
																										// the
																										// neural
																										// network

			// create ANN and define parameters to TRAIN:
			Backpropagation backprop = new Backpropagation(nn, neuralDataSetToTrain,
					LearningAlgorithm.LearningMode.BATCH);
			backprop.setLearningRate(typedLearningRate);
			backprop.setMaxEpochs(typedEpochs);
			backprop.setGeneralErrorMeasurement(Backpropagation.ErrorMeasurement.SimpleError);
			backprop.setOverallErrorMeasurement(Backpropagation.ErrorMeasurement.MSE);
			backprop.setMinOverallError(0.001);
			backprop.setMomentumRate(0.7);
			backprop.setTestingDataSet(neuralDataSetToTest);
			backprop.printTraining = true;
			backprop.showPlotError = true;
			backprop.setPanelErrorEvolution(panelErrorEvolution);
			
			// train ANN:
			try {
				backprop.forward();
				// neuralDataSetToTrain.printNeuralOutput();

				backprop.train(document);
				document.insertString(document.getLength(), "\nKraj treninga.", null);
				if (backprop.getMinOverallError() >= backprop.getOverallGeneralError()) {
					document.insertString(document.getLength(), "\nUspješno izvršen trening!", null);
					
				} else {
					document.insertString(document.getLength(), "\nNeuspješno izvršen trening!\n", null);
				
				}

				document.insertString(document.getLength(),
						"\nUkupna greška: " + String.valueOf(backprop.getOverallGeneralError()), null); // Overall
																										// Error
				document.insertString(document.getLength(),
						"\nMinimalna greška: " + String.valueOf(backprop.getMinOverallError()), null); // Min
																										// Overall
																										// Error
																										// -
																										// najniza
				document.insertString(document.getLength(),
						"\nEpoha obučavanja: " + String.valueOf(backprop.getEpoch()), null); // Epochs
																								// of
																								// training

				// System.out.println("Target Outputs (TRAIN):");
				// neuralDataSetToTrain.printTargetOutput();

				// System.out.println("Neural Output after training:");
				// backprop.forward();
				// neuralDataSetToTrain.printNeuralOutput();
			} catch (NeuralException ne) {
				ne.printStackTrace();
			}

			// System.out.println("Dataset to test created");
			// neuralDataSetToTest.printInput();
			// neuralDataSetToTest.printTargetOutput();

			try {

				backprop.test();// forward();

				// neuralDataSetToTest.printNeuralOutput();

				// System.out.println("Target Outputs (TEST):");
				// neuralDataSetToTest.printTargetOutput();

				ArrayList<ArrayList<Double>> listOutputValues = neuralDataSetToTest.getArrayNeuralOutputData();

				// denormalize test data
				double[][] dataNormalizedOutputTest = extractMatrixByArrayList(outputColumns, dataNormalizedToTest,
						listOutputValues);

				double[] dataDenormalizedOutputTest1 = dn
						.denormalize(ArrayOperations.getColumn(dataNormalizedOutputTest, 0), outputColumns[0]);
				double[] dataDenormalizedOutputTest2 = dn
						.denormalize(ArrayOperations.getColumn(dataNormalizedOutputTest, 1), outputColumns[1]);

				// adapt data (test):
				double[][] dataOutputTestAdapted = adaptData(outputColumns, dataNormalizedToTest,
						dataDenormalizedOutputTest1, dataDenormalizedOutputTest2);

				// target output:
				ArrayList<ArrayList<Double>> listTargetValues = neuralDataSetToTest.getArrayTargetOutputData();

				double[][] dataTargetOutputTest = extractMatrixByArrayList(outputColumns, dataNormalizedToTest,
						listTargetValues);

				double[] dataDenormalizedTargetOutputTest1 = dn
						.denormalize(ArrayOperations.getColumn(dataTargetOutputTest, 0), outputColumns[0]);
				double[] dataDenormalizedTargetOutputTest2 = dn
						.denormalize(ArrayOperations.getColumn(dataTargetOutputTest, 1), outputColumns[1]);

				// adapt data (target):
				double[][] dataOutputTargetTestAdapted = adaptData(outputColumns, dataNormalizedToTest,
						dataDenormalizedTargetOutputTest1, dataDenormalizedTargetOutputTest2);

				// calculate confusion matrix:
				double[][] confusionMatrix = neuralDataSetToTest.outputData
						.calculateConfusionMatrix(dataOutputTestAdapted, dataOutputTargetTestAdapted);

				document.insertString(document.getLength(), "\n\n\n### Rezultat za " + CHOSEN_OPTION + " ###", null);

				document.insertString(document.getLength(), "\n\n### Matrica konfuzije ###\n", null);
				document.insertString(document.getLength(),
						Arrays.deepToString(confusionMatrix).replaceAll("],", "]\n"), null);

				document.insertString(document.getLength(), "\n", null);
				// calculate another performance measures
				neuralDataSetToTest.outputData.calculatePerformanceMeasures(confusionMatrix, document);

				document.insertString(document.getLength(), "\n\n\n", null);

			} catch (Exception e) {
				e.printStackTrace();
			}

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}

	}
	
	public boolean pokreniTest2(String CHOSEN_OPTION, int typedEpochs, int typedNumHdnLayer, double typedLearningRate,
			int numberOfInputs, int numberOfOutputs, int[] inputColumns, int[] outputColumns, int brEksperimenta,
			DataSet dataSet) {
		boolean uspjesno = false;
		try {
			
			StyledDocument document = (StyledDocument) txtRezultat.getDocument();

			document.insertString(document.getLength(),
					"*** EKSPERIMENT #" + brEksperimenta + " ***\nBroj epoha: " + typedEpochs, null);
			document.insertString(document.getLength(), "\nBroj neurona u skrivenom sloju: " + typedNumHdnLayer, null);
			document.insertString(document.getLength(), "\nStopa učenja: " + typedLearningRate, null);
			document.insertString(document.getLength(), "\nIzabran je set podataka za: " + CHOSEN_OPTION + "\n", null);
			int[] numberOfHiddenNeurons = { typedNumHdnLayer };

			// HyperTan hl0Fnc = new HyperTan(1.0);
			Sigmoid hl0Fnc = new Sigmoid(1.0);
			Linear outputAcFnc = new Linear(1.0);

			IActivationFunction[] hiddenAcFnc = { hl0Fnc };

			document.insertString(document.getLength(), "\nKreiranje neuronske mreže...", null);
			NeuralNet nn = new NeuralNet(numberOfInputs, numberOfOutputs, numberOfHiddenNeurons, hiddenAcFnc,
					outputAcFnc, new UniformInitialization(-1.0, 1.0));
			document.insertString(document.getLength(), "\nNeuronska mreža je kreirana.\n", null);
			// nn.print();

			// load data
			double[][] _neuralDataSet = dataSet.getData();

			// normalize data
			DataNormalization dn = new DataNormalization(-1.0, 1.0);
			double[][] dataNormalized = new double[_neuralDataSet.length][_neuralDataSet[0].length];
			dataNormalized = dn.normalize(_neuralDataSet);

			double[][] dataNormalizedToTrain = Arrays.copyOfRange(dataNormalized, 0,
					(int) Math.ceil(dataNormalized.length * (0.8)));
			double[][] dataNormalizedToTest = Arrays.copyOfRange(dataNormalized,
					(int) Math.ceil(dataNormalized.length * (0.8)) + 1, dataNormalized.length);

			// normalized data to train ANN:
			NeuralDataSet neuralDataSetToTrain = new NeuralDataSet(dataNormalizedToTrain, inputColumns, outputColumns);

			// normalized data to test ANN:
			NeuralDataSet neuralDataSetToTest = new NeuralDataSet(dataNormalizedToTest, inputColumns, outputColumns);

			// System.out.println("Dataset to train created");
			// neuralDataSetToTrain.printInput();
			// neuralDataSetToTrain.printTargetOutput();

			document.insertString(document.getLength(), "\nPrikaz rezultata neuronske mreže.\n", null); // Getting the
																										// first
																										// output
																										// of
																										// the
																										// neural
																										// network

			// create ANN and define parameters to TRAIN:
			Backpropagation backprop = new Backpropagation(nn, neuralDataSetToTrain,
					LearningAlgorithm.LearningMode.BATCH);
			backprop.setLearningRate(typedLearningRate);
			backprop.setMaxEpochs(typedEpochs);
			backprop.setGeneralErrorMeasurement(Backpropagation.ErrorMeasurement.SimpleError);
			backprop.setOverallErrorMeasurement(Backpropagation.ErrorMeasurement.MSE);
			backprop.setMinOverallError(0.001);
			backprop.setMomentumRate(0.7);
			backprop.setTestingDataSet(neuralDataSetToTest);
			backprop.printTraining = true;
			backprop.showPlotError = true;
			backprop.setPanelErrorEvolution(panelErrorEvolution);
			
			// train ANN:
			try {
				backprop.forward();
				// neuralDataSetToTrain.printNeuralOutput();

				backprop.train(document);
				document.insertString(document.getLength(), "\nKraj treninga.", null);
				if (backprop.getMinOverallError() >= backprop.getOverallGeneralError()) {
					document.insertString(document.getLength(), "\nUspješno izvršen trening!", null);
					uspjesno = true;
				} else {
					document.insertString(document.getLength(), "\nNeuspješno izvršen trening!\n", null);
					uspjesno = false;
				}

				document.insertString(document.getLength(),
						"\nUkupna greška: " + String.valueOf(backprop.getOverallGeneralError()), null); // Overall
																										// Error
				document.insertString(document.getLength(),
						"\nMinimalna greška: " + String.valueOf(backprop.getMinOverallError()), null); // Min
																										// Overall
																										// Error
																										// -
																										// najniza
				document.insertString(document.getLength(),
						"\nEpoha obučavanja: " + String.valueOf(backprop.getEpoch()), null); // Epochs
																								// of
																								// training

				// System.out.println("Target Outputs (TRAIN):");
				// neuralDataSetToTrain.printTargetOutput();

				// System.out.println("Neural Output after training:");
				// backprop.forward();
				// neuralDataSetToTrain.printNeuralOutput();
			} catch (NeuralException ne) {
				ne.printStackTrace();
			}

			// System.out.println("Dataset to test created");
			// neuralDataSetToTest.printInput();
			// neuralDataSetToTest.printTargetOutput();

			try {

				backprop.test();// forward();

				// neuralDataSetToTest.printNeuralOutput();

				// System.out.println("Target Outputs (TEST):");
				// neuralDataSetToTest.printTargetOutput();

				ArrayList<ArrayList<Double>> listOutputValues = neuralDataSetToTest.getArrayNeuralOutputData();

				// denormalize test data
				double[][] dataNormalizedOutputTest = extractMatrixByArrayList(outputColumns, dataNormalizedToTest,
						listOutputValues);

				double[] dataDenormalizedOutputTest1 = dn
						.denormalize(ArrayOperations.getColumn(dataNormalizedOutputTest, 0), outputColumns[0]);
				double[] dataDenormalizedOutputTest2 = dn
						.denormalize(ArrayOperations.getColumn(dataNormalizedOutputTest, 1), outputColumns[1]);

				// adapt data (test):
				double[][] dataOutputTestAdapted = adaptData(outputColumns, dataNormalizedToTest,
						dataDenormalizedOutputTest1, dataDenormalizedOutputTest2);

				// target output:
				ArrayList<ArrayList<Double>> listTargetValues = neuralDataSetToTest.getArrayTargetOutputData();

				double[][] dataTargetOutputTest = extractMatrixByArrayList(outputColumns, dataNormalizedToTest,
						listTargetValues);

				double[] dataDenormalizedTargetOutputTest1 = dn
						.denormalize(ArrayOperations.getColumn(dataTargetOutputTest, 0), outputColumns[0]);
				double[] dataDenormalizedTargetOutputTest2 = dn
						.denormalize(ArrayOperations.getColumn(dataTargetOutputTest, 1), outputColumns[1]);

				// adapt data (target):
				double[][] dataOutputTargetTestAdapted = adaptData(outputColumns, dataNormalizedToTest,
						dataDenormalizedTargetOutputTest1, dataDenormalizedTargetOutputTest2);

				// calculate confusion matrix:
				double[][] confusionMatrix = neuralDataSetToTest.outputData
						.calculateConfusionMatrix(dataOutputTestAdapted, dataOutputTargetTestAdapted);

				document.insertString(document.getLength(), "\n\n\n### Rezultat za " + CHOSEN_OPTION + " ###", null);

				document.insertString(document.getLength(), "\n\n### Matrica konfuzije ###\n", null);
				document.insertString(document.getLength(),
						Arrays.deepToString(confusionMatrix).replaceAll("],", "]\n"), null);

				document.insertString(document.getLength(), "\n", null);
				// calculate another performance measures
				neuralDataSetToTest.outputData.calculatePerformanceMeasures(confusionMatrix, document);

				document.insertString(document.getLength(), "\n\n\n", null);

				return uspjesno;
				
			} catch (Exception e) {
				e.printStackTrace();
			}

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}

		return uspjesno;
		

	}

	private static double[][] extractMatrixByArrayList(int[] outputColumns, double[][] data,
			ArrayList<ArrayList<Double>> list) {
		double[][] matrix = new double[data.length][outputColumns.length];
		int i = 0, j = 0;
		for (ArrayList<Double> arrayList : list) {
			for (Double value : arrayList) {
				matrix[i][j] = value;
				j++;
			}
			i++;
			j = 0;
		}
		return matrix;
	}

	private static double[][] adaptData(int[] outputColumns, double[][] data, double[] dataOutput1,
			double[] dataOutput2) {
		double[][] matrix = new double[data.length][outputColumns.length];
		for (int k = 0; k < dataOutput1.length; k++) {
			double v1 = dataOutput1[k];
			double v2 = dataOutput2[k];
			if (v1 <= 0.50) {
				matrix[k][0] = 0.0;
			} else {
				matrix[k][0] = 1.0;
			}
			if (v2 <= 0.50) {
				matrix[k][1] = 0.0;
			} else {
				matrix[k][1] = 1.0;
			}
		}
		return matrix;
	}

}
