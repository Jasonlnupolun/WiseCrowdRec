package com.feiyu.springmvc.model;

import java.util.ArrayList;

public class RBMUserInfo {
	private int userID; 
	private int userIdx;
	private ArrayList<Tuple<Integer,Integer>> ratedMovies;

	public RBMUserInfo(int userID, int userIdx, ArrayList<Tuple<Integer,Integer>> ratedMovies) {
		this.userID = userID;
		this.userIdx = userIdx;
		this.ratedMovies = ratedMovies;
	}

	public int getUserID() {
		return this.userID;
	}
	
	public int getUserIdx() {
		return this.userIdx;
	}
	
	public ArrayList<Tuple<Integer,Integer>> getRatedMovies() {
		// Tuple<MovieIdx, Rating>
		return this.ratedMovies;
	}

	@Override
	public String toString() {
		return "RBMUserInfo:{"
				+"userID:"+ Integer.toString(this.userID)
				+",userIdx:" + Integer.toString(this.userIdx) 
				+",ratedMovies:" + this.ratedMovies 
				+ "}";
	}
}
