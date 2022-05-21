package com.example.jobmatch;

public class MatchesObj {
    private String matchId;
    private String matchName;
    private String matchImageUrl;

    public MatchesObj(String matchId, String matchName, String matchImageUrl) {
        this.matchId = matchId;
        this.matchName = matchName;
        this.matchImageUrl = matchImageUrl;
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
