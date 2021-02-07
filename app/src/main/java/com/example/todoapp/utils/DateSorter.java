package com.example.todoapp.utils;

import com.example.todoapp.CertainPeriodTasks;

import java.util.Comparator;

public class DateSorter implements Comparator<CertainPeriodTasks> {
    @Override
    public int compare(CertainPeriodTasks o1, CertainPeriodTasks o2) {
        return o1.getStartDateLong().compareTo(o2.getStartDateLong());
    }
}
