package com.example.oktravelapplictaion.login;

public class LoginResult {
    private String userName;
    private String password;
    private String email;
    private String phoneNumber;
    private String imageUrl;
    private String accessToken;
    private String refreshToken;

    public String getRefreshToken() {
        return refreshToken;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public String getUserName() {
        return userName;
    }

    public String getPassword() {
        return password;
    }

    public String getEmail() {
        return email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getImageUrl() {
        return imageUrl;
    }
}
