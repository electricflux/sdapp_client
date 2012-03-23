package com.parking.location;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

public class ParkingLocationManager {

   private LocationManager locationManager; 
   private LocationListener locationListener;
   private Context myContext = null;
   private Location lastKnownLocation;
   
   public ParkingLocationManager(Context context){
      myContext = context;
      
   }
   
   public void setUpLocationServices() {
         
          //Get System location manager
          locationManager = (LocationManager) myContext.getSystemService(Context.LOCATION_SERVICE);

          //Listener to listen to location updates
          locationListener = new LocationListener(){

           @Override
           public void onLocationChanged(Location location) {
              //TODO implement
              setCurrentNewLocation(location);
              sendIntentToMapActivity();
           }

           @Override
           public void onProviderDisabled(String provider) {
              
           }

           @Override
           public void onProviderEnabled(String provider) {
              
           }

           @Override
           public void onStatusChanged(String provider, int status, Bundle extras) {
              
           }
            
         };
         
         //Register the listener with the location manager, look for updates from network and GPS both
         //TODO Look for other providers?
         locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
         locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
         
         //Update last known location
         setLastKnownLocation(locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER));
         
     }

   private void setLastKnownLocation(Location l) {
      lastKnownLocation = l;
      
   }
   
   public Location getLastKnownLocation() {
      return lastKnownLocation;
      
   }
   
   protected void sendIntentToMapActivity() {
      // TODO Auto-generated method stub
      
   }

   protected void setCurrentNewLocation(Location location) {
       // TODO Auto-generated method stub
  
      
   }

   public void stopListeningToLocationUpdates(){
      // Remove the listener you previously added
      locationManager.removeUpdates(locationListener);
      
   }
   
}
