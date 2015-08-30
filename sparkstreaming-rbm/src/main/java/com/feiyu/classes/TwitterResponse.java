package com.feiyu.classes;
/**
 * 
 * @author feiyu
 *
 */

public class TwitterResponse {
  private String twitterResponseStatus;
  private String twitterResponseMessage;
  private String oauthToken;

  public String getTwitterResponseStatus() {
    return twitterResponseStatus;
  }

  public void setTwitterResponseStatus(String twitterResponseStatus) {
    this.twitterResponseStatus = twitterResponseStatus;
  }

  public String getTwitterResponseMessage() {
    return twitterResponseMessage;
  }

  public void setTwitterResponseMessage(String twitterResponseMessage) {
    this.twitterResponseMessage = twitterResponseMessage;
  }

  public String getOauthToken() {
    return oauthToken;
  }

  public void setOauthToken(String oauthToken) {
    this.oauthToken = oauthToken;
  }

  @Override
  public String toString() {
    return "TwitterResponse:{"
        +"twitterResponseStatus:"+ twitterResponseStatus
        +",twitterResponseMessage:" + twitterResponseMessage
        +",oauthToken:" + oauthToken
        + "}";
  }
}
