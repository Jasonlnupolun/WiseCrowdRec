package com.feiyu.springmvc.model;

/**
 * @author feiyu
 */
public class D3Vertex {
  String name;
  String fullname;
  String entity;

  public D3Vertex(String name, String fullname, String entity) {
    this.name = name;
    this.fullname = fullname;
    this.entity = entity;
  } 

  public String getName() {
    return this.name;
  }

  public String getFullname() {
    return this.fullname;
  }

  public String getEntity() {
    return this.entity;
  }

  @Override
  public String toString() {
    return "D3Vertex:{"
        +"name:"+ this.name 
        +",fullname:"+ this.fullname 
        +",entity:"+ this.entity 
        +"}";
  }
}
