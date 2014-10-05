package com.feiyu.springmvc.model;

public class Tuple<X, Y> {
	private X x; 
	private Y y; 
	
	public Tuple(X x, Y y) { 
		this.x = x; 
		this.y = y; 
	} 
	
	public X getFirst(){
		return this.x;
	}
	
	public Y getSecond() {
		return this.y;
	}
}
