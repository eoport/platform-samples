package com.geocento.projects.eoport.apiclientexample;

import com.geocento.projects.eoport.api.*;
import com.geocento.projects.eoport.utils.ApiClient;
import com.github.scribejava.core.builder.ServiceBuilder;
import com.github.scribejava.core.model.OAuth2AccessToken;
import com.github.scribejava.core.oauth.OAuth20Service;

import java.util.Calendar;
import java.util.Date;

public class EOPortApiUtil {

    static private String authUrl = "https://keycloak.svc.eoport.eu/";
    static private String authRealm = "eoport";
    private static String apiKey = System.getProperty("eoportapikey"); //"eoportapiconfidential";
    private static String apiSecret = System.getProperty("eoportapisecret"); //"90be5bdb-6dab-4a4f-808f-731eae5fd933";
    private static String apiBaseUrl = "https://webapp.dev02.eoport.eu/api";

    private String userName;
    private String password;

    private OAuth2AccessToken token;
    private Date tokenValidity;

    private OAuth20Service service;

    public EOPortApiUtil(String userName, String password) {
        this.userName = userName;
        this.password = password;
        service = new ServiceBuilder(apiKey)
                .apiSecret(apiSecret)
                .debugStream(System.out)
                .build(TokenAPI.getInstance(authUrl, authRealm));
    }

    private String getToken() throws Exception {
        boolean hasToken = token != null;
        // token is valid so return access token
        if(hasToken && (tokenValidity == null || tokenValidity.after(new Date()))) {
            return token.getAccessToken();
        }
        // token needs refreshing
        if(hasToken) {
            String refreshToken = token.getRefreshToken();
            if (refreshToken != null) {
                // try to refresh instead
                try {
                    token = service.refreshAccessToken(refreshToken);
                    updateValidity(token);
                    return token.getAccessToken();
                } catch (Exception e) {

                }
            }
        }
        // no token or refresh didn't work so ask for a new token
        token = service.getAccessTokenPasswordGrant(
                userName,
                password,
                "openid profile");
        updateValidity(token);

        return token.getAccessToken();
    }

    private void updateValidity(OAuth2AccessToken token) {
        Integer expiresIn = token.getExpiresIn();
        if(expiresIn == null) {
            // make the token permanent
            tokenValidity = null;
            return;
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        // use 30s as a buffer
        calendar.add(Calendar.SECOND, expiresIn - 30);
        tokenValidity = calendar.getTime();
    }

    private ApiClient getAPIClient() throws Exception {
        ApiClient apiClient = new ApiClient();
//        apiClient.setBasePath(Utils.buildUrl(apiBaseUrl, basePath));
        apiClient.setBasePath(apiBaseUrl);
        apiClient.setAccessToken(getToken());
        return apiClient;
    }

    public AccountResourceApi getAccountApi() throws Exception {
        return new AccountResourceApi(getAPIClient());
    }

    public ServiceProvidersResourceApi getServiceProvidersResourceApi() throws Exception {
        return new ServiceProvidersResourceApi(getAPIClient());
    }

    public ServicesResourceApi getServicesResourceApi() throws Exception {
        return new ServicesResourceApi(getAPIClient());
    }

    public SubscriptionsResourceApi getSubscriptionsResourceApi() throws Exception {
        return new SubscriptionsResourceApi(getAPIClient());
    }

    public InvoicesResourceApi getInvoicesResourceApi() throws Exception {
        return new InvoicesResourceApi(getAPIClient());
    }
}
