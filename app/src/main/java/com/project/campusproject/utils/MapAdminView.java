package com.project.campusproject.utils;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PointF;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.google.gson.Gson;
import com.here.android.mpa.common.GeoCoordinate;
import com.here.android.mpa.common.Image;
import com.here.android.mpa.common.OnEngineInitListener;
import com.here.android.mpa.common.ViewObject;
import com.here.android.mpa.mapping.AndroidXMapFragment;
import com.here.android.mpa.mapping.Map;
import com.here.android.mpa.mapping.MapGesture;
import com.here.android.mpa.mapping.MapLabeledMarker;
import com.here.android.mpa.mapping.MapMarker;
import com.here.android.mpa.mapping.MapOverlay;
import com.project.campusproject.AddLocationActivity;
import com.project.campusproject.R;
import com.project.campusproject.RaiseIssueActivity;
import com.project.campusproject.data.LocationData;
import com.project.campusproject.data.ReportProblem;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MapAdminView {
    private AndroidXMapFragment m_mapFragment;
    private AppCompatActivity m_activity;
    private Map m_map;
    private AppCompatButton addBtn;
    private AppCompatButton clearBtn;
    private GeoCoordinate pickedLatLng;
    private List<LocationData> locationDataList;
    private boolean isMarkersAdded;
    int[] imageArray = { R.mipmap.poi,R.mipmap.food, R.mipmap.block,
            R.mipmap.ground, R.mipmap.learning,R.mipmap.people,R.mipmap.road };

    public MapAdminView(AppCompatActivity activity, AppCompatButton add, AppCompatButton clear,List<LocationData> locationDatas) {
        m_activity = activity;
        addBtn = add;
        clearBtn = clear;
        locationDataList =  locationDatas;
        initMapFragment();
    }

    private void addMarker(LocationData locationData){
        Double latitude = Double.parseDouble(locationData.getLatitude());
        Double longitude = Double.parseDouble(locationData.getLongitude());
        GeoCoordinate geoCoordinate = new GeoCoordinate(latitude,longitude);
        Image icon = new Image();
        try {
            if(locationData.getIcon()!=null){
                icon.setImageResource(imageArray[locationData.getIcon()]);
            }else {
                icon.setImageResource(imageArray[0]);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }


        MapLabeledMarker mapLabeledMarker = new MapLabeledMarker(geoCoordinate,icon);
        mapLabeledMarker.setLabelText("ENG",locationData.getName());
        mapLabeledMarker.setTitle(locationData.getName());
        mapLabeledMarker.setDescription(locationData.getAddress());
        m_map.addMapObject(mapLabeledMarker);



    }

    public void updateMarkers(List<LocationData> locationData){
        this.locationDataList = locationData;
        if(m_map!=null){
            if(!isMarkersAdded){
                for(int i = 0;i<locationDataList.size();i++){
                    addMarker(locationDataList.get(i));
                }
            }
        }

    }


    private AndroidXMapFragment getMapFragment() {
        return (AndroidXMapFragment) m_activity.getSupportFragmentManager().findFragmentById(R.id.mapfragment);
    }

    private void initMapFragment() {
        /* Locate the mapFragment UI element */
        m_mapFragment = getMapFragment();

        // Set path of disk cache
        String diskCacheRoot = m_activity.getFilesDir().getPath()
                + File.separator + ".isolated-here-maps";

        boolean success = com.here.android.mpa.common.MapSettings.setIsolatedDiskCacheRootPath(diskCacheRoot);
        if (!success) {
            // Setting the isolated disk cache was not successful, please check if the path is valid and
            // ensure that it does not match the default location
            // (getExternalStorageDirectory()/.here-maps).
        } else {
            if (m_mapFragment != null) {
                /* Initialize the AndroidXMapFragment, results will be given via the called back. */
                m_mapFragment.init(new OnEngineInitListener() {
                    @Override
                    public void onEngineInitializationCompleted(Error error) {

                        if (error == Error.NONE) {

                            clearBtn.setOnClickListener(view -> {
                                pickedLatLng = null;
                                clearBtn.setVisibility(View.GONE);
                                if (m_positionIndicatorFixed != null)
                                    m_map.removeMapObject(m_positionIndicatorFixed);
                                addBtn.setText("Add New");
                            });

                            addBtn.setOnClickListener(view -> {
                                if (pickedLatLng != null) {
                                    LocationData locationData = new LocationData();
                                    String lat = String.valueOf(pickedLatLng.getLatitude());
                                    String longi = String.valueOf(pickedLatLng.getLongitude());
                                    locationData.setLatitude(lat);
                                    locationData.setLongitude(longi);
                                    pickedLatLng = null;
                                    clearBtn.setVisibility(View.GONE);
                                    addBtn.setText("Add New");
                                    if (m_positionIndicatorFixed != null)
                                        m_map.removeMapObject(m_positionIndicatorFixed);
                                    Intent intent = new Intent(m_activity, AddLocationActivity.class);
                                    intent.putExtra("data", new Gson().toJson(locationData));
                                    m_activity.startActivity(intent);
                                } else {
                                    Intent intent = new Intent(m_activity, AddLocationActivity.class);
                                    m_activity.startActivity(intent);
                                }
                            });

                            /*
                             * If no error returned from map fragment initialization, the map will be
                             * rendered on screen at this moment.Further actions on map can be provided
                             * by calling Map APIs.
                             */
                            m_map = m_mapFragment.getMap();
                            m_map.setZoomLevel(14);
                            GeoCoordinate geoCoordinate = new GeoCoordinate(17.5947027, 78.1230401);
                            m_map.setCenter(geoCoordinate, Map.Animation.NONE);

                            /* create an image to mark coordinate when tap event happens */

                            /*Image icon = new Image();
                            m_positionIndicatorFixed = new MapMarker();
                            try {
                                icon.setImageResource(R.mipmap.circle);
                                m_positionIndicatorFixed.setIcon(icon);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                            m_positionIndicatorFixed.setVisible(true);
                            m_positionIndicatorFixed.setCoordinate(geoCoordinate);
                            m_map.addMapObject(m_positionIndicatorFixed);*/

                            if(locationDataList.isEmpty()){
                                isMarkersAdded = false;
                            }
                            for(int i = 0;i<locationDataList.size();i++){
                                addMarker(locationDataList.get(i));
                            }

                            m_mapFragment.getMapGesture()
                                    .addOnGestureListener(new MapGesture.OnGestureListener() {
                                        @Override
                                        public void onPanStart() {
                                            showMsg("onPanStart");
                                        }

                                        @Override
                                        public void onPanEnd() {
                                            /* show toast message for onPanEnd gesture callback */
                                            showMsg("onPanEnd");
                                        }

                                        @Override
                                        public void onMultiFingerManipulationStart() {

                                        }

                                        @Override
                                        public void onMultiFingerManipulationEnd() {

                                        }

                                        @Override
                                        public boolean onMapObjectsSelected(List<ViewObject> list) {
                                            return false;
                                        }

                                        @Override
                                        public boolean onTapEvent(PointF pointF) {
                                            clearBtn.setVisibility(View.VISIBLE);
                                            addBtn.setText("Add This");
                                            pickedLatLng = m_map.pixelToGeo(pointF);
                                            m_map.setCenter(pickedLatLng, Map.Animation.NONE);

                                            /* create an image to mark coordinate when tap event happens */

                                            if (m_positionIndicatorFixed != null)
                                                m_map.removeMapObject(m_positionIndicatorFixed);
                                            Image icon = new Image();
                                            try {
                                                icon.setImageResource(R.mipmap.mapsred);
                                            } catch (IOException e) {
                                                e.printStackTrace();
                                            }

                                            m_positionIndicatorFixed = new MapLabeledMarker(pickedLatLng, icon);
                                            String showData = pickedLatLng.getLatitude() + "\n" + pickedLatLng.getLongitude();
                                            m_positionIndicatorFixed.setVisible(true);
                                            m_positionIndicatorFixed.setLabelText("ENG", showData);
                                            m_map.addMapObject(m_positionIndicatorFixed);

                                            /* show toast message for onPanEnd gesture callback */
                                            showMsg("onTapEvent");
                                            /*
                                             * add map screen marker at coordinates of gesture. if map
                                             * screen marker already exists, change to new coordinate
                                             */


                                            return false;
                                        }

                                        @Override
                                        public boolean onDoubleTapEvent(PointF pointF) {
                                            return false;
                                        }

                                        @Override
                                        public void onPinchLocked() {

                                        }

                                        @Override
                                        public boolean onPinchZoomEvent(float v, PointF pointF) {
                                            return false;
                                        }

                                        @Override
                                        public void onRotateLocked() {

                                        }

                                        @Override
                                        public boolean onRotateEvent(float v) {
                                            /* show toast message for onRotateEvent gesture callback */
                                            showMsg("onRotateEvent");
                                            return false;
                                        }

                                        @Override
                                        public boolean onTiltEvent(float v) {
                                            return false;
                                        }

                                        @Override
                                        public boolean onLongPressEvent(PointF pointF) {

                                            GeoCoordinate geoCoordinate = m_map.pixelToGeo(pointF);
                                            LocationData locationData = new LocationData();
                                            String lat = String.valueOf(geoCoordinate.getLatitude());
                                            String longi = String.valueOf(geoCoordinate.getLongitude());
                                            locationData.setLatitude(lat);
                                            locationData.setLongitude(longi);
                                            AlertDialog.Builder builder =
                                                    new AlertDialog.Builder(m_activity);
                                            builder.setTitle("Confirm");
                                            builder.setMessage("Do you want to add an amenity in \n Latitude : " + lat + "\n Longitude : " + longi + "\n this location ?");
                                            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {
                                                    dialogInterface.dismiss();
                                                    Intent intent = new Intent(m_activity, AddLocationActivity.class);
                                                    intent.putExtra("data", new Gson().toJson(locationData));
                                                    m_activity.startActivity(intent);
                                                }
                                            });
                                            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {
                                                    dialogInterface.dismiss();
                                                }
                                            });
                                            builder.show();
                                            //Toast.makeText(m_activity, geoCoordinate.toString(), Toast.LENGTH_SHORT).show();
                                            return false;
                                        }

                                        @Override
                                        public void onLongPressRelease() {

                                        }

                                        @Override
                                        public boolean onTwoFingerTapEvent(PointF pointF) {
                                            return false;
                                        }
                                    }, 0, false);
                        } else {
                            new AlertDialog.Builder(m_activity).setMessage(
                                    "Error : " + error.name() + "\n\n" + error.getDetails())
                                    .setTitle("Map Engine Failure")
                                    .setNegativeButton(android.R.string.cancel,
                                            new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(
                                                        DialogInterface dialog,
                                                        int which) {
                                                    m_activity.finish();
                                                }
                                            }).create().show();
                        }
                    }
                });
            }
        }
    }

    private MapLabeledMarker m_positionIndicatorFixed = null;

    /**
     * This utility function is intended to show message for gestures callback
     */
    private void showMsg(String msg) {
        final Toast msgToast = Toast.makeText(m_activity, msg, Toast.LENGTH_SHORT);

        //msgToast.show();

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                msgToast.cancel();
            }
        }, 1000);

    }
}
