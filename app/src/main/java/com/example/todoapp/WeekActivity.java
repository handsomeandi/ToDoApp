package com.example.todoapp;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.todoapp.utils.DateSorter;
import com.example.todoapp.utils.TaskApi;
import com.example.todoapp.utils.Utils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class WeekActivity extends AppCompatActivity {

    RecyclerView daysRecyclerView;
    TextView dateLabel;
    String startDate, endDate;
    private ImageButton addTaskBtn;
    TaskRecyclerViewAdapter adapter;
    ArrayList<CertainPeriodTasks> days_tasks = new ArrayList<>();


    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private CollectionReference collectionReference = db.collection("Tasks");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_week);

        daysRecyclerView = findViewById(R.id.daysRecyclerView);
        dateLabel = findViewById(R.id.dateTopLabel);
        addTaskBtn = findViewById(R.id.addNewTaskButton);

        Intent intent = getIntent();
        if(intent != null){
            startDate = intent.getStringExtra("startDate");
            endDate = intent.getStringExtra("endDate");
            String week = startDate.substring(6) + "." + startDate.substring(4,6) + " - " + endDate.substring(6) + "." + endDate.substring(4,6) + " " + endDate.substring(0,4);
            dateLabel.setText(week);
        }
        adapter = new TaskRecyclerViewAdapter(WeekActivity.this, "weekActivity");

        addTaskBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent add_intent = new Intent(WeekActivity.this,AddTaskActivity.class);
                add_intent.putExtra("date", startDate);
                startActivity(add_intent);
            }
        });

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onStop() {
        super.onStop();
        days_tasks.clear();
        adapter.setTasks(days_tasks);
        adapter.notifyDataSetChanged();
        daysRecyclerView.setAdapter(adapter);
        daysRecyclerView.removeAllViews();
    }

    @Override
    protected void onStart() {
        super.onStart();

        ArrayList<UserTask> foundTasks = new ArrayList<>();

        String year_month = startDate.substring(0,6);
        int startDateNum;
        int endDateNum;
        if(startDate.substring(6).startsWith("0")){
            startDateNum = Integer.parseInt(startDate.substring(7));
        }else{
            startDateNum = Integer.parseInt(startDate.substring(6));
        }
        if(endDate.substring(6).startsWith("0")){
            endDateNum = Integer.parseInt(endDate.substring(7));
        }else{
            endDateNum = Integer.parseInt(endDate.substring(6));
        }
        collectionReference.whereEqualTo("userID", TaskApi.getInstance().getUserID()).
                whereGreaterThanOrEqualTo("date",Long.parseLong(startDate)).
                whereLessThanOrEqualTo("date", Long.parseLong(endDate)).
                get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (!task.getResult().isEmpty()) {
                    for (QueryDocumentSnapshot tasks : task.getResult()) {
                        UserTask user_task = tasks.toObject(UserTask.class);
                        foundTasks.add(user_task);

                    }

                    for (LocalDate i = Utils.getInstance().parseStringToDate(startDate); i.compareTo(Utils.getInstance().parseStringToDate(endDate)) <= 0; i = i.plusDays(1)) {
                        String start_end_date = Utils.getInstance().parseDateToString(i);
                        CertainPeriodTasks day = new CertainPeriodTasks(start_end_date, start_end_date);
                        for (UserTask found_task : foundTasks) {
                            if (found_task.getDate() == Long.parseLong(start_end_date)) {
                                day.addTaskBody("- " + found_task.getTask_body());
                            }
                        }
                        days_tasks.add(day);

                    }
                    days_tasks.sort(new DateSorter());
                    adapter.setTasks(days_tasks);
                    daysRecyclerView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                    daysRecyclerView.setLayoutManager(new LinearLayoutManager(WeekActivity.this));
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.month_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch(item.getItemId()){
            case R.id.weeks_action:
                Intent intent = new Intent(WeekActivity.this, PlannerActivity.class);
                startActivity(intent);
                break;
            case R.id.days_action:
                startActivity(new Intent(WeekActivity.this, MonthDaysActivity.class));
                break;
            case R.id.tasks_action:
                Intent week_intent = new Intent(WeekActivity.this, WeekTasksActivity.class);
                week_intent.putExtra("start_date", startDate);
                week_intent.putExtra("end_date", endDate);
                startActivity(week_intent);
                break;
            case R.id.add_task:
                Intent add_intent = new Intent(WeekActivity.this,AddTaskActivity.class);
                add_intent.putExtra("date", startDate);
                startActivity(add_intent);
            case R.id.main_menu:
                Intent main_menu_intent = new Intent( WeekActivity.this,MainActivity.class);
                startActivity(main_menu_intent);
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }
}