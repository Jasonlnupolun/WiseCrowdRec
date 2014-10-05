package com.feiyu.deeplearning.RBM;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;

import com.feiyu.springmvc.model.Tuple;

public class RestrictedBoltzmannMachinesWithSoftmax {
	// movie rating is rated from 0 to 10, therefore the softmax in this model is an 11-way softmax
	private int sizeSoftmax;
	private int numMovies;
	private int sizeHiddenUnits;

	private double[][][] Md; // 3D data matrix, users X movies X softmax (users X movies X rating), m-by-n-by-k
	private double[][][] Mw; // 3D weight matrix, movies X softmax X hidden units, n-by-k-by-l
	private double[][][] Mpha; // 3D positive hidden associations matrix, oneUser X softmax X hidden units, 1-by-k-by-l
	private double[][][] Mphp; // 3D positive hidden probabilities matrix, oneUser X softmax X hidden units, 1-by-k-by-l; Mpha->p(u(i)s(β)h(j))->Mphp
	private double[][][] MdT; // transpose data matrix, movies X users X softmax , n-by-m-by-k
	private double[][][] MwT; // transpose weight matrix, hidden units X movies X softmax , n-by-k-by-l

	private Random randomN = new Random();

	public RestrictedBoltzmannMachinesWithSoftmax(
			int sizeSoftmax, int numMovies, int sizeHiddenUnits
			) {
		this.sizeSoftmax = sizeSoftmax;
		this.numMovies = numMovies;
		this.sizeHiddenUnits = sizeHiddenUnits;
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

	private void setBiasUnitsToOnes_DataMatrix() {
		for (int i=0; i<this.sizeSoftmax; i++) {
			this.Md[0][0][i] = 1;
		}
	}
	
	private void transposeDataMatrix() {
		this.MdT = new double[this.numMovies+1][1][this.sizeSoftmax]; 
		for (int x=0; x<this.numMovies+1; x++) {
			for(int z=0; z<this.sizeSoftmax; z++) {
				this.MdT[x][0][z]=this.Md[0][x][z];
			}
		}
		
		System.out.println("\n Transpose matrix of Data Matrix -> One User");
		this.printMatrix(this.numMovies+1, 1, this.sizeSoftmax, "transposeDataMatrix");
	}

	private void transposeWeightMatrix() {
		this.MdT = new double[this.numMovies+1][1][this.sizeSoftmax]; 
		for (int x=0; x<this.numMovies+1; x++) {
			for(int z=0; z<this.sizeSoftmax; z++) {
				this.MdT[x][0][z]=this.Md[0][x][z];
			}
		}
		
		System.out.println("\n Transpose matrix of Data Matrix -> One User");
		this.printMatrix(this.numMovies+1, 1, this.sizeSoftmax, "transposeDataMatrix");
	}
	//////////////////////
	private void initializeWeightMatrix() {
		this.setBiasUnitsToZeros_WeightMatrix();
		for (int i=1; i<=this.numMovies; i++) {
			for (int y=1; y<=this.sizeHiddenUnits; y++) {
				for (int z=0; z<this.sizeSoftmax; z++) {
					this.Mw[i][y][z] = 0.1 * this.randomN.nextGaussian(); 
					//with mean 0.0 and standard deviation 0.1 
				}
			}
		}

		System.out.println("\n3D Weight Matrix");
		this.printMatrix(this.numMovies+1, this.sizeHiddenUnits+1, this.sizeSoftmax, "weightMatrix");
	}	

	public void updateTheDataMatrix_oneUser(ArrayList<Tuple<Integer,Integer>> ratedMoviesIndices) {
		this.Md = new double[1][this.numMovies+1][this.sizeSoftmax]; // one layer in the 3D data matrix is for bias unit
		int sizeOfRatedMovies = ratedMoviesIndices.size();
		for (int i=0; i<sizeOfRatedMovies; i++) {
			this.Md[0][ratedMoviesIndices.get(i).getFirst()][ratedMoviesIndices.get(i).getSecond()] = 1;
		}
		this.setBiasUnitsToOnes_DataMatrix();

		System.out.println("\n3D Data Matrix -> One User");
		this.printMatrix(1, this.numMovies+1, this.sizeSoftmax, "dataMatrix");
	}

	public void getThePhaMatrix_oneUser() {
		this.Mpha = new double[1][this.sizeHiddenUnits+1][this.sizeSoftmax];
		double temp;
		for (int y=0; y<this.sizeHiddenUnits+1; y++) {
			for (int z=0; z<this.sizeSoftmax; z++) {
				temp = 0;
				for (int i=0; i<=this.numMovies; i++) {
					temp += this.Md[0][i][z]*this.Mw[i][y][z]; 
					//System.out.println(0+" "+i+" "+z+" "+this.Md[0][i][z]+"--"+i+" "+y+" "+z+" "+this.Mw[i][y][z]+"--"+this.Md[0][i][z]*this.Mw[i][y][z]+" "+temp);
				}
				this.Mpha[0][y][z] = temp;
			}
		}

		System.out.println("\n3D positive hidden associations matrix -> One User");
		this.printMatrix(1, this.sizeHiddenUnits+1, this.sizeSoftmax, "phaMatrix");
	}

	public void getThePhpMatrix_oneUser() {
		//Mpha->p(u(i)s(β)h(j))->Mphp
		this.Mphp = new double[1][this.sizeHiddenUnits+1][this.sizeSoftmax];
		double currentSumOfSoftmax;
		for (int y=0; y<this.sizeHiddenUnits+1; y++) {
			currentSumOfSoftmax = 0;
			for (int z=0; z<this.sizeSoftmax; z++) {
				currentSumOfSoftmax += Math.exp(this.Mpha[0][y][z]);
			}
			for (int z=0; z<this.sizeSoftmax; z++) {
//				System.out.println(0+" "+y+" "+z+" "+Math.pow(Math.E, this.Mpha[0][y][z])+" "+currentSumOfSoftmax);
				this.Mphp[0][y][z] = Math.exp(this.Mpha[0][y][z]) / currentSumOfSoftmax;
			}
		}
		System.out.println("\n3D positive hidden probabilities matrix -> One User");
		this.printMatrix(1, this.sizeHiddenUnits+1, this.sizeSoftmax, "phpMatrix");
	}
	
	public void getNewWeightMatrix_nextStep() {
		this.transposeDataMatrix();
		
		for (int x=0; x<this.numMovies+1; x++) {
			for (int y=0; y<this.sizeHiddenUnits+1; y++) {
				for (int z=0; z<this.sizeSoftmax; z++) {
					this.Mw[x][y][z] = this.MdT[x][0][z]*this.Mphp[0][y][z]; 
				}
			}
		}

		System.out.println("\n new Weight Matrix --> next iteration");
		this.printMatrix(this.numMovies+1, this.sizeHiddenUnits+1, this.sizeSoftmax, "weightMatrix");
		
	}

	//////////////////////
	public void RBM_CD_phases(int numOfCD){
		// RBM Contrastive Divergence
		for (int i=1; i<=numOfCD; i++){ 
			System.out.println("\n-------\nCD: "+i);
			// positive CD phase
			this.getThePhaMatrix_oneUser();
			this.getThePhpMatrix_oneUser();
			this.getNewWeightMatrix_nextStep();
			
			// negative CD phase
			
		}
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
					case "transposeDataMatrix": System.out.print(" "+this.MdT[x][y][z]); 
					}
				}
				System.out.println(" softmax "); 
			}
			System.out.println(" layer "); 
		}
	}

	public static void main(String[] argv) {
		int sizeSoftmax = 2; //rating from 0 to 10
		int numMovies = 6;
		int sizeHiddenUnits = 2;

		RestrictedBoltzmannMachinesWithSoftmax rbmSoftmax = new RestrictedBoltzmannMachinesWithSoftmax(
				sizeSoftmax, numMovies, sizeHiddenUnits
				);
		
		rbmSoftmax.initializeWeightMatrix();

		ArrayList<Tuple<Integer,Integer>> ratedMoviesIndices = new ArrayList<Tuple<Integer,Integer>>();
		ratedMoviesIndices.add(new Tuple<Integer, Integer>(1,1));
		ratedMoviesIndices.add(new Tuple<Integer, Integer>(2,1));
		ratedMoviesIndices.add(new Tuple<Integer, Integer>(3,1));
		ratedMoviesIndices.add(new Tuple<Integer, Integer>(4,0));
		ratedMoviesIndices.add(new Tuple<Integer, Integer>(5,0));
		ratedMoviesIndices.add(new Tuple<Integer, Integer>(6,0));
		rbmSoftmax.updateTheDataMatrix_oneUser(ratedMoviesIndices);
		
		rbmSoftmax.RBM_CD_phases(100);
	}

}
