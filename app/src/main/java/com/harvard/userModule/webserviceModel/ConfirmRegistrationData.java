package com.harvard.userModule.webserviceModel;

/**
 * Created by Rohit on 3/1/2017.
 */

public class ConfirmRegistrationData {
    private boolean verified;
    private String message;
    private String userId;

    public boolean isVerified() {
        return verified;
    }

    public void setVerified(boolean verified) {
        this.verified = verified;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
