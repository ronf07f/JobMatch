package com.example.jobmatch;

import java.util.Map;
import java.util.Objects;

public class Users {
    private String userName;
    private final String phone;
    private final int age;
    private final String profileImageUrl;
    private final String userType;
    private final String experience;

    /**
     * build a user
     * @param userName
     * @param phone
     * @param age
     * @param profileImageUrl
     * @param userType
     * @param experience
     */
    public Users(String userName, String phone, int age, String profileImageUrl, String userType, String experience) {
        this.userName = userName;
        this.phone = phone;
        this.age = age;
        this.profileImageUrl = profileImageUrl;
        this.userType = userType;
        this.experience = experience;
    }

    /**
     * build user from map
     * @param user
     */
    public Users(Map user) {
        this.userName = Objects.requireNonNull(user.get("userName")).toString();
        this.phone = Objects.requireNonNull(user.get("phone")).toString();
        this.age = Integer.parseInt(Objects.requireNonNull(user.get("age")).toString());
        this.profileImageUrl = Objects.requireNonNull(user.get("profileImageUrl")).toString();
        this.userType = Objects.requireNonNull(user.get("userType")).toString();
        this.experience = Objects.requireNonNull(user.get("experience")).toString();
    }

    public String getExperience() {
        return experience;
    }

    public String getUserName() {
        return userName;
    }

    public void setName(String userName) {
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
}


