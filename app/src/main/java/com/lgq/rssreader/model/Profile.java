package com.lgq.rssreader.model;

/**
 * Created by redel on 2015-09-13.
 */
public class Profile {
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isEvernoteConnected() {
        return evernoteConnected;
    }

    public void setEvernoteConnected(boolean evernoteConnected) {
        this.evernoteConnected = evernoteConnected;
    }

    public String getFamilyName() {
        return familyName;
    }

    public void setFamilyName(String familyName) {
        this.familyName = familyName;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getGivenName() {
        return givenName;
    }

    public void setGivenName(String givenName) {
        this.givenName = givenName;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public String getLocalPicture() {
        return localPicture;
    }

    public void setLocalPicture(String localPicture) {
        this.localPicture = localPicture;
    }

    public boolean isPocketConnected() {
        return pocketConnected;
    }

    public void setPocketConnected(boolean pocketConnected) {
        this.pocketConnected = pocketConnected;
    }

    public String getReader() {
        return reader;
    }

    public void setReader(String reader) {
        this.reader = reader;
    }

    public String getWave() {
        return wave;
    }

    public void setWave(String wave) {
        this.wave = wave;
    }

    private String email;
    private boolean evernoteConnected;
    private String familyName;
    private String gender;
    private String givenName;
    private String account;
    private String id;
    private String locale;
    private String picture;
    private String localPicture;
    private boolean pocketConnected;
    private String reader;
    private String wave;
}
