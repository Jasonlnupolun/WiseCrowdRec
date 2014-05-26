package com.feiyu.springmvc.Dao;

import java.util.List;

import com.feiyu.springmvc.model.EntityInfo;

public interface EntityInfoESDao {
	   public List<EntityInfo> getAllEntityInfo();
	   public EntityInfo getEntityInfo(int rollNo);
	   public void updateEntityInfo(EntityInfo entityInfo);
	   public void deleteEntityInfo(EntityInfo entityInfo);
}
