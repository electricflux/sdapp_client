
package com.parking.dashboard.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import com.parking.dashboard.R;
import com.parking.findparking.FindParking;
import com.parking.locatemycar.LocateMyCar;
import com.parking.payforspot.PayForSpot;
import com.parking.paymenthistory.PaymentHistory;

public class DashboardActivity extends Activity{
   
   
   @Override
   protected void onCreate(Bundle savedInstanceState) {
      // TODO Auto-generated method stub
      super.onCreate(savedInstanceState);
      setContentView(R.layout.dashboard);
      
    //attach event handler to dash buttons
      DashboardClickListener dBClickListener = new DashboardClickListener();
      findViewById(R.id.dashboard_button_add).setOnClickListener(dBClickListener);
      findViewById(R.id.dashboard_button_viewall).setOnClickListener(dBClickListener);
      findViewById(R.id.dashboard_button_manage).setOnClickListener(dBClickListener);
      findViewById(R.id.dashboard_button_personalbests).setOnClickListener(dBClickListener);
      
   }

   private class DashboardClickListener implements OnClickListener {
      @Override
      public void onClick(View v) {
          Intent i = null;
          switch (v.getId()) {
              case R.id.dashboard_button_add:
                  i = new Intent(DashboardActivity.this, PaymentHistory.class);
                  break;
              case R.id.dashboard_button_viewall:
                  i = new Intent(DashboardActivity.this, PayForSpot.class);
                  break;
              case R.id.dashboard_button_manage:
                  i = new Intent(DashboardActivity.this, FindParking.class);
                  break;
              case R.id.dashboard_button_personalbests:
                  i = new Intent(DashboardActivity.this, LocateMyCar.class);
                  break;
              default:
                  break;
          }
          if(i != null) {
              startActivity(i);
          }
      }
  }
   

}
