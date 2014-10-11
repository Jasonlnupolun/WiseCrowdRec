package com.feiyu.springmvc.model;
/**
 * @author feiyu
 */

public class RBMMovieInfo {
	private int movieIdx;
	private int count;

	public RBMMovieInfo(int movieIdx, int count) {
		this.movieIdx = movieIdx;
		this.count = count;
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
				+"movieIdx:" + Integer.toString(this.movieIdx) 
				+",count:" + Integer.toString(this.count) 
				+ "}";
	}	
}