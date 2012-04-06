package com.parking.findparking;

import com.parking.dashboard.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class ParkingSpotAndPaymentInformation extends Activity{

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      // TODO Auto-generated method stub
      super.onCreate(savedInstanceState);
      setContentView(R.layout.pspot_and_payment_info);
      Intent starterIntent = getIntent();
      Bundle bundle = starterIntent.getExtras();
      String all = bundle.getString("info");
      
      TextView textAll = (TextView) findViewById(R.id.textViewAll);
      textAll.setText(all);
      
      
   }

   
}
