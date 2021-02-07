package com.example.todoapp;

import com.google.firebase.firestore.Exclude;

import java.time.LocalTime;

public class UserTask {
    private String task_body;
    private String priority;
    private String username;
    private String userID;


    private Long date;
    private Long time;
    private Long time_num;


    private boolean _completed;
    private String documentId;

    public UserTask(){

    }

    public UserTask(String task_body, String priority, String username, long date) {
        this.task_body = task_body;
        this.priority = priority;
        this.username = username;
        this.date = date;
    }


    public Long getTime_num() {
        return time_num;
    }

    public void setTime_num(long time_num) {
        this.time_num = time_num;
    }
    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    @Exclude
    public String getDocumentId() {
        return documentId;
    }


    public String getTask_body() {
        return task_body;
    }

    public void setTask_body(String task_body) {
        this.task_body = task_body;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public long getTime() {
        return time;
    }


    public boolean is_completed() {
        return this._completed;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    void set_time(long time){
        this.time = time;
    }

    void setCompleted(boolean completed){
        this._completed = completed;
    }
}
