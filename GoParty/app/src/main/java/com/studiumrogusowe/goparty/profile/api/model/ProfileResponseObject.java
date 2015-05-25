package com.studiumrogusowe.goparty.profile.api.model;

import java.util.List;

/**
 * Created by Piotrek on 2015-05-23.
 */
public class ProfileResponseObject {

    private String first_name;
    private String last_name;
    private List<String> favourite_genres;
    private List<String> favourite_bands;
    private String picture_url;

    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public List<String> getFavourite_genres() {
        return favourite_genres;
    }

    public void setFavourite_genres(List<String> favourite_genres) {
        this.favourite_genres = favourite_genres;
    }

    public List<String> getFavourite_bands() {
        return favourite_bands;
    }

    public void setFavourite_bands(List<String> favourite_bands) {
        this.favourite_bands = favourite_bands;
    }

    public String getPicture_url() {
        return picture_url;
    }

    public void setPicture_url(String picture_url) {
        this.picture_url = picture_url;
    }
}
