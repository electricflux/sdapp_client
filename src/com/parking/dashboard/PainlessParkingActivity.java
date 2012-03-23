package com.parking.dashboard;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.SharedPreferences.Editor;
import android.location.LocationManager;
import android.os.Bundle;

public class PainlessParkingActivity extends Activity {
    
    private static final String TAG = "PainlessParkingActivity";
    protected LocationManager locationManager;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
    }
    
    
    @Override
   protected void onResume() {
      
      super.onResume();
      
   }

      
}