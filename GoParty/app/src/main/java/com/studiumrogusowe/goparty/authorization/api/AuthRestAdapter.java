package com.studiumrogusowe.goparty.authorization.api;

import retrofit.RestAdapter;

/**
 * Created by O10 on 16.04.15.
 * Adapter do wykonywania zapytań restowych jako singleton
 */
public class AuthRestAdapter {
    private static volatile AuthRestAdapter authenticatorRestAdapter;

    private RestAdapter restAdapter;
    private AuthApi authApi;

    private AuthRestAdapter() {
    }

    public static AuthRestAdapter getInstance() {
        if (authenticatorRestAdapter == null) {
            synchronized (AuthRestAdapter.class) {
                authenticatorRestAdapter = new AuthRestAdapter();
                authenticatorRestAdapter.restAdapter = new RestAdapter.Builder().setEndpoint(AuthorizationUtilities.API_PATH)
                        .setLogLevel(RestAdapter.LogLevel.FULL).build();
                authenticatorRestAdapter.authApi = authenticatorRestAdapter.restAdapter.create(AuthApi.class);
            }
        }
        return authenticatorRestAdapter;
    }


    public AuthApi getAuthApi() {
        return authApi;
    }
}
