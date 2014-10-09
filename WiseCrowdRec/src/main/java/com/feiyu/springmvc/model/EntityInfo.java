package com.feiyu.springmvc.model;


public class EntityInfo implements java.io.Serializable {
	private static final long serialVersionUID = 255025607516525468L;
	private String entity;
	private String category;
	private int sentiment;
	private String sentimentCSS;
	private String time;
	private String text;
	private long userid;

	public EntityInfo(String entity, String category, int sentiment, 
			String sentimentCSS, String time, String text, long userid) {
		this.entity = entity;
		this.category = category;
		this.sentiment = sentiment;
		this.sentimentCSS = sentimentCSS;
		this.time = time;
		this.text = text;
		this.userid = userid;
	}

	public String getEntity() {
		return entity;
	}

	public String getCategory() {
		return category;
	}

	public int getSentitment() {
		return sentiment;
	}

	public String getSentitmentCSS() {
		return sentimentCSS;
	}

	public String getTime() {
		return time;
	}

	public String getText() {
		return text;
	}
	
	public long getUserid() {
		return userid;
	}

	@Override
	public String toString() {
		return "EntityInfo:{"
				+"entity:"+ entity
				+",category:" + category
				+",sentiment:" + sentiment
				+",sentimentCSS:" + sentimentCSS
				+",time:" + time
				+",text:" + text
				+",userid:" + userid
				+ "}";
	}
}
