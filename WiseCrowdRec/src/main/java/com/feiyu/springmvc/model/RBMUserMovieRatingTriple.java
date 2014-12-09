package com.feiyu.springmvc.model;
/**
 * @author feiyu
 */

public class RBMUserMovieRatingTriple {
  private int userIdx;
  private int movieIdx;
  private int softmaxIdx;

  public RBMUserMovieRatingTriple(int userIdx, int movieIdx, int softmaxIdx) {
    this.userIdx = userIdx;
    this.movieIdx = movieIdx;
    this.softmaxIdx = softmaxIdx;
  }

  public int getUserIdx() {
    return this.userIdx;
  }

  public int getMovieIdx() {
    return this.movieIdx;
  }

  public int getSoftmaxIdx() {
    return this.softmaxIdx;
  }

  @Override
  public String toString() {
    return "RBMUserMovieRatingTriple:{"
        +"userIdx:"+ this.userIdx
        +",movieIdx:" + this.movieIdx 
        +",softmaxIdx:" + this.softmaxIdx
        + "}";
  } 
}
