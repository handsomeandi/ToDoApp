package com.example.todoapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.example.todoapp.utils.TaskApi;
import com.example.todoapp.utils.Utils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Map;

public class HabitActivity extends AppCompatActivity {

    GridLayout habitDays;
    TextView habitLabel, monthLabel;
    PopupWindow fail_accomplish_popup;
    ArrayList<Map<String,Boolean>> dates_checked = new ArrayList<>();
    boolean checkbox_click = true;



    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private CollectionReference collectionReference = db.collection("Habits");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_habit);

        habitDays = (GridLayout) findViewById(R.id.daysOfMonth);
        habitLabel = findViewById(R.id.habitLabel);
        monthLabel = findViewById(R.id.monthNameLabel);



        Intent habit_intent = getIntent();
        if(habit_intent != null){
            String habitNameFromIntent = habit_intent.getStringExtra("habit_label");
            LocalDate date = LocalDate.now();
            Utils utils = Utils.getInstance();
            String start_date = utils.get_start_of_month(date);
            String end_date_string = utils.get_end_of_month(date);
            LocalDate end_date = utils.parseStringToDate(end_date_string).plusDays(1);
            LocalDate current_date = utils.parseStringToDate(start_date);
            habitLabel.setText(habitNameFromIntent);
            monthLabel.setText(utils.get_month_name_by_num(start_date.substring(4,6)));
            collectionReference.whereEqualTo("userID", TaskApi.getInstance().getUserID()).whereEqualTo("habit_name",habitNameFromIntent)
                        .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        for(QueryDocumentSnapshot habit: task.getResult()){
                            UserHabit userHabit = habit.toObject(UserHabit.class);
                            dates_checked = userHabit.getDates_checked();
                        }
                    }
                });
            while(!current_date.equals(end_date)){
                Boolean habit_accomplished = null;
                String current_date_string = utils.parseDateToString(current_date);
                if(!dates_checked.isEmpty()){
                    for(Map<String,Boolean> checkedDate : dates_checked){
                        for(Map.Entry<String,Boolean> key_values : checkedDate.entrySet()){
                            if(current_date_string.equals(key_values.getKey())){
                                habit_accomplished = key_values.getValue();
                            }
                        }
                    }
                }
                final float scale = getResources().getDisplayMetrics().density;

                ImageButton cell = new ImageButton(HabitActivity.this);
                ViewGroup.LayoutParams layParams = new ViewGroup.LayoutParams((int) (50 * scale),(int) (50 * scale));
//            layParams.height = (int) (20 * scale);
//            layParams.width = (int) (30 * scale);

                cell.setLayoutParams(layParams);
                if(habit_accomplished != null){
                    if(habit_accomplished){
                        cell.setImageResource(R.drawable.checkbox_completed);
                    }else{
                        cell.setImageResource(R.drawable.ic_checkbox_cross);
                    }
                }else{
                    cell.setImageResource(R.drawable.checkbox_off_back);
                }
                cell.setBackgroundColor(getColor(R.color.white));
                cell.setTag(current_date_string);




                cell.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        CheckboxPopup checkboxPopup = new CheckboxPopup();
                        checkboxPopup.showPopup(v);
                    }
                });
                habitDays.addView(cell);
                habitDays.invalidate();
                current_date = current_date.plusDays(1);
            }
        }
            }
}