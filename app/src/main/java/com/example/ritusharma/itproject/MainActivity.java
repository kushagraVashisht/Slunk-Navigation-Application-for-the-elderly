package com.example.ritusharma.itproject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ritusharma.itproject.Auth_SignIn.CaringStatus;
import com.example.ritusharma.itproject.Auth_SignIn.FriendsListActivity;
import com.example.ritusharma.itproject.Auth_SignIn.EditUser;
import com.example.ritusharma.itproject.Auth_SignIn.PhoneAuthActivity;
import com.example.ritusharma.itproject.Auth_SignIn.UserObj;
import com.example.ritusharma.itproject.Calendar.CalendarLayout;
import com.example.ritusharma.itproject.SinchModules.*;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mapbox.android.core.location.LocationEngine;
import com.mapbox.android.core.location.LocationEngineListener;
import com.mapbox.android.core.location.LocationEnginePriority;
import com.mapbox.android.core.location.LocationEngineProvider;
import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.api.directions.v5.models.DirectionsResponse;
import com.mapbox.api.directions.v5.models.DirectionsRoute;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.plugins.locationlayer.LocationLayerPlugin;
import com.mapbox.mapboxsdk.plugins.locationlayer.modes.CameraMode;
import com.mapbox.mapboxsdk.plugins.locationlayer.modes.RenderMode;
import com.mapbox.services.android.navigation.ui.v5.NavigationLauncher;
import com.mapbox.services.android.navigation.ui.v5.NavigationLauncherOptions;
import com.mapbox.services.android.navigation.ui.v5.route.NavigationMapRoute;
import com.mapbox.services.android.navigation.v5.navigation.NavigationRoute;
import com.sinch.android.rtc.SinchError;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends BaseActivity
        implements OnMapReadyCallback, LocationEngineListener, PermissionsListener, MapboxMap.OnMapClickListener,
        GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks,
        SinchService.StartFailedListener, NavigationView.OnNavigationItemSelectedListener {

    /* Firebase Objs */
    private DatabaseReference firDB_users;
    private FirebaseUser user;
    private UserObj currUser;

    /* Navigation Objects */
    private static final String ACTIVITY_TAG = "MainActivity";
    private GeoDataClient mGeoDataClient;
    private MapView mapView;
    private MapboxMap map;
    private PermissionsManager permissionsManager;
    private LocationEngine locationEngine;
    private LocationLayerPlugin locationLayerPlugin;
    private Location originLocation;
    private Point originPosition;
    private Point destinationPosition;
    private DirectionsRoute currentRoute;
    private Marker destinationMarker;
    private com.mapbox.mapboxsdk.geometry.LatLng originCoord;
    private com.mapbox.mapboxsdk.geometry.LatLng destinationCoord;
    private NavigationMapRoute navigationMapRoute;
    private Button startButton;
    private static final int GOOGLE_API_CLIENT_ID = 0;
    private AutoCompleteTextView mAutocompleteTextView;
    private TextView mNameView;
    private GoogleApiClient mGoogleApiClient;
    private PlaceArrayAdapter mPlaceArrayAdapter;
    private static final com.google.android.gms.maps.model.LatLng BOUNDS_MOUNTAIN_VIEW_LAT = new com.google.android.gms.maps.model.LatLng(
            37.398160, -122.180831);
    private static final com.google.android.gms.maps.model.LatLng BOUNDS_MOUNTAIN_VIEW_LONG = new com.google.android.gms.maps.model.LatLng(
            37.430610, -121.972090);
    private static final LatLngBounds BOUNDS_MOUNTAIN_VIEW = new LatLngBounds(BOUNDS_MOUNTAIN_VIEW_LAT,
            BOUNDS_MOUNTAIN_VIEW_LONG);
    private com.google.android.gms.maps.model.LatLng PlaceIDString;
    private Double PlaceIDStringLatitude;
    private Double PlaceIDStringLongitude;
    private String Latitude;
    private String Longitude;
    private Double DestinationLat;
    private Double DestinationLong;
    private LatLng FinalDestination;
    private String NameOfTheLocation;

    /*********************************************************************************************/

    /**
     * On creation of the activity:
     * * Set up the User Objects (Opening login if needed)
     * * Start the location activity
     * * Set up the Drawer actions
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Mapbox.getInstance(this, "YOUR MAPBOX MAPS ANDROID API KEY HERE");
        setContentView(R.layout.activity_main);
        mapView = (MapView)findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        firDB_users = FirebaseDatabase.getInstance().getReference(getString(R.string.User_Tag));

        /* Get user session, login if required */
        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            OpenMainLogin();
        } else {
            getUserObj();
        }

        /* Start Location Services */
        startActivity(new Intent(MainActivity.this, LocationActivity.class));
        
        /* Set up the Drawer navigator */
        Toolbar toolbarMain = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbarMain);

        /* The drawer get's triggered */
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbarMain,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        /* The navigation view get's triggered */
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    /*********************************************************************************************/

    /**
     * Assigns the currUser object to the currently authenticated FireBase user
     * by filtering the UID.
     */
    private void getUserObj() {
        /* We get the data once, then abandon listening until re-called */
        firDB_users.addListenerForSingleValueEvent(new ValueEventListener() {
            /**
             * Grabs all the data and sets currUser to be the user with the
             * matching Uid.
             * @param usersSnapshot - The snapshot of all users in the DB
             */
            @Override
            public void onDataChange(DataSnapshot usersSnapshot) {
                /* Look through all of the Users */
                for (DataSnapshot u_snap : usersSnapshot.getChildren()) {
                    /* Assigns the currUser Object when we find it */
                    if (u_snap.child(getString(R.string.usr_uid)).getValue(String.class).equals(user.getUid())) {
                        currUser = u_snap.getValue(UserObj.class);
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Do Nothing
            }
        });
    }

    /*********************************************************************************************/

    /**
     * If the Navi Drawer is open when the back button is pressed, close it.
     * Otherwise, just go back to the previous screen if possible.
     */
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    /*********************************************************************************************/

    /**
     * What happens when a NaviDrawer Item is clicked?
     * @param item - The selected item
     * @return - Always True
     */
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        /* What to open, selection dependant */
        switch (id) {
            case R.id.nav_home:
                OpenHomeScreen();
                break;
            case R.id.nav_calendar:
                OpenCalendar();
                break;
            case R.id.nav_friends:
                OpenFriendsList();
                break;
            case R.id.nav_mydetails:
                OpenEditUser();
                break;
            case R.id.nav_logout:
                Logout();
                break;
            default:
                /* Do Nothing */
                break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /*********************************************************************************************/

    /**
     * Opens the MainActivity
     */
    private void OpenHomeScreen() {
        // startActivity(new Intent(this, MainActivity.class));
    }

    /*********************************************************************************************/

    /**
     * App opens for the first time and displays a map
     * @param mapboxMap - the map
     */
    @Override
    public void onMapReady(MapboxMap mapboxMap) {

        this.map = mapboxMap;
        enableLocationPlugin();

        /* Getting the coordinates of the origin location(current position of the user) */
        originCoord = new LatLng(originLocation.getLatitude(), originLocation.getLongitude());

        map.addOnMapClickListener(this);

        /* Adding the on-click listener to the start button which in turn activates the navigation */
        startButton = findViewById(R.id.startButton);
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavigationLauncherOptions options = NavigationLauncherOptions.builder()
                        .directionsRoute(currentRoute)
                        .shouldSimulateRoute(false)
                        .build();
                NavigationLauncher.startNavigation(MainActivity.this, options);
            }
        });

        /* Adding the auto-complete search-bar to the application */
        mAutocompleteTextView = (AutoCompleteTextView) findViewById(R.id.autoCompleteTextView);
        mAutocompleteTextView.setThreshold(3);
        mNameView = (TextView) findViewById(R.id.name);
        mGoogleApiClient = new GoogleApiClient.Builder(MainActivity.this).addApi(Places.GEO_DATA_API)
                .enableAutoManage(this, GOOGLE_API_CLIENT_ID, this)
                .addConnectionCallbacks(this)
                .build();

        mAutocompleteTextView.setOnItemClickListener(mAutocompleteClickListener);
        mPlaceArrayAdapter = new PlaceArrayAdapter(this, android.R.layout.simple_list_item_1, BOUNDS_MOUNTAIN_VIEW,
                null);
        mAutocompleteTextView.setAdapter(mPlaceArrayAdapter);
    }

    /*********************************************************************************************/

    /**
     * Choosing one of the locations from the drop down menu
     */
    private AdapterView.OnItemClickListener mAutocompleteClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            final PlaceArrayAdapter.PlaceAutocomplete item = mPlaceArrayAdapter.getItem(position);
            final String placeId = String.valueOf(item.placeId);

            Log.i(ACTIVITY_TAG, "Selected: " + item.description);
            PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi.getPlaceById(mGoogleApiClient, placeId);
            placeResult.setResultCallback(mUpdatePlaceDetailsCallback);
            Log.i(ACTIVITY_TAG, "");
            Log.i(ACTIVITY_TAG, "Fetching details for ID: " + item.placeId);

            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

            Places.GeoDataApi.getPlaceById(mGoogleApiClient, placeId)
                    .setResultCallback(new ResultCallback<PlaceBuffer>() {
                        @Override
                        public void onResult(PlaceBuffer p) {
                            if (p.getStatus().isSuccess()) {
                                final Place mPlace = p.get(0);
                                PlaceIDString = mPlace.getLatLng();

                                /* Hide Keyboard: */
                                InputMethodManager imm = (InputMethodManager) getSystemService(
                                        Activity.INPUT_METHOD_SERVICE);
                                imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);

                                /* Extracting latitude and longitude from the place-id string from
                                 * the google search result
                                 */
                                PlaceIDStringLatitude = PlaceIDString.latitude;
                                PlaceIDStringLongitude = PlaceIDString.longitude;

                                Latitude = PlaceIDStringLatitude.toString();
                                Longitude = PlaceIDStringLongitude.toString();

                                DestinationLat = Double.parseDouble(Latitude);
                                DestinationLong = Double.parseDouble(Longitude);

                                FinalDestination = new LatLng(DestinationLat, DestinationLong);

                                /* Log activities to check the latitude and longitude of the location */
                                Log.i(ACTIVITY_TAG, "Destination lat is : " + DestinationLat);
                                Log.i(ACTIVITY_TAG, "Destination lat is : " + DestinationLong);

                                Log.i(ACTIVITY_TAG, "LatLng is: " + PlaceIDString);

                                Log.i(ACTIVITY_TAG, "FinalDestination lat is : " + FinalDestination.getLatitude());
                                Log.i(ACTIVITY_TAG, "FinalDestination lat is : " + FinalDestination.getLongitude());

                                /* Adding marker to the map and simulating a route to the location */
                                destinationMarker = map.addMarker(new MarkerOptions()
                                        .position(FinalDestination)
                                        .title("Destination")
                                        .snippet("Destination position"));
                                mapView.getMapAsync(new OnMapReadyCallback() {
                                    @Override
                                    public void onMapReady(MapboxMap mapboxMap) {
                                        mapboxMap.setCameraPosition(new CameraPosition.Builder()
                                                .target(FinalDestination)
                                                .zoom(15.0)
                                                .build());
                                    }
                                });
                                destinationPosition = Point.fromLngLat(FinalDestination.getLongitude(),
                                        FinalDestination.getLatitude());
                                originPosition = Point.fromLngLat(originCoord.getLongitude(),
                                        originCoord.getLatitude());
                                getRoute(originPosition, destinationPosition);
                                startButton.setEnabled(true);
                                startButton.setBackgroundResource(R.color.mapboxBlue);

                            }
                            p.release();
                        }
                    });
        }
    };

    /*********************************************************************************************/

    /**
     *  Validating the location selected
     */
    private ResultCallback<PlaceBuffer> mUpdatePlaceDetailsCallback = new ResultCallback<PlaceBuffer>() {
        @Override
        public void onResult(PlaceBuffer places) {
            if (!places.getStatus().isSuccess()) {
                Log.e(ACTIVITY_TAG, "Place query did not complete. Error: " + places.getStatus().toString());
                return;
            }
            // Selecting the first object buffer.
            final Place place = places.get(0);
            CharSequence attributions = places.getAttributions();

            mNameView.setText(Html.fromHtml(place.getAddress() + ""));
        }
    };

    /*********************************************************************************************/

    /**
     * Function requesting appropriate permissions from the user
     */
    private void enableLocationPlugin() {
        if (PermissionsManager.areLocationPermissionsGranted(this)) {
            initializeLocationEngine();
            locationLayerPlugin = new LocationLayerPlugin(mapView, map);
            locationLayerPlugin.setLocationLayerEnabled(true);
            locationLayerPlugin.setCameraMode(CameraMode.TRACKING);
            getLifecycle().addObserver(locationLayerPlugin);
            locationLayerPlugin.setRenderMode(RenderMode.NORMAL);
        } else {
            permissionsManager = new PermissionsManager(this);
            permissionsManager.requestLocationPermissions(this);
        }
    }

    /*********************************************************************************************/

    /**
     * Requests location Updates
     */
    @SuppressLint("MissingPermission")
    @Override
    public void onConnected() {
        locationEngine.requestLocationUpdates();
    }

    /*********************************************************************************************/

    /**
     * Takes care of the origin location and changes the camera position whenever
     * the location is changed
     * 
     * @param location - The current location
     */
    @Override
    public void onLocationChanged(Location location) {

    }

    /*********************************************************************************************/

    /**
     * Gives a toast or dialog explaining why location services are required if user
     * denies permission
     * 
     * @param permissionsToExplain - Explanation why location services are required
     */
    @Override
    public void onExplanationNeeded(List<String> permissionsToExplain) {
        Toast.makeText(MainActivity.this,
                "We need access to your current location to enable navigation services. We're not Facebook!(promise)",
                Toast.LENGTH_LONG).show();
    }

    /*********************************************************************************************/

    /**
     * Permission granted
     * 
     * @param granted - just a boolean variable keeping track whether the permission
     *                has been granted or not
     */
    @Override
    public void onPermissionResult(boolean granted) {
        if (granted) {
            enableLocationPlugin();
        } else {
//             Toast.makeText(this, R.string.user_location_permission_not_granted,
//             Toast.LENGTH_LONG).show();
             finish();
        }
    }

    /*********************************************************************************************/

    /**
     * Takes care of all the permissions
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
            @NonNull int[] grantResults) {
        permissionsManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    /*********************************************************************************************/

    /**
     * Initializing the location Engine. Sets the origin location to the last
     * location(if any). If there's no last Location, then simply adds a
     * locationEngine listener and zooms in the camera at the new location
     */
    @SuppressLint("MissingPermission")
    private void initializeLocationEngine() {
        LocationEngineProvider locationEngineProvider = new LocationEngineProvider(this);
        locationEngine = locationEngineProvider.obtainBestLocationEngineAvailable();
        locationEngine.setPriority(LocationEnginePriority.HIGH_ACCURACY);
        locationEngine.activate();

        @SuppressLint("MissingPermission")
        Location lastLocation = locationEngine.getLastLocation();
        if (lastLocation != null) {
            originLocation = lastLocation;
        } else {
            locationEngine.addLocationEngineListener(this);
        }
    }

    /*********************************************************************************************/

    /**
     * Ensures that the camera zooms in on the new location
     */
    private void setCameraPosition(Location location) {
        map.animateCamera(CameraUpdateFactory
                .newLatLngZoom(new LatLng(originCoord.getLatitude(), originCoord.getLongitude()), 15.0));
    }

    /*********************************************************************************************/

    /**
     * Checks when the map is clicked on and gets route between the two positions
     * 
     * @param point - getting the longitude and latitude of the point where we
     *              clicked on the map
     */
    @Override
    public void onMapClick(@NonNull LatLng point) {
        if (destinationMarker != null) {
            map.removeMarker(destinationMarker);
        }
        FinalDestination = point;
        destinationCoord = point;
        destinationMarker = map.addMarker(new MarkerOptions()
                .position(destinationCoord)
                .title("Destination")
                .snippet("Destination position"));
        destinationPosition = Point.fromLngLat(FinalDestination.getLongitude(), FinalDestination.getLatitude());
        originPosition = Point.fromLngLat(originCoord.getLongitude(), originCoord.getLatitude());
        getRoute(originPosition, destinationPosition);
        startButton.setEnabled(true);
        startButton.setBackgroundResource(R.color.mapboxBlue);
    }

    /*********************************************************************************************/

    /**
     * Gets the route between origin and destination
     * 
     * @param origin      - origin position
     * @param destination - destination position
     */
    private void getRoute(Point origin, Point destination) {
        NavigationRoute.builder(this)
                .accessToken(Mapbox.getAccessToken())
                .origin(origin)
                .destination(destination)
                .build()
                .getRoute(new Callback<DirectionsResponse>() {
                    @Override
                    public void onResponse(Call<DirectionsResponse> call, Response<DirectionsResponse> response) {
                        // You can get the generic HTTP info about the response
                        Log.d(ACTIVITY_TAG, "Response code: " + response.code());
                        if (response.body() == null) {
                            Log.e(ACTIVITY_TAG, "No routes found, make sure you set the right user and access token.");
                            return;
                        } else if (response.body().routes().size() < 1) {
                            Log.e(ACTIVITY_TAG, "No routes found");
                            return;
                        }

                        currentRoute = response.body().routes().get(0);

                        // Draw the route on the map
                        if (navigationMapRoute != null) {
                            navigationMapRoute.removeRoute();
                        } else {
                            navigationMapRoute = new NavigationMapRoute(null, mapView, map, R.style.NavigationMapRoute);
                        }
                        navigationMapRoute.addRoute(currentRoute);
                    }

                    @Override
                    public void onFailure(Call<DirectionsResponse> call, Throwable throwable) {
                        Log.e(ACTIVITY_TAG, "Error: " + throwable.getMessage());
                    }
                });
    }

    /*********************************************************************************************/

    /**
     * Opens the Activity that shows the user's friend list
     */
    public void OpenFriendsList() {
        try {
            /* Puts the user's details into the next activity if it can get them */
            Intent i = new Intent(MainActivity.this, FriendsListActivity.class);

            i.putExtra("User/" + getString(R.string.usr_uid), currUser.getUID());
            i.putExtra("User/" + getString(R.string.usr_fname), currUser.getFname());
            i.putExtra("User/" + getString(R.string.usr_lname), currUser.getLname());
            i.putExtra("User/" + getString(R.string.usr_dispname), currUser.getUsername());
            i.putExtra("User/" + getString(R.string.usr_mobNum), currUser.getMobNum());
            i.putExtra("User/" + getString(R.string.usr_caringstat), currUser.getCaringStat());

            startActivity(i);
        } catch (Exception e) {
            Toast.makeText(this, "Waiting for Firebase, try again.", Toast.LENGTH_LONG).show();
            getUserObj();
        }
    }


    /*********************************************************************************************/

    /**
     * Opens the calendar view
     */
    public void OpenCalendar() {
        startActivity(new Intent(MainActivity.this, CalendarLayout.class));
    }

    /*********************************************************************************************/

    /**
     * Opens the login for the VideoCall module
     */
    public void OpenCallScreen() {
        Intent callScreen = new Intent(this, VideoCallScreenActivity.class);
        callScreen.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(callScreen);
    }

    /*********************************************************************************************/

    /**
     * Opens the user details screen, allowing the user to change their details
     */
    public void OpenEditUser() {
        /* Sends the user details to the new activity */
        Intent i = new Intent(MainActivity.this, EditUser.class);

        i.putExtra("User/" + getString(R.string.usr_uid), currUser.getUID());
        i.putExtra("User/" + getString(R.string.usr_fname), currUser.getFname());
        i.putExtra("User/" + getString(R.string.usr_lname), currUser.getLname());
        i.putExtra("User/" + getString(R.string.usr_dispname), currUser.getUsername());
        i.putExtra("User/" + getString(R.string.usr_mobNum), currUser.getMobNum());
        i.putExtra("User/" + getString(R.string.usr_caringstat), currUser.getCaringStat());

        startActivity(i);
    }

    /*********************************************************************************************/

    /**
     * Opens the main login window for the user
     */
    public void OpenMainLogin() {
        startActivity(new Intent(MainActivity.this, PhoneAuthActivity.class));
    }

    /*********************************************************************************************/

    /**
     * Logs out the user session and opens the sign-in page
     */
    public void Logout() {
        FirebaseAuth.getInstance().signOut();
        sinchLogout();
        OpenMainLogin();
    }

    /*********************************************************************************************/

    /**
     * Logs out Sinch Services
     */
    private void sinchLogout() {
        if (getSinchServiceInterface() != null) {
            getSinchServiceInterface().stopClient();
        }
        finish();
    }

    /*********************************************************************************************/

    /**
     * Activity lifecycle events
     */
    @SuppressLint("MissingPermission")
    @Override
    public void onStart() {
        super.onStart();
        mapView.onStart();
        if (locationLayerPlugin != null) {
            locationLayerPlugin.onStart();
        }
    }

    /*********************************************************************************************/

    /**
     * Activity lifecycle events
     */
    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    /*********************************************************************************************/

    /**
     * Activity lifecycle events
     */
    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    /*********************************************************************************************/

    /**
     * Activity lifecycle events
     */
    @Override
    protected void onStop() {
        super.onStop();
        mapView.onStop();
        if (locationLayerPlugin != null) {
            locationLayerPlugin.onStart();
        }
    }

    /*********************************************************************************************/

    /**
     * Activity lifecycle events
     */
    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    /*********************************************************************************************/

    /**
     * Activity lifecycle events
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    /*********************************************************************************************/

    /**
     * Activity lifecycle events
     */
    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    /*********************************************************************************************/

    /**
     * SINCH methods
     * This method is invoked when the connection is established with the SinchService
     */
    @Override
    protected void onServiceConnected() {
        // mLoginButton.setEnabled(true);
        getSinchServiceInterface().setStartListener(this);
    }

    /*********************************************************************************************/

    /**
     * The method is invoked when the Sinch Service fails to start
     * @param error - The error which occurs
     */
    @Override
    public void onStartFailed(SinchError error) {
        Toast.makeText(this, "Cant Start Sinch Service", Toast.LENGTH_LONG).show();
    }

    /*********************************************************************************************/

    /**
     * The method does nothing actually. It is invoked when the Sinch Service is started succesfully
     */
    @Override
    public void onStarted() {
        // Toast.makeText(this, "Sinch Service Connected Automatically",
        // Toast.LENGTH_LONG).show();
    }

    /*********************************************************************************************/

    /**
     * Login is clicked to manually to connect to the Sinch Service
     */
    private void loginSinch() {
        // this will be the user-id
        String userName = FirebaseAuth.getInstance().getCurrentUser().getUid();

        if (!getSinchServiceInterface().isStarted()) {
            getSinchServiceInterface().startClient(userName);
            showSpinner();
        } else {
            Toast.makeText(this, "Sinch Service Connected", Toast.LENGTH_LONG).show();
        }
    }

    /*********************************************************************************************/

    /**
     * The method shows the loading spinner when the sinch services are being launched.
     */
    private void showSpinner() {

    }

    /*********************************************************************************************/

    /**
     * The method is invoked when the Google Places API is succesfully connected.
     * Used for the google autocomplete-search API
     * @param bundle - Constructs a Bundle containing a copy of the mappings from the given Bundle.
     */
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mPlaceArrayAdapter.setGoogleApiClient(mGoogleApiClient);
        Log.i(ACTIVITY_TAG, "Google Places API connected.");
    }

    /*********************************************************************************************/

    /**
     * This method gets invoked when the app gets disconnected from the google play services
     * (Not necessarily the internet)
     */
    @Override
    public void onConnectionSuspended(int i) {
        mPlaceArrayAdapter.setGoogleApiClient(null);
        Log.e(ACTIVITY_TAG, "Google Places API connection suspended.");
    }

    /*********************************************************************************************/

    /**
     * This method gets invoked when the client fails to connect to the google play services
     * @param connectionResult - Contains all possible error codes for when a client fails to connect to Google Play services
     */
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.e(ACTIVITY_TAG, "Google Places API connection failed with error code: " + connectionResult.getErrorCode());

        Toast.makeText(this, "Google Places API connection failed with error code:" + connectionResult.getErrorCode(),
                Toast.LENGTH_LONG).show();
    }
}

/*********************************************************************************************/
