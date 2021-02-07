package com.example.todoapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.todoapp.utils.TaskApi;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class MainActivity extends AppCompatActivity {

    Button plannerBtn,statisticsBtn,trackerBtn, signOutBtn;
    TextView usernameLabel;
    FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser currentUser;

    //Firestore connection
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private CollectionReference collectionReference = db.collection("Users");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        plannerBtn = findViewById(R.id.plannerBtn);
        statisticsBtn = findViewById(R.id.stat_btn);
        signOutBtn = findViewById(R.id.signOutBtn);
        usernameLabel = findViewById(R.id.usernameLabel);
        trackerBtn = findViewById(R.id.trackerBtn);
        firebaseAuth = FirebaseAuth.getInstance();


        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                currentUser = firebaseAuth.getCurrentUser();

                if(currentUser == null){
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                }else{
                    String userId = currentUser.getUid();
                    collectionReference.whereEqualTo("userId", userId).addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                            if(!value.isEmpty()){
                                for(QueryDocumentSnapshot user : value){
                                    TaskApi task_api = TaskApi.getInstance();
                                    task_api.setUsername(user.getString("username"));
                                    task_api.setUserID(user.getString("userId"));
                                    usernameLabel.setText(task_api.getUsername());

                                }
                            }
                        }
                    });
                }
            }
        };

        signOutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(firebaseAuth != null && currentUser != null){
                    firebaseAuth.signOut();
                }
            }
        });

        plannerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, PlannerActivity.class);
                startActivity(intent);
            }
        });
        trackerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, TrackerActivity.class);
                startActivity(intent);
            }
        });
//        statisticsBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startActivity(new Intent(MainActivity.this, AddTaskActivity.class));
//            }
//        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        currentUser = firebaseAuth.getCurrentUser();

        firebaseAuth.addAuthStateListener(authStateListener);

    }
}