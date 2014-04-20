/**
 * @author feiyu
 */
package com.feiyu.model;

import java.util.Date;
import java.util.HashMap;

public class Tweet {
	private Date _time;
	private String _lang;
	private HashMap<String, String> _entities;
	private String _text;
	
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
	
	public String getText() {
		return _text;
	}
	
	public void setText(String text) {
		_text = text;
	}

	@Override
	public String toString() {
		return "Tweet [lang=" + _lang + ", time=" + _time + "]";
	}
}
