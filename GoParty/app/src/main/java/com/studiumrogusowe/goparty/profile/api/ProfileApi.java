package com.studiumrogusowe.goparty.profile.api;

import com.studiumrogusowe.goparty.authorization.api.model.AuthFacebookLoginBody;
import com.studiumrogusowe.goparty.authorization.api.model.AuthRefreshBodyObject;
import com.studiumrogusowe.goparty.authorization.api.model.AuthResponseObject;
import com.studiumrogusowe.goparty.profile.api.model.ProfileResponseObject;

import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.Header;
import retrofit.http.POST;

/**
 * Created by Piotrek on 2015-05-23.
 */
public interface ProfileApi {

    @GET("/profile")
    void getProfile(@Header("Authorization") String token,Callback<ProfileResponseObject> reponseCallback);

    @POST("/auth/signin/facebook")
    void getToken(@Body AuthFacebookLoginBody authFacebookLoginBody, Callback<AuthResponseObject> reponseCallback);
}
