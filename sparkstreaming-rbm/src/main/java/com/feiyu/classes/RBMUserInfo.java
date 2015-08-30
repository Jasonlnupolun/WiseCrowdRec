package com.feiyu.classes;

import java.util.HashMap;
/**
 * @author feiyu
 */

public class RBMUserInfo {
  private int userIdx;
  private HashMap<Integer,Integer> ratedMovies;

  public RBMUserInfo(int userIdx, HashMap<Integer,Integer> ratedMovies) {
    this.userIdx = userIdx;
    this.ratedMovies = ratedMovies;
  }

  public int getUserIdx() {
    return this.userIdx;
  }

  public HashMap<Integer,Integer> getRatedMovies() {
    // HashMap<Integer, Integer>(movieIdx, rating)
    return this.ratedMovies;
  }

  @Override
  public String toString() {
    return "RBMUserInfo:{"
        +"userIdx:" + Integer.toString(this.userIdx) 
        +",ratedMovies:" + this.ratedMovies 
        + "}";
  }
}
