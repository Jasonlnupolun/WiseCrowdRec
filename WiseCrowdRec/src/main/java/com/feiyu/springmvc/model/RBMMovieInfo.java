package com.feiyu.springmvc.model;

public class RBMMovieInfo {
	private int movieID;
	private int movieIdx;
	private int count;

	public RBMMovieInfo(int movieID, int movieIdx, int count) {
		this.movieID = movieID;
		this.movieIdx = movieIdx;
		this.count = count;
	}
	
	public int getMovieID() {
		return this.movieID;
	}
	
	public int getMovieIdx() {
		return this.movieIdx;
	}
	
	public int getMovieCount() {
		return this.count;
	}
	
	@Override
	public String toString() {
		return "RBMMovieInfo:{"
				+"movieID:"+ Integer.toString(this.movieID)
				+",movieIdx:" + Integer.toString(this.movieIdx) 
				+",count:" + Integer.toString(this.count) 
				+ "}";
	}	
}
