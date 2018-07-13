package com.leothosthoren.go4lunch.controler.fragments;


import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.PlaceDetectionClient;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.leothosthoren.go4lunch.R;
import com.leothosthoren.go4lunch.base.BaseFragment;

import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 */
public class MapViewFragment extends BaseFragment implements OnMapReadyCallback {


    //CONSTANT
    public static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    public static final float DEFAULT_ZOOM = 15f;
    public static final String TAG = MapViewFragment.class.getSimpleName();
    private static final int MAX_PLACES = 100;
    private static final int REQUEST_PICK_PLACE = 2;

    //VAR
    private MapView mMapView;
    private GoogleMap mMap;
    private boolean mLocationPermissionGranted;
    private Location mLastKnownLocation;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private LatLng mDefaultLocation = new LatLng(48.7927684, 2.3591994999999315);
    private GeoDataClient mGeoDataClient;
    private PlaceDetectionClient mPlaceDetectionClient;
    private GoogleApiClient mGoogleApiClient;

    @Override
    protected BaseFragment newInstance() {
        return new MapViewFragment();
    }

    @Override
    protected int getFragmentLayout() {
        return R.layout.fragment_map_view;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void configureDesign() {
        instantiatePlacesApiClients();
    }

    @Override
    protected void updateDesign() {

    }

    //---------------------------------------------------------------------------------------------//
    //                                         MAP                                                 //
    //---------------------------------------------------------------------------------------------//

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        getLocationPermission();
        getDeviceLocation();
        updateUI();

    }

    //---------------------------------------------------------------------------------------------//
    //                                     CONFIGURATION                                           //
    //---------------------------------------------------------------------------------------------//


    //Useful to initiate a map insid a fragment != Activity
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mMapView = (MapView) view.findViewById(R.id.map);
        if (mMapView != null) {
            mMapView.onCreate(null);
            mMapView.onResume();
            mMapView.getMapAsync(this);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void instantiatePlacesApiClients() {
        // Construct a GeoDataClient.
        mGeoDataClient = Places.getGeoDataClient(Objects.requireNonNull(getContext()));

        // Construct a PlaceDetectionClient.
        mPlaceDetectionClient = Places.getPlaceDetectionClient(getContext());

        // Construct a FusedLocationProviderClient
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getContext());

    }
    //---------------------------------------------------------------------------------------------//
    //                                      PERMISSION                                             //
    //---------------------------------------------------------------------------------------------//

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void getLocationPermission() {
        if (ContextCompat.checkSelfPermission(Objects.requireNonNull(getContext()),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(Objects.requireNonNull(getActivity()),
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions
            , @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true;
                }
            }
        }
        //Update Ui here
        updateUI();
    }

    //---------------------------------------------------------------------------------------------//
    //                                         LOCATION                                                  //
    //---------------------------------------------------------------------------------------------//

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void updateUI() {
        if (mMap == null) {
            return;
        }
        try {
            if (mLocationPermissionGranted) {
                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(true);
            } else {
                mMap.setMyLocationEnabled(false);
                mMap.getUiSettings().setMyLocationButtonEnabled(false);
                mLastKnownLocation = null;
                //Try to obtain location permission
                getLocationPermission();
            }
        } catch (SecurityException e) {
            Log.e(TAG, "updateUI: SecurityException " + e.getMessage());
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void getDeviceLocation() {
        try {
            if (mLocationPermissionGranted && getActivity() != null) {
                Task<Location> locationResult = mFusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(getActivity(), new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful()) {
                            // Set the map's camera position to the current location of the device.
                            mLastKnownLocation = task.getResult();
                            //Move camera toward device position
                            if (mLastKnownLocation != null) {

                                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                        new LatLng(mLastKnownLocation.getLatitude(),
                                                mLastKnownLocation.getLongitude()), DEFAULT_ZOOM));
                            } else {
                                Toast.makeText(getContext(),
                                        "Make sure your emulator device got map position on true",
                                        Toast.LENGTH_SHORT).show();
                            }

                        } else {
                            Log.d(TAG, "Current location is null. Using defaults.");
                            Log.e(TAG, "getPhoneLocation => Exception: %s" + task.getException());
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mDefaultLocation, DEFAULT_ZOOM));
                            mMap.getUiSettings().setMyLocationButtonEnabled(false);
                        }
                    }
                });
            }
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage());

        }
    }

}
