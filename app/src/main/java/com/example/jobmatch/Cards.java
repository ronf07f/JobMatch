

package com.example.jobmatch;

public class Cards {
    private final String userId;
    private final String userName;
    private final int age;
    private final String profileImageUrl;
    private final String userType;
    private final String experience;
    public Cards(Users user, String userId){
        this.userName = user.getUserName();
        this.age = user.getAge();
        this.profileImageUrl = user.getProfileImageUrl();
        this.userType = user.getUserType();
        this.experience = user.getExperience();
        this.userId=userId;
    }
    /**
     *
     * @return return the experience with a prefix that depends on the user type
     */
    public String getExperienceForCard() {
        String prefix = "";
        switch (userType) {
            case GlobalVerbs.EMPLOYEE:
                prefix = "Experience:";
                break;
            case GlobalVerbs.EMPLOYER:
                prefix = "Experience Needed:";
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
        return prefix+" "+userName;
    }

    /**
     *
     * @return return the age with a prefix that depends on the user type
     */
    public String getAgeForCard(){
        String prefix = "";
        switch (userType){
            case GlobalVerbs.EMPLOYEE:
                prefix = "Age:";
                break;
            case GlobalVerbs.EMPLOYER:
                prefix = "Required Age:";
                break;
        }
        return prefix+" "+age;
    }

    /**
     *
     * @return  returns the user id
     */
    public String getUserId() {
        return userId;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public String getExperience() {
        return experience;
    }

}


