package com.example.jobmatch;

public class Cards {
    private String userId;
    private String name;
    private int age;
    private String position;
    private String job_OR_perv_jobs;

    public Cards(String userId, String name) {
        this.userId = userId;
        this.name = name;
    }
    /*
    public Cards(String userId, String name, int age, String position, String job_OR_perv_jobs) {
        this.userId = userId;
        this.name = name;
        this.age = age;
        this.position = position;
        this.job_OR_perv_jobs = job_OR_perv_jobs;
    }
*/
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getJob_OR_perv_jobs() {
        return job_OR_perv_jobs;
    }

    public void setJob_OR_perv_jobs(String job_OR_perv_jobs) {
        this.job_OR_perv_jobs = job_OR_perv_jobs;
    }


}
