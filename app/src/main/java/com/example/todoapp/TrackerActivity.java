package com.example.todoapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import com.example.todoapp.utils.TaskApi;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.auth.User;

import java.util.ArrayList;

public class TrackerActivity extends AppCompatActivity {

    HabitsRecyclerViewAdapter habits_adapter;
    ImageButton addHabitBtn;
    RecyclerView habitsRecyclerView;
    ArrayList<UserHabit> habits = new ArrayList<>();


    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private CollectionReference collectionReference = db.collection("Habits");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tracker);

        addHabitBtn = findViewById(R.id.addHabitBtn);
        habitsRecyclerView = findViewById(R.id.habitsRecyclerView);


        collectionReference.whereEqualTo("userID", TaskApi.getInstance().getUserID())
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(!task.getResult().isEmpty()){
                    for (QueryDocumentSnapshot habit : task.getResult()) {
                        UserHabit user_habit = habit.toObject(UserHabit.class);
                        habits.add(user_habit);
                    }
                    habits_adapter = new HabitsRecyclerViewAdapter(TrackerActivity.this);
                    habits_adapter.setHabits(habits);
                    habitsRecyclerView.setAdapter(habits_adapter);
                    habitsRecyclerView.setLayoutManager(new LinearLayoutManager(TrackerActivity.this));
                }
            }
        });





        addHabitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TrackerActivity.this,AddHabitActivity.class);
                startActivity(intent);
            }
        });
    }
}