package com.feiyu.classes;
/**
 * @author feiyu
 */

public class Entity implements java.io.Serializable {

  private static final long serialVersionUID = 5592251444007299403L;
  String entityID;
  int count;
  EntityInfo entityInfo;

  public Entity () {
  }

  public void setEntityID(String entityID) {
    this.entityID = entityID;
  }

  public String getEntityID() {
    return entityID;
  }

  public void setCount(int count) {
    this.count = count;
  }

  public int getCount() {
    return count;
  }

  public void setEntityInfo(EntityInfo entityInfo) {
    this.entityInfo = entityInfo;
  }

  public EntityInfo getEntityInfo() {
    return entityInfo;
  }

  @Override
  public String toString() {
    return "Entity:{"
        +"entityID:"+ entityID
        +",count:"+ count 
        +",entityInfo:"+ entityInfo.toString()
        +"}";
  }
}
