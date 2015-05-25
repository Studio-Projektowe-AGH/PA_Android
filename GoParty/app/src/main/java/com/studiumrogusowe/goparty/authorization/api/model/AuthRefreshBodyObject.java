package com.studiumrogusowe.goparty.authorization.api.model;

/**
 * Created by O10 on 16.04.15.
 * To jak będzie wyglądało body zależy już od panów z API
 * przykładowo możemy wysyłać sam refresh token tak jak w przykładzie
 * Może się skończyć też tak że nie będzie refresha tylko cały czas będziemy się normalnie logować (request logowania) - wtedy to do wywalenia
 */
public class AuthRefreshBodyObject {
    private String ref_token;

    public AuthRefreshBodyObject(String ref_token) {
        this.ref_token = ref_token;
    }

    public AuthRefreshBodyObject() {
    }

    public String getRef_token() {
        return ref_token;
    }

    public void setRef_token(String ref_token) {
        this.ref_token = ref_token;
    }
}
