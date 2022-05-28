package com.example.jobmatch;

import android.util.Log;

import java.util.HashMap;
import java.util.Map;

public class Users {
    private String userName;
    private String phone;
    private int age;
    private String profileImageUrl;
    private String userType;
    private String experience;

    public Users(String userName, String phone, int age, String profileImageUrl, String userType,String experience) {
        this.userName = userName;
        this.phone = phone;
        this.age = age;
        this.profileImageUrl = profileImageUrl;
        this.userType = userType;
        this.experience=experience;
    }

    public Users(Map user) {
        this.userName = user.get("userName").toString();
        this.phone = user.get("phone").toString();
        this.age = Integer.parseInt(user.get("age").toString());
        this.profileImageUrl = user.get("profileImageUrl").toString();
        this.userType = user.get("userType").toString();
        this.experience = user.get("experience").toString();
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getExperience() {
        return experience;
    }

    public void setExperience(String experience) {
        this.experience = experience;
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

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }
}
