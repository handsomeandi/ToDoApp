package com.example.todoapp;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
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

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MonthTasksActivity extends AppCompatActivity {

    ImageButton addTaskBtn, nextMonthBtn, prevMonthBtn;
    ListView periodTasksList;
    TextView monthLabel;

    Long start_date, end_date;

    int month_difference = 0;

    LocalDate date;

    List<String> months_tasks = new ArrayList<>();
    Map<String,String> date_tasks = new HashMap<>();

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private CollectionReference collectionReference = db.collection("Tasks");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_month_tasks);

        Intent intent = getIntent();
        Utils util = Utils.getInstance();
        addTaskBtn = findViewById(R.id.addTaskButtonWeekTasks);
        periodTasksList = findViewById(R.id.periodTasksWeekTasks);
        nextMonthBtn = findViewById(R.id.nextMonthTasksBtn);
        prevMonthBtn = findViewById(R.id.prevMonthTasksBtn);
        monthLabel = findViewById(R.id.monthLabel);

        if(intent!=null){
            start_date = Long.parseLong(intent.getStringExtra("start_date"));
            end_date = Long.parseLong(intent.getStringExtra("end_date"));
            load_tasks();
        }

        nextMonthBtn.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                clear_list();
                month_difference += 1;
                date = util.define_month(month_difference);
                start_date = Long.parseLong(util.get_start_of_month(date));
                end_date = Long.parseLong(util.get_end_of_month(date));
                load_tasks();
            }
        });
        prevMonthBtn.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                clear_list();
                month_difference -= 1;
                date = util.define_month(month_difference);
                start_date = Long.parseLong(util.get_start_of_month(date));
                end_date = Long.parseLong(util.get_end_of_month(date));
                load_tasks();
            }
        });

        addTaskBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MonthTasksActivity.this,AddTaskActivity.class));
            }
        });


    }


    private void clear_list(){
        months_tasks.clear();
        periodTasksList.removeAllViewsInLayout();
    }

    private void load_tasks(){
        String stringForTopLabel = Utils.getInstance().get_month_name_by_num(String.valueOf(start_date).substring(4,6)) + " " + String.valueOf(start_date).substring(0,4);
        monthLabel.setText(stringForTopLabel);
        collectionReference.whereEqualTo("userID", TaskApi.getInstance().getUserID()).
                whereGreaterThanOrEqualTo("date",start_date).
                whereLessThanOrEqualTo("date", end_date).
                get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(!task.getResult().isEmpty()){
                    for (QueryDocumentSnapshot month_task : task.getResult()) {
                        UserTask user_task = month_task.toObject(UserTask.class);
                        months_tasks.add("- " + user_task.getTask_body());
                        date_tasks.put(user_task.getTask_body(), String.valueOf(user_task.getDate()));
                    }
                    periodTasksList.setAdapter(new ArrayAdapter<String>(MonthTasksActivity.this, android.R.layout.simple_list_item_1,months_tasks));
                    periodTasksList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            String date = date_tasks.get(months_tasks.get(position).substring(2));
                            Intent day_intent = new Intent(MonthTasksActivity.this, DayActivity.class);
                            day_intent.putExtra("date", date);
                            MonthTasksActivity.this.startActivity(day_intent);
                        }
                    });
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(MonthTasksActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}