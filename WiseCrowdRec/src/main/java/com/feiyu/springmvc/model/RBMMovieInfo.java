package com.feiyu.springmvc.model;
/**
 * @author feiyu
 */

public class RBMMovieInfo {
	private int movieIdx;
	private String mid;
	private int count;

	public RBMMovieInfo(int movieIdx, String mid, int count) {
		this.movieIdx = movieIdx;
		this.mid = mid;
		this.count = count;
	}

	public int getMovieIdx() {
		return this.movieIdx;
	}
	
	public String getMid() {
		return this.mid;
	}

	public int getMovieCount() {
		return this.count;
	}

	@Override
	public String toString() {
		return "RBMMovieInfo:{"
				+"movieIdx:" + Integer.toString(this.movieIdx) 
				+",mid:" + this.mid 
				+",count:" + Integer.toString(this.count) 
				+ "}";
	}	
}