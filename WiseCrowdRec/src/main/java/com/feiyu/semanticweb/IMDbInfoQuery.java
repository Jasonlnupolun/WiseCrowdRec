package com.feiyu.semanticweb;

import java.io.IOException;

import com.feiyu.util.GlobalVariables;
import com.feiyu.util.InitializeWCR;
import com.omertron.themoviedbapi.MovieDbException;
import com.omertron.themoviedbapi.model.MovieDb;

/**
 * Using TheMovieDB API 
 * Java API -> https://github.com/holgerbrandl/themoviedbapi/
 * json API -> http://docs.themoviedb.apiary.io/
 * 
 * api-themoviedb (Java API -> https://github.com/Omertron/api-themoviedb/)
 *
 * @author feiyu
 *
 */

public class IMDbInfoQuery {
	
	public String getMoiveName(String IMDbID) throws MovieDbException {
		MovieDb result = GlobalVariables.TMDB.getMovieInfoImdb(IMDbID,"en-US"); // WiseCrowdRec only analyze english tweets
		return result.getOriginalTitle();
	}
	
	public static void main(String[] argv) throws MovieDbException, IOException {
		InitializeWCR initWcr = new InitializeWCR();
		initWcr.getWiseCrowdRecConfigInfo();
		initWcr.themoviedbOrgInitial();
		
		IMDbInfoQuery imdbIQ = new IMDbInfoQuery();	
		System.out.println(imdbIQ.getMoiveName("tt0109830"));
	}
}
