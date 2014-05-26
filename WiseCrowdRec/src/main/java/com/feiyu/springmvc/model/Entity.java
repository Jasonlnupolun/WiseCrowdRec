package com.feiyu.springmvc.model;

import io.searchbox.annotations.JestId;

public class Entity {
	
	@JestId
	String entityID;
	int count;
	EntityInfo entityInfo;
	
	public Entity (String entityID, int count, EntityInfo entityInfo) {
		this.entityID = entityID;
		this.count = count;
		this.entityInfo = entityInfo;
	}
	
	public String getEntityID() {
		return entityID;
	}
	
	public int getCount() {
		return count;
	}
	
	public EntityInfo getEntityInfo() {
		return entityInfo;
	}
	
	@Override
	public String toString() {
		return "Entity:{"
				+"entityID:"+ entityID
				+",count:"+ count 
				+",entityInfoJson:"+ entityInfo.toString()
				+"}";
	}
}
