/**
 * @author feiyu
 */
package com.feiyu.springmvc.model;

import java.util.Date;
import java.util.HashMap;

public class Tweet implements java.io.Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 6159227444048700185L;
	private Date _time;
	private String _lang;
	private HashMap<String, String> _entities;
	private int _sentiment;
	private String _text;
	
	public Tweet() {
	}

	public Tweet(Date time, String lang, HashMap<String, String> entities, 
			String text, int sentiment) {
		_time = time;
		_lang = lang;
		_entities = entities;
		_sentiment = sentiment;
		_text = text;
	}

	public Date getTime() {
		return _time;
	}

	public void setTime(Date time) {
		_time = time;
	}

	public String getLang() {
		return _lang;
	}

	public void setLang(String lang) {
		_lang = lang;
	}

	public HashMap<String, String> getEntities() {
		return _entities;
	}

	public void setEntities(HashMap<String, String> entities) {
		_entities = entities;
	}

	public int getSentiment() {
		return _sentiment;
	}

	public void setSentiment(int sentiment) {
		this._sentiment = sentiment;
	}

	public String getText() {
		return _text;
	}

	public void setText(String text) {
		_text = text;
	}

	@Override
	public String toString() {
		return "Tweet:{"
				+"time:"+_time
				+",lang:"+_lang
				+",sentiment:"+_sentiment
				+",entities:"+ _entities
				+",text:"+_text
				+"}";
	}
}
