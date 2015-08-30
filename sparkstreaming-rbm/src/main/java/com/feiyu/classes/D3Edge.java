package com.feiyu.classes;
/**
 * @author feiyu
 */

public class D3Edge {
  String source;
  String target;
  String value;
  String distance;

  public D3Edge(String source, String target, String value, String distance) { 
    this.source = source;
    this.target = target;
    this.value = value;
    this.distance = distance;
  }

  public String getSource() {
    return this.source;
  }

  public String getTarget() {
    return this.target;
  }
  public String getValue() {
    return this.value;
  }
  public String getDistance() {
    return this.distance;
  }

  @Override
  public String toString() {
    return "D3Edge:{"
        +"source:"+ this.source 
        +",target:"+ this.target 
        +",value:"+ this.value 
        +",distance:"+ this.distance
        +"}";
  }
}
