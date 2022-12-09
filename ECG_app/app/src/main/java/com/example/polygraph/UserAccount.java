package com.example.polygraph;


/**
 * 사용자 계정 정보 모델 클래스
 */

public class UserAccount {

    private String idToken;
    private String emailId;
    private String password;


    public UserAccount() { }





    public String getIdToken() {
        return idToken;
    }

    public void setIdToken(String idToken) {
        this.idToken = idToken;
    }

    public void setEmailId(String emailId) {
        this.emailId = emailId;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPassword() {
        return password;
    }

    public String getEmailId() {
        return emailId;
    }
}

