package com.parking.dashboard;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

import com.parking.location.FroyoLocationUpdateRequester;
import com.parking.location.ILastLocationFinder;
import com.parking.location.LegacyLastLocationFinder;
import com.parking.location.LocationUpdateRequester;
import com.parking.services.ParkingSpotsService;
import com.parking.utils.ParkingConstants;

public class PainlessParkingActivity extends Activity {
    
    private static final String TAG = "PainlessParkingActivity";
    protected ILastLocationFinder lastLocationFinder;
    protected LocationUpdateRequester locationUpdateRequester;
    protected LocationManager locationManager;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        
        lastLocationFinder = new LegacyLastLocationFinder(this);
        lastLocationFinder.setChangedLocationListener(oneShotLastLocationUpdateListener);
        locationUpdateRequester = new FroyoLocationUpdateRequester(locationManager);
        
    }
    
    /**
     * One-off location listener that receives updates from the {@link LastLocationFinder}.
     * This is triggered where the last known location is outside the bounds of our maximum
     * distance and latency.
     */
    protected LocationListener oneShotLastLocationUpdateListener = new LocationListener() {
      public void onLocationChanged(Location l) {
        updatePlaces(l, ParkingConstants.DEFAULT_RADIUS, true);
      }
     
      public void onProviderDisabled(String provider) {}
      public void onStatusChanged(String provider, int status, Bundle extras) {}
      public void onProviderEnabled(String provider) {}
    };

      protected void updatePlaces(Location location, int radius, boolean forceRefresh) {
         if (location != null) {
           Log.d(TAG, "Updating place list.");
           // Start the ParkingSpotsService. Note that we use an action rather than specifying the 
           // class directly. That's because we have different variations of the Service for different
           // platform versions.
           Intent updateServiceIntent = new Intent(this, ParkingSpotsService.class);
           updateServiceIntent.putExtra(ParkingConstants.EXTRA_KEY_LOCATION, location);
           updateServiceIntent.putExtra(ParkingConstants.EXTRA_KEY_RADIUS, radius);
           updateServiceIntent.putExtra(ParkingConstants.EXTRA_KEY_FORCEREFRESH, forceRefresh);
           startService(updateServiceIntent);
         }
         else
           Log.d(TAG, "Updating place list for: No Previous Location Found");
       }
      
}