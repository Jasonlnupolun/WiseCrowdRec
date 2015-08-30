package com.feiyu.utils;
/**
 * @author feiyu
 */

import java.io.IOException;
import java.util.PriorityQueue;

import org.codehaus.jettison.json.JSONException;
import org.junit.Test;

import com.feiyu.springmvc.model.MovieWithCountComparable;
import com.feiyu.storm.streamingdatacollection.bolt.MovieCounter;

public class GetTopKTest {
  GetTopK getTopK = new GetTopK();

  @Test
  public void testGetTopK() throws JSONException, IOException {
    PriorityQueue<MovieWithCountComparable> heap = new PriorityQueue<MovieWithCountComparable>();

    heap.add(new MovieWithCountComparable("M1", new MovieCounter(1) ));
    heap.add(new MovieWithCountComparable("M2", new MovieCounter(6) ));
    heap.add(new MovieWithCountComparable("M3", new MovieCounter(3) ));
    heap.add(new MovieWithCountComparable("M4", new MovieCounter(10) ));
    heap.add(new MovieWithCountComparable("M5",new MovieCounter(9) ));
    //heap -> GlobalVariables.GlobalVariables.STORM_MOVIELIST_HEAP

    //		getTopK.getTopKMovies(3);
  }
}
