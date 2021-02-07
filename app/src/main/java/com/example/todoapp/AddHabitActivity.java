package com.example.todoapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.todoapp.utils.TaskApi;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AddHabitActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {


    Button addHabitBtn;
    EditText habitBody;
    Spinner habitPriorities;
    String priority;


    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private CollectionReference collectionReference = db.collection("Habits");



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_habit);

        habitBody = findViewById(R.id.habitBody);
        habitPriorities = findViewById(R.id.prioritySpinner);
        addHabitBtn = findViewById(R.id.addNewHabitBtn);
        habitPriorities.setOnItemSelectedListener(this);

        addHabitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!habitBody.getText().equals(null)){
                    TaskApi task_api = TaskApi.getInstance();
                    String habit_body = habitBody.getText().toString().trim();
                    String user_id = task_api.getUserID();
                    UserHabit habit_obj = new UserHabit();
                    habit_obj.setUserID(user_id);
                    habit_obj.setHabit_name(habit_body);
                    habit_obj.setPriority(priority);
                    habit_obj.setDates_checked(null);
                    //                try {
                    //                    priorityTest.setText(String.valueOf(taskDatePicker.getYear()) + String.valueOf(taskDatePicker.getMonth()) + String.valueOf(taskDatePicker.getDayOfMonth()));
                    //                }catch (Exception e){
                    //                    priorityTest.setText(e.getMessage());
                    //                }
                    collectionReference.add(habit_obj);
                    collectionReference.whereEqualTo("userID", TaskApi.getInstance().getUserID()).
                            whereEqualTo("habit_name", habit_obj.getHabit_name()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @RequiresApi(api = Build.VERSION_CODES.N)
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            Intent intent = new Intent(AddHabitActivity.this, TrackerActivity.class);
                            startActivity(intent);
                        }});
                }
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();


        ArrayAdapter<CharSequence> priority_adapter = ArrayAdapter.createFromResource(this,
                R.array.priorities, android.R.layout.simple_spinner_item);
        priority_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        habitPriorities.setAdapter(priority_adapter);

    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        priority = parent.getItemAtPosition(position).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        priority = parent.getSelectedItem().toString();
    }
}