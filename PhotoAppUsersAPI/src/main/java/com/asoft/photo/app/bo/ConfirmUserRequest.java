package com.asoft.photo.app.bo;

import com.google.gson.JsonObject;

public class ConfirmUserRequest {
    private JsonObject userDetail;
    private AppClientDetail appClientDetail;
    private String confirmationCode;

    public JsonObject getUserDetail() {
        return userDetail;
    }

    public void setUserDetail(JsonObject userDetail) {
        this.userDetail = userDetail;
    }

    public AppClientDetail getAppClientDetail() {
        return appClientDetail;
    }

    public void setAppClientDetail(AppClientDetail appClientDetail) {
        this.appClientDetail = appClientDetail;
    }

    public String getConfirmationCode() {
        return confirmationCode;
    }

    public void setConfirmationCode(String confirmationCode) {
        this.confirmationCode = confirmationCode;
    }
}
