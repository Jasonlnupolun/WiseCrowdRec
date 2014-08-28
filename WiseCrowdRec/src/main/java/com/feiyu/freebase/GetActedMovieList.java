package com.feiyu.freebase;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URLEncoder;
import java.util.Arrays;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeRequestUrl;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeTokenRequest;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson.JacksonFactory;

public class GetActedMovieList {
	private static final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
    private static final JsonFactory JSON_FACTORY = new JacksonFactory();

    // User settings
    private static final String CLIENT_ID = "YOUR-CLIENT-ID";
    private static final String CLIENT_SECRET = "YOUR-CLIENT-SECRET";
    private static final String CALLBACK_URL = "urn:ietf:wg:oauth:2.0:oob";

    private static final String SCOPE = "https://www.googleapis.com/auth/freebase";
    private static final String MQLWRITE = "https://www.googleapis.com/freebase/v1sandbox/mqlwrite";
    
    public String requestAuthCode() throws IOException {
        String url = new GoogleAuthorizationCodeRequestUrl(CLIENT_ID, CALLBACK_URL, Arrays.asList(SCOPE)).setState("/profile").build();
        System.out.println("Go to this url, login, and past the code: " + url);

        InputStreamReader converter = new InputStreamReader(System.in);
        BufferedReader in = new BufferedReader(converter);
        return in.readLine();
    }
    public GoogleTokenResponse requestAuthToken(String authCode) throws IOException {
        return new GoogleAuthorizationCodeTokenRequest(HTTP_TRANSPORT, JSON_FACTORY,
                CLIENT_ID, CLIENT_SECRET, authCode, CALLBACK_URL).execute();
    }

    public static void main(String[] args) {
    	GetActedMovieList getActedMovieList = new GetActedMovieList();
        try {
            String auth = getActedMovieList.requestAuthCode();
            GoogleTokenResponse token = getActedMovieList.requestAuthToken(auth);

            GoogleCredential cred = new GoogleCredential.Builder().setTransport(HTTP_TRANSPORT)
                    .setJsonFactory(JSON_FACTORY)
                    .setClientSecrets(CLIENT_ID, CLIENT_SECRET)
                    .build();
            cred.setAccessToken(token.getAccessToken());
            cred.setRefreshToken(token.getRefreshToken());

            System.out.println(token.toString());
            System.out.println(cred.toString());

            String query = "{\"create\":\"unconditional\",\"id\":null,\"name\":\"Nowhere\",\"type\":\"/location/location\"}";
            String url = MQLWRITE + "?oauth_token=" + cred.getAccessToken() + "&query=" + URLEncoder.encode(query, "UTF-8");

            HttpClient client = HttpClientBuilder.create().build();
            HttpGet get = new HttpGet(url);
            HttpResponse response = client.execute(get);

            System.out.println("Attempting: " + get.getURI().toString());
            System.out.println("Response: " + EntityUtils.toString(response.getEntity()));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
