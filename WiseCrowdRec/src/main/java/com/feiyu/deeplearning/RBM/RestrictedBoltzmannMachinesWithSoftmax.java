package com.feiyu.deeplearning.RBM;

import java.util.ArrayList;
import java.util.Random;

import com.feiyu.springmvc.model.Tuple;

public class RestrictedBoltzmannMachinesWithSoftmax {
	// movie rating is rated from 0 to 10, therefore the softmax in this model is an 11-way softmax
	private int numMovies;
	private int sizeSoftmax;
	private int sizeHiddenUnits;
	private double learningRate;
	private int numSteps; 

	private double[][][] Mw; // 3D weight matrix, movies X hidden units X softmax , n-by-l-by-k
	private double[][][] MwT; // transpose weight matrix, hidden units X movies X softmax , l-by-n-by-k
	private double[][][] Md; // 3D data matrix, users X movies X softmax (users X movies X rating), m-by-n-by-k
	private double[][][] MdT; // transpose data matrix, movies X users X softmax , n-by-1-by-k

	private double[][][] Mpha; // 3D positive hidden associations matrix, oneUser X hidden X softmax units, 1-by-l-by-k
	private double[][][] Mphp; // 3D positive hidden probabilities matrix, oneUser X hidden units X softmax, 1-by-l-by-k; Mpha->p(u(i)s(β)h(j))->Mphp
	private double[][][] Mphs; // 3D positive hidden states matrix, oneUser X hidden units X softmax, 1-by-l-by-k; only one unit in a softmax is 1, the rest are all zeros
	
	private double[][][] Mwpos; // positive 3D weight matrix, movies X hidden units X softmax , n-by-l-by-k
	private double[][][] Mwneg; // negative 3D weight matrix, movies X hidden units X softmax , n-by-l-by-k
	
	private double[][][] Mnva; // 3D negative visible associations matrix, oneUser X movies X softmax, 1-by-n-by-k
	private double[][][] Mnvp; // 3D negative visible probabilities matrix, oneUserX movies X softmax , 1-by-n-by-k; Mnva->p(u(i)s(β)h(j))->Mnvp
	                           // "When hidden units are being driven by reconstructions, always use probabilities without sampling"
							   // from "A Practical Guide to Training Restricted Boltzmann Machines " by Geoffrey Hinton
							   // https://www.cs.toronto.edu/~hinton/absps/guideTR.pdf
	private double[][][] MnvpT; // transpose negative visible probabilities matrix, movies X oneUser X softmax , n-by-1-by-k; 
	
	private double[][][] Mnha; // 3D negative hidden associations matrix, oneUser X hidden X softmax units, 1-by-l-by-k
	private double[][][] Mnhp; // 3D negative hidden probabilities matrix, oneUser X hidden units X softmax, 1-by-l-by-k; Mnha->p(u(i)s(β)h(j))->Mnhp

	private Random randomN = new Random();

	public RestrictedBoltzmannMachinesWithSoftmax(
			int numMovies, int sizeSoftmax, int sizeHiddenUnits, double learningRate, int numSteps
			) {
		this.numMovies = numMovies;
		this.sizeSoftmax = sizeSoftmax;
		this.sizeHiddenUnits = sizeHiddenUnits;
		this.learningRate = learningRate;
		this.numSteps = numSteps;
		this.initializeWeightMatrix();
	}
	
	//////////////////////
	public void trainRBMWeightMatrix(ArrayList<Tuple<Integer,Integer>> ratedMoviesIndices){
		System.out.println("\n----------------------------\n----------------------------\nNew Person .. ");
		this.updateTheDataMatrix_oneUser(ratedMoviesIndices);
		System.out.println("\n Recent 3D Weight Matrix");
		this.printMatrix(this.numMovies+1, this.sizeHiddenUnits+1, this.sizeSoftmax, "weightMatrix");
		
		for (int i=1; i<=this.numSteps; i++){ 
			System.out.println("\n-------\nStep: "+i);
			// positive Contrastive Divergence(CD) phase
			this.getPhaMatrixOrNhaMatrix_oneUser(true);
			this.getPhpMatrixAndThePhsMatrixOrNhpMatrix_oneUser(true);
			this.getPositiveOrNegativeWeightMatrix_ForNextStep(true);
			
			// negative CD phase
			this.getTheNvaMatrix_oneUser();
			this.getTheNvpMatrix_oneUser();
			this.getPhaMatrixOrNhaMatrix_oneUser(false);
			this.getPhpMatrixAndThePhsMatrixOrNhpMatrix_oneUser(false);
			this.getPositiveOrNegativeWeightMatrix_ForNextStep(false);
			
			// get the weight matrix of next step
			this.getWeightMatrix_ForNextStep();
		}
	}
	
	public void predictUserPreference() {
		
	}

	//////////////////////
	private void initializeWeightMatrix() {
		this.Mw = new double[this.numMovies+1][this.sizeHiddenUnits+1][this.sizeSoftmax];
		this.setBiasUnitsToZeros_WeightMatrix();
		for (int i=1; i<=this.numMovies; i++) {
			for (int y=1; y<=this.sizeHiddenUnits; y++) {
				for (int z=0; z<this.sizeSoftmax; z++) {
					this.Mw[i][y][z] = 0.1 * this.randomN.nextGaussian(); 
					//with mean 0.0 and standard deviation 0.1 
				}
			}
		}

		System.out.println("\n Initialized 3D Weight Matrix");
		this.printMatrix(this.numMovies+1, this.sizeHiddenUnits+1, this.sizeSoftmax, "weightMatrix");
	}	

	private void updateTheDataMatrix_oneUser(ArrayList<Tuple<Integer,Integer>> ratedMoviesIndices) {
		this.Md = new double[1][this.numMovies+1][this.sizeSoftmax]; // one layer in the 3D data matrix is for bias unit
		int sizeOfRatedMovies = ratedMoviesIndices.size();
		for (int i=0; i<sizeOfRatedMovies; i++) {
			this.Md[0][ratedMoviesIndices.get(i).getFirst()][ratedMoviesIndices.get(i).getSecond()] = 1;
		}
		this.setBiasUnitsToOnes_DataMatrix(true);

		System.out.println("\n3D Data Matrix -> One User");
		this.printMatrix(1, this.numMovies+1, this.sizeSoftmax, "dataMatrix");
	}

	//////////////////////
	private void getPhaMatrixOrNhaMatrix_oneUser(boolean isPositiveCD) {
		if (isPositiveCD) {
			this.Mpha = new double[1][this.sizeHiddenUnits+1][this.sizeSoftmax];
		} else {
			this.Mnha = new double[1][this.sizeHiddenUnits+1][this.sizeSoftmax];
		}
		double temp;
		for (int y=0; y<this.sizeHiddenUnits+1; y++) {
			for (int z=0; z<this.sizeSoftmax; z++) {
				temp = 0;
				for (int i=0; i<=this.numMovies; i++) {
					if (isPositiveCD) {
						temp += this.Md[0][i][z]*this.Mw[i][y][z]; 
						//System.out.println(0+" "+i+" "+z+" "+this.Md[0][i][z]+"--"+i+" "+y+" "+z+" "+this.Mw[i][y][z]+"--"+this.Md[0][i][z]*this.Mw[i][y][z]+" "+temp);
					} else {
						temp += this.Mnvp[0][i][z]*this.Mw[i][y][z]; 
					}
				}
				if (isPositiveCD) {
					this.Mpha[0][y][z] = temp;
				} else {
					this.Mnha[0][y][z] = temp;
				}
			}
		}

		if (isPositiveCD) {
			System.out.println("\n3D positive hidden associations matrix -> One User");
			this.printMatrix(1, this.sizeHiddenUnits+1, this.sizeSoftmax, "phaMatrix");
		} else {
			System.out.println("\n3D negative hidden associations matrix -> One User");
			this.printMatrix(1, this.sizeHiddenUnits+1, this.sizeSoftmax, "nhaMatrix");
		}
	}

	private void getPhpMatrixAndThePhsMatrixOrNhpMatrix_oneUser(boolean isPositiveCD) {
		if (isPositiveCD) {
			//Mpha->p(u(i)s(β)h(j))->Mphp
			// Get the Mphp and Mphs matrices at the same time
			// for the Mphs matrix, only one unit in a softmax is 1, the rest are all zeros
			// therefore in a softmax only unit with the largest probability will be set as 1, the rest are all zeros
			this.Mphp = new double[1][this.sizeHiddenUnits+1][this.sizeSoftmax];
			this.Mphs = new double[1][this.sizeHiddenUnits+1][this.sizeSoftmax];
		} else {
			this.Mnhp = new double[1][this.sizeHiddenUnits+1][this.sizeSoftmax];
		}
		double currentSumOfSoftmax, maxProb, curProb;
		int maxProbIdx;
		
		for (int y=0; y<this.sizeHiddenUnits+1; y++) {
			currentSumOfSoftmax = 0; 
			maxProb = -1; 
			curProb = -1;
			maxProbIdx=-1;
			for (int z=0; z<this.sizeSoftmax; z++) {
				if (isPositiveCD) {
					curProb = Math.exp(this.Mpha[0][y][z]); 
					if (curProb > maxProb) {  //@ Special cases: what if every unit in a softmax has the same probability? e.g: 0.25, 0.25, 0.25, 0.25 in a 4-way softmax
						maxProb = curProb;
						maxProbIdx = z;
					}
				} else {
					curProb = Math.exp(this.Mnha[0][y][z]); 
				}
				currentSumOfSoftmax += curProb;
			}
			for (int z=0; z<this.sizeSoftmax; z++) {
//				System.out.println(0+" "+y+" "+z+" "+Math.pow(Math.E, this.Mpha[0][y][z])+" "+currentSumOfSoftmax);
				if (isPositiveCD) {
					this.Mphp[0][y][z] = Math.exp(this.Mpha[0][y][z]) / currentSumOfSoftmax;
					if (z == maxProbIdx) {
						this.Mphs[0][y][z] = 1;
					}
				} else {
					this.Mnhp[0][y][z] = Math.exp(this.Mnha[0][y][z]) / currentSumOfSoftmax;
				}
			}
		}
		
		if (isPositiveCD) {
			System.out.println("\n3D positive hidden probabilities matrix -> One User");
			this.printMatrix(1, this.sizeHiddenUnits+1, this.sizeSoftmax, "phpMatrix");
			System.out.println("\n3D positive hidden states matrix -> One User");
			this.printMatrix(1, this.sizeHiddenUnits+1, this.sizeSoftmax, "phsMatrix");
		} else {
			System.out.println("\n3D negative hidden probabilities matrix -> One User");
			this.printMatrix(1, this.sizeHiddenUnits+1, this.sizeSoftmax, "nhpMatrix");
		}
	}
	
	private void getPositiveOrNegativeWeightMatrix_ForNextStep(boolean isPositiveCD) {
		if (isPositiveCD) {
			this.transposeDataMatrix(true);
			this.Mwpos = new double[this.numMovies+1][this.sizeHiddenUnits+1][this.sizeSoftmax];
		} else {
			this.transposeDataMatrix(false);
			this.Mwneg = new double[this.numMovies+1][this.sizeHiddenUnits+1][this.sizeSoftmax];
		}
		
		for (int x=0; x<this.numMovies+1; x++) {
			for (int y=0; y<this.sizeHiddenUnits+1; y++) {
				for (int z=0; z<this.sizeSoftmax; z++) {
					if (isPositiveCD) {
						this.Mwpos[x][y][z] = this.MdT[x][0][z]*this.Mphp[0][y][z]; 
					} else {
						this.Mwneg[x][y][z] = this.MnvpT[x][0][z]*this.Mnhp[0][y][z]; 
					}
				}
			}
		}

		if (isPositiveCD) { 
			System.out.println("\n positive Weight Matrix --> next iteration");
			this.printMatrix(this.numMovies+1, this.sizeHiddenUnits+1, this.sizeSoftmax, "weightMatrix_positive");
		} else {
			System.out.println("\n negative Weight Matrix --> next iteration");
			this.printMatrix(this.numMovies+1, this.sizeHiddenUnits+1, this.sizeSoftmax, "weightMatrix_negative");
		}
	}
	
	private void getTheNvaMatrix_oneUser() {
		this.transposeWeightMatrix();
		this.Mnva = new double[1][this.numMovies+1][this.sizeSoftmax];
		double productSum;
		for (int y=0; y<this.numMovies+1; y++) {
			for (int z=0; z<this.sizeSoftmax; z++) {
				productSum = 0;
				for (int i=0; i<this.sizeHiddenUnits+1; i++) {
					productSum += this.Mphs[0][i][z]*this.MwT[i][y][z]; 
				}
				this.Mnva[0][y][z] = productSum; 
			}
		}
		
		System.out.println("\n3D Negtive visible activations Matrix -> One User");
		this.printMatrix(1, this.numMovies+1, this.sizeSoftmax, "nvaMatrix");
	}
	
	private void getTheNvpMatrix_oneUser() {
		//Mnva->p(u(i)s(β)h(j))->Mnvp
		this.Mnvp = new double[1][this.numMovies+1][this.sizeSoftmax];
		double currentSumOfSoftmax;
		
		for (int y=0; y<this.numMovies+1; y++) {
			currentSumOfSoftmax = 0; 
			for (int z=0; z<this.sizeSoftmax; z++) {
				currentSumOfSoftmax += Math.exp(this.Mnva[0][y][z]);
			}
			for (int z=0; z<this.sizeSoftmax; z++) {
				this.Mnvp[0][y][z] = Math.exp(this.Mnva[0][y][z]) / currentSumOfSoftmax;
			}
		}
		
		System.out.println("\n3D negative visible probabilities matrix -> One User");
		this.printMatrix(1, this.numMovies+1, this.sizeSoftmax, "nvpMatrix");
		
		this.setBiasUnitsToOnes_DataMatrix(false);
		System.out.println("\n3D negative visible probabilities matrix (fixed bias units) -> One User");
		this.printMatrix(1, this.numMovies+1, this.sizeSoftmax, "nvpMatrix");
	}
	
	private void getWeightMatrix_ForNextStep() {
		for (int i=0; i<this.numMovies+1; i++) {
			for (int y=0; y<this.sizeHiddenUnits+1; y++) {
				for (int z=0; z<this.sizeSoftmax; z++) {
					this.Mw[i][y][z] += this.learningRate * (this.Mwpos[i][y][z]-this.Mwneg[i][y][z]); 
				}
			}
		}

		System.out.println("\n New Weight Matrix for the next step");
		this.printMatrix(this.numMovies+1, this.sizeHiddenUnits+1, this.sizeSoftmax, "weightMatrix");
	}
	
	//////////////////////
	private void setBiasUnitsToZeros_WeightMatrix() {
		for (int y=0; y<this.sizeHiddenUnits+1; y++) {
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

	private void setBiasUnitsToOnes_DataMatrix(boolean isForPositivePhase) {
		for (int i=0; i<this.sizeSoftmax; i++) {
			if (isForPositivePhase) {
				this.Md[0][0][i] = 1;
			} else {
				this.Mnvp[0][0][i] = 1;
			}
		}
	}
	
	private void transposeDataMatrix(boolean isPositiveCD) {
		if (isPositiveCD) {
			this.MdT = new double[this.numMovies+1][1][this.sizeSoftmax]; 
		} else {
			this.MnvpT = new double[this.numMovies+1][1][this.sizeSoftmax]; 
		}
		for (int x=0; x<this.numMovies+1; x++) {
			for(int z=0; z<this.sizeSoftmax; z++) {
				if (isPositiveCD) {
					this.MdT[x][0][z]=this.Md[0][x][z];
				} else {
					this.MnvpT[x][0][z]=this.Mnvp[0][x][z];
				}
			}
		}
		
		if (isPositiveCD) {
			System.out.println("\n Transpose matrix of positive Data Matrix Md -> One User");
			this.printMatrix(this.numMovies+1, 1, this.sizeSoftmax, "transposeDataMatrix_Positive");
		} else {
			System.out.println("\n Transpose matrix of negative Data Matrix Mnvp -> One User");
			this.printMatrix(this.numMovies+1, 1, this.sizeSoftmax, "transposeDataMatrix_Negative");
		}
	}

	private void transposeWeightMatrix() {
		this.MwT = new double[this.sizeHiddenUnits+1][this.numMovies+1][this.sizeSoftmax]; 
		for (int x=0; x<this.sizeHiddenUnits+1; x++) {
			for (int y=0; y<this.numMovies+1; y++) {
				for(int z=0; z<this.sizeSoftmax; z++) {
					this.MwT[x][y][z]=this.Mw[y][x][z];
				}
			}
		}
		
		System.out.println("\n Transpose matrix of Weight Matrix");
		this.printMatrix(this.sizeHiddenUnits+1, this.numMovies+1, this.sizeSoftmax, "transposeWeightMatrix");
	}
	
	//////////////////////
	private void printMatrix(int sizeX, int sizeY, int sizeZ, String matrixName) {
		for (int x=0; x<sizeX; x++) {
			for (int y=0; y<sizeY; y++) {
				for (int z=0; z<sizeZ; z++) {
					switch(matrixName) {
					case "weightMatrix": System.out.print(" "+this.Mw[x][y][z]); break;
					case "dataMatrix": System.out.print(" "+this.Md[x][y][z]); break;
					case "phaMatrix": System.out.print(" "+this.Mpha[x][y][z]); break; 
					case "phpMatrix": System.out.print(" "+this.Mphp[x][y][z]); break;
					case "phsMatrix": System.out.print(" "+this.Mphs[x][y][z]); break;
					case "transposeDataMatrix_Positive": System.out.print(" "+this.MdT[x][y][z]); break; 
					case "transposeDataMatrix_Negative": System.out.print(" "+this.MnvpT[x][y][z]); break; 
					case "transposeWeightMatrix": System.out.print(" "+this.MwT[x][y][z]); break;
					case "nvaMatrix": System.out.print(" "+this.Mnva[x][y][z]); break;
					case "nvpMatrix": System.out.print(" "+this.Mnvp[x][y][z]); break;
					case "nhaMatrix": System.out.print(" "+this.Mnha[x][y][z]); break; 
					case "nhpMatrix": System.out.print(" "+this.Mnhp[x][y][z]); break;  
					case "weightMatrix_positive": System.out.print(" "+this.Mwpos[x][y][z]); break;
					case "weightMatrix_negative": System.out.print(" "+this.Mwneg[x][y][z]); 
					}
				}
				System.out.println(" softmax "); 
			}
			System.out.println(" layer "); 
		}
	}
	
	public void printTrainedWeightMatrix_RBM() {
		System.out.println("\n----------------------------\n----------------------------\nThe finally trained Weight Matrix of this RBM model:");
		this.printMatrix(this.numMovies+1, this.sizeHiddenUnits+1, this.sizeSoftmax, "weightMatrix");
	}
	
	public ArrayList<Tuple<Integer,Integer>> insertTraningData_OneUser(int a, int b, int c, int d, int e, int f) {
		ArrayList<Tuple<Integer,Integer>> ratedMoviesIndices = new ArrayList<Tuple<Integer,Integer>>();
		ratedMoviesIndices.add(new Tuple<Integer, Integer>(1,a));
		ratedMoviesIndices.add(new Tuple<Integer, Integer>(2,b));
		ratedMoviesIndices.add(new Tuple<Integer, Integer>(3,c));
		ratedMoviesIndices.add(new Tuple<Integer, Integer>(4,d));
		ratedMoviesIndices.add(new Tuple<Integer, Integer>(5,e));
		ratedMoviesIndices.add(new Tuple<Integer, Integer>(6,f));
		return ratedMoviesIndices;
	}
	
	public static void main(String[] argv) {
		int numMovies = 6;
		int sizeSoftmax = 2; //rating from 0 to 10
		int sizeHiddenUnits = 2;
		double learningRate = 0.1;
		int numSteps = 30;

		RestrictedBoltzmannMachinesWithSoftmax rbmSoftmax = new RestrictedBoltzmannMachinesWithSoftmax(
				numMovies, sizeSoftmax, sizeHiddenUnits, learningRate, numSteps
				);
		
		rbmSoftmax.trainRBMWeightMatrix(rbmSoftmax.insertTraningData_OneUser(1,1,1,0,0,0));
		rbmSoftmax.trainRBMWeightMatrix(rbmSoftmax.insertTraningData_OneUser(1,0,1,0,0,0));
		rbmSoftmax.trainRBMWeightMatrix(rbmSoftmax.insertTraningData_OneUser(1,1,1,0,0,0));
		rbmSoftmax.trainRBMWeightMatrix(rbmSoftmax.insertTraningData_OneUser(0,0,1,1,1,0));
		rbmSoftmax.trainRBMWeightMatrix(rbmSoftmax.insertTraningData_OneUser(0,0,1,1,0,0));
		rbmSoftmax.trainRBMWeightMatrix(rbmSoftmax.insertTraningData_OneUser(0,0,1,1,1,0));
		
		rbmSoftmax.printTrainedWeightMatrix_RBM();
	}
}
