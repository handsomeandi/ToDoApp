package com.example.todoapp;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
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
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Objects;

import io.grpc.okhttp.internal.Util;


@RequiresApi(api = Build.VERSION_CODES.O)
public class PlannerActivity extends AppCompatActivity {

    private RecyclerView weeksRecyclerView;
    private TaskRecyclerViewAdapter adapter;
    private TextView dateTopLabel;
    private ImageButton nextWeekBtn, prevWeekBtn, addTaskBtn;
    private LocalDate date;
    private int month_difference = 0;

    private ArrayList<CertainPeriodTasks> weekTasks = new ArrayList<>();
    private String[] days_of_weeks;
    private ArrayList<String> days_of_weeks_test = new ArrayList<>();

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private CollectionReference collectionReference = db.collection("Tasks");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_planner);

        weeksRecyclerView = findViewById(R.id.weeksRecyclerView);
        dateTopLabel = findViewById(R.id.dateTopLabel);
        nextWeekBtn = findViewById(R.id.nextMonthWeeksBtn);
        prevWeekBtn = findViewById(R.id.prevMonthWeeksBtn);
        addTaskBtn = findViewById(R.id.addTaskButton);
        adapter = new TaskRecyclerViewAdapter(PlannerActivity.this, "monthActivity");

        date = LocalDate.now();

        nextWeekBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearRecyclerView();
                month_difference += 1;
                date = Utils.getInstance().define_month(month_difference);
                load_weeks();
            }
        });

        prevWeekBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearRecyclerView();
                month_difference -= 1;
                date = Utils.getInstance().define_month(month_difference);
                load_weeks();
            }
        });

        addTaskBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent add_intent = new Intent(PlannerActivity.this,AddTaskActivity.class);
                add_intent.putExtra("date", Utils.getInstance().get_start_of_month(date));
                startActivity(add_intent);
            }
        });

    }

    @Override
    protected void onStop() {
        super.onStop();
        clearRecyclerView();
    }

    private void clearRecyclerView(){
        days_of_weeks_test.clear();
        weekTasks.clear();
        adapter.setTasks(weekTasks);
        adapter.notifyDataSetChanged();
        weeksRecyclerView.setAdapter(adapter);
        weeksRecyclerView.removeAllViews();
    }


    @Override
    protected void onStart() {
        super.onStart();
        load_weeks();

    }




    private void load_weeks(){
        String start_date_string = Utils.getInstance().get_start_of_month(date);
        String end_date_string = Utils.getInstance().get_end_of_month(date);
        LocalDate start_date = Utils.getInstance().parseStringToDate(start_date_string);
        LocalDate end_date = Utils.getInstance().parseStringToDate(end_date_string);
//        String year_month = start_date_string.substring(0,6);
//        days_of_weeks = new String[]{start_date_string, year_month + "07", year_month + "08",year_month +  "14", year_month + "15", year_month + "21", year_month + "22", end_date_string};
        LocalDate tmp_date = start_date;
        int difference_in_days = 0;
        while (tmp_date.compareTo(end_date) < 0){
            days_of_weeks_test.add(Utils.getInstance().parseDateToString(tmp_date));
            days_of_weeks_test.add(Utils.getInstance().parseDateToString(start_date.plusDays(difference_in_days+6)));
            difference_in_days += 7;
            tmp_date = start_date.plusDays(difference_in_days);
        }


        ArrayList<UserTask> found_tasks = new ArrayList<>();

        String stringForTopLabel = Utils.getInstance().get_month_name_by_num(Utils.getInstance().parseDateToString(Utils.getInstance().define_month(month_difference)).substring(4,6)) + " " + start_date_string.substring(0,4);
        dateTopLabel.setText(stringForTopLabel);
        collectionReference.whereEqualTo("userID", TaskApi.getInstance().getUserID()).
                whereGreaterThanOrEqualTo("date",Long.parseLong(start_date_string)).
                whereLessThanOrEqualTo("date", Long.parseLong(end_date_string)).
                get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (!task.getResult().isEmpty()) {
                    for (QueryDocumentSnapshot tasks : task.getResult()) {
                        UserTask user_task = tasks.toObject(UserTask.class);
                        found_tasks.add(user_task);
                    }
                    for(int i=0;i+1<days_of_weeks_test.size();i+=2){
                        CertainPeriodTasks month_tasks_week = new CertainPeriodTasks(days_of_weeks_test.get(i), days_of_weeks_test.get(i+1));
                        for(UserTask found_task : found_tasks){
                            if((found_task.getDate() >= Long.parseLong(days_of_weeks_test.get(i))) && found_task.getDate() <= Long.parseLong(days_of_weeks_test.get(i+1))){
                                month_tasks_week.addTaskBody("- " + found_task.getTask_body());
                            }
                        }
                        weekTasks.add(month_tasks_week);
                    }
                    weekTasks.sort(new DateSorter());
                    adapter.setTasks(weekTasks);
                    weeksRecyclerView.setAdapter(adapter);
                    weeksRecyclerView.setLayoutManager(new LinearLayoutManager(PlannerActivity.this));
                    adapter.notifyDataSetChanged();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
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
                Intent intent = new Intent(PlannerActivity.this, PlannerActivity.class);
                startActivity(intent);
                break;
            case R.id.days_action:
                startActivity(new Intent(PlannerActivity.this, MonthDaysActivity.class));
                break;
            case R.id.tasks_action:
                Intent intent_month_tasks = new Intent(PlannerActivity.this, MonthTasksActivity.class);
                intent_month_tasks.putExtra("start_date", Utils.getInstance().get_start_of_month(date));
                intent_month_tasks.putExtra("end_date",Utils.getInstance().get_end_of_month(date));
                startActivity(intent_month_tasks);
                break;
            case R.id.add_task:
                Intent add_intent = new Intent(PlannerActivity.this,AddTaskActivity.class);
                add_intent.putExtra("date", Utils.getInstance().get_start_of_month(date));
                startActivity(add_intent);
            case R.id.main_menu:
                Intent main_menu_intent = new Intent( PlannerActivity.this,MainActivity.class);
                startActivity(main_menu_intent);
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }
}