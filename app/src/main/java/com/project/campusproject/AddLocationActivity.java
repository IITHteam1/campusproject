package com.project.campusproject;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.project.campusproject.data.LocationData;
import com.project.campusproject.utils.SpinnerAdapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AddLocationActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    AppCompatEditText et_name,et_address,et_latitude,et_longitude;
    ProgressBar progress_circular;
    AppCompatButton submitDataBtn;
    Spinner mySpinner;
    int iconSelected = 0;


    int[] imageArray = { R.mipmap.poi,R.mipmap.food, R.mipmap.block,
            R.mipmap.ground, R.mipmap.learning,R.mipmap.people,R.mipmap.road };

    //Ids will be 1,2,3,4,5,6

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_location);
        initViews();
        initSpinner();
        if(getIntent().getStringExtra("data")!=null){
            LocationData locationData = new Gson().fromJson(getIntent().getStringExtra("data"),LocationData.class);
            et_latitude.setText(locationData.getLatitude());
            et_longitude.setText(locationData.getLongitude());
        }
        progress_circular = findViewById(R.id.progress_circular);
        submitDataBtn = findViewById(R.id.submitDataBtn);
        submitDataBtn.setEnabled(false);

        final List<LocationData> locationDataList = new ArrayList<>();

        FirebaseDatabase database = FirebaseDatabase.getInstance();

        final DatabaseReference myRef = database.getReference();
        final DatabaseReference locationsListRef = myRef.child("locations");
        Query query = locationsListRef.orderByChild("name");
        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                submitDataBtn.setEnabled(true);
                progress_circular.setVisibility(View.GONE);
                locationDataList.clear();
                for(DataSnapshot ds : dataSnapshot.getChildren()) {
                    LocationData locationData = ds.getValue(LocationData.class);
                    locationDataList.add(locationData);
                    Log.d("AddLocationData", new Gson().toJson(locationDataList));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        };
        query.addListenerForSingleValueEvent(eventListener);



        submitDataBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(validateData()){
                    String name = et_name.getText().toString();
                    String address = et_address.getText().toString();
                    String latitude = et_latitude.getText().toString();
                    String longitude = et_longitude.getText().toString();
                    LocationData locationData = new LocationData();
                    locationData.setName(name);
                    locationData.setAddress(address);
                    locationData.setLatitude(latitude);
                    locationData.setLongitude(longitude);
                    locationData.setIcon(iconSelected);
                    String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());
                    locationData.setId(timeStamp);
                    locationDataList.add(locationData);
                    locationsListRef.setValue(locationDataList);

                    et_name.setText("");
                    et_address.setText("");
                    et_latitude.setText("");
                    et_longitude.setText("");
                    et_name.requestFocus();
                    Toast.makeText(AddLocationActivity.this, "Added data.", Toast.LENGTH_SHORT).show();

                }else {
                    Toast.makeText(AddLocationActivity.this, "Some fields are empty.", Toast.LENGTH_SHORT).show();


                }
            }
        });
    }

    private void initViews() {
        et_name = findViewById(R.id.et_name);
        et_address = findViewById(R.id.et_address);
        et_latitude = findViewById(R.id.et_latitude);
        et_longitude = findViewById(R.id.et_longitude);
    }

    private void initSpinner() {
        mySpinner = findViewById(R.id.mySpinner);
        mySpinner.setOnItemSelectedListener(this);

        SpinnerAdapter customAdapter = new SpinnerAdapter(this,imageArray);
        mySpinner.setAdapter(customAdapter);

    }

    private boolean validateData() {
        boolean isOkay = true;
        String name = et_name.getText().toString();
        String address = et_address.getText().toString();
        String latitude = et_latitude.getText().toString();
        String longitude = et_longitude.getText().toString();
        if(name.isEmpty()||address.isEmpty()||latitude.isEmpty()||longitude.isEmpty()){
            isOkay = false;
        }
        return isOkay;
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        iconSelected = i;
        //Toast.makeText(this, "Selected", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}
