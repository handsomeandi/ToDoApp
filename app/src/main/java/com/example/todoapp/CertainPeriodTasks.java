package com.example.todoapp;

import android.widget.ArrayAdapter;

import com.example.todoapp.utils.TaskApi;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;


public class CertainPeriodTasks{
    private String start_date;
    private String end_date;
    private ArrayList<String> tasks_bodies = new ArrayList<>();
    private boolean isExpanded;


    //Connection to Firestore
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private CollectionReference collectionReference = db.collection("Tasks");

    //Должен быть контейнер, содержащий задачи(тело задачи) и их даты за определенный период(неделя, день)
    public CertainPeriodTasks(String start_date, String end_date) {
        this.start_date = start_date;
        this.end_date = end_date;
        this.isExpanded = false;
    }


    public boolean isExpanded() {
        return isExpanded;
    }

    public void setExpanded(boolean expanded) {
        isExpanded = expanded;
    }

    public ArrayList<String> getTasks_bodies() {
        return tasks_bodies;
    }


    public void addTaskBody(String task_body){
        this.tasks_bodies.add(task_body);
    }


    public Long getStartDateLong(){
        return Long.parseLong(start_date);
    }

    public String getStart_date() {
        return start_date;
    }

    public void setStart_date(String start_date) {
        this.start_date = start_date;
    }

    public String getEnd_date() {
        return end_date;
    }

    public void setEnd_date(String end_date) {
        this.end_date = end_date;
    }
}
