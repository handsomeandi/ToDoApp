package com.example.todoapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.todoapp.utils.TaskApi;
import com.example.todoapp.utils.Utils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WeekTasksActivity extends AppCompatActivity {

    ImageButton addTaskBtn;
    ListView periodTasksList;
    TextView weekLabel;

    Long start_date, end_date;


    List<String> week_tasks = new ArrayList<>();
    Map<String,String> date_tasks = new HashMap<>();

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private CollectionReference collectionReference = db.collection("Tasks");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_week_tasks);

        addTaskBtn = findViewById(R.id.addTaskButtonWeekTasks);
        periodTasksList = findViewById(R.id.periodTasksWeekTasks);
        weekLabel = findViewById(R.id.weekLabelWeekTasks);

        Intent intent = getIntent();
        if(intent != null){
            String start_string = intent.getStringExtra("start_date");
            String end_string = intent.getStringExtra("end_date");
            start_date = Long.parseLong(start_string);
            end_date = Long.parseLong(end_string);
            String date_text =  start_string.substring(6) + "." + start_string.substring(4,6) + " - " + end_string.substring(6) + "." + end_string.substring(4,6);
            weekLabel.setText(date_text);
            load_tasks();
        }

        addTaskBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(WeekTasksActivity.this,AddTaskActivity.class));
            }
        });
    }

    private void load_tasks(){
        collectionReference.whereEqualTo("userID", TaskApi.getInstance().getUserID()).
                whereGreaterThanOrEqualTo("date",start_date).
                whereLessThanOrEqualTo("date", end_date).
                get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(!task.getResult().isEmpty()){
                    for (QueryDocumentSnapshot month_task : task.getResult()) {
                        UserTask user_task = month_task.toObject(UserTask.class);
                        week_tasks.add("- " + user_task.getTask_body());
                        date_tasks.put(user_task.getTask_body(), String.valueOf(user_task.getDate()));
                    }
                    periodTasksList.setAdapter(new ArrayAdapter<String>(WeekTasksActivity.this, android.R.layout.simple_list_item_1, week_tasks));
                    periodTasksList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            String date = date_tasks.get(week_tasks.get(position).substring(2));
                            Intent day_intent = new Intent(WeekTasksActivity.this, DayActivity.class);
                            day_intent.putExtra("date", date);
                            WeekTasksActivity.this.startActivity(day_intent);
                        }
                    });
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(WeekTasksActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}