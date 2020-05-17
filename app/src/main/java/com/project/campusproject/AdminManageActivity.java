package com.project.campusproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class AdminManageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_manage);
        TextView showLocationTv = findViewById(R.id.showLocationTv);
        showLocationTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(AdminManageActivity.this,ViewIssuesActivity.class));
            }
        });
        TextView addLocationTv = findViewById(R.id.addLocationTv);
        addLocationTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(AdminManageActivity.this,AdminMapActivity.class));
            }
        });
    }
}
