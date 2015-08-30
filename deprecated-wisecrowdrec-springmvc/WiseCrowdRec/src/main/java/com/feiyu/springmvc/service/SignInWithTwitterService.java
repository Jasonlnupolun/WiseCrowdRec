package com.feiyu.springmvc.service;
/**
 * reference: 
 * https://github.com/apache/httpcore/blob/4.3.x/httpcore/src/examples/org/apache/http/examples/ElementalHttpPost.java
 * https://dev.twitter.com/docs/auth/implementing-sign-twitter
 * https://dev.twitter.com/discussions/13935
 * http://oauth.net/core/1.0a/
 * 
 * @author feiyu
 */

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URLEncoder;
import java.security.InvalidKeyException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.Calendar;
import java.util.StringTokenizer;
import java.util.UUID;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.net.ssl.SSLContext;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.ConnectionReuseStrategy;
import org.apache.http.Consts;
import org.apache.http.HttpException;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.DefaultBHttpClientConnection;
import org.apache.http.impl.DefaultConnectionReuseStrategy;
import org.apache.http.message.BasicHttpEntityEnclosingRequest;
import org.apache.http.protocol.HttpCoreContext;
import org.apache.http.protocol.HttpProcessor;
import org.apache.http.protocol.HttpProcessorBuilder;
import org.apache.http.protocol.HttpRequestExecutor;
import org.apache.http.protocol.RequestConnControl;
import org.apache.http.protocol.RequestContent;
import org.apache.http.protocol.RequestExpectContinue;
import org.apache.http.protocol.RequestTargetHost;
import org.apache.http.protocol.RequestUserAgent;
import org.apache.http.util.EntityUtils;

import com.feiyu.elasticsearch.SerializeBeans2JSON;
import com.feiyu.springmvc.model.TwitterResponse;
import com.feiyu.utils.GlobalVariables;
import com.feiyu.utils.InitializeWCR;

public class SignInWithTwitterService {
  /* 
   * Example request (Authorization header has been wrapped):
POST /oauth/request_token HTTP/1.1
User-Agent: themattharris' HTTP Client 
Host: api.twitter.com
Accept: <*>/<*>
Authorization: 
        OAuth oauth_callback="http%3A%2F%2Flocalhost%2Fsign-in-with-twitter%2F",
              oauth_consumer_key="cChZNFj6T5R0TigYB9yd1w",
              oauth_nonce="ea9ec8429b68d6b77cd5600adbbb0456",
              oauth_signature="F1Li3tvehgcraF8DMJ7OyxO4w9Y%3D",
              oauth_signature_method="HMAC-SHA1",
              oauth_timestamp="1318467427",
              oauth_version="1.0"
   */
  public String obtainingARequestToken (String callbackURL) throws IOException, HttpException, NoSuchAlgorithmException, KeyManagementException, InvalidKeyException {
    System.out.println("\n---------------obtainingARequestToken---------------");

    String METHOD = "POST";
    String twitter_request_token = "https://api.twitter.com/oauth/request_token";
    String twitter_request_token_host = "api.twitter.com";
    String twitter_request_token_path = "/oauth/request_token";
    String oauth_callback = URLEncoder.encode(callbackURL, "UTF-8"); 
    String oauth_nonce = UUID.randomUUID().toString().replace("-", ""); // a nonce is an arbitrary number used only once in a cryptographic communication
    String oauth_signature_method = "HMAC-SHA1";
    String oauth_signature_method_Mac = "HmacSHA1";
    String oauth_timestamp = (new Long(Calendar.getInstance().getTimeInMillis()/1000)).toString();
    String oauth_version = "1.0";
    String paraString = 
        "oauth_callback=" + oauth_callback
        + "&oauth_consumer_key=" + GlobalVariables.TWT_APP_OAUTH_CONSUMER_KEY 
        + "&oauth_nonce=" + oauth_nonce 
        + "&oauth_signature_method=" + oauth_signature_method
        + "&oauth_timestamp=" + oauth_timestamp 
        + "&oauth_version="+ oauth_version;			
    String signature_basestring = METHOD 
        + "&" + URLEncoder.encode(twitter_request_token, "UTF-8") 
        + "&" + URLEncoder.encode(paraString, "UTF-8");

    //Response parameters
    String oauth_token = null;
    String oauth_token_secret = null;
    String oauth_callback_confirmed = null;

    // https://dev.twitter.com/docs/auth/creating-signature
    // http://oauth.net/core/1.0/#signing_process
    Mac mac = Mac.getInstance(oauth_signature_method_Mac); // HMAC-SHA1
    String keyString = GlobalVariables.TWT_APP_OAUTH_CONSUMER_SECRET + "&";
    SecretKeySpec secretkey = new SecretKeySpec(keyString.getBytes(), oauth_signature_method_Mac);//mac.getAlgorithm()
    mac.init(secretkey);
    String oauth_signature = new String(Base64.encodeBase64(mac.doFinal(signature_basestring.getBytes()))).trim();
    // http://stackoverflow.com/questions/5997955/library-for-generating-hmac-sha1-oauth-signature-on-android
    //		System.out.println("\nbaseString --> "+signature_basestring);
    System.out.println("obtainingARequestToken->oauth_signature --> "+oauth_signature);

    String http_msg_header_value = 
        "OAuth "
            + "oauth_callback=\""+ oauth_callback//URLEncoder.encode(callback_string,"UTF-8")
            + "\",oauth_consumer_key=\"" + GlobalVariables.TWT_APP_OAUTH_CONSUMER_KEY
            + "\",oauth_nonce=\"" + oauth_nonce 
            + "\",oauth_signature=\"" + URLEncoder.encode(oauth_signature, "UTF-8") 
            + "\",oauth_signature_method=\"" + oauth_signature_method//HMAC-SHA1
            + "\",oauth_timestamp=\"" + oauth_timestamp 
            + "\",oauth_version=\"" + oauth_version//1.0
            + "\"";
    //		System.out.println("authorization_header_string=" + http_msg_header_value); 	// print out authorization_header_string for error checking

    HttpProcessor httpproc = HttpProcessorBuilder.create()
        .add(new RequestContent())
        .add(new RequestTargetHost())
        .add(new RequestConnControl())
        .add(new RequestUserAgent("HttpCore/1.1")) //@ User-Agent: themattharris' HTTP Client
        .add(new RequestExpectContinue(false)).build(); //true

    HttpRequestExecutor httpexecutor = new HttpRequestExecutor();
    HttpCoreContext coreContext = HttpCoreContext.create();
    HttpHost host = new HttpHost(twitter_request_token_host, 443); 
    //api.twitter.com use, 80 -> HTTP, 443 -> HTTPS
    // 80 -> HTTP for debugging
    // twitter requires 443 https
    coreContext.setTargetHost(host);
    DefaultBHttpClientConnection conn = new DefaultBHttpClientConnection(8 * 1024); //@
    ConnectionReuseStrategy connStrategy = DefaultConnectionReuseStrategy.INSTANCE;

    TwitterResponse jsonTwitterResponseMsg = new TwitterResponse();

    try {
      if (!conn.isOpen()) {
        //				Socket socket = new Socket(host.getHostName(), host.getPort());
        //				conn.bind(socket);

        SSLContext sslcontext = SSLContext.getInstance("TLS");
        sslcontext.init(null, null, null);
        Socket socket = sslcontext.getSocketFactory().createSocket();
        socket.connect(new InetSocketAddress(host.getHostName(), host.getPort()));
        conn.bind(socket);
      }

      BasicHttpEntityEnclosingRequest request = new BasicHttpEntityEnclosingRequest(METHOD, twitter_request_token_path); // POST /oauth/request_token
      request.setEntity(new StringEntity("", ContentType.create("application/x-www-form-urlencoded", Consts.UTF_8))); 
      // In HTTP there are two ways to POST data: application/x-www-form-urlencoded and multipart/form-data. 
      request.addHeader("Authorization", http_msg_header_value);
      System.out.println("\n>>>>> Request URI: " + request.toString()); //@

      httpexecutor.preProcess(request, httpproc, coreContext);
      HttpResponse response = httpexecutor.execute(request, conn, coreContext);
      httpexecutor.postProcess(response, httpproc, coreContext);

      System.out.println("\n<<<<< Response MSG: " + response.getStatusLine() + "\n"+response.toString());

      if (response.getStatusLine().toString().indexOf("200") == -1) {
        System.out.println("Response failed!!!");
        jsonTwitterResponseMsg.setTwitterResponseStatus("Failure");
        jsonTwitterResponseMsg.setTwitterResponseMessage("The HTTP status of the response from twitter is not 200!");
      } else {
        //  Your application should verify that oauth_callback_confirmed is true and store the other two values for the next steps.
        String responseText = EntityUtils.toString(response.getEntity());
        System.out.println("\nresponseText -> " + responseText);

        oauth_callback_confirmed = responseText.substring(responseText.indexOf("oauth_callback_confirmed=")+25);
        if (oauth_callback_confirmed.equals("true")) {
          StringTokenizer strTokenizer = new StringTokenizer(responseText, "&");
          String curToken = "";
          while (strTokenizer.hasMoreTokens()) {
            curToken = strTokenizer.nextToken();
            if (curToken.startsWith("oauth_token=")) {
              oauth_token = curToken.substring(curToken.indexOf("=")+1);
              System.out.println("oauth_token="+oauth_token);
            } else if (curToken.startsWith("oauth_token_secret=")) {
              oauth_token_secret = curToken.substring(curToken.indexOf("=")+1);
              System.out.println("oauth_token_secret="+oauth_token_secret);
            }
          }
          jsonTwitterResponseMsg.setTwitterResponseStatus("Success");
          jsonTwitterResponseMsg.setTwitterResponseMessage("Got the oauth_token");
          jsonTwitterResponseMsg.setOauthToken(oauth_token);
        } else {
          System.out.println("oauth_token_secret=false!!!");
          System.out.println("oauth_token="+oauth_token);
          System.out.println("oauth_token_secret="+oauth_token_secret);

          jsonTwitterResponseMsg.setTwitterResponseStatus("Failure");
          jsonTwitterResponseMsg.setTwitterResponseMessage("The HTTP status of the response from twitter is 200, but the oauth_callback_confirmed is false!");
        }

      }

      System.out.println("==============");
      if (!connStrategy.keepAlive(response, coreContext)) {
        conn.close();
      } else {
        System.out.println("Connection kept alive...");
      }
    } finally {
      conn.close();
    }
    SerializeBeans2JSON sb2json = new SerializeBeans2JSON();
    String jsonResponseMsg = sb2json.serializeBeans2JSON(jsonTwitterResponseMsg);
    System.out.println("jsonResponseMsg->"+jsonResponseMsg);
    return jsonResponseMsg;
  }

  public String converRequestToken2AccessToken(String oauth_token, String oauth_verifier) throws Exception {
    System.out.println("\n---------------converRequestToken2AccessToken---------------");

    String METHOD = "POST";
    String twitter_access_token = "https://api.twitter.com/oauth/access_token";
    String twitter_access_token_host = "api.twitter.com";
    String twitter_access_token_path = "/oauth/access_token";
    String oauth_nonce = UUID.randomUUID().toString().replace("-", ""); // a nonce is an arbitrary number used only once in a cryptographic communication
    String oauth_signature_method = "HMAC-SHA1";
    String oauth_signature_method_Mac = "HmacSHA1";
    String oauth_timestamp = (new Long(Calendar.getInstance().getTimeInMillis()/1000)).toString();
    String oauth_version = "1.0";
    String paraString = 
        "&oauth_consumer_key=" + GlobalVariables.TWT_APP_OAUTH_CONSUMER_KEY 
        + "&oauth_nonce=" + oauth_nonce 
        + "&oauth_signature_method=" + oauth_signature_method
        + "&oauth_timestamp=" + oauth_timestamp 
        + "&oauth_token=" + oauth_token 
        + "&oauth_version="+ oauth_version;			
    String signature_basestring = METHOD 
        + "&" + URLEncoder.encode(twitter_access_token, "UTF-8") 
        + "&" + URLEncoder.encode(paraString, "UTF-8");

    //Response parameters
    String new_oauth_token = null;
    String new_oauth_token_secret = null;
    String user_id = null;
    String screen_name = null;

    // https://dev.twitter.com/docs/auth/creating-signature
    // http://oauth.net/core/1.0/#signing_process
    Mac mac = Mac.getInstance(oauth_signature_method_Mac); // HMAC-SHA1
    String keyString = GlobalVariables.TWT_APP_OAUTH_CONSUMER_SECRET + "&";
    SecretKeySpec secretkey = new SecretKeySpec(keyString.getBytes(), oauth_signature_method_Mac);//mac.getAlgorithm()
    mac.init(secretkey);
    String oauth_signature = new String(Base64.encodeBase64(mac.doFinal(signature_basestring.getBytes()))).trim();
    // http://stackoverflow.com/questions/5997955/library-for-generating-hmac-sha1-oauth-signature-on-android
    //		System.out.println("\nbaseString --> "+signature_basestring);
    System.out.println("converRequestToken2AccessToken->oauth_signature --> "+oauth_signature);

    String http_msg_header_value = 
        "OAuth "
            + "oauth_consumer_key=\"" + GlobalVariables.TWT_APP_OAUTH_CONSUMER_KEY
            + "\",oauth_nonce=\"" + oauth_nonce 
            + "\",oauth_signature=\"" + URLEncoder.encode(oauth_signature, "UTF-8") 
            + "\",oauth_signature_method=\"" + oauth_signature_method//HMAC-SHA1
            + "\",oauth_timestamp=\"" + oauth_timestamp 
            + "\",oauth_token=\"" + oauth_token 
            + "\",oauth_version=\"" + oauth_version//1.0
            + "\"";
    //		System.out.println("authorization_header_string=" + http_msg_header_value); 	// print out authorization_header_string for error checking

    HttpProcessor httpproc = HttpProcessorBuilder.create()
        .add(new RequestContent())
        .add(new RequestTargetHost())
        .add(new RequestConnControl())
        .add(new RequestUserAgent("HttpCore/1.1")) //@ User-Agent: themattharris' HTTP Client
        .add(new RequestExpectContinue(false)).build(); //true

    HttpRequestExecutor httpexecutor = new HttpRequestExecutor();
    HttpCoreContext coreContext = HttpCoreContext.create();
    HttpHost host = new HttpHost(twitter_access_token_host, 443); 
    //api.twitter.com use, 80 -> HTTP, 443 -> HTTPS
    // 80 -> HTTP for debugging
    // twitter requires 443 https
    coreContext.setTargetHost(host);
    DefaultBHttpClientConnection conn = new DefaultBHttpClientConnection(8 * 1024); //@
    ConnectionReuseStrategy connStrategy = DefaultConnectionReuseStrategy.INSTANCE;

    try {
      if (!conn.isOpen()) {
        //				Socket socket = new Socket(host.getHostName(), host.getPort());
        //				conn.bind(socket);

        SSLContext sslcontext = SSLContext.getInstance("TLS");
        sslcontext.init(null, null, null);
        Socket socket = sslcontext.getSocketFactory().createSocket();
        socket.connect(new InetSocketAddress(host.getHostName(), host.getPort()));
        conn.bind(socket);
      }

      BasicHttpEntityEnclosingRequest request = new BasicHttpEntityEnclosingRequest(METHOD, twitter_access_token_path); // POST /oauth/access_token
      request.setEntity(new StringEntity("oauth_verifier=" + URLEncoder.encode(oauth_verifier, "UTF-8"), ContentType.create("application/x-www-form-urlencoded", Consts.UTF_8))); 
      // In HTTP there are two ways to POST data: application/x-www-form-urlencoded and multipart/form-data. 
      request.addHeader("Authorization", http_msg_header_value);
      System.out.println("\n>>>>> Request URI: " + request.toString()); //@

      httpexecutor.preProcess(request, httpproc, coreContext);
      HttpResponse response = httpexecutor.execute(request, conn, coreContext);
      httpexecutor.postProcess(response, httpproc, coreContext);

      System.out.println("\n<<<<< Response MSG: " + response.getStatusLine() + "\n"+response.toString());

      if (response.getStatusLine().toString().indexOf("200") == -1) {
        System.out.println("Response failed!!!");
        //				jsonTwitterResponseMsg.setTwitterResponseStatus("Failure");
        //				jsonTwitterResponseMsg.setTwitterResponseMessage("The HTTP status of the response from twitter is not 200!");
      } else {
        //  Your application should verify that oauth_callback_confirmed is true and store the other two values for the next steps.
        String responseText = EntityUtils.toString(response.getEntity());
        System.out.println("\nresponseText -> " + responseText);

        StringTokenizer strTokenizer = new StringTokenizer(responseText, "&");
        String curToken = "";
        while (strTokenizer.hasMoreTokens()) {
          curToken = strTokenizer.nextToken();
          if (curToken.startsWith("oauth_token=")) {
            new_oauth_token = curToken.substring(curToken.indexOf("=")+1);
            System.out.println("new_oauth_token="+new_oauth_token);
          } else if (curToken.startsWith("oauth_token_secret=")) {
            new_oauth_token_secret = curToken.substring(curToken.indexOf("=")+1);
            System.out.println("new_oauth_token_secret="+new_oauth_token_secret);
          } else if (curToken.startsWith("user_id=")) {
            user_id = curToken.substring(curToken.indexOf("=")+1);
            System.out.println("user_id="+user_id);
          } else if (curToken.startsWith("screen_name=")) {
            screen_name = curToken.substring(curToken.indexOf("=")+1);
            System.out.println("screen_name="+screen_name);
          }
        }
        GlobalVariables.AST_CASSANDRA_UL.insertDataToDB(user_id, new_oauth_token, new_oauth_token_secret, screen_name);

        //				jsonTwitterResponseMsg.setTwitterResponseStatus("Success");
        //				jsonTwitterResponseMsg.setTwitterResponseMessage("Got the oauth_token");
        //				jsonTwitterResponseMsg.setOauthToken(oauth_token);
      }

      System.out.println("==============");
      if (!connStrategy.keepAlive(response, coreContext)) {
        conn.close();
      } else {
        System.out.println("Connection kept alive...");
      }
    } finally {
      conn.close();
    }
    return user_id;
  }

  public static void main(String[] argv) throws Exception {
    InitializeWCR initWCR = new InitializeWCR();
    initWCR.getWiseCrowdRecConfigInfo();
    initWCR.signInWithTwitterGetAppOauth();

    SignInWithTwitterService s = new SignInWithTwitterService();
    s.obtainingARequestToken("http://127.0.0.1:9999/WiseCrowdRec");
    s.converRequestToken2AccessToken("", "");
  }
}