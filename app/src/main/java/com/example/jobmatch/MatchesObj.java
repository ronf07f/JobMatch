package com.example.jobmatch;

public class MatchesObj {
    private final String matchId;
    private final String matchName;
    private final String matchImageUrl;
    private final String matchPhone;
    private final String userName;

    public  MatchesObj(String matchId, String matchName, String matchImageUrl,String phone,String userName) {
        this.matchId = matchId;
        this.matchName = matchName;
        this.matchImageUrl = matchImageUrl;
        this.matchPhone = phone;
        this.userName = userName;
    }

    public String getUserName() {
        return userName;
    }

    public String getMatchPhone() {
        return matchPhone;
    }

    public String getMatchId() {
        return matchId;
    }

    public String getMatchName() {
        return matchName;
    }

    public String getMatchImageUrl() {
        return matchImageUrl;
    }
}
