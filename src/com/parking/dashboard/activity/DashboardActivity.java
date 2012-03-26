
package com.parking.dashboard.activity;

import java.io.IOException;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.SQLException;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;

import com.parking.dashboard.R;
import com.parking.findparking.FindParkingTabs;
import com.parking.locatemycar.LocateMyCar;
import com.parking.payforspot.PayForSpot;
import com.parking.paymenthistory.PaymentHistory;
import com.parking.dbManager.DataBaseHelper;

public class DashboardActivity extends Activity{
   
   
   public static final String TAG = "DashboardActivity";
   public static Context myContext = null;

@Override
   protected void onCreate(Bundle savedInstanceState) {
      // TODO Auto-generated method stub
      super.onCreate(savedInstanceState);
      setContentView(R.layout.dashboard);
      
    //attach event handler to dash buttons
      DashboardClickListener dBClickListener = new DashboardClickListener();
      findViewById(R.id.dashboard_button_find_parking).setOnClickListener(dBClickListener);
      findViewById(R.id.dashboard_button_viewall).setOnClickListener(dBClickListener);
      findViewById(R.id.dashboard_button_manage).setOnClickListener(dBClickListener);
      findViewById(R.id.dashboard_button_personalbests).setOnClickListener(dBClickListener);
      
      DashboardActivity.myContext = getApplicationContext();
      
		Log.v(TAG, "Create DB: 1 ");

		DataBaseHelper myDbHelper = null;
		myDbHelper = new DataBaseHelper(null);
		myDbHelper = new DataBaseHelper(getAppContext());

		try {

			myDbHelper.createDataBase();

		} catch (IOException ioe) {

			throw new Error("Unable to create database");

		}

		try {

			myDbHelper.openDataBase();
			myDbHelper.dbquery(1);
			myDbHelper.close();

		} catch (SQLException sqle) {

			throw sqle;

		}
      
   }

	public static Context getAppContext() {
	    return DashboardActivity.myContext;
	}

   private class DashboardClickListener implements OnClickListener {
      @Override
      public void onClick(View v) {
          Intent i = null;
          switch (v.getId()) {
              case R.id.dashboard_button_find_parking:
                  i = new Intent(DashboardActivity.this, FindParkingTabs.class);
                  break;
              case R.id.dashboard_button_viewall:
                  i = new Intent(DashboardActivity.this, PayForSpot.class);
                  break;
              case R.id.dashboard_button_manage:
                  i = new Intent(DashboardActivity.this, PaymentHistory.class);
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
