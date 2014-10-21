package com.feiyu.springmvc.model;

import java.util.ArrayList;
import java.util.HashMap;
/**
 * @author feiyu
 */

public class RBMClientWeightMatixForPredict {
	private int kthRBM; 
	private	long timeTrained; 
	private	HashMap<String, RBMMovieInfo> movieHashMap; 
	private	double[][][] Mw_rbm;
	private boolean successfullyTrainedThisRBM;
	private ArrayList<String> movieNameWithIdx;

	public RBMClientWeightMatixForPredict(
			int kthRBM, long timeTrained, HashMap<String, RBMMovieInfo> movieHashMap, double[][][] Mw_rbm,
			boolean successfullyTrainedThisRBM, ArrayList<String> movieNameWithIdx
			) {
		this.kthRBM = kthRBM;
		this.timeTrained = timeTrained;
		this.movieHashMap = movieHashMap;
		this.Mw_rbm = Mw_rbm;
		this.successfullyTrainedThisRBM = successfullyTrainedThisRBM;
		this.movieNameWithIdx = movieNameWithIdx;
	}

	public int getKthRBM() {
		return this.kthRBM;
	}

	public long getTimeTrained() {
		return this.timeTrained;
	}

	public HashMap<String, RBMMovieInfo> getMovieHashMap() {
		return this.movieHashMap;
	}

	public double[][][] getWeightMatrixCurrentRBM() {
		return this.Mw_rbm;
	}
	
	public boolean getSuccessfullyTrainedThisRBM() {
		return this.successfullyTrainedThisRBM;
	}
	
	public ArrayList<String> getMovieNameWithIdx() {
		return this.movieNameWithIdx;
	}

	@Override
	public String toString() {
		return "RBMClientWeightMatixForPredict:{"
				+"kthRBM:"+ this.kthRBM
				+",timeTrained:" + this.timeTrained
				+",movieHashMap:" + this.movieHashMap
				+",Mw_rbm:" + this.Mw_rbm
				+",successfullyTrainedThisRBM:" + this.successfullyTrainedThisRBM
				+",movieNameWithIdx:" + this.movieNameWithIdx
				+ "}";
	}
}
