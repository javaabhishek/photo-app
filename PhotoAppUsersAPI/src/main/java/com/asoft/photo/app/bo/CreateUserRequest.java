package com.asoft.photo.app.bo;

import com.google.gson.JsonObject;

public class CreateUserRequest {
    private JsonObject userDetail;
    private AppClientDetail appClientDetails;

    public JsonObject getUserDetail() {
        return userDetail;
    }

    public void setUserDetail(JsonObject userDetail) {
        this.userDetail = userDetail;
    }

    public AppClientDetail getAppClientDetails() {
        return appClientDetails;
    }

    public void setAppClientDetails(AppClientDetail appClientDetails) {
        this.appClientDetails = appClientDetails;
    }
}
