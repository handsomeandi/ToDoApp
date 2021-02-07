package com.example.todoapp;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.todoapp.utils.TaskApi;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class AddTaskActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private Spinner prioritiesSpinner;
    private TimePicker taskTimePicker;
    private EditText taskBody;
    private DatePicker taskDatePicker;
    private Button addNewTaskBtn;
    private TextView newTaskLabel;
    private String task_body_extra;
    private String date_from_intent;

    private String priority;

    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser user;

    //Connection to Firestore
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private CollectionReference collectionReference = db.collection("Tasks");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);


        init_views();


        Intent intent = getIntent();
        task_body_extra = intent.getStringExtra("task_body");
        date_from_intent = intent.getStringExtra("date");

        if(date_from_intent != null){
            setPickerDate(date_from_intent);
        }


        if(task_body_extra != null){
            addNewTaskBtn.setText("Готово");
            newTaskLabel.setText("Редактировать задачу");
            collectionReference.whereEqualTo("userID", TaskApi.getInstance().getUserID()).whereEqualTo("task_body", task_body_extra).
                    get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @RequiresApi(api = Build.VERSION_CODES.M)
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if(!task.getResult().isEmpty()){
                        for (QueryDocumentSnapshot tasks : task.getResult()) {
                            UserTask user_task = tasks.toObject(UserTask.class);
                            taskBody.setText(user_task.getTask_body());
                            String date = String.valueOf(user_task.getDate());
                            setPickerDate(date);
                            String time = String.valueOf(user_task.getTime());
                            int hour = 10;
                            int minute = 30;
                            if(time.length() == 3){
                                hour = Integer.parseInt(time.substring(0,1));
                                if(date.substring(1).startsWith("0")){
                                    minute = Integer.parseInt(date.substring(2));
                                }else{
                                    minute = Integer.parseInt(time.substring(1));
                                }
                            }else if(time.length() == 4){
                                hour = Integer.parseInt(time.substring(0,2));
                                if(date.substring(2).startsWith("0")){
                                    minute = Integer.parseInt(date.substring(3));
                                }else{
                                    minute = Integer.parseInt(time.substring(2));
                                }

                            }
                            taskTimePicker.setHour(hour);
                            taskTimePicker.setMinute(minute);
                            break;
                        }
                    }
                }
            });

        }

        firebaseAuth = FirebaseAuth.getInstance();

        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                user = firebaseAuth.getCurrentUser();
                if(user == null){
                    startActivity(new Intent(AddTaskActivity.this,LoginActivity.class));
                }else{
                }
            }
        };

        addNewTaskBtn.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                if(task_body_extra != null){
                    collectionReference.whereEqualTo("userID", TaskApi.getInstance().getUserID()).
                            whereEqualTo("task_body", task_body_extra).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @RequiresApi(api = Build.VERSION_CODES.N)
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if(!task.getResult().isEmpty()){
                                for (QueryDocumentSnapshot tasks : task.getResult()) {
                                    DocumentReference docRef = db.collection("Tasks").document(tasks.getId());
                                    String date_str = getDateString();
                                    String time_str = getTimeString();
                                    long date_long = Long.parseLong(date_str);
                                    long time_long = Long.parseLong(time_str);
                                    long time_num = taskTimePicker.getHour()*3600 + taskTimePicker.getMinute()*60;
                                    docRef.update("task_body",taskBody.getText().toString());
                                    docRef.update("date",date_long);
                                    docRef.update("time",time_long);
                                    docRef.update("time_num",time_num);
                                    docRef.update("priority",priority);
                                    Intent intent = new Intent(AddTaskActivity.this, DayActivity.class);
                                    intent.putExtra("date", date_str);
                                    startActivity(intent);
                                    break;
                                }
                            }
                        }
                    });
                }else{
                    TaskApi task_api = TaskApi.getInstance();
                    String task_body = taskBody.getText().toString().trim();
                    String username = task_api.getUsername();
                    String user_id = task_api.getUserID();
                    String date_str = getDateString();
                    String time_str = getTimeString();
                    Long date_long = Long.parseLong(date_str);
                    Long time_long = Long.parseLong(time_str);
                    UserTask task_obj = new UserTask();
                    task_obj.set_time(time_long);
                    task_obj.setDate(date_long);
                    task_obj.setTask_body(task_body);
                    task_obj.setPriority(priority);
                    task_obj.setUsername(username);
                    task_obj.setUserID(user_id);
                    task_obj.setCompleted(false);
                    task_obj.setTime_num(taskTimePicker.getHour()*3600 + taskTimePicker.getMinute()*60);
    //                try {
    //                    priorityTest.setText(String.valueOf(taskDatePicker.getYear()) + String.valueOf(taskDatePicker.getMonth()) + String.valueOf(taskDatePicker.getDayOfMonth()));
    //                }catch (Exception e){
    //                    priorityTest.setText(e.getMessage());
    //                }
                    collectionReference.add(task_obj);
                    collectionReference.whereEqualTo("userID", TaskApi.getInstance().getUserID()).
                            whereEqualTo("task_body", task_obj.getTask_body()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @RequiresApi(api = Build.VERSION_CODES.N)
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            Intent intent = new Intent(AddTaskActivity.this, DayActivity.class);
                            intent.putExtra("date", date_str);
                            startActivity(intent);
                        }});
                }
            }
        });

        prioritiesSpinner.setOnItemSelectedListener(this);



    }

    @Override
    protected void onStart() {
        firebaseAuth.addAuthStateListener(authStateListener);
        user = firebaseAuth.getCurrentUser();

        super.onStart();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private String getTimeString(){
        String minute = "10";
        if(taskTimePicker.getMinute()<10){
            minute = "0" + String.valueOf(taskTimePicker.getMinute());
        }else{
            minute = String.valueOf(taskTimePicker.getMinute());
        }
        String time = String.valueOf(taskTimePicker.getHour()) + minute;
        return time;
    }

    private void setPickerDate(String date){
        int year = Integer.parseInt(date.substring(0,4));
        int month;
        if(date.substring(4,6).startsWith("0")){
            month = Integer.parseInt(date.substring(5,6));
        }else{
            month = Integer.parseInt(date.substring(4,6));
        }
        int day;
        if(date.substring(6).startsWith("0")){
            day = Integer.parseInt(date.substring(7));
        }else{
            day = Integer.parseInt(date.substring(6));
        }
        taskDatePicker.init(year, month-1, day, new DatePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

            }
        });
    }

    private String getDateString(){
        String year = String.valueOf(taskDatePicker.getYear());
        String month;
        String day;
        if(taskDatePicker.getMonth()+1 < 10){
            month  = "0" + String.valueOf(taskDatePicker.getMonth()+1);
        }else{
            month = String.valueOf(taskDatePicker.getMonth()+1);
        }
        if(taskDatePicker.getDayOfMonth() < 10){
            day  = "0" + String.valueOf(taskDatePicker.getDayOfMonth());
        }else{
            day = String.valueOf(taskDatePicker.getDayOfMonth());
        }
        String date_str = year + month + day;
        return date_str;
    }

    private void init_views() {
        taskBody = findViewById(R.id.habitBody);
        taskDatePicker = findViewById(R.id.taskDatePicker);
        addNewTaskBtn = findViewById(R.id.addNewTaskBtn);
        prioritiesSpinner = findViewById(R.id.prioritySpinner);
        taskTimePicker = findViewById(R.id.taskTimePicker);
        newTaskLabel = findViewById(R.id.newTaskLabel);

        taskTimePicker.setIs24HourView(true);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.priorities, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        prioritiesSpinner.setAdapter(adapter);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        priority = parent.getItemAtPosition(position).toString();
//        priorityTest.setText(priority);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        priority = parent.getSelectedItem().toString();
        Toast.makeText(AddTaskActivity.this, "Priority: " + priority, Toast.LENGTH_SHORT).show();
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
                Intent intent = new Intent(AddTaskActivity.this, PlannerActivity.class);
                startActivity(intent);
                break;
            case R.id.days_action:
                startActivity(new Intent(AddTaskActivity.this, MonthDaysActivity.class));
                break;
            case R.id.tasks_action:
                startActivity(new Intent(AddTaskActivity.this, MonthTasksActivity.class));
                break;
            case R.id.add_task:
                startActivity(new Intent(AddTaskActivity.this, AddTaskActivity.class));
            case R.id.main_menu:
                Intent main_menu_intent = new Intent( AddTaskActivity.this,MainActivity.class);
                startActivity(main_menu_intent);
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }
}