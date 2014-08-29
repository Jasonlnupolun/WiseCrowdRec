package com.feiyu.springmvc.model;

import com.feiyu.storm.streamingdatacollection.bolt.MovieCounter;

public class MovieWithCountComparable implements Comparable<MovieWithCountComparable> {
	private String movieName;
	private MovieCounter count;
	
	public MovieWithCountComparable(String movieName, MovieCounter count) {
		this.movieName = movieName; 
		this.count = count;
	}

	public MovieWithCountComparable(MovieWithCountComparable mwcComparable) {
		this.movieName = mwcComparable.movieName; 
		this.count = mwcComparable.count;
	}

	public String getMovieName() {
		return movieName;
	}

	public void setMovieName(String movieName) {
		this.movieName = movieName;
	}

	public int getCount() {
		return count._count;
	}

	public void setCount(int count) {
		this.count._count = count;
	}
	
	@Override
	public int compareTo(MovieWithCountComparable other) {
		return this.getCount() > other.getCount() ? -1 : (this.getCount() < other.getCount() ? 1 : 0);
	}	

	@Override
	public String toString() {
		return "MovieWithCountComparable:{"
				+"MovieName:"+ this.movieName
				+",count:" + this.count
				+ "}";
	}
}
