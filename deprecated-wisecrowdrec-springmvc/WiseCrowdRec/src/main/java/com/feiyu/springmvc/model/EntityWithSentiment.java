package com.feiyu.springmvc.model;
/**
 * @author feiyu
 */

import java.util.HashMap;

public class EntityWithSentiment {
  private HashMap<String, String> entityWithCategory;
  private int sentiment;

  public EntityWithSentiment() {
  }

  public void setEntityWithCategory(HashMap<String, String> entityWithCategory) {
    this.entityWithCategory = entityWithCategory;
  }

  public HashMap<String, String> getEntityWithCategory() {
    return this.entityWithCategory;
  }

  public void setSentiment(int sentiment) {
    this.sentiment = sentiment; 
  }

  public int getSentiment() {
    return this.sentiment;
  }

  @Override
  public String toString() {
    return "EntityWithSentiment:{"
        +"EntityWithCategory:"+ this.entityWithCategory
        +",sentiment:" + this.sentiment 
        + "}";
  }

}
