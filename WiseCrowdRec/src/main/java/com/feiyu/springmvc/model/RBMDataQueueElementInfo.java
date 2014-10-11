package com.feiyu.springmvc.model;

import java.util.HashMap;
/**
 * @author feiyu
 */

public class RBMDataQueueElementInfo {
	boolean isForTrain;
	int kthRBM;
	HashMap<String, RBMUserInfo> userHashMap;

	public RBMDataQueueElementInfo(boolean isForTrain, int kthRBM, HashMap<String, RBMUserInfo> userHashMap) {
		this.isForTrain = isForTrain;
		this.kthRBM = kthRBM;
		this.userHashMap = userHashMap;
	}

	public boolean getIsForTrain() {
		return this.isForTrain;
	}

	public int getKthRBM() {
		return this.kthRBM;
	}

	public HashMap<String, RBMUserInfo> getUserHashMap() {
		return this.userHashMap;
	}

	@Override
	public String toString() {
		return "RBMDataQueueElementInfo:{"
				+"isForTrain:"+ this.isForTrain
				+",kthRBM:"+ this.kthRBM
				+",userHashMap:" + this.userHashMap
				+ "}";
	}
}
