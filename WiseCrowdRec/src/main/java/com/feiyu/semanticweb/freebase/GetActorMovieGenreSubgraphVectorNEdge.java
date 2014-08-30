package com.feiyu.semanticweb.freebase;
/**
 * reference: https://developers.google.com/freebase/v1/mql-overview
 * @author feiyu
 * 
 * freebase query tool https://www.freebase.com/query
 */

import java.io.IOException;

import com.feiyu.semanticweb.IMDbInfoQuery;
import com.feiyu.utils.GlobalVariables;
import com.feiyu.utils.InitializeWCR;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.jayway.jsonpath.JsonPath;
import com.omertron.themoviedbapi.MovieDbException;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class GetActorMovieGenreSubgraphVectorNEdge {
	HttpTransport httpTransport = new NetHttpTransport();
	HttpRequestFactory requestFactory = httpTransport.createRequestFactory();
	GenericUrl url;
	JSONParser parser = new JSONParser();

	private void init() {
		url = new GenericUrl("https://www.googleapis.com/freebase/v1/mqlread");
		// http://wiki.freebase.com/wiki/How_to_obtain_an_API_key
		url.put("key", GlobalVariables.FREEBASE_API_KEY); 
	}

	public void getMovieListByActorName(String actorName) throws IOException, ParseException {
		this.init();
		System.out.println("-----Actor Name: "+actorName+"------getMovieListByActorName");
		String query = "[{\"starring\": [{\"actor\": \""
				+ actorName 
				+"\"}],\"type\": \"/film/film\",\"name\": null,\"mid\": null}]";
		//      String query = "[{\"id\":null,\"name\":null,\"type\":\"/astronomy/planet\"}]";
		url.put("query", query);
		HttpRequest request = requestFactory.buildGetRequest(url);
		HttpResponse httpResponse = request.execute();

		JSONObject response = (JSONObject)parser.parse(httpResponse.parseAsString());
		JSONArray results = (JSONArray)response.get("result");
		for (Object result : results) {
			System.out.println(JsonPath.read(result,"$.name").toString());
		}
	}

	public void getActorNamesByMovieName(String movieName) throws IOException, ParseException {
		this.init();
		System.out.println("-----Movie Name: "+movieName+"------getActorNamesByMovieName");
		// https://code.google.com/p/json-path/
		String query = "[{\"type\":\"/film/film\",\"name\":\""
				+ movieName
				+ "\",\"mid\":null,\"starring\":[{\"actor\":null,\"mid\":null}]}]";
		url.put("query", query);
		HttpRequest request = requestFactory.buildGetRequest(url);
		HttpResponse httpResponse = request.execute();

		JSONObject response = (JSONObject)parser.parse(httpResponse.parseAsString());
		JSONArray results = (JSONArray)response.get("result");
		for (Object result : results) {
			//				System.out.println(JsonPath.read(result,"$.starring[*].actor").toString());
			String[] actors= JsonPath.read(result,"$.starring[*].actor").toString()
					.replace("[\"", "")
					.replace("\"]", "")
					.split("\",\"");
			for (String actor : actors) {
				System.out.println(actor);
			}
		}
	}

	public void getActorNamesByIMDbMovieID(String IMDbID) throws MovieDbException, IOException, ParseException {
		IMDbInfoQuery imdbIQ = new IMDbInfoQuery();
		String movieName = imdbIQ.getMoiveName(IMDbID);
		System.out.println("-----Movie Name: "+movieName+"------getActorNamesByIMDbMovieID");
		this.getActorNamesByMovieName(movieName);
	}

	public void getFilmGenreByActorNMovieName(String actorName, String movieName) throws IOException, ParseException {
		this.init();
		System.out.println("-----Movie Genre of "+movieName+" "+actorName+"------getFilmGenreByActorNMovieName");

		String query = "[{\"type\":\"/film/film\",\"name\":\""
				+movieName
				+"\",\"genre\": [],\"starring\":[{\"actor\":\""
				+actorName
				+"\"}]}]";
		url.put("query", query);
		HttpRequest request = requestFactory.buildGetRequest(url);
		HttpResponse httpResponse = request.execute();

		JSONObject response = (JSONObject)parser.parse(httpResponse.parseAsString());
		JSONArray results = (JSONArray)response.get("result");
		for (Object result : results) {
			//				System.out.println(JsonPath.read(result,"$.starring[*].actor").toString());
			String[] actors= JsonPath.read(result,"$.genre").toString()
					.replace("[\"", "")
					.replace("\"]", "")
					.split("\",\"");
			for (String actor : actors) {
				System.out.println(actor);
			}
		}
	}

	public static void main(String[] args) {
		try {
			InitializeWCR initWCR = new InitializeWCR();
			initWCR.getWiseCrowdRecConfigInfo();
			initWCR.getFreebaseInfo();

			initWCR.themoviedbOrgInitial();

			GetActorMovieGenreSubgraphVectorNEdge getAMGSubGraphVE = new GetActorMovieGenreSubgraphVectorNEdge();
			getAMGSubGraphVE.getMovieListByActorName("Kiefer Sutherland");
			getAMGSubGraphVE.getActorNamesByMovieName("Inception");
			getAMGSubGraphVE.getActorNamesByIMDbMovieID("tt0109830");
			getAMGSubGraphVE.getFilmGenreByActorNMovieName("Kiefer Sutherland", "Stand by Me");

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}