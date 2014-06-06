/**
 * @author feiyu
 */
package com.feiyu.springmvc.service;

public interface SearchTweets {

	public void searchTweetsFromNowOn(boolean isDynamicSearch);

	public void searchTweetsRandomSample(boolean isDynamicSearch);

	//public void searchTweetsGardenhouse();
}
