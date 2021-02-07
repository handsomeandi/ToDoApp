package com.example.todoapp.utils;

import android.app.Application;


public class TaskApi extends Application {
    private static TaskApi task_instance;
    private String userID;
    private String username;

    public static TaskApi getInstance(){
        if(task_instance==null){
            task_instance = new TaskApi();
        }
        return task_instance;
    }

    public TaskApi(){

    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
