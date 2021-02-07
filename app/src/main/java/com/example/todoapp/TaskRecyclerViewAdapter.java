package com.example.todoapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.cardview.widget.CardView;

import com.example.todoapp.utils.DateSorter;

import java.util.ArrayList;

public class TaskRecyclerViewAdapter extends RecyclerView.Adapter<TaskRecyclerViewAdapter.ViewHolder> {

    private Context mContext;
    private Class intentContextClass;
    private String activityName;
    private ArrayList<CertainPeriodTasks> tasks = new ArrayList<>();


    public TaskRecyclerViewAdapter(Context mContext, String activityName) {
        this.mContext = mContext;
        this.activityName = activityName;
    }



    @NonNull
    @Override
    public TaskRecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.tasks_list_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskRecyclerViewAdapter.ViewHolder holder, int position) {
        if(activityName.equals("monthActivity")){
            intentContextClass = WeekActivity.class;
        }else if(activityName.equals("weekActivity")){
            intentContextClass = DayActivity.class;
        }

        Intent intent = new Intent(mContext, intentContextClass);
        if(tasks.get(position).getStart_date().equals(tasks.get(position).getEnd_date())){
            intent.putExtra ("date",tasks.get(position).getStart_date());
            String date = tasks.get(position).getStart_date().substring(6) + "." + tasks.get(position).getStart_date().substring(4,6);
            holder.dateLabel.setText(date);
        }else{
            intent.putExtra("startDate", tasks.get(position).getStart_date());
            intent.putExtra("endDate", tasks.get(position).getEnd_date());
            String date = tasks.get(position).getStart_date().substring(6) + "." + tasks.get(position).getStart_date().substring(4,6) + " - " + tasks.get(position).getEnd_date().substring(6) + "." + tasks.get(position).getEnd_date().substring(4,6);
            holder.dateLabel.setText(date);
        }

        holder.parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mContext.startActivity(intent);
            }
        });

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                mContext, android.R.layout.simple_list_item_1, tasks.get(position).getTasks_bodies()
        );

        holder.certainPeriodTasksList.setAdapter(arrayAdapter);

    }

    @Override
    public int getItemCount() {
        return tasks.size();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void setTasks(ArrayList<CertainPeriodTasks> tasks){
        tasks.sort(new DateSorter());
        this.tasks = tasks;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView dateLabel;
        ListView certainPeriodTasksList;
        CardView parent;
        ImageView expandBtn;
        RelativeLayout taskItem;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            dateLabel = itemView.findViewById(R.id.date_label);
            certainPeriodTasksList = itemView.findViewById(R.id.certainPeriodTasks);
            parent = itemView.findViewById(R.id.parent);
            expandBtn = itemView.findViewById(R.id.expandBtn);
            taskItem = itemView.findViewById(R.id.task_item);

            expandBtn.setOnClickListener(new View.OnClickListener() {
                @SuppressLint("UseCompatLoadingForDrawables")
                @Override
                public void onClick(View v) {
                    CertainPeriodTasks task = tasks.get(getAdapterPosition());
                    task.setExpanded(!task.isExpanded());
                    ViewGroup.LayoutParams params = taskItem.getLayoutParams();
                    final float scale = mContext.getResources().getDisplayMetrics().density;
                    int height;
                    if(task.isExpanded()){
                        expandBtn.setImageDrawable(mContext.getDrawable(R.drawable.ic_arrow_up));
                        height  = (int) (350 * scale);
                    }else{
                        expandBtn.setImageDrawable(mContext.getDrawable(R.drawable.ic_arrow_down));
                        height  = (int) (120 * scale);
                    }
                    params.height = height;
                    taskItem.setLayoutParams(params);
                }
            });
        }
    }
}
