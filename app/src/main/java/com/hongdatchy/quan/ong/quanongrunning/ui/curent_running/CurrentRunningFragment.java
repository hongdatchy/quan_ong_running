package com.hongdatchy.quan.ong.quanongrunning.ui.curent_running;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.hongdatchy.quan.ong.quanongrunning.Common;
import com.hongdatchy.quan.ong.quanongrunning.LocalStorage;
import com.hongdatchy.quan.ong.quanongrunning.R;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Objects;

import static com.google.android.gms.maps.GoogleMap.MAP_TYPE_HYBRID;


public class CurrentRunningFragment extends Fragment implements OnMapReadyCallback {
    private final static int REQUEST_CODE = 101;
    private final static int RequestCheck =102;
    private GoogleMap googleMap;
    ArrayList<LatLng> latLngList = new ArrayList<>();

    FusedLocationProviderClient fusedLocationProviderClient;
    LocationRequest locationRequest;

    FloatingActionButton createRunning, endRunning;
    Polyline line;

    LocationCallback locationCallback;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_current_running, container, false);
        SupportMapFragment supportMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.google_map);
        assert supportMapFragment != null;
        supportMapFragment.getMapAsync(this);
        createRunning = view.findViewById(R.id.fab);
        endRunning = view.findViewById(R.id.endRunning);

        return view;
    }

    @Override
    public void onMapReady(@NonNull GoogleMap map) {
        googleMap = map;
        googleMap.setMapType(MAP_TYPE_HYBRID);
        endRunning.setVisibility(View.GONE);
        switch (Common.status) {
            case "review_running":
                LocalStorage db = new LocalStorage();
                db.load(requireContext());
                latLngList = db.listOLists.get(Common.indexProductClicked);
                drawPolyline();
                addMaker(latLngList.get(0), "Origin");
                addMaker(latLngList.get(latLngList.size() - 1), "Destination");
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLngList.get(0), 16));
                break;
            case "default":
                LatLng HungYen = new LatLng(20.678920, 106.104850);
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(HungYen, 16));
                break;
            case "running":
                latLngList = Common.latLngList;
                handelCreateRunning(true);
                System.out.println("latLngList" + latLngList);
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLngList.get(latLngList.size() - 1), 16));
                addMaker(latLngList.get(0), "Origin");
                break;
        }
        createRunning.setOnClickListener(v -> {
            handelCreateRunning(false);
        });



        endRunning.setOnClickListener(v -> {

            if(fusedLocationProviderClient != null){
                Task<Void> voidTask = fusedLocationProviderClient.removeLocationUpdates(locationCallback);
                System.out.println(voidTask);// must have this line to remove_location_update working !!!???

                LocalStorage db = new LocalStorage();
                db.update(latLngList, getContext());
                LatLng lastOfList = latLngList.get(latLngList.size()-1);
                addMaker(lastOfList, "Destination");
                endRunning.setVisibility(View.GONE);
            }
//            Common.status = "end_running";
        });
    }

    void handelCreateRunning(boolean isGoBackRunning){
        if (ActivityCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            executionRunning(isGoBackRunning);
            googleMap.setMyLocationEnabled(true);
            googleMap.getUiSettings().setMyLocationButtonEnabled(false);
        }
        else {
            ActivityCompat.requestPermissions(requireActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_CODE);
        }
    }

    @SuppressLint("MissingPermission")
    private void executionRunning(boolean isGoBackRunning) {
        endRunning.setVisibility(View.GONE);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireActivity());
        LocationManager locationManager = (LocationManager) requireActivity().getSystemService(Context.LOCATION_SERVICE);
        if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)){
            locationRequest = LocationRequest.create();
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            locationRequest.setInterval(10000);
            locationRequest.setFastestInterval(1000);

            if(!isGoBackRunning){
                latLngList.clear();
            }

            locationCallback = new LocationCallback(){
                @Override
                public void onLocationResult(@NotNull LocationResult locationResult) {
                    Common.status = "running";
                    Location location =  locationResult.getLastLocation();

                    if(latLngList.size()== 0){
                        googleMap.clear();
                        latLngList.add(new LatLng(location.getLatitude(), location.getLongitude()));
                        addMaker(new LatLng(location.getLatitude(), location.getLongitude()), "Origin");
                        moveCamera(new LatLng(location.getLatitude(), location.getLongitude()));
                    }else {
                        Location lastLocation = new Location("");
                        lastLocation.setLatitude(latLngList.get(latLngList.size()-1).latitude);
                        lastLocation.setLongitude(latLngList.get(latLngList.size()-1).longitude);
                        if(location.distanceTo(lastLocation) > 10){
                            latLngList.add(new LatLng(location.getLatitude(), location.getLongitude()));
                            System.out.println("update is working");
                            drawPolyline();
                        }
                    }
                    if(latLngList.size() > 10){
                        endRunning.setVisibility(View.VISIBLE);
                    }
                    Common.latLngList = latLngList;
                }

            };
            fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
        }else{
            turnOnGPS();
        }
    }

    public void drawPolyline(){
        if(line != null){
            line.remove();
        }
        PolylineOptions options = new PolylineOptions().width(10).color(Color.BLUE).geodesic(true);
        for (int z = 0; z < latLngList.size(); z++) {

            options.add(latLngList.get(z));
        }

        line = googleMap.addPolyline(options);
    }

    public void turnOnGPS(){
        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
        builder.setAlwaysShow(true);
        Task<LocationSettingsResponse> result = LocationServices.getSettingsClient(requireActivity().getApplicationContext())
                .checkLocationSettings(builder.build());
        result.addOnCompleteListener(task -> {
            try {
                task.getResult(ApiException.class);
            } catch (ApiException e) {
                switch (e.getStatusCode()){
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        try {
                            ResolvableApiException resolvableApiException = (ResolvableApiException) e;
                            resolvableApiException.startResolutionForResult(requireActivity(), RequestCheck);
                        } catch (IntentSender.SendIntentException sendIntentException) {
                            sendIntentException.printStackTrace();
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        break;
                }
            }
        });
    }

    public void addMaker(LatLng latLng, String type){

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        if(type.equals("Origin")){
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
        }else {
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
        }
        markerOptions.title(type);
        Objects.requireNonNull(googleMap.addMarker(markerOptions)).showInfoWindow();
    }

    void moveCamera(LatLng latLng){
        googleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
    }

}
