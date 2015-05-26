package com.studiumrogusowe.goparty.profile.api.model;

import android.widget.EditText;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Piotrek on 2015-05-26.
 */
public class ProfileBodyObject {

    private String first_name;
    private String last_name;
    private List<String> favourite_genres;
    private List<String> favourite_bands;


    public List<String> convertEditTextToList(EditText et){

        List<String> list = new ArrayList<String>();

        String[] splitted = et.getText().toString().split(",");

        for (String s : splitted){

            list.add(s.trim());
        }
        return list;
    }


    // getters and setters
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
}
