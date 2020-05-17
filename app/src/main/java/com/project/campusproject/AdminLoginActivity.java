package com.project.campusproject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.project.campusproject.data.LoginData;

public class AdminLoginActivity extends AppCompatActivity {

    TextInputEditText et_pword,et_uname;
    ProgressBar progress_circular;
    AppCompatButton loginBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_login);
        loginBtn = findViewById(R.id.loginBtn);
        progress_circular = findViewById(R.id.progress_circular);
        et_pword = findViewById(R.id.et_pword);
        et_uname = findViewById(R.id.et_uname);
        loginBtn.setVisibility(View.GONE);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference myRef = database.getReference();
        final DatabaseReference login = myRef.child("login");
        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                progress_circular.setVisibility(View.GONE);
                loginBtn.setVisibility(View.VISIBLE);
                LoginData post = dataSnapshot.getValue(LoginData.class);
                Log.e("LoginData", new Gson().toJson(post));
                loginBtn.setOnClickListener(view -> {
                    if(auth(post))
                        startActivity(new Intent(AdminLoginActivity.this,AdminManageActivity.class));
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w("Data", "loadPost:onCancelled", databaseError.toException());
                // ...
            }
        };
        login.addValueEventListener(postListener);
    }

    private boolean auth(LoginData login) {
        String uname = et_uname.getText().toString();
        String pwrd = et_pword.getText().toString();
        if(!uname.equals(login.getUsername())){
            Toast.makeText(this, "Authentication Failed", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(!pwrd.equals(login.getPassword())){
            Toast.makeText(this, "Authentication Failed", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;

    }
}
