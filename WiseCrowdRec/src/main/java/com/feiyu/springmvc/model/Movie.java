package com.feiyu.springmvc.model;

public class Movie {
	String IMDbID;
	String movieName;
	int rating;
	
	public Movie() {
	}
	
	public void setIMDbID(String IMDbID) {
		this.IMDbID = IMDbID;
	}
	
	public String getIMDbID() {
		return this.IMDbID;
	}
	
	public void setMovieName(String movieName) {
		this.movieName = movieName;
	}
	
	public String getMovieName() {
		return this.movieName;
	}

	public void setRating(int rating) {
		this.rating = rating;
	}
	
	public int getRating() {
		return this.rating;
	}
	
	@Override
    public String toString() {
		return "Movie:{"
				+"IMDbID:"+ this.IMDbID 
				+",movieName:" + this.movieName
				+",rating:" + this.rating
				+ "}";
	}	
}
