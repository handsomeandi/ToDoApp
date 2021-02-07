package com.example.todoapp;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.example.todoapp.utils.TaskApi;
import com.example.todoapp.utils.Utils;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.auth.User;

import java.time.LocalDate;
import java.util.ArrayList;

public class HabitsRecyclerViewAdapter extends RecyclerView.Adapter<HabitsRecyclerViewAdapter.ViewHolder> {

    private Context mContext;
    private ArrayList<UserHabit> user_habits = new ArrayList<>();

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private CollectionReference collectionReference = db.collection("Habits");


    public HabitsRecyclerViewAdapter(Context context){
        this.mContext = context;
    }

    @NonNull
    @Override
    public HabitsRecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.habit_card,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HabitsRecyclerViewAdapter.ViewHolder holder, int position) {
        LocalDate date = LocalDate.now();
        Utils utils = Utils.getInstance();
        String start_date = utils.get_start_of_month(date);
        String end_date_string = utils.get_end_of_month(date);
        LocalDate end_date = utils.parseStringToDate(end_date_string).plusDays(1);
        LocalDate current_date = utils.parseStringToDate(start_date);
        holder.habitName.setText(user_habits.get(position).getHabit_name());
        holder.habitMonth.setText("January");
        while(!current_date.equals(end_date)){
            final float scale = mContext.getResources().getDisplayMetrics().density;

            ImageButton cell = new ImageButton(holder.itemView.getContext());
            ViewGroup.LayoutParams layParams = new ViewGroup.LayoutParams((int) (30 * scale),(int) (20 * scale));
//            layParams.height = (int) (20 * scale);
//            layParams.width = (int) (30 * scale);
            cell.setLayoutParams(layParams);
            cell.setImageResource(R.drawable.checkbox_off_back);
            cell.setBackgroundColor(mContext.getColor(R.color.white));
            holder.habitCheckboxes.addView(cell);
            holder.habitCheckboxes.invalidate();
            current_date = current_date.plusDays(1);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext,HabitActivity.class);
                intent.putExtra("habit_label", user_habits.get(position).getHabit_name());
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return user_habits.size();
    }

    public void setHabits(ArrayList<UserHabit> habits){
        this.user_habits = habits;
        notifyDataSetChanged();
    }


    public class ViewHolder extends RecyclerView.ViewHolder{
        GridLayout habitCheckboxes;
        TextView habitMonth, habitName;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            habitCheckboxes = (GridLayout) itemView.findViewById(R.id.habitCheckboxes);
            habitMonth = itemView.findViewById(R.id.habitMonth);
            habitName = itemView.findViewById(R.id.habitName);

        }
    }
}
