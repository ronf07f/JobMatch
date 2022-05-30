package com.example.jobmatch;
public class Cards {
    private final String userId;
    private String userName;
    private final String phone;
    private final int age;
    private final String profileImageUrl;
    private final String userType;
    private final String experience;

    /**
     * @param user
     * @param userId
     */
    public Cards(Users user, String userId){
        this.userName = user.getUserName();
        this.phone = user.getPhone();
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

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPhone() {
        return phone;
    }

    public int getAge() {
        return age;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public String getUserType() {
        return userType;
    }

    public String getExperience() {
        return experience;
    }
}

