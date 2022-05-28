package com.example.jobmatch;
public class Cards extends Users{
    private final String userId;

    public Cards(Users user, String userId){
        super(user.getUserName(),user.getPhone(), user.getAge(), user.getProfileImageUrl(),user.getUserType(),user.getExperience());
        this.userId=userId;
    }

    public String getExperienceForCard(){
        String prefix = "";
        switch (getUserType()){
            case GlobalVerbs.EMPLOYEE:
                prefix = "Experience:";
                break;
            case GlobalVerbs.EMPLOYER:
                prefix = "Needed Experience:";
                break;
        }
        return prefix+" "+getExperience();
    }

    public String getUserNameForCard(){
        String prefix = "Name:";
        return prefix+" "+getUserName();
    }

    public String getPhoneForCard(){
        String prefix = "phone";
        return prefix+" "+getUserName();
    }

    public String getAgeForCard(){
        String prefix = "";
        switch (getUserType()){
            case GlobalVerbs.EMPLOYEE:
                prefix = "Age:";
                break;
            case GlobalVerbs.EMPLOYER:
                prefix = "Required Age:";
                break;
        }
        return prefix+" "+getAge();
    }

    public String getUserId() {
        return userId;
    }
}
