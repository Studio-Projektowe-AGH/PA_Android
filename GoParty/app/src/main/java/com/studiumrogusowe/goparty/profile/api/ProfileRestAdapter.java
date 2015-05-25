package com.studiumrogusowe.goparty.profile.api;

import retrofit.RestAdapter;

public class ProfileRestAdapter {
    private static volatile ProfileRestAdapter profileRestAdapter;

    private RestAdapter restAdapter;
    private ProfileApi profileApi;

    private ProfileRestAdapter() {
    }

    public static ProfileRestAdapter getInstance() {
        if (profileRestAdapter == null) {
            synchronized (ProfileRestAdapter.class) {
                profileRestAdapter = new ProfileRestAdapter();
                profileRestAdapter.restAdapter = new RestAdapter.Builder().setEndpoint(ProfileUtilities.API_PATH)
                        .setLogLevel(RestAdapter.LogLevel.FULL).build();
                profileRestAdapter.profileApi = profileRestAdapter.restAdapter.create(ProfileApi.class);
            }
        }
        return profileRestAdapter;
    }


    public ProfileApi getProfileApi() {
        return profileApi;
    }
}
