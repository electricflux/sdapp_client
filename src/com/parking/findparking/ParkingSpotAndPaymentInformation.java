package com.parking.findparking;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.parking.billing.BillingConstants.ResponseCode;
import com.parking.billing.ParkingBillingService;
import com.parking.dashboard.R;
import com.parking.dashboard.activity.DashboardActivity;

public class ParkingSpotAndPaymentInformation extends Activity{

   private ParkingBillingService mParkingBillingService = new ParkingBillingService();
   int billingSupported = 0;
   
   @Override
   protected void onCreate(Bundle savedInstanceState) {
      
      super.onCreate(savedInstanceState);
      setContentView(R.layout.pspot_and_payment_info);
      Intent starterIntent = getIntent();
      Bundle bundle = starterIntent.getExtras();
      String all = bundle.getString("info");
      
      TextView textAll = (TextView) findViewById(R.id.textViewAll);
      textAll.setText(all);
      
      //Set up connection to the service
      mParkingBillingService.setContext(DashboardActivity.myContext);
      
      
      Button payButton = (Button) findViewById(R.id.payButton);
      payButton.setOnClickListener(new OnClickListener(){
         @Override
         public void onClick(View arg0) {
            billingSupported = mParkingBillingService.isInAppBillingSupported();
            if(billingSupported == ResponseCode.RESULT_OK.ordinal())
            {
               Toast.makeText(DashboardActivity.myContext, "SUPPORTED YAY", Toast.LENGTH_SHORT).show();
               
            }
            else
            {
               Toast.makeText(DashboardActivity.myContext, "NAY :(", Toast.LENGTH_SHORT).show();
            }
         }
      });
      
   }

   
}
