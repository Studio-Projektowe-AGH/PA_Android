package com.studiumrogusowe.goparty.authorization.api.model;

/**
 * Created by O10 on 10.05.15.
 */
public class AuthFacebookLoginBody {
    private String accessToken;

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }
}
