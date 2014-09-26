package com.feiyu.deeplearning.RBM;

import java.util.Random;

public class RestrictedBoltzmannMachinesWithSoftmax {
	// movie rating is rated from 0 to 10, therefore the softmax in this model is an 11-way softmax
	private int sizeSoftmax;
	private int numUsers;
	private int numMovies;
	private int sizeHiddenUnits;
	private double[][][] Md; // 3D data matrix, users X movies X softmax (users X movies X rating), m-by-n-by-k
	private double[][][] Mw; // 3D weight matrix, movies X softmax X hidden units, n-by-k-by-l
	
	private Random randomN = new Random();
	
	public RestrictedBoltzmannMachinesWithSoftmax(
			int sizeSoftmax, int numUsers, int numMovies, int sizeHiddenUnits,
			double[][][] data
			) {
		this.sizeSoftmax = sizeSoftmax;
		this.numUsers = numUsers;
		this.numMovies = numMovies;
		this.sizeHiddenUnits = sizeHiddenUnits;
		this.Md = data;
		this.Mw = new double[this.numMovies+2][this.sizeHiddenUnits+1][this.sizeSoftmax];
	}
	
	private void setBiasUnitsToZeros_WeightMatrix() {
		for (int y=0; y<=this.sizeHiddenUnits; y++) {
			for (int z=0; z<this.sizeSoftmax; z++) {
				this.Mw[0][y][z] = 0;
 			}
		}
		
		for (int i=1; i<=this.numMovies; i++) {
			for (int z=0; z<this.sizeSoftmax; z++) {
				this.Mw[i][0][z] = 0;
			}
		}
	}
	
	public void initializeWeightMatrix() {
		this.setBiasUnitsToZeros_WeightMatrix();
		for (int i=1; i<=this.numMovies; i++) {
			for (int y=1; y<=this.sizeHiddenUnits; y++) {
				for (int z=0; z<this.sizeSoftmax; z++) {
					this.Mw[i][y][z] = 0.1 * this.randomN.nextGaussian(); 
					//with mean 0.0 and standard deviation 0.1 
				}
			}
		}
	}
	
	public void printWeightMatrix() {
		for (int i=0; i<=this.numMovies; i++) {
			for (int y=0; y<=this.sizeHiddenUnits; y++) {
				for (int z=0; z<this.sizeSoftmax; z++) {
					System.out.print(" "+this.Mw[i][y][z]); 
					//with mean 0.0 and standard deviation 0.1 
				}
				System.out.println(" softmax "); 
			}
			System.out.println(" layer "); 
		}
	}
	
	public void updateTheDataMatrix_oneUserAtATime() {
		
	}
	
	public static void main(String[] argv) {
		int sizeSoftmax = 2;
		int numUsers = 6;
		int numMovies = 6;
		int sizeHiddenUnits = 2;
		double[][][] data = new double[numUsers+1][numMovies+2][sizeSoftmax]; // one layer in the 3D data matrix is for bias unit
		
		RestrictedBoltzmannMachinesWithSoftmax rbmSoftmax = new RestrictedBoltzmannMachinesWithSoftmax(
				sizeSoftmax, numUsers, numMovies, sizeHiddenUnits,
				data
				);
		rbmSoftmax.initializeWeightMatrix();
		rbmSoftmax.printWeightMatrix();
	}

}
