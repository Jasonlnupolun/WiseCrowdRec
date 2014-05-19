package com.feiyu.model;

import java.util.List;

public class EntityList {
    private String keywordPhrases;
    private List<EntityInfo> entitiesInfo;
    
    public EntityList() {
    	
    }

    public EntityList(String keywordPhrases, List<EntityInfo> entitiesInfo) {
        this.keywordPhrases = keywordPhrases;
        this.entitiesInfo = entitiesInfo;
    }

    public String getKeywordPhrases() {
        return keywordPhrases;
    }

    public List<EntityInfo> getEntitiesInfo() {
        return entitiesInfo;
    }
    
    public String toString() {
		return "EntityList:{"
				+"keyword phrases:"+ keywordPhrases
				+",# of entitiesInfo:" + entitiesInfo.size()
				+ "}";
	}
}
