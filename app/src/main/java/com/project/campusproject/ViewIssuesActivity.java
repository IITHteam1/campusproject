package com.project.campusproject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.jaredrummler.materialspinner.MaterialSpinner;
import com.project.campusproject.data.ReportProblem;
import com.project.campusproject.utils.MyRecyclerViewAdapter;

import java.util.ArrayList;
import java.util.List;

public class ViewIssuesActivity extends AppCompatActivity implements MyRecyclerViewAdapter.ItemClickListener{

    List<ReportProblem> locationDataList = new ArrayList<>();
    List<ReportProblem> swapLocationDataList = new ArrayList<>();

    List<ReportProblem> drainageDataList = new ArrayList<>();
    List<ReportProblem> electricDataList = new ArrayList<>();
    List<ReportProblem> garbageDataList = new ArrayList<>();
    List<ReportProblem> otherDataList = new ArrayList<>();

    RecyclerView rvIssues;
    MaterialSpinner spinner;
    ProgressBar progress_circular;
    MyRecyclerViewAdapter myRecyclerViewAdapter;
    DatabaseReference locationsListRefForResolve = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_issues);
        progress_circular = findViewById(R.id.progress_circular);
        rvIssues = findViewById(R.id.rvIssues);
        myRecyclerViewAdapter = new MyRecyclerViewAdapter(ViewIssuesActivity.this,swapLocationDataList);
        myRecyclerViewAdapter.setClickListener(this);
        rvIssues.setAdapter(myRecyclerViewAdapter);
        spinner = findViewById(R.id.spinner);
        spinner.setItems("Drainage","Electricity","Garbage","Other");
        spinner.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, Object item) {
                if(position == 0){
                    swapLocationDataList.clear();
                    swapLocationDataList.addAll(drainageDataList);
                    myRecyclerViewAdapter.notifyDataSetChanged();
                }if(position == 1){
                    swapLocationDataList.clear();
                    swapLocationDataList.addAll(electricDataList);
                    myRecyclerViewAdapter.notifyDataSetChanged();
                }if(position == 2){
                    swapLocationDataList.clear();
                    swapLocationDataList.addAll(garbageDataList);
                    myRecyclerViewAdapter.notifyDataSetChanged();
                }if(position == 3){
                    swapLocationDataList.clear();
                    swapLocationDataList.addAll(otherDataList);
                    myRecyclerViewAdapter.notifyDataSetChanged();
                }

            }
        });
        rvIssues.setLayoutManager(new LinearLayoutManager(this));
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference myRef = database.getReference();
        final DatabaseReference locationsListRef = myRef.child("complaints");
        locationsListRefForResolve = locationsListRef;
        Query query = locationsListRef.orderByChild("latitude");
        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                progress_circular.setVisibility(View.GONE);
                swapLocationDataList.clear();
                locationDataList.clear();
                for(DataSnapshot ds : dataSnapshot.getChildren()) {
                    ReportProblem locationData = ds.getValue(ReportProblem.class);
                    locationDataList.add(locationData);
                    if(locationData != null){

                        if(locationData.getDrainage()!=null){
                            if(locationData.getDrainage().getStatus() == 1){
                                drainageDataList.add(locationData);
                            }
                        }
                        if(locationData.getElectrical()!=null){
                            if(locationData.getElectrical().getStatus() == 1){
                                electricDataList.add(locationData);
                            }
                        }
                        if(locationData.getGarbage()!=null){
                            if(locationData.getGarbage().getStatus() == 1){
                                garbageDataList.add(locationData);
                            }
                        }
                        if(locationData.getOther()!=null){
                            if(locationData.getOther().getStatus() == 1){
                                otherDataList.add(locationData);
                            }
                        }


                    }

                }
                swapLocationDataList.addAll(drainageDataList);
                myRecyclerViewAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        };
        query.addListenerForSingleValueEvent(eventListener);
    }

    @Override
    public void onItemClick(View view, int position,ReportProblem reportProblem) {
        swapLocationDataList.remove(position);
        myRecyclerViewAdapter.notifyDataSetChanged();
        for(int k = 0 ; k <locationDataList.size();k++){
            if(reportProblem.getLatitude() == locationDataList.get(k).getLatitude() && reportProblem.getLongitude() == locationDataList.get(k).getLongitude()){
                locationDataList.remove(k);
                Log.e("Location Data","Equal");
            }
        }
        if(locationsListRefForResolve!= null){
            locationsListRefForResolve.setValue(locationDataList);
        }

        Toast.makeText(this, "Resolved", Toast.LENGTH_SHORT).show();
    }
}
