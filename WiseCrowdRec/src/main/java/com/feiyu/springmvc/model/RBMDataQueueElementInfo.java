package com.feiyu.springmvc.model;

import java.util.ArrayList;
import java.util.HashMap;
/**
 * @author feiyu
 */

public class RBMDataQueueElementInfo {
	private int kthRBM;
	private	HashMap<String, RBMMovieInfo> movieHashMap;
	private	HashMap<String, RBMUserInfo> userHashMapTrain;
	private	HashMap<String, RBMUserInfo> userHashMapTest;
	private ArrayList<String> movieNameWithIdx;

	public RBMDataQueueElementInfo(
			int kthRBM, HashMap<String, RBMMovieInfo> movieHashMap, 
			HashMap<String, RBMUserInfo> userHashMapTrain, HashMap<String,RBMUserInfo> userHashMapTest,
			ArrayList<String> movieNameWithIdx) {
		this.kthRBM = kthRBM;
		this.movieHashMap = movieHashMap;
		this.userHashMapTrain = userHashMapTrain;
		this.userHashMapTest = userHashMapTest;
		this.movieNameWithIdx = movieNameWithIdx;
	}

	public void setKthRBM(int kthRBM) {
		this.kthRBM = kthRBM;
	}

	public void setMovieHashMap(HashMap<String, RBMMovieInfo> movieHashMap) {
		this.movieHashMap = movieHashMap;
	}

	public void setUserHashMapTrain(HashMap<String, RBMUserInfo> userHashMapTrain) {
		this.userHashMapTrain = userHashMapTrain;
	}

	public void setUserHashMapTest(HashMap<String,RBMUserInfo> userHashMapTest) {
		this.userHashMapTest = userHashMapTest;
	}
	
	public void setMovieNameWithIdx(ArrayList<String> movieNameWithIdx) {
		this.movieNameWithIdx = movieNameWithIdx;
	}

	public int getKthRBM() {
		return this.kthRBM;
	}

	public HashMap<String, RBMMovieInfo> getMovieHashMap() {
		return this.movieHashMap;
	}

	public HashMap<String, RBMUserInfo> getUserHashMapTrain() {
		return this.userHashMapTrain;
	}

	public HashMap<String, RBMUserInfo> getUserHashMapTest() {
		return this.userHashMapTest;
	}
	
	public ArrayList<String> getMovieNameWithIdx() {
		return this.movieNameWithIdx;
	}

	@Override
	public String toString() {
		return "RBMDataQueueElementInfo:{"
				+"kthRBM:"+ this.kthRBM
				+",movieHashMap:" + this.movieHashMap
				+",userHashMapTrain:"+ this.userHashMapTrain
				+",userHashMapTest:"+ this.userHashMapTest
				+",movieNameWithIdx:" + this.movieNameWithIdx
				+ "}";
	}
}
