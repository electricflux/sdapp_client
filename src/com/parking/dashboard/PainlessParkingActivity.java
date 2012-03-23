package com.parking.dashboard;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.os.Bundle;

import com.parking.datamanager.UserLocation;
import com.parking.location.ParkingLocationManager;

public class PainlessParkingActivity extends Activity {
    
    private static final String TAG = "PainlessParkingActivity";
    private Context myContext = getApplicationContext();
    private UserLocation userLocation;
    private ParkingLocationManager appLocManager;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        ParkingLocationManager appLocManager = new ParkingLocationManager(myContext);
        appLocManager.setUpLocationServices();
        
        
    }
    



   @Override
   protected void onResume() {
      
      super.onResume();
      
   }


   @Override
   protected void onDestroy() {

      appLocManager.stopListeningToLocationUpdates();
      super.onDestroy();
      
   }

    
   
}