package com.example.todoapp;

import java.util.ArrayList;
import java.util.Map;

public class UserHabit {
    private String habit_name;
    private String priority;
    private String userID;
    private ArrayList<Map<String, Boolean>> dates_checked;

    public UserHabit() {
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getHabit_name() {
        return habit_name;
    }

    public void setHabit_name(String habit_name) {
        this.habit_name = habit_name;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public ArrayList<Map<String, Boolean>> getDates_checked() {
        return dates_checked;
    }

    public void setDates_checked(ArrayList<Map<String, Boolean>> dates_checked) {
        this.dates_checked = dates_checked;
    }

    public void add_date_checked(Map<String,Boolean> date){
        dates_checked.add(date);
    }

    public void delete_date_checked(String date){
        for(Map<String,Boolean> date_checked : dates_checked){
            for(Map.Entry<String,Boolean> key_values : date_checked.entrySet()){
                if(key_values.getKey().equals(date)){
                    dates_checked.remove(date_checked);
                }
            }
        }
    }
}
