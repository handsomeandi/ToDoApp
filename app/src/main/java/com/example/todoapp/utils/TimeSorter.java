package com.example.todoapp.utils;

import com.example.todoapp.UserTask;

import java.util.Comparator;

public class TimeSorter implements Comparator<UserTask> {
    @Override
    public int compare(UserTask o1, UserTask o2) {
        return o1.getTime_num().compareTo(o2.getTime_num());
    }
}
