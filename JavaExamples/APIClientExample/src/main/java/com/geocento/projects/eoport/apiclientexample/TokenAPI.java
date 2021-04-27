package com.geocento.projects.eoport.apiclientexample;

import com.github.scribejava.core.builder.api.DefaultApi20;

import java.util.HashMap;

public class TokenAPI extends DefaultApi20 {

    private String apiUrl;
    private String realm;

    public TokenAPI(String apiUrl, String realm) {
        this.apiUrl = apiUrl;
        this.realm = realm;
    }

    private static final HashMap<String, TokenAPI> tokenAPIInstances = new HashMap<String, TokenAPI>();

    public static TokenAPI getInstance(String apiUrl, String realm) {
        TokenAPI instance = tokenAPIInstances.get(apiUrl);
        if(instance == null) {
            instance = new TokenAPI(apiUrl, realm);
            tokenAPIInstances.put(apiUrl, instance);
        }
        return instance;
    }
 
    @Override
    public String getAccessTokenEndpoint() {
        return Utils.buildUrl(apiUrl, "/auth/realms/" + realm + "/protocol/openid-connect/token");
    }

    @Override
    protected String getAuthorizationBaseUrl() {
        return null;
    }

}