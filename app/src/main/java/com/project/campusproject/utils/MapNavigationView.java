package com.project.campusproject.utils;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.here.android.mpa.common.GeoBoundingBox;
import com.here.android.mpa.common.GeoCoordinate;
import com.here.android.mpa.common.GeoPosition;
import com.here.android.mpa.common.Image;
import com.here.android.mpa.common.OnEngineInitListener;
import com.here.android.mpa.guidance.NavigationManager;
import com.here.android.mpa.mapping.AndroidXMapFragment;
import com.here.android.mpa.mapping.Map;
import com.here.android.mpa.mapping.MapLabeledMarker;
import com.here.android.mpa.mapping.MapMarker;
import com.here.android.mpa.mapping.MapOverlay;
import com.here.android.mpa.mapping.MapRoute;
import com.here.android.mpa.routing.CoreRouter;
import com.here.android.mpa.routing.Route;
import com.here.android.mpa.routing.RouteOptions;
import com.here.android.mpa.routing.RoutePlan;
import com.here.android.mpa.routing.RouteResult;
import com.here.android.mpa.routing.RouteWaypoint;
import com.here.android.mpa.routing.Router;
import com.here.android.mpa.routing.RoutingError;
import com.project.campusproject.R;
import com.project.campusproject.data.LocationData;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.List;

public class MapNavigationView {
    private AndroidXMapFragment m_mapFragment;
        private AppCompatActivity m_activity;
        private Button m_naviControlButton;
        private Map m_map;
        private NavigationManager m_navigationManager;
        private GeoBoundingBox m_geoBoundingBox;
        private Route m_route;
        private boolean m_foregroundServiceStarted;
        private GPSTracker mgps;

        private GeoCoordinate startLocation;
        private GeoCoordinate endLocation;
        LocationData locationData = new LocationData();


        public MapNavigationView(AppCompatActivity activity, LocationData locationData) {
            m_activity = activity;
            mgps = new GPSTracker(m_activity);
            this.locationData = locationData;
            this.endLocation = new GeoCoordinate(Double.parseDouble(locationData.getLatitude()),Double.parseDouble(locationData.getLongitude()));
            initMapFragment();
            initNaviControlButton();
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
                                m_map = m_mapFragment.getMap();

                                createDestMarker(endLocation);
                                if(mgps.canGetLocation){
                                    startLocation = new GeoCoordinate(mgps.latitude, mgps.longitude);
                                }else {
                                    startLocation = new GeoCoordinate(17.2332235,78.2376723);
                                }
                                m_map.setCenter(endLocation,
                                        Map.Animation.NONE);
                                createStartMarker(startLocation);

                                //Put this call in Map.onTransformListener if the animation(Linear/Bow)
                                //is used in setCenter()
                                m_map.setZoomLevel(13.2);
                                /*
                                 * Get the NavigationManager instance.It is responsible for providing voice
                                 * and visual instructions while driving and walking
                                 */
                                m_navigationManager = NavigationManager.getInstance();
                            } else {
                                new AlertDialog.Builder(m_activity).setMessage(
                                        "Error : " + error.name() + "\n\n" + error.getDetails())
                                        .setTitle("Error")
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

    private void createStartMarker(GeoCoordinate geoCoordinate) {
        Image icon = new Image();
        try {
            icon.setImageResource(R.mipmap.mapsblue);
        } catch (IOException e) {
            e.printStackTrace();
        }
        MapLabeledMarker m_positionIndicatorFixed = new MapLabeledMarker(geoCoordinate,icon);
        m_positionIndicatorFixed.setLabelText("ENG","My Location");
        m_positionIndicatorFixed.setTitle("My Location");
        m_positionIndicatorFixed.setDescription("My Location");
        m_positionIndicatorFixed.setVisible(true);
        m_map.addMapObject(m_positionIndicatorFixed);

    }

    private void createDestMarker(GeoCoordinate geoCoordinate) {
        Image icon = new Image();
        try {
            icon.setImageResource(R.mipmap.mapsred);
        } catch (IOException e) {
            e.printStackTrace();
        }
        MapLabeledMarker m_positionIndicatorFixed = new MapLabeledMarker(geoCoordinate,icon);
        m_positionIndicatorFixed.setLabelText("ENG",locationData.getName());
        m_positionIndicatorFixed.setTitle(locationData.getName());
        m_positionIndicatorFixed.setDescription(locationData.getName());
        m_positionIndicatorFixed.setVisible(true);
        m_map.addMapObject(m_positionIndicatorFixed);
    }

    private void createRoute() {
            /* Initialize a CoreRouter */
            CoreRouter coreRouter = new CoreRouter();

            /* Initialize a RoutePlan */
            RoutePlan routePlan = new RoutePlan();

            /*
             * Initialize a RouteOption. HERE Mobile SDK allow users to define their own parameters for the
             * route calculation,including transport modes,route types and route restrictions etc.Please
             * refer to API doc for full list of APIs
             */
            RouteOptions routeOptions = new RouteOptions();
            /* Other transport modes are also available e.g Pedestrian */
            routeOptions.setTransportMode(RouteOptions.TransportMode.CAR);
            /* Disable highway in this route. */
            routeOptions.setHighwaysAllowed(false);
            /* Calculate the shortest route available. */
            routeOptions.setRouteType(RouteOptions.Type.SHORTEST);
            /* Calculate 1 route. */
            routeOptions.setRouteCount(1);
            /* Finally set the route option */
            routePlan.setRouteOptions(routeOptions);

            /* Define waypoints for the route */
            /* START: 4350 Still Creek Dr */
            RouteWaypoint startPoint = new RouteWaypoint(startLocation);
            /* END: Langley BC */
            RouteWaypoint destination = new RouteWaypoint(endLocation);

            /* Add both waypoints to the route plan */
            routePlan.addWaypoint(startPoint);
            routePlan.addWaypoint(destination);

            /* Trigger the route calculation,results will be called back via the listener */
            coreRouter.calculateRoute(routePlan,
                    new Router.Listener<List<RouteResult>, RoutingError>() {

                        @Override
                        public void onProgress(int i) {
                            /* The calculation progress can be retrieved in this callback. */
                        }

                        @Override
                        public void onCalculateRouteFinished(List<RouteResult> routeResults,
                                                             RoutingError routingError) {
                            /* Calculation is done.Let's handle the result */
                            if (routingError == RoutingError.NONE) {
                                if (routeResults.get(0).getRoute() != null) {

                                    m_route = routeResults.get(0).getRoute();
                                    /* Create a MapRoute so that it can be placed on the map */
                                    MapRoute mapRoute = new MapRoute(routeResults.get(0).getRoute());

                                    /* Show the maneuver number on top of the route */
                                    mapRoute.setManeuverNumberVisible(true);

                                    /* Add the MapRoute to the map */
                                    m_map.addMapObject(mapRoute);

                                    /*
                                     * We may also want to make sure the map view is orientated properly
                                     * so the entire route can be easily seen.
                                     */
                                    m_geoBoundingBox = routeResults.get(0).getRoute().getBoundingBox();
                                    m_map.zoomTo(m_geoBoundingBox, Map.Animation.NONE,
                                            Map.MOVE_PRESERVE_ORIENTATION);

                                    startNavigation();
                                } else {
                                    Toast.makeText(m_activity,
                                            "Error:route results returned is not valid",
                                            Toast.LENGTH_LONG).show();
                                }
                            } else {
                                Toast.makeText(m_activity,
                                        "Error:route calculation returned error code: " + routingError,
                                        Toast.LENGTH_LONG).show();

                            }
                        }
                    });
        }

        private void initNaviControlButton() {
            m_naviControlButton = (Button) m_activity.findViewById(R.id.naviCtrlButton);
            m_naviControlButton.setText("Start Navigation");
            m_naviControlButton.setOnClickListener(new View.OnClickListener() {
                @Override

                public void onClick(View v) {
                    /*
                     * To start a turn-by-turn navigation, a concrete route object is required.We use
                     * the same steps from Routing sample app to create a route from 4350 Still Creek Dr
                     * to Langley BC without going on HWY.
                     *
                     * The route calculation requires local map data.Unless there is pre-downloaded map
                     * data on device by utilizing MapLoader APIs,it's not recommended to trigger the
                     * route calculation immediately after the MapEngine is initialized.The
                     * INSUFFICIENT_MAP_DATA error code may be returned by CoreRouter in this case.
                     *
                     */
                    if (m_route == null) {
                        createRoute();
                    } else {
                        m_navigationManager.stop();
                        /*
                         * Restore the map orientation to show entire route on screen
                         */
                        m_map.zoomTo(m_geoBoundingBox, Map.Animation.NONE, 0f);
                        m_naviControlButton.setText("Start Navigation");
                        m_route = null;
                    }
                }
            });
        }

        /*
         * Android 8.0 (API level 26) limits how frequently background apps can retrieve the user's
         * current location. Apps can receive location updates only a few times each hour.
         * See href="https://developer.android.com/about/versions/oreo/background-location-limits.html
         * In order to retrieve location updates more frequently start a foreground service.
         * See https://developer.android.com/guide/components/services.html#Foreground
         */
        private void startForegroundService() {
            if (!m_foregroundServiceStarted) {
                m_foregroundServiceStarted = true;
                Intent startIntent = new Intent(m_activity, ForegroundService.class);
                startIntent.setAction(ForegroundService.START_ACTION);
                m_activity.getApplicationContext().startService(startIntent);
            }
        }

        private void stopForegroundService() {
            if (m_foregroundServiceStarted) {
                m_foregroundServiceStarted = false;
                Intent stopIntent = new Intent(m_activity, ForegroundService.class);
                stopIntent.setAction(ForegroundService.STOP_ACTION);
                m_activity.getApplicationContext().startService(stopIntent);
            }
        }

        private void startNavigation() {
            m_naviControlButton.setText("Stop Navigation");
            /* Configure Navigation manager to launch navigation on current map */
            m_navigationManager.setMap(m_map);
            m_navigationManager.startNavigation(m_route);
            m_map.setTilt(60);
            startForegroundService();
            m_navigationManager.setMapUpdateMode(NavigationManager.MapUpdateMode.ROADVIEW);

            /*
             * NavigationManager contains a number of listeners which we can use to monitor the
             * navigation status and getting relevant instructions.In this example, we will add 2
             * listeners for demo purpose,please refer to HERE Android SDK API documentation for details
             */
            addNavigationListeners();
        }

        private void addNavigationListeners() {

            /*
             * Register a NavigationManagerEventListener to monitor the status change on
             * NavigationManager
             */
            m_navigationManager.addNavigationManagerEventListener(
                    new WeakReference<NavigationManager.NavigationManagerEventListener>(
                            m_navigationManagerEventListener));

            /* Register a PositionListener to monitor the position updates */
            m_navigationManager.addPositionListener(
                    new WeakReference<NavigationManager.PositionListener>(m_positionListener));
        }

        private NavigationManager.PositionListener m_positionListener = new NavigationManager.PositionListener() {
            @Override
            public void onPositionUpdated(GeoPosition geoPosition) {
                /* Current position information can be retrieved in this callback */
            }
        };

        private NavigationManager.NavigationManagerEventListener m_navigationManagerEventListener = new NavigationManager.NavigationManagerEventListener() {
            @Override
            public void onRunningStateChanged() {
                //Toast.makeText(m_activity, "Running state changed", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNavigationModeChanged() {
               // Toast.makeText(m_activity, "Navigation mode changed", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onEnded(NavigationManager.NavigationMode navigationMode) {
                //Toast.makeText(m_activity, navigationMode + " was ended", Toast.LENGTH_SHORT).show();
                stopForegroundService();
            }

            @Override
            public void onMapUpdateModeChanged(NavigationManager.MapUpdateMode mapUpdateMode) {
               // Toast.makeText(m_activity, "Map update mode is changed to " + mapUpdateMode, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onRouteUpdated(Route route) {
                Toast.makeText(m_activity, "Route updated", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCountryInfo(String s, String s1) {
                Toast.makeText(m_activity, "Country info updated from " + s + " to " + s1, Toast.LENGTH_SHORT).show();
            }
        };

        public void onDestroy() {
            /* Stop the navigation when app is destroyed */
            if (m_navigationManager != null) {
                stopForegroundService();
                m_navigationManager.stop();
            }
        }
}