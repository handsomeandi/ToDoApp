package com.example.todoapp;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

public class CheckboxPopup {

    public CheckboxPopup() {

    }

    public void showPopup(View view){
        LayoutInflater inflater = (LayoutInflater) view.getContext().getSystemService(view.getContext().LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.accomplish_fail_popup, null);

        int width = LinearLayout.LayoutParams.MATCH_PARENT;
        int height = LinearLayout.LayoutParams.MATCH_PARENT;


        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, false);


        ImageButton check_complete = popupView.findViewById(R.id.check_completed);
        ImageButton check_failed = popupView.findViewById(R.id.check_failed);




        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);



        popupView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                popupWindow.dismiss();
                return true;
            }
        });
    }

}
