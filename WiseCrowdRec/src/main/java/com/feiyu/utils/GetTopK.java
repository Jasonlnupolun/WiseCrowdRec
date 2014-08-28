package com.feiyu.utils;

import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import com.feiyu.springmvc.model.MovieWithCountComparable;

public class GetTopK {
	
	/**
	 * get top K movies by using max-heap
	 * http://www.sanfoundry.com/java-program-implement-max-heap/
	 * @throws JSONException 
	 * @throws IOException 
	 */
	public void getTopKMovies(int k) throws JSONException, IOException {
		String labe1 = "MovieName";
		String labe2 = "HybridRating";
		Queue<MovieWithCountComparable> queue = new LinkedList<MovieWithCountComparable>();
		JSONArray jsonArray = new JSONArray();
		JSONObject jsonObject;

		int heapSize = GlobalVariables.STORM_MOVIELIST_HEAP.size();
		if (heapSize <= 0) {
			System.out.println("The heap STORM_MOVIELIST_HEAP is empty!!");
			return;
		} 
		
		int n = -1;
		if (k> heapSize) {
			n = heapSize;
		} else{
			n= k;
		}
		
		for (int i=0; i<n; i++) {
			MovieWithCountComparable mvcC = new MovieWithCountComparable(GlobalVariables.STORM_MOVIELIST_HEAP.poll());  
			queue.add(mvcC);
			
			jsonObject = new JSONObject();
			jsonObject.put(labe1, mvcC.getMovieName());
			jsonObject.put(labe2, String.valueOf(mvcC.getCount()));
			
			jsonArray.put(jsonObject);
		}
		for (int i=0; i<n; i++) {
			GlobalVariables.STORM_MOVIELIST_HEAP.add(queue.poll());
		}
		String json = jsonArray.toString(); 

		GlobalVariables.RABBITMQ_CHANNEL.basicPublish(
				"", 
				GlobalVariables.RABBITMQ_QUEUE_NAME_STORMHISTOGRAMCHART, 
				null, 
				json.getBytes());
		System.out.println(" [x] getTopKMovies: message sent to queue buffer: " + json);
		System.out.println(json);
	}
}
