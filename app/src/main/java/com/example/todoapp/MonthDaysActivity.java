package com.example.todoapp;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.DatePicker;

public class MonthDaysActivity extends AppCompatActivity {

    DatePicker picker;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_month_days);

        picker = findViewById(R.id.dayPicker);

        picker.setOnDateChangedListener(new DatePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                String date = "";
                if(monthOfYear < 10){
                    date = String.valueOf(year) + "0" + String.valueOf(monthOfYear+1);
                }else{
                    date = String.valueOf(year) + String.valueOf(monthOfYear+1);
                }
                if(dayOfMonth < 10){
                    date += "0" + String.valueOf(dayOfMonth);
                }else{
                    date += String.valueOf(dayOfMonth);
                }
                Intent intent = new Intent(MonthDaysActivity.this, DayActivity.class);
                intent.putExtra("date", date);
                startActivity(intent);
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
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
                Intent intent = new Intent(MonthDaysActivity.this, PlannerActivity.class);
                startActivity(intent);
                break;
            case R.id.days_action:
                startActivity(new Intent(MonthDaysActivity.this, MonthDaysActivity.class));
                break;
            case R.id.tasks_action:
                startActivity(new Intent(MonthDaysActivity.this, MonthTasksActivity.class));
                break;
            case R.id.add_task:
                startActivity(new Intent(MonthDaysActivity.this, AddTaskActivity.class));
            case R.id.main_menu:
                Intent main_menu_intent = new Intent( MonthDaysActivity.this,MainActivity.class);
                startActivity(main_menu_intent);
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }
}