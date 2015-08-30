package com.feiyu.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeOperations {
  public boolean is9To13PST() {
    DateFormat df = new SimpleDateFormat("HH");
    Date dateobj = new Date();
    int curHour = Integer.valueOf(df.format(dateobj));
    if (curHour>=16 && curHour<20) {//UTC
      return true;
    } 
    return false;
  }

  public boolean is13To17PST() {
    DateFormat df = new SimpleDateFormat("HH");
    Date dateobj = new Date();
    int curHour = Integer.valueOf(df.format(dateobj));
    if (curHour>=20 && curHour<=23) {//UTC 
      return true;
    } 
    return false;
  }

  public boolean isOtherTime() {
    return (!this.is9To13PST())&&(!this.is13To17PST());
  }

  public void setIsReachLimitFlags() {
    DateFormat df = new SimpleDateFormat("HH");
    Date dateobj = new Date();
    int curHour = Integer.valueOf(df.format(dateobj));
    if (curHour>=16 && curHour<20) {//UTC
      GlobalVariables.FREEBASE_IS_REACH_LIMIT_9_13 = true;
      GlobalVariables.FREEBASE_IS_REACH_LIMIT_13_17 = false;
      GlobalVariables.FREEBASE_IS_REACH_LIMIT_OTHERS = false;
    } else if (curHour>=20 && curHour<=23) {// UTC 
      GlobalVariables.FREEBASE_IS_REACH_LIMIT_9_13 = false;
      GlobalVariables.FREEBASE_IS_REACH_LIMIT_13_17 = true;
      GlobalVariables.FREEBASE_IS_REACH_LIMIT_OTHERS = false;
    } else {
      GlobalVariables.FREEBASE_IS_REACH_LIMIT_9_13 = false;
      GlobalVariables.FREEBASE_IS_REACH_LIMIT_13_17 = false;
      GlobalVariables.FREEBASE_IS_REACH_LIMIT_OTHERS = true;
    }
  }

  public void chooseFreebaseKeyByTime() {
    DateFormat df = new SimpleDateFormat("HH");
    Date dateobj = new Date();
    int curHour = Integer.valueOf(df.format(dateobj));
    //System.out.println(curHour);
    //if (curHour>=9 && curHour<13) {//PST
    if (curHour>=16 && curHour<20) {//UTC
      GlobalVariables.FREEBASE_URL.put("key", GlobalVariables.WCR_PROPS.getProperty("freebase.api.key1")); 
      //} else if (curHour>=13 && curHour<17) { 
    } else if (curHour>=20 && curHour<=23) {// UTC 
      GlobalVariables.FREEBASE_URL.put("key", GlobalVariables.WCR_PROPS.getProperty("freebase.api.key2")); 
    } else {
      GlobalVariables.FREEBASE_URL.put("key", GlobalVariables.WCR_PROPS.getProperty("freebase.api.key3")); 
    }
  }

  public static void main(String[] argv) {
    TimeOperations timeOps = new TimeOperations();
    timeOps.chooseFreebaseKeyByTime();
  }
}
