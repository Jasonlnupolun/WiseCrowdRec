package com.feiyu.model;

public class EntityInfo {
	private String entity;
	private String category;
	private int sentiment;
	private String sentimentCSS;
	private String time;
	private String text;

	public EntityInfo(String entity, String category, int sentiment, String sentimentCSS, String time, String text) {
		this.entity = entity;
		this.category = category;
		this.sentiment = sentiment;
		this.sentimentCSS = sentimentCSS;
		this.time = time;
		this.text = text;
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

	@Override
	public String toString() {
		return "EntityInfo:{"
				+"entity:"+ entity
				+",category:" + category
				+",sentiment:" + sentiment
				+",sentimentCSS:" + sentimentCSS
				+",time:" + time
				+",text:" + text
				+ "}";
	}
}