package com.parking.findparking;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.content.Intent;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.AdapterView.OnItemSelectedListener;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.OverlayItem;
import com.parking.billing.BillingConstants;
import com.parking.billing.BillingService;
import com.parking.billing.CatalogAdapter;
import com.parking.billing.ParkingPayment;
import com.parking.billing.PurchaseDatabase;
import com.parking.billing.ResponseHandler;

import com.parking.dashboard.R;
import com.parking.dashboard.activity.DashboardActivity;
import com.parking.datamanager.ParkingLocationDataEntry;
import com.parking.timewidget.TimeActivity;
import com.parking.utils.AppPreferences;
import com.parking.utils.LocationUtility;

public class ParkingSpotSelector extends Activity implements OnClickListener,
OnItemSelectedListener {
	private static final long NUM_MILLIS_IN_A_MINUTE = 60*1000;
	public static ParkingLocationDataEntry parkingLocationObj = new ParkingLocationDataEntry();
	private static final float FLAT_PARKING_SPOT_RATE_PER_MINUTE = 
			0.16f;
	private static final String TAG = ParkingSpotSelector.class.getSimpleName();
	private Spinner mSelectItemSpinner;
	private Spinner mSelectLicensePlateSpinner;
	private Button mDirButton;
	private Button mBuyButton;
	
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.parkingselector);

		//Get on the spot selected 
		Intent starterIntent = getIntent();
		Bundle bundle = starterIntent.getExtras();

		/** Create parkingLocationObj */
		String all = bundle.getString("info");
		parkingLocationObj = LocationUtility.convertStringToObject(all);
		Log.v(TAG,"OnCreate called: ");
		
		//Since you may leave the application if you choose to get driving direction
		//So to get back the context do below
	
		if (DashboardActivity.myContext == null)
			DashboardActivity.myContext = getApplicationContext();
		/** Use the object to populate fields */
		TextView textAll = (TextView) findViewById(R.id.parkingAllDetailsTextView);
		String locAddress = "Address unavailable";
		Geocoder geoCoder = new Geocoder(ParkingSpotSelector.this, Locale.getDefault());
		
		GeoPoint gp = new GeoPoint((int)(parkingLocationObj.getLatitude()*1E6),(int)(parkingLocationObj.getLongitude()*1E6));
		locAddress = LocationUtility.ConvertPointToLocation(gp, geoCoder);
		Log.v(TAG,"Setting address to: "+locAddress);
		parkingLocationObj.setAddress(locAddress);


		String typeToDisplay  = 
				parkingLocationObj.getType() == null ? "Not known" : ""+parkingLocationObj.getType();

		textAll.setText( "\n"
				+ "Address: " + parkingLocationObj.getAddress() + "\n"
				+ "Type:" + typeToDisplay + "\n"
				+ "MeterId: " + parkingLocationObj.getMeterID() + "\n"				
				+ "Number of Parking Spots at this location: " + parkingLocationObj.getQuantity() + "\n"
				);

		setupWidgets();
		
		// Check if billing is supported.
	}
	
	public void onResume() {
		
		Log.v(TAG,"OnResume Called: ");
		super.onResume();
	}
	
	private void setupWidgets() {
		mBuyButton = (Button) findViewById(R.id.buy_button);
		mBuyButton.setEnabled(true);
		mBuyButton.setOnClickListener(this);
		mDirButton = (Button) findViewById(R.id.directions);
		mDirButton.setEnabled(true);
		mDirButton.setOnClickListener(this);

		mSelectLicensePlateSpinner = (Spinner) findViewById(R.id.license_plate_choices);
		List<String> myList = new ArrayList<String>();
		myList.addAll(AppPreferences.getInstance().getLicensePlateList());
		//myList.add("other");
		
		ArrayAdapter<String> spinnerArrayAdapter = 
				new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, 
						myList);
		spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		mSelectLicensePlateSpinner.setAdapter(spinnerArrayAdapter);
		

	}
	/**
	 * Called when an item in the spinner is selected.
	 */
	public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
		//1 hr 15 min
	}

	public void onNothingSelected(AdapterView<?> arg0) {
	}		

	/**
	 * Called when the Pay Now button is pressed
	 */
	public void onClick(View v) {
		if (v == mBuyButton) {
			
			/** Set latitude and longitude in application preferences */
			AppPreferences.getInstance().setLastPaidLocationLatitude(
					parkingLocationObj.getLatitude());
			AppPreferences.getInstance().setLastPaidLocationLongitude(
					parkingLocationObj.getLongitude());
			final ParkingLocationDataEntry parkingObj = parkingLocationObj; 
			String Obj = LocationUtility.convertObjToString(parkingObj);
			
			Intent pspotInfo = new Intent(ParkingSpotSelector.this, TimeActivity.class); //ParkingSpotAndPaymentInformation.class);
			pspotInfo.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			pspotInfo.putExtra("info", Obj);
			Log.v(TAG,"Passing snippet: ");
			if(pspotInfo != null) {
				Log.v(TAG, "About to start Activity");
				startActivity(pspotInfo);
			}
			
		//	Intent i = new Intent(ParkingSpotSelector.this, TimeActivity.class);
			
			/** Sanity check */
		}
		else if (v == mDirButton) {
			/** Set latitude and longitude in application preferences */
			AppPreferences.getInstance().setLastPaidLocationLatitude(
					parkingLocationObj.getLatitude());
			AppPreferences.getInstance().setLastPaidLocationLongitude(
					parkingLocationObj.getLongitude());
			
			double lat = AppPreferences.getInstance().getLastPaidLocationLatitude();
			double lon = AppPreferences.getInstance().getLastPaidLocationLongitude();
			Intent intent = new Intent(android.content.Intent.ACTION_VIEW, 
					Uri.parse("http://maps.google.com/maps?daddr="+lat+","+lon+""));
			intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
			startActivity(intent);
			
		}
			
	}


}
