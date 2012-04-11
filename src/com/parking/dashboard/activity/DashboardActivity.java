
package com.parking.dashboard.activity;

import java.io.IOException;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

import com.parking.application.ParkingApplication;
import com.parking.auth.Authenticator;
import com.parking.dashboard.R;
import com.parking.dbManager.DataBaseHelper;
import com.parking.findparking.FindParkingTabs;
import com.parking.locatemycar.LocateMyCar;
import com.parking.paymenthistory.PaymentHistory;
import com.parking.rulesandregulations.RulesandRegulations;
import com.parking.towingcontacts.TowingContacts;
import com.parking.utils.Utility;

public class DashboardActivity extends Activity{
	private static final String TAG = DashboardActivity.class.getSimpleName();

	public static Context myContext = null;
	public static DataBaseHelper myDbHelper = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dashboard);
		myContext = getApplicationContext();
		DashboardActivity.myContext = getApplicationContext();

		//attach event handler to dash buttons
		DashboardClickListener dBClickListener = new DashboardClickListener();
		findViewById(R.id.dashboard_button_find_parking).setOnClickListener(dBClickListener);
		findViewById(R.id.dashboard_button_viewall).setOnClickListener(dBClickListener);
		findViewById(R.id.dashboard_button_personalbests).setOnClickListener(dBClickListener);
		//c. changes
		findViewById(R.id.dashboard_button_parkandremind).setOnClickListener(dBClickListener);
		findViewById(R.id.dashboard_button_towingcontact).setOnClickListener(dBClickListener);
		findViewById(R.id.dashboard_button_parkingrules).setOnClickListener(dBClickListener);

		//Initialize the DB here
		if(createOrCopyDB()){
			Toast.makeText(myContext, "Database Initialized!", Toast.LENGTH_SHORT).show();
		}
		else
		{
			Toast.makeText(myContext, "Failed to init DB!", Toast.LENGTH_SHORT).show();
		}


	}

	private boolean createOrCopyDB() {

		DataBaseHelper myDbHelper = new DataBaseHelper(null);
		myDbHelper = new DataBaseHelper(getAppContext());
		boolean retVal = false;

		try {
			retVal = myDbHelper.createDataBase();

		} catch (IOException ioe) {
			Log.e(TAG, "FATAL: Unable to Create DB for QuickPark!");
			retVal = false;
			throw new Error("Unable to create database");
		}

		myDbHelper.close();
		return retVal;


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
				//	ParkingLocationsAll mParkingLocationsAll = new ParkingLocationsAll();
				//	mParkingLocationsAll.getParkingLocations(10, myDbHelper);
				break;

			case R.id.dashboard_button_personalbests:
				i = new Intent(DashboardActivity.this, PaymentHistory.class);
				break;
				//Added to locate my car
			case R.id.dashboard_button_viewall:
			//i = new Intent(DashboardActivity.this, LocateMyCar.class);
			//TODO: Provide lat lon from SharedPreference
			double lat = 32.71283;
			double lon = -117.165695;
			Intent intent = new Intent(android.content.Intent.ACTION_VIEW, 
			Uri.parse("http://maps.google.com/maps?daddr="+lat+","+lon+""));
			intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
			startActivity(intent);
			break;
			case R.id.dashboard_button_parkandremind:
				Toast.makeText(
						DashboardActivity.this, "Functionality coming soon" , Toast.LENGTH_SHORT);
				//i = new Intent(DashboardActivity.this, LocateMyCar.class);
				break;
			case R.id.dashboard_button_towingcontact:
				i = new Intent(DashboardActivity.this, TowingContacts.class);
				break;
			case R.id.dashboard_button_parkingrules:
				i = new Intent(DashboardActivity.this, RulesandRegulations.class);
				break;                  

			default:
				break;
			}
			if(i != null) {
				Log.v(TAG, "About to start Activity");
				startActivity(i);
			}
		}
	}


	@Override
	protected void onResume()
	{
		super.onResume();
		if (false == ParkingApplication.isUserAuthenticated())
		{
			Log.v(TAG,"User is not authenticated. Initiating authentication sequence.");
			Authenticator.authenticate(this.getBaseContext());
		}
	}

}
