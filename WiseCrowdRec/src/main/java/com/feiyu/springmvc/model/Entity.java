package com.feiyu.springmvc.model;

public class Entity {
	String entityID;
	EntityInfo entityInfo;
	
	public Entity (String entityID, EntityInfo entityInfo) {
		this.entityID = entityID;
		this.entityInfo = entityInfo;
	}
	
	public String getEntityID() {
		return entityID;
	}
	
	public EntityInfo getEntityInfo() {
		return entityInfo;
	}
	
	@Override
	public String toString() {
		return "Entity:{"
				+"entityID:"+ entityID
				+",entityInfoJson:"+ entityInfo.toString()
				+"}";
	}
}
