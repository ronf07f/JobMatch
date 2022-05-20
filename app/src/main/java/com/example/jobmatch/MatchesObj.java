package com.example.jobmatch;

public class MatchesObj {
    private String userId;

    public MatchesObj(String username) {
        this.userId = username;
    }

    public String getUserid() {
        return userId;
    }

    public void setUserId(String username) {
        this.userId = username;
    }
}
