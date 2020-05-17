package com.project.campusproject.utils;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PointF;
import android.os.Handler;
import android.util.Log;
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
import com.here.android.mpa.mapping.MapObject;
import com.here.android.mpa.mapping.MapOverlay;
import com.nokia.maps.MapLabeledMarkerImpl;
import com.project.campusproject.R;
import com.project.campusproject.RaiseIssueActivity;
import com.project.campusproject.data.LocationData;
import com.project.campusproject.data.ReportProblem;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MapFragmentView {
    private AndroidXMapFragment m_mapFragment;
    private AppCompatActivity m_activity;
    private Map m_map;
    List<LocationData> locationList;
    private GeoCoordinate pickedLatLng;
    private MapLabeledMarker m_positionIndicatorFixed;
    private MapLabeledMarker m_positionIndicatorFixedItem;


    boolean isMarkersAdded = false;
    int[] imageArray = { R.mipmap.poi,R.mipmap.food, R.mipmap.block,
            R.mipmap.ground, R.mipmap.learning,R.mipmap.people,R.mipmap.road };



    AppCompatButton clearMap,btnAdd;
    private GPSTracker mGpsTracker;
    public MapFragmentView(AppCompatActivity activity, AppCompatButton clearMap,AppCompatButton btnAdd) {
        m_activity = activity;
        locationList = new ArrayList<>();
        this.clearMap = clearMap;
        this.btnAdd = btnAdd;
        this.mGpsTracker = new GPSTracker(activity);
        initMapFragment();
    }

    public void updateMarkers(List<LocationData> locationData){
        this.locationList = locationData;
        if(m_map!=null){
            if(!isMarkersAdded){
                for(int i = 0;i<locationList.size();i++){
                    addMarker(locationList.get(i));
                }
            }
        }

    }



    public void clickedList(LocationData locationData){
        Double latitude = Double.parseDouble(locationData.getLatitude());
        Double longitude = Double.parseDouble(locationData.getLongitude());
        GeoCoordinate geoCoordinate = new GeoCoordinate(latitude,longitude);
        m_map.setCenter(geoCoordinate,Map.Animation.NONE);
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
                    public void onEngineInitializationCompleted(OnEngineInitListener.Error error) {

                        if (error == Error.NONE) {


                            clearMap.setOnClickListener(view -> {
                                pickedLatLng = null;
                                clearMap.setVisibility(View.GONE);
                                btnAdd.setVisibility(View.GONE);
                                if (m_positionIndicatorFixed != null)
                                    m_map.removeMapObject(m_positionIndicatorFixed);
                                btnAdd.setText("Add New");
                            });

                            btnAdd.setOnClickListener(view -> {
                                if(pickedLatLng!=null){
                                    ReportProblem reportProblem = new ReportProblem();
                                    reportProblem.setLatitude(pickedLatLng.getLatitude());
                                    reportProblem.setLongitude(pickedLatLng.getLongitude());
                                    pickedLatLng = null;
                                    clearMap.setVisibility(View.GONE);
                                    btnAdd.setVisibility(View.GONE);
                                    if (m_positionIndicatorFixed != null)
                                        m_map.removeMapObject(m_positionIndicatorFixed);
                                    Intent intent = new Intent(m_activity, RaiseIssueActivity.class);
                                    intent.putExtra("data",new Gson().toJson(reportProblem));
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
                            GeoCoordinate geoCoordinate;
                            if(mGpsTracker.canGetLocation){
                                geoCoordinate = new GeoCoordinate(mGpsTracker.latitude,mGpsTracker.longitude);
                            }else {
                                geoCoordinate = new GeoCoordinate(17.5947027,78.1230401);
                            }
                            m_map.setCenter(geoCoordinate,Map.Animation.NONE);
                            //

                            /* create an image to mark coordinate when tap event happens */

                            Image icon = new Image();
                            try {
                                icon.setImageResource(R.mipmap.mapsblue);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            m_positionIndicatorFixedItem = new MapLabeledMarker(geoCoordinate,icon);
                            m_positionIndicatorFixedItem.setLabelText("ENG","Current Location");


                            m_positionIndicatorFixedItem.setVisible(true);
                            m_positionIndicatorFixedItem.setCoordinate(geoCoordinate);
                            m_map.addMapObject(m_positionIndicatorFixedItem);
                            //

                            if(locationList.isEmpty()){
                                isMarkersAdded = false;
                            }
                            for(int i = 0;i<locationList.size();i++){
                                addMarker(locationList.get(i));
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
                                            for (ViewObject viewObject : list) {
                                                if (viewObject.getBaseType() == ViewObject.Type.USER_OBJECT) {
                                                    MapObject mapObject = (MapObject) viewObject;

                                                    if (mapObject.getType() == MapObject.Type.LABELED_MARKER) {

                                                        MapLabeledMarker window_marker = ((MapLabeledMarker)mapObject);
                                                        showDialog(window_marker.getTitle(),window_marker.getDescription());
                                                        return false;
                                                    }
                                                }
                                            }
                                            return false;
                                        }

                                        @Override
                                        public boolean onTapEvent(PointF pointF) {
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
                                            pickedLatLng = m_map.pixelToGeo(pointF);

                                            btnAdd.setVisibility(View.VISIBLE);
                                            clearMap.setVisibility(View.VISIBLE);
                                            btnAdd.setText("Report");
                                            pickedLatLng = m_map.pixelToGeo(pointF);
                                            m_map.setCenter(pickedLatLng,Map.Animation.NONE);

                                            /* create an image to mark coordinate when tap event happens */

                                            if (m_positionIndicatorFixed != null)
                                                m_map.removeMapObject(m_positionIndicatorFixed);
                                            Image icon = new Image();
                                            try {
                                                icon.setImageResource(R.mipmap.mapsred);
                                            } catch (IOException e) {
                                                e.printStackTrace();
                                            }

                                            m_positionIndicatorFixed = new MapLabeledMarker(pickedLatLng,icon);
                                            String showData = pickedLatLng.getLatitude()+"\n"+pickedLatLng.getLongitude();
                                            m_positionIndicatorFixed.setVisible(true);
                                            m_positionIndicatorFixed.setLabelText("ENG",showData);
                                            m_map.addMapObject(m_positionIndicatorFixed);

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



    private boolean m_returningToRoadViewMode = false;





    //private MapMarker m_positionIndicatorFixedItem = null;





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

    private void showDialog(String title, String desc) {
        AlertDialog.Builder builder =
                new AlertDialog.Builder(m_activity);
        builder.setTitle(title);
        builder.setMessage(desc);
        builder.setPositiveButton("Close", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.show();
    }

}
