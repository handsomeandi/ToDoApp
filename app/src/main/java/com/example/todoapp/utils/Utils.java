package com.example.todoapp.utils;


import android.os.Build;

import androidx.annotation.RequiresApi;

import java.time.DayOfWeek;
import java.time.LocalDate;

public class Utils {
    private static Utils utils_instance;

    public static Utils getInstance(){
        if(utils_instance==null){
            utils_instance = new Utils();
        }
        return utils_instance;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public String get_start_of_month(LocalDate date){
        int month = date.getMonthValue();
        int year = date.getYear();
        int day = date.getDayOfMonth();
        LocalDate tmp_date = LocalDate.of(year,month,1);
        while(!tmp_date.getDayOfWeek().equals(DayOfWeek.MONDAY)){
            tmp_date = tmp_date.minusDays(1);
        }
        month = tmp_date.getMonthValue();
        year = tmp_date.getYear();
        day = tmp_date.getDayOfMonth();
        String start_of_month = String.valueOf(year);
        if(month<10){
            start_of_month += "0" + String.valueOf(month);
        }else{
            start_of_month += String.valueOf(month);
        }
        if(day<10){
            start_of_month += "0" + String.valueOf(day);
        }else{
            start_of_month += String.valueOf(day);
        }
        return start_of_month;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public String get_end_of_month(LocalDate date){
        LocalDate end_date = date;
        int num_of_days = date.lengthOfMonth();
        LocalDate tmp_date = LocalDate.of(date.getYear(),date.getMonthValue(),num_of_days);
        while(!tmp_date.getDayOfWeek().equals(DayOfWeek.SUNDAY)){
            tmp_date = tmp_date.plusDays(1);
        }
        String end_of_month = String.valueOf(tmp_date.getYear());
        int month = tmp_date.getMonthValue();
        int day = tmp_date.getDayOfMonth();
        if(month<10){
            end_of_month += "0" + String.valueOf(month);
        }else{
            end_of_month += String.valueOf(month);
        }
        if(day<10){
            end_of_month += "0" + String.valueOf(tmp_date.getDayOfMonth());
        }else{
            end_of_month += String.valueOf(tmp_date.getDayOfMonth());
        }
        return end_of_month;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public String parseDateToString(LocalDate date){
        String date_string;
        int year = date.getYear();
        int month = date.getMonthValue();
        int day = date.getDayOfMonth();
        date_string = String.valueOf(year);
        if(month < 10){
            date_string += "0" + String.valueOf(month);
        }else{
            date_string += String.valueOf(month);
        }
        if(day<10){
            date_string += "0" + String.valueOf(day);
        }else{
            date_string += String.valueOf(day);
        }
        return date_string;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public LocalDate parseStringToDate(String date){
        LocalDate localDate;
        int year = Integer.parseInt(date.substring(0,4));
        int month;
        int day;
        if(date.substring(4,6).startsWith("0")){
            month = Integer.parseInt(date.substring(5,6));
        }else{
            month = Integer.parseInt(date.substring(4,6));
        }
        if(date.substring(6).startsWith("0")){
            day = Integer.parseInt(date.substring(7));
        }else{
            day = Integer.parseInt(date.substring(6));
        }
        localDate = LocalDate.of(year,month,day);
        return localDate;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public LocalDate define_month(int month_difference){
        LocalDate date;
        if(month_difference>0){
            date = LocalDate.now().plusMonths(month_difference);
        }else if(month_difference<0){
            date = LocalDate.now().minusMonths(month_difference*(-1));
        }else{
            date = LocalDate.now();
        }
        return date;
    }

    public String get_month_name_by_num(String num){
        String month_name = null;
        switch (num){
            case "01":
                month_name = "Январь";
                break;
            case "02":
                month_name = "Февраль";
                break;
            case "03":
                month_name = "Март";
                break;
            case "04":
                month_name = "Апрель";
                break;
            case "05":
                month_name = "Май";
                break;
            case "06":
                month_name = "Июнь";
                break;
            case "07":
                month_name = "Июль";
                break;
            case "08":
                month_name = "Август";
                break;
            case "09":
                month_name = "Сентябрь";
                break;
            case "10":
                month_name = "Октябрь";
                break;
            case "11":
                month_name = "Ноябрь";
                break;
            case "12":
                month_name = "Декабрь";
                break;
            default:
                break;
        }
        return month_name;
    }

    public Utils(){

    }
}
