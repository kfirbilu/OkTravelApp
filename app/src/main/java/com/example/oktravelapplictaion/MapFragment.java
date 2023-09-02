package com.example.oktravelapplictaion;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.oktravelapplictaion.model.Location;
import com.example.oktravelapplictaion.model.Model;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.HashMap;


public class MapFragment extends Fragment {

    float longitude;
    float latitude;
    int ZOOM_LEVEL=5;
    BitmapDescriptor LOCATION_ICON;

    String post_id;
    private GoogleMap mMap;
    MapViewModel viewModel;
    ImageButton liked_posts_btn;
    ImageButton my_posts_btn;
    ImageButton all_posts_btn;
    SharedPreferences sp;

    FusedLocationProviderClient client;
    float myLongitude;
    float myLatitude;
    TextView location_name_et;
    ImageButton search_btn;


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        viewModel=new ViewModelProvider(this).get(MapViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {



        //initialize view
        View view =inflater.inflate(R.layout.fragment_map, container, false);
        liked_posts_btn = view.findViewById(R.id.liked_posts_mapFragment_ibtn);
        my_posts_btn = view.findViewById(R.id.my_posts_mapFragment_ibtn);
        all_posts_btn = view.findViewById(R.id.all_posts_mapFragment_ibtn);
        LOCATION_ICON = BitmapDescriptorFactory.fromResource(R.drawable.pin_32);




        liked_posts_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sp = getActivity().getSharedPreferences("login", Context.MODE_PRIVATE);
                String userName = sp.getString("userName","");
                Model.instance.getLikedLocations(userName, liked_locations -> {
                    if (liked_locations == null || liked_locations.size() == 0){
                        Toast.makeText(getContext(), "Does not exist", Toast.LENGTH_LONG).show();
                        mMap.clear();
                    }
                    else {
                        mMap.clear();
                        viewModel.setDb_locations(liked_locations);
                        for (Location m : viewModel.getDb_locations()) {
                            if(m.isIs_deleted()==false)
                                mMap.addMarker(new MarkerOptions().title(m.getLocationName()).position(new LatLng(m.getLatitude(), m.getLongitude())).snippet(m.getPostId()).visible(true).icon(LOCATION_ICON));
                        }
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(liked_locations.get(0).getLatitude(), liked_locations.get(0).getLongitude()), ZOOM_LEVEL));

                    }
                });
            }
        });

        my_posts_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sp = getActivity().getSharedPreferences("login", Context.MODE_PRIVATE);
                String userName = sp.getString("userName","");
                Model.instance.getMyPostsLocations(userName, my_posts_locations -> {
                    if (my_posts_locations == null || my_posts_locations.size() == 0){
                        Toast.makeText(getContext(), "Does not exist", Toast.LENGTH_LONG).show();
                        mMap.clear();
                    }
                    else {
                        mMap.clear();
                        viewModel.setDb_locations(my_posts_locations);
                        for (Location m : viewModel.getDb_locations()) {
                            if(m.isIs_deleted()==false)
                                mMap.addMarker(new MarkerOptions().title(m.getLocationName()).position(new LatLng(m.getLatitude(), m.getLongitude())).snippet(m.getPostId()).visible(true).icon(LOCATION_ICON));
                        }
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(my_posts_locations.get(0).getLatitude(), my_posts_locations.get(0).getLongitude()), ZOOM_LEVEL));
                    }
                });
            }
        });

        all_posts_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMap.clear();
                Model.instance.getAllLocations(locationsList -> {
                    if (locationsList == null || locationsList.size() == 0){
                        Toast.makeText(getContext(), "Does not exist", Toast.LENGTH_LONG).show();
                        mMap.clear();
                    }
                    else {
                        mMap.clear();
                        viewModel.setDb_locations(locationsList);
                        for (Location m : viewModel.getDb_locations()) {
                            if(m.isIs_deleted()==false)
                                mMap.addMarker(new MarkerOptions().title(m.getLocationName()).position(new LatLng(m.getLatitude(), m.getLongitude())).snippet(m.getPostId()).visible(true).icon(LOCATION_ICON));
                        }
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(locationsList.get(0).getLatitude(), locationsList.get(0).getLongitude()), ZOOM_LEVEL));
                    }
                });
            }
        });

        location_name_et = view.findViewById(R.id.location_et_map_fragment);
        search_btn = view.findViewById(R.id.search_mapFragment_ibtn);
        search_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HashMap<String,String> map=new HashMap<>();
                map.put("postTitle", location_name_et.getText().toString());
                Model.instance.locationSearch(map,locationsList ->{
                    if (locationsList == null || locationsList.size() == 0){
                        Toast.makeText(getContext(), "Does not exist", Toast.LENGTH_LONG).show();
                        mMap.clear();
                    }
                    else {
                        mMap.clear();
                        viewModel.setDb_locations(locationsList);
                        for (Location m : viewModel.getDb_locations()) {
                            if(m.isIs_deleted()==false)
                                mMap.addMarker(new MarkerOptions().title(m.getLocationName()).position(new LatLng(m.getLatitude(), m.getLongitude())).snippet(m.getPostId()).visible(true).icon(LOCATION_ICON));
                        }
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(locationsList.get(0).getLatitude(), locationsList.get(0).getLongitude()), ZOOM_LEVEL));
                    }
                });
            }
        });




        //initialize map fragment
        SupportMapFragment supportMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.google_map);

        //Async map
         supportMapFragment.getMapAsync((new OnMapReadyCallback() {
            @Override
            public void onMapReady(@NonNull GoogleMap googleMap) {
                mMap = googleMap;

                if(!Model.isFromNewPost&&!Model.isFromEditPost&&!Model.isFromFeed) {
                    Model.instance.getAllLocations(locationsList -> {
                        viewModel.setDb_locations(locationsList);
                        for (Location m : viewModel.getDb_locations()) {
                            if(m.isIs_deleted()==false)
                                mMap.addMarker(new MarkerOptions().title(m.getLocationName()).position(new LatLng(m.getLatitude(), m.getLongitude())).snippet(m.getPostId()).visible(true).icon(LOCATION_ICON));
                        }
                    });

                    MarkerOptions myLocation = new MarkerOptions();
                    longitude = Util.myLongitude;
                    latitude = Util.myLatitude;
                    myLocation.position(new LatLng(latitude, longitude)).title("My location").icon(LOCATION_ICON);
                    mMap.addMarker(myLocation);
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), ZOOM_LEVEL));

                }

                if(Model.isFromNewPost){
                    MarkerOptions myLocation = new MarkerOptions();
                    longitude = Util.myLongitude;
                    latitude = Util.myLatitude;
                    myLocation.position(new LatLng(latitude, longitude)).title("My location").icon(LOCATION_ICON);
                    mMap.addMarker(myLocation);
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), ZOOM_LEVEL));

                }


                //load location from post from the feed
                if(Model.isFromFeed){
                    Log.d("TAG","from feed");
                    MarkerOptions m = new MarkerOptions();
                    longitude = MapFragmentArgs.fromBundle(getArguments()).getLongitude();
                    latitude = MapFragmentArgs.fromBundle(getArguments()).getLatitude();
                    m.position(new LatLng(latitude, longitude)).icon(LOCATION_ICON);
                    mMap.addMarker(m);
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), ZOOM_LEVEL));

                }

                //load location from post from the edit post
                else if(Model.isFromEditPost){
                    Log.d("TAG","from edit");
                    MarkerOptions m = new MarkerOptions();
                    longitude = MapFragmentArgs.fromBundle(getArguments()).getLongitude();
                    latitude = MapFragmentArgs.fromBundle(getArguments()).getLatitude();
                    m.position(new LatLng(latitude, longitude)).icon(LOCATION_ICON);
                    mMap.addMarker(m);
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), ZOOM_LEVEL));
                }

                mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                    @Override
                    public void onMapClick(@NonNull LatLng latLng) {

                        if(Model.isFromNewPost){

                            MarkerOptions clicked = new MarkerOptions().title(latLng.latitude + " : " + latLng.longitude).position(latLng).snippet("clicked").visible(true).icon(LOCATION_ICON);

                            mMap.addMarker(clicked);

                            // add clicked location to the general locations map located in the Model
                            Model.locationsList.add(clicked);
                            Model.isFromNewPost = false;

                           longitude = (float) clicked.getPosition().longitude;
                           latitude = (float) clicked.getPosition().latitude;

                            Navigation.findNavController(view).navigate(MapFragmentDirections.actionMapFragmentToNewPostFragment2().setLatitude(latitude).setLongitude(longitude));
                        }

                        if(Model.isFromEditPost){

                            post_id = MapFragmentArgs.fromBundle(getArguments()).getPostId();

                            MarkerOptions clicked = new MarkerOptions().title(latLng.latitude + " : " + latLng.longitude).position(latLng).snippet("clicked").visible(true).icon(LOCATION_ICON);

                            mMap.addMarker(clicked);

                            // add clicked location to the general locations map located in the Model
                            Model.locationsList.add(clicked);
                            Model.isFromEditPost = false;

                            longitude = (float) clicked.getPosition().longitude;
                            latitude = (float) clicked.getPosition().latitude;

                            Navigation.findNavController(view).navigate(MapFragmentDirections.actionMapFragmentToFragmentEditPost(post_id).setLatitude(latitude).setLongitude(longitude).setPostId(post_id));
                        }
                    }
                });


                mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(@NonNull Marker marker) {
                        String postId = marker.getSnippet();
                        if(postId!=null) {
                            Model.instance.getPostById(postId, post -> {
                                if(post != null)
                                    Navigation.findNavController(view).navigate(MapFragmentDirections.actionMapFragmentToPostDetailsFragment22(post.getPostID()));
                            });
                        }
                        return false;
                    }
                });


            }


        }));



        return view;
    }


public void checkPermissions(){
    // check condition
    if (ContextCompat.checkSelfPermission(
            getActivity(),
            Manifest.permission
                    .ACCESS_FINE_LOCATION)
            == PackageManager
            .PERMISSION_GRANTED
            && ContextCompat.checkSelfPermission(
            getActivity(),
            Manifest.permission
                    .ACCESS_COARSE_LOCATION)
            == PackageManager
            .PERMISSION_GRANTED) {
        // When permission is granted
        // Call method
        getCurrentLocation();
    }
    else {
        // When permission is not granted
        // Call method
        requestPermissions(
                new String[] {
                        Manifest.permission
                                .ACCESS_FINE_LOCATION,
                        Manifest.permission
                                .ACCESS_COARSE_LOCATION },
                100);
    }
}


    @Override
    public void onRequestPermissionsResult(
            int requestCode, @NonNull String[] permissions,
            @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(
                requestCode, permissions, grantResults);
        // Check condition
        if (requestCode == 100 && (grantResults.length > 0)
                && (grantResults[0] + grantResults[1]
                == PackageManager.PERMISSION_GRANTED)) {
            // When permission are granted
            // Call  method
            getCurrentLocation();
        }
        else {
            // When permission are denied
            // Display toast
            Toast
                    .makeText(getActivity(),
                            "Permission denied",
                            Toast.LENGTH_SHORT)
                    .show();
        }
    }


    @SuppressLint("MissingPermission")
    private void getCurrentLocation()
    {
        // Initialize Location manager
        LocationManager locationManager
                = (LocationManager)getActivity()
                .getSystemService(
                        Context.LOCATION_SERVICE);
        // Check condition
        if (locationManager.isProviderEnabled(
                LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(
                LocationManager.NETWORK_PROVIDER)) {
            // When location service is enabled
            // Get last location
            client.getLastLocation().addOnCompleteListener(
                    new OnCompleteListener<android.location.Location>() {
                        @Override
                        public void onComplete(
                                @NonNull Task<android.location.Location> task)
                        {

                            // Initialize location
                            android.location.Location location
                                    = task.getResult();
                            // Check condition
                            if (location != null) {
                                // When location result is not
                                // null set latitude

                                myLatitude = (float) location.getLatitude();

                                myLongitude = (float) location.getLongitude();

                            }
                            else {
                                // When location result is null
                                // initialize location request
                                LocationRequest locationRequest
                                        = new LocationRequest()
                                        .setPriority(
                                                LocationRequest
                                                        .PRIORITY_HIGH_ACCURACY)
                                        .setInterval(10000)
                                        .setFastestInterval(
                                                1000)
                                        .setNumUpdates(1);

                                // Initialize location call back
                                LocationCallback
                                        locationCallback
                                        = new LocationCallback() {
                                    @Override
                                    public void
                                    onLocationResult(
                                            LocationResult
                                                    locationResult)
                                    {
                                        // Initialize
                                        // location
                                        android.location.Location location1
                                                = locationResult
                                                .getLastLocation();
                                        // Set latitude

                                        myLatitude = (float) location1.getLatitude();

                                        myLongitude = (float) location1.getLongitude();

                                    }
                                };

                                // Request location updates
                                client.requestLocationUpdates(
                                        locationRequest,
                                        locationCallback,
                                        Looper.myLooper());
                            }
                        }
                    });
        }
        else {
            // When location service is not enabled
            // open location setting
            startActivity(
                    new Intent(
                            Settings
                                    .ACTION_LOCATION_SOURCE_SETTINGS)
                            .setFlags(
                                    Intent.FLAG_ACTIVITY_NEW_TASK));
        }
    }


}










