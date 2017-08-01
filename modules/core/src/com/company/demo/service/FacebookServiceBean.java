package com.company.demo.service;

import com.company.demo.core.FacebookConfig;
import com.google.common.base.Strings;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.haulmont.bali.util.URLEncodeUtils;
import com.haulmont.cuba.core.global.Configuration;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.BasicHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.io.IOException;

@Service(FacebookService.NAME)
public class FacebookServiceBean implements FacebookService {

    protected static final String FB_ACCESS_TOKEN_PATH = "https://graph.facebook.com/oauth/access_token?";

    @Inject
    private Configuration configuration;

    @Override
    public String getLoginUrl(String appUrl, OAuth2ResponseType responseType) {
        FacebookConfig config = configuration.getConfig(FacebookConfig.class);

        String startUrl = "http://www.facebook.com/dialog/oauth";
        String appId = config.getFacebookAppId();
        String scope = config.getFacebookAppScope();

        String redirectUrl = getEncodedRedirectUrl(appUrl);
        return startUrl + "?client_id=" + appId +
                "&response_type=" + responseType.getId() +
                "&redirect_uri=" + redirectUrl +
                "&scope=" + scope;
    }

    @Override
    public FacebookUserData getUserData(String appUrl, String code) {
        String accessToken = getAccessToken(code, appUrl);
        String userDataJson = getUserDataAsJson(accessToken);

        JsonParser parser = new JsonParser();

        JsonObject jsonObject = parser.parse(userDataJson).getAsJsonObject();
        String id = Strings.nullToEmpty(jsonObject.get("id").getAsString());
        String name = Strings.nullToEmpty(jsonObject.get("name").getAsString());
        String email = Strings.nullToEmpty(jsonObject.get("email").getAsString().replace("\u0040", "@"));

        return new FacebookUserData(id, name, email);
    }

    private String getAccessToken(String code, String appUrl) {
        String accessTokenPath = getAccessTokenPath(code, appUrl);
        String response = requestAccessToken(accessTokenPath);
        JsonParser parser = new JsonParser();
        JsonObject asJsonObject = parser.parse(response).getAsJsonObject();
        return asJsonObject.get("access_token").getAsString();
    }

    private String getUserDataAsJson(String accessToken) {
        FacebookConfig config = configuration.getConfig(FacebookConfig.class);

        String fields = config.getFacebookFields();
        String format = "json";
        String url = "https://graph.facebook.com/v2.8/me?access_token=" + accessToken +
                "&fields=" + fields +
                "&format=" + format;
        return requestUserData(url);
    }

    private String getEncodedRedirectUrl(String appUrl) {
        return URLEncodeUtils.encodeUtf8(appUrl);
    }

    private String requestUserData(String url) {
        HttpClientConnectionManager cm = new BasicHttpClientConnectionManager();
        HttpClient httpClient = HttpClientBuilder.create().setConnectionManager(cm).build();

        HttpGet getRequest = new HttpGet(url);
        try {
            HttpResponse httpResponse = httpClient.execute(getRequest);
            if (httpResponse.getStatusLine().getStatusCode() != 200) {
                throw new RuntimeException("Unable to access Facebook API. Response HTTP status: "
                        + httpResponse.getStatusLine().getStatusCode());
            }
            return EntityUtils.toString(httpResponse.getEntity());
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        } finally {
            getRequest.releaseConnection();
        }
    }

    private String getAccessTokenPath(String code, String appUrl) {
        FacebookConfig config = configuration.getConfig(FacebookConfig.class);

        String redirectUrl = getEncodedRedirectUrl(appUrl);
        String appId = config.getFacebookAppId();
        String appSecret = config.getFacebookAppSecret();

        return FB_ACCESS_TOKEN_PATH + "client_id=" + appId
                + "&redirect_uri=" + redirectUrl +
                "&client_secret=" + appSecret + "&code=" + code;
    }

    private String requestAccessToken(String accessTokenPath) {
        HttpClientConnectionManager cm = new BasicHttpClientConnectionManager();
        HttpClient httpClient = HttpClientBuilder.create().setConnectionManager(cm).build();

        HttpGet getRequest = new HttpGet(accessTokenPath);
        try {
            HttpResponse httpResponse = httpClient.execute(getRequest);
            if (httpResponse.getStatusLine().getStatusCode() != 200) {
                throw new RuntimeException("Unable to access FB API. Response HTTP status: "
                        + httpResponse.getStatusLine().getStatusCode());
            }
            return EntityUtils.toString(httpResponse.getEntity());
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        } finally {
            getRequest.releaseConnection();
        }
    }
}