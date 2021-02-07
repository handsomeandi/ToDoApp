package com.example.todoapp;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.example.todoapp.utils.TaskApi;
import com.example.todoapp.utils.TimeSorter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class DayTasksRecyclerViewAdapter extends RecyclerView.Adapter<DayTasksRecyclerViewAdapter.ViewHolder>{

    private Context mContext;
    private Class intentContextClass;
    private ArrayList<UserTask> day_tasks = new ArrayList<>();


    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private CollectionReference collectionReference = db.collection("Tasks");

    public DayTasksRecyclerViewAdapter(Context context){
        this.mContext = context;
    }


    @NonNull
    @Override
    public DayTasksRecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.day_card,parent,false);
        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull DayTasksRecyclerViewAdapter.ViewHolder holder, int position) {
        String time_str = String.valueOf(day_tasks.get(position).getTime());
        String time = "";
        if(time_str.length() == 4){
            time = time_str.substring(0,2) + ":" + time_str.substring(2);
        }else if(time_str.length() == 3){
            time ="0" + time_str.substring(0,1) + ":" + time_str.substring(1);
        }
        holder.taskTime.setText(time);
        final boolean[] is_completed = {day_tasks.get(position).is_completed()};
        holder.isCompletedButton.setChecked(is_completed[0]);
        if(is_completed[0]) {
            float transparency = 0.5f;
            holder.taskTime.setAlpha(transparency);
            holder.isCompletedButton.setAlpha(transparency);
            holder.taskBody.setAlpha(transparency);
            holder.deleteBtn.setAlpha(transparency);
        }else{
            float transparency = 1f;
            holder.taskTime.setAlpha(transparency);
            holder.isCompletedButton.setAlpha(transparency);
            holder.taskBody.setAlpha(transparency);
            holder.deleteBtn.setAlpha(transparency);
        }
        holder.isCompletedButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                collectionReference.whereEqualTo("userID", TaskApi.getInstance().getUserID()).
                        whereEqualTo("task_body", day_tasks.get(position).getTask_body()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @SuppressLint("Range")
                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(!task.getResult().isEmpty()){
                            for (QueryDocumentSnapshot tasks : task.getResult()) {
                                DocumentReference docRef = db.collection("Tasks").document(tasks.getId());
                                is_completed[0] = !is_completed[0];
                                docRef.update("_completed",is_completed[0]);
                                day_tasks.get(position).setCompleted(is_completed[0]);
                                if(is_completed[0]) {
                                    float transparency = 0.5f;
                                    holder.taskTime.setAlpha(transparency);
                                    holder.isCompletedButton.setAlpha(transparency);
                                    holder.taskBody.setAlpha(transparency);
                                    holder.deleteBtn.setAlpha(transparency);
                                }else{
                                    float transparency = 1f;
                                    holder.taskTime.setAlpha(transparency);
                                    holder.isCompletedButton.setAlpha(transparency);
                                    holder.taskBody.setAlpha(transparency);
                                    holder.deleteBtn.setAlpha(transparency);
                                }

                                break;
                            }
                        }
                    }
                });
            }
        });
        String priority = day_tasks.get(position).getPriority();
        switch (priority){
            case "Высокий":
                holder.taskBody.setTextColor(this.mContext.getResources().getColor(R.color.white));
                holder.taskBody.setBackgroundColor(this.mContext.getResources().getColor(R.color.red));
                break;
            case "Средний":
                holder.taskBody.setTextColor(this.mContext.getResources().getColor(R.color.white));
                holder.taskBody.setBackgroundColor(this.mContext.getResources().getColor(R.color.orange));
                break;
            case "Низкий:":
                holder.taskBody.setTextColor(this.mContext.getResources().getColor(R.color.black));
                holder.taskBody.setBackgroundColor(this.mContext.getResources().getColor(R.color.white));
                break;
            default:
                break;
        }
        holder.taskBody.setText(day_tasks.get(position).getTask_body());
        holder.taskBody.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, AddTaskActivity.class);
                intent.putExtra("task_body", day_tasks.get(position).getTask_body());
                mContext.startActivity(intent);
            }
        });
        holder.deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alert = new AlertDialog.Builder(mContext);
                alert.setPositiveButton("Удалить", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        collectionReference.whereEqualTo("userID", TaskApi.getInstance().getUserID()).
                                whereEqualTo("task_body", day_tasks.get(position).getTask_body()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @RequiresApi(api = Build.VERSION_CODES.N)
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if(!task.getResult().isEmpty()){
                                    for (QueryDocumentSnapshot tasks : task.getResult()) {
                                        DocumentReference docRef = db.collection("Tasks").document(tasks.getId());
                                        docRef.delete();
                                        day_tasks.remove(day_tasks.get(position));
                                        notifyDataSetChanged();
                                        break;
                                    }
                                }
                            }
                        });
                    }
                });
                alert.setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                alert.setMessage("Удалить задачу?");
                alert.create().show();

            }
        });
    }

    @Override
    public int getItemCount() {
        return day_tasks.size();
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    public void setDay_tasks(ArrayList<UserTask> tasks){
        tasks.sort(new TimeSorter());
        this.day_tasks = tasks;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView taskTime, taskBody;
        CheckBox isCompletedButton;
        ImageButton deleteBtn;



        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            taskTime = itemView.findViewById(R.id.taskTime);
            taskBody = itemView.findViewById(R.id.habitBody);
            isCompletedButton = itemView.findViewById(R.id.isCompletedButton);
            deleteBtn = itemView.findViewById(R.id.deleteBtn);
        }
    }
}
