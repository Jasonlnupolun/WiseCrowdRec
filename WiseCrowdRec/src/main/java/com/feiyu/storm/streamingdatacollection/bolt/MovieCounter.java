package com.feiyu.storm.streamingdatacollection.bolt;
/**
 * 
 * @author feiyu
 *
 */

public class MovieCounter{
  public int _count;
  public MovieCounter(int v){
    _count = v;
  }

  public String toString(){
    return String.valueOf(_count);
  }
}
