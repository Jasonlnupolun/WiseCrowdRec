package com.feiyu.springmvc.model;

public class MovieWithCount implements java.io.Serializable {
	private static final long serialVersionUID = -7011806344559558554L;
	private Movie movie;
	private int count;

	public MovieWithCount() {

	}

	public Movie getMovie() {
		return movie;
	}

	public void setMovie(Movie movie) {
		this.movie = movie;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	@Override
	public String toString() {
		return "MovieWithCount:{"
				+"Movie:"+ this.movie.toString()
				+",count:" + this.count
				+ "}";
	}	
}
