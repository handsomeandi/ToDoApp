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
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.todoapp.utils.TaskApi;
import com.example.todoapp.utils.TimeSorter;
import com.example.todoapp.utils.Utils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class DayActivity extends AppCompatActivity {


    TextView dateLabel;
    RecyclerView tasksRecyclerView;
    ImageButton addTaskBtn;
    String date;
    DayTasksRecyclerViewAdapter adapter;
    ArrayList<UserTask> day_tasks = new ArrayList<>();


    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private CollectionReference collectionReference = db.collection("Tasks");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_day);

        dateLabel = findViewById(R.id.dateTopLabel);
        tasksRecyclerView = findViewById(R.id.tasksRecyclerView);
        addTaskBtn = findViewById(R.id.addTaskButtonWeekTasks);

        Intent intent = getIntent();
        if(intent != null){
            date = intent.getStringExtra("date");
            String date_text = date.substring(6) + "." + date.substring(4,6) + " " + date.substring(0,4);
            dateLabel.setText(date_text);
        }

        adapter = new DayTasksRecyclerViewAdapter(DayActivity.this);

        addTaskBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent add_intent = new Intent( DayActivity.this,AddTaskActivity.class);
                add_intent.putExtra("date", date);
                startActivity(add_intent);
            }
        });
    }


    @Override
    protected void onStart() {
        super.onStart();

        long date_num = Long.parseLong(date);

        collectionReference.whereEqualTo("userID", TaskApi.getInstance().getUserID()).
                whereEqualTo("date", date_num).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(!task.getResult().isEmpty()){
                    for (QueryDocumentSnapshot tasks : task.getResult()) {
                        UserTask user_task = tasks.toObject(UserTask.class);
                        day_tasks.add(user_task);
                    }
                    day_tasks.sort(new TimeSorter());
                    adapter.setDay_tasks(day_tasks);
                    tasksRecyclerView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                    tasksRecyclerView.setLayoutManager(new LinearLayoutManager(DayActivity.this));
                }
            }
        });

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onStop() {
        super.onStop();
        day_tasks.clear();
        adapter.setDay_tasks(day_tasks);
        adapter.notifyDataSetChanged();
        tasksRecyclerView.setAdapter(adapter);
        tasksRecyclerView.removeAllViews();
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
                Intent intent = new Intent(DayActivity.this, PlannerActivity.class);
                startActivity(intent);
                break;
            case R.id.days_action:
                startActivity(new Intent(DayActivity.this, MonthDaysActivity.class));
                break;
            case R.id.tasks_action:
                Intent intent_tasks = new Intent(DayActivity.this, DayActivity.class);
                intent_tasks.putExtra("date", date);
                startActivity(intent_tasks);
                break;
            case R.id.add_task:
                Intent add_intent = new Intent( DayActivity.this,AddTaskActivity.class);
                add_intent.putExtra("date", date);
                startActivity(add_intent);
            case R.id.main_menu:
                Intent main_menu_intent = new Intent( DayActivity.this,MainActivity.class);
                startActivity(main_menu_intent);
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }


}