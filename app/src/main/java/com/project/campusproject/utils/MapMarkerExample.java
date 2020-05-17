package com.project.campusproject.utils;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

/*import com.google.gson.Gson;
import com.here.sdk.core.Anchor2D;
import com.here.sdk.core.GeoCoordinates;
import com.here.sdk.core.Metadata;
import com.here.sdk.core.Point2D;
import com.here.sdk.gestures.TapListener;
import com.here.sdk.mapview.MapCamera;
import com.here.sdk.mapview.MapImage;
import com.here.sdk.mapview.MapImageFactory;
import com.here.sdk.mapview.MapMarker;
import com.here.sdk.mapview.MapView;
import com.here.sdk.mapview.MapViewBase;
import com.here.sdk.mapview.PickMapItemsResult;
import com.project.campusproject.R;
import com.project.campusproject.RaiseIssueActivity;
import com.project.campusproject.ShowLocationActivity;
import com.project.campusproject.data.LocationData;

import java.util.ArrayList;
import java.util.List;*/

public class MapMarkerExample {
    /*private Context context;
    private MapView mapView;
    private final List<MapMarker> mapMarkerList = new ArrayList<>();
    private List<LocationData> locationDataList = new ArrayList<>();
    private GPSTracker mgps;


    public MapMarkerExample(Context context, MapView mapView, List<LocationData> locationData) {
        this.context = context;
        this.mapView = mapView;
        this.locationDataList = locationData;
        mgps = new GPSTracker(context);

        if(mgps.canGetLocation){
            Log.e("DataPointLatitude",String.valueOf(mgps.latitude));
            Log.e("DataPointLongitude",String.valueOf(mgps.longitude));

            GeoCoordinates geoCoordinates = new GeoCoordinates(mgps.latitude,mgps.longitude);
            addMyLocation(geoCoordinates,"My Location");
            MapCamera camera = mapView.getCamera();
            double distanceInMeters = 1000 * 10;
            camera.lookAt(new GeoCoordinates(mgps.latitude, mgps.longitude), distanceInMeters);

        }else {
            MapCamera camera = mapView.getCamera();
            double distanceInMeters = 1000 * 10;
            camera.lookAt(new GeoCoordinates(10.103209, 76.323783), distanceInMeters);
        }

        *//*MapCamera camera = mapView.getCamera();
        double distanceInMeters = 1000 * 10;
        camera.lookAt(new GeoCoordinates(10.103209, 76.323783), distanceInMeters);*//*

        // Setting a tap handler to pick markers from map
        setTapGestureHandler();

        Toast.makeText(context,"You can tap markers.", Toast.LENGTH_LONG).show();
    }

    public void showAnchoredMapMarkers() {
        for(LocationData locationData : locationDataList){
            Double latitude = Double.parseDouble(locationData.getLatitude());
            Double longitude = Double.parseDouble(locationData.getLongitude());
            GeoCoordinates geoCoordinates = new GeoCoordinates(latitude,longitude);
            addPOIMapMarker(geoCoordinates,locationData.getName(),locationData.getAddress(),new Gson().toJson(locationData));
        }
    }


    public void clearMap() {
        for (MapMarker mapMarker : mapMarkerList) {
            mapView.getMapScene().removeMapMarker(mapMarker);
        }
        mapMarkerList.clear();
    }

    private void setTapGestureHandler() {
        mapView.getGestures().setTapListener(new TapListener() {
            @Override
            public void onTap(Point2D touchPoint) {
                pickMapMarker(touchPoint);
            }
        });
    }

    private void pickMapMarker(final Point2D touchPoint) {
        float radiusInPixel = 2;
        mapView.pickMapItems(touchPoint, radiusInPixel, new MapViewBase.PickMapItemsCallback() {
            @Override
            public void onPickMapItems(@Nullable PickMapItemsResult pickMapItemsResult) {
                if (pickMapItemsResult == null) {
                    return;
                }

                List<MapMarker> mapMarkerList = pickMapItemsResult.getMarkers();
                if (mapMarkerList.size() == 0) {
                    return;
                }
                MapMarker topmostMapMarker = mapMarkerList.get(0);

                Metadata metadata = topmostMapMarker.getMetadata();
                if (metadata != null) {
                    String message = "No Title found.";
                    String address = "";
                    String data = "";
                    String string = metadata.getString("key_poi");
                    String stringAdd = metadata.getString("key_address");
                    String stringdata = metadata.getString("key_data");
                    if (string != null) {
                        message = string;
                    }if (stringAdd != null) {
                        address = stringAdd;
                    }if (stringdata != null) {
                        data = stringdata;
                    }
                    showDialog(message,address,data);
                    return;
                }

                showDialog("Location: " +
                        topmostMapMarker.getCoordinates().latitude + ", " +
                        topmostMapMarker.getCoordinates().longitude,"","" );
            }
        });
    }

    private void addPOIMapMarker(GeoCoordinates geoCoordinates,String locationName,String locationAddress,String locationData) {
        MapImage mapImage = MapImageFactory.fromResource(context.getResources(), R.mipmap.poi);

        // The bottom, middle position should point to the location.
        // By default, the anchor point is set to 0.5, 0.5.
        Anchor2D anchor2D = new Anchor2D(0.5F, 1);
        MapMarker mapMarker = new MapMarker(geoCoordinates, mapImage, anchor2D);

        Metadata metadata = new Metadata();
        metadata.setString("key_poi", locationName);
        metadata.setString("key_address", locationAddress);
        metadata.setString("key_data", locationData);
        mapMarker.setMetadata(metadata);

        mapView.getMapScene().addMapMarker(mapMarker);
        mapMarkerList.add(mapMarker);
    }

    private void addMyLocation(GeoCoordinates geoCoordinates,String myLocation) {
        Log.e("MyLocation","LocationData");
        MapImage mapImage = MapImageFactory.fromResource(context.getResources(), R.mipmap.circle);

        // The bottom, middle position should point to the location.
        // By default, the anchor point is set to 0.5, 0.5.
        Anchor2D anchor2D = new Anchor2D(0.5F, 1);
        MapMarker mapMarker = new MapMarker(geoCoordinates, mapImage, anchor2D);

        Metadata metadata = new Metadata();
        metadata.setString("key_poi", myLocation);
        mapMarker.setMetadata(metadata);

        mapView.getMapScene().addMapMarker(mapMarker);
        mapMarkerList.add(mapMarker);
    }


    private void showDialog(String title, String message,String data) {
        AlertDialog.Builder builder =
                new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setMessage(message);
        if(!data.isEmpty()){
            builder.setPositiveButton("Raise Issue", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Intent intent = new Intent(context, RaiseIssueActivity.class);
                    intent.putExtra("data",data);
                    context.startActivity(intent);
                }
            });
        }
        builder.show();
    }*/
}
