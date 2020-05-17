package com.project.campusproject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.project.campusproject.data.Issue;
import com.project.campusproject.data.LocationData;
import com.project.campusproject.data.ReportProblem;

import java.util.ArrayList;
import java.util.List;

public class RaiseIssueActivity extends AppCompatActivity {

    AppCompatEditText et_issue_drainage;
    AppCompatEditText et_electrical_drainage;
    AppCompatEditText et_garbage_drainage;
    AppCompatEditText et_other_drainage;

    ReportProblem locationDataPassed;
    List<ReportProblem> locationDataList = new ArrayList<>();

    CheckBox drainageCb;
    CheckBox electricCb;
    CheckBox garbageCb;
    CheckBox otherCb;

    TextInputLayout textinputDrainage;
    TextInputLayout electricalLayout;
    TextInputLayout garbageLayout;
    TextInputLayout otherLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_raise_issue);

        et_issue_drainage = findViewById(R.id.et_issue_drainage);
        et_electrical_drainage = findViewById(R.id.et_electrical_issue);
        et_garbage_drainage = findViewById(R.id.et_garbage_issue);
        et_other_drainage = findViewById(R.id.et_other_issue);

        textinputDrainage = findViewById(R.id.textinputDrainage);
        drainageCb  = findViewById(R.id.drainageCb);
        electricalLayout = findViewById(R.id.electricalLayout);
        electricCb  = findViewById(R.id.electricCb);
        garbageLayout = findViewById(R.id.garbageLayout);
        garbageCb  = findViewById(R.id.garbageCb);
        otherLayout = findViewById(R.id.otherLayout);
        otherCb  = findViewById(R.id.otherCb);

        drainageCb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if(isChecked){
                    otherLayout.setVisibility(View.GONE);
                    garbageLayout.setVisibility(View.GONE);
                    electricalLayout.setVisibility(View.GONE);

                    textinputDrainage.setVisibility(View.VISIBLE);
                }else {
                    textinputDrainage.setVisibility(View.GONE);
                }
            }
        });
        electricCb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if(isChecked){
                    textinputDrainage.setVisibility(View.GONE);
                    garbageLayout.setVisibility(View.GONE);
                    otherLayout.setVisibility(View.GONE);

                    electricalLayout.setVisibility(View.VISIBLE);
                }else {
                    electricalLayout.setVisibility(View.GONE);
                }
            }
        });
        garbageCb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if(isChecked){
                    electricalLayout.setVisibility(View.GONE);
                    textinputDrainage.setVisibility(View.GONE);
                    otherLayout.setVisibility(View.GONE);

                    garbageLayout.setVisibility(View.VISIBLE);
                }else {
                    garbageLayout.setVisibility(View.GONE);
                }
            }
        });
        otherCb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if(isChecked){
                    garbageLayout.setVisibility(View.GONE);
                    electricalLayout.setVisibility(View.GONE);
                    textinputDrainage.setVisibility(View.GONE);

                    otherLayout.setVisibility(View.VISIBLE);
                }else {
                    otherLayout.setVisibility(View.GONE);
                }
            }
        });



        locationDataPassed = new Gson().fromJson(getIntent().getStringExtra("data"),ReportProblem.class);
        AppCompatButton submitIssue = findViewById(R.id.submitIssue);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference myRef = database.getReference();
        final DatabaseReference locationsListRef = myRef.child("complaints");
        Query query = locationsListRef.orderByChild("latitude");
        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                locationDataList.clear();
                for(DataSnapshot ds : dataSnapshot.getChildren()) {
                    ReportProblem locationData = ds.getValue(ReportProblem.class);
                    locationDataList.add(locationData);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        };
        query.addListenerForSingleValueEvent(eventListener);

        submitIssue.setOnClickListener(view -> {
            String issueD = et_issue_drainage.getText().toString();
            String issueE = et_electrical_drainage.getText().toString();
            String issueG = et_garbage_drainage.getText().toString();
            String issueO = et_other_drainage.getText().toString();
            Issue Drainissue = new Issue();
            Issue Electricissue = new Issue();
            Issue Garbageissue = new Issue();
            Issue Otherissue = new Issue();
            if(drainageCb.isChecked()){
                Drainissue.setStatus(1);
                Drainissue.setDesc(issueD);
            }else {
                Drainissue.setStatus(0);
                Drainissue.setDesc("");
            }
            locationDataPassed.setDrainage(Drainissue);

            if(electricCb.isChecked()){
                Electricissue.setStatus(1);
                Electricissue.setDesc(issueE);
            }else {
                Electricissue.setStatus(0);
                Electricissue.setDesc("");
            }
            locationDataPassed.setElectrical(Electricissue);

            if(garbageCb.isChecked()){
                Garbageissue.setStatus(1);
                Garbageissue.setDesc(issueG);
            }else {
                Garbageissue.setStatus(0);
                Garbageissue.setDesc("");
            }
            locationDataPassed.setGarbage(Garbageissue);

            if(otherCb.isChecked()){
                Otherissue.setStatus(1);
                Otherissue.setDesc(issueO);
            }else {
                Otherissue.setStatus(0);
                Otherissue.setDesc("");
            }
            locationDataPassed.setOther(Otherissue);

            locationDataList.add(locationDataPassed);
            locationsListRef.setValue(locationDataList);
            Toast.makeText(this, "Reported Issue.", Toast.LENGTH_SHORT).show();
            finish();


        });

    }
}
