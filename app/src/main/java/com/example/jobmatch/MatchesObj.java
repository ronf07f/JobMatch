package com.example.jobmatch;

public class MatchesObj {
    private String matchId;
    private String matchName;
    private String matchImageUrl;
    private String matchPhone;
    private String userName;

    public MatchesObj(String matchId, String matchName, String matchImageUrl,String phone,String userName) {
        this.matchId = matchId;
        this.matchName = matchName;
        this.matchImageUrl = matchImageUrl;
        this.matchPhone = phone;
        this.userName = userName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getMatchPhone() {
        return matchPhone;
    }

    public void setMatchPhone(String matchPhone) {
        this.matchPhone = matchPhone;
    }

    public String getMatchId() {
        return matchId;
    }

    public void setMatchId(String matchId) {
        this.matchId = matchId;
    }

    public String getMatchName() {
        return matchName;
    }

    public void setMatchName(String matchName) {
        this.matchName = matchName;
    }

    public String getMatchImageUrl() {
        return matchImageUrl;
    }

    public void setMatchImageUrl(String matchImageUrl) {
        this.matchImageUrl = matchImageUrl;
    }
}
