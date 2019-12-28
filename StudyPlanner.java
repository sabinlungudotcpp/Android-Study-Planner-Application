package com.example.twitterclone;

public class StudyPlanner { // Study Planner class
    private int plannerID; // Planner ID variable to store the unique id of the planner of the student
    private String module;
    private String day;
    private String startTime;
    private String endTime;

    public StudyPlanner(int plannerID, String module, String day, String startTime, String endTime) { // Constructor 1.
        this.plannerID = plannerID;
        this.module = module;
        this.day = day;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public StudyPlanner(String module, String day, String startTime, String endTime) { // Constructor 2.
        this.module = module;
        this.day = day;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public StudyPlanner() { // Default constructor
        super(); // Call super method
    }

    public int getPlannerID() {
        return this.plannerID;
    }

    public void setPlannerID(int plannerID) {
        this.plannerID = plannerID;
    }

    public String getModule() {
        return module;
    }

    public void setModule(String module) {
        this.module = module;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() { // Gets the end time of the plan
        return this.endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }
}