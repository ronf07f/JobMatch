package com.example.jobmatch;
public class Cards extends Users{
    private final String userId;

    /**
     * @param user
     * @param userId
     */
    public Cards(Users user, String userId){
        super(user.getUserName(),user.getPhone(), user.getAge(), user.getProfileImageUrl(),user.getUserType(),user.getExperience());
        this.userId=userId;
    }
//get*variable*ForCard gives back the variable with the correct prefix for the cards

    /**
     *
     * @return return the experience with a prefix that depends on the user type
     */
    public String getExperienceForCard() {
        String prefix = "";
        switch (getUserType()) {
            case GlobalVerbs.EMPLOYEE:
                prefix = "Experience:";
                break;
            case GlobalVerbs.EMPLOYER:
                prefix = "Needed Experience:";
                break;
        }
        return prefix + " " + getExperience();
    }

    /**
     *
     * @return return the name with a prefix
     *
     */
    public String getUserNameForCard(){
        String prefix = "Name:";
        return prefix+" "+getUserName();
    }

    /**
     *
     * @return return the age with a prefix that depends on the user type
     */
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

    /**
     *
     * @return  returns the user id
     */
    public String getUserId() {
        return userId;
    }
}
