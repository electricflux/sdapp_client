package com.parking.findparking;

import java.util.LinkedList;
import java.util.List;
import java.util.Locale;


import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;


import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;
import com.google.android.maps.OverlayItem;
import com.parking.auth.AsyncTaskResultNotifierInterface;
import com.parking.dashboard.R;

import com.parking.datamanager.ParkingLocationDataEntry;
import com.parking.datamanager.ParkingLocationsAll;
import com.parking.dbManager.DataBaseHelper;
import com.parking.location.ParkingLocationManager;
import com.parking.location.ParkingSpots;
import com.parking.utils.LocationUtility;
import com.parking.utils.ParkingConstants;

public class FindParkingMapActivity extends MapActivity implements AsyncTaskResultNotifierInterface, LocationListener {

	private static final String TAG = "FindParkingMap";
	ParkMapView mMapView;
	private static MapController mapController;
	private static MapOverLays itemizedOverlays;
	private static MapOverLays itemizedOverlays2;
	private static MapOverLays itemizedOverlays3;
	
	private static Context myContext;
	Handler mHandler = new Handler();
	private ParkingLocationManager pLocationManger = null;
	
	ProgressDialog progDialog;
	
	private GetLocationList mGetLocationList = null;
	
	private static LocationManager locationManager = null;
	ParkingLocationsAll mParkingLocationsAll = new ParkingLocationsAll();
	public MyLocationOverlay myLocOverlay ;
	private boolean locationFixedToDowntown = false;
	private boolean staticDowntownMapPopulated = false;

	@Override
	public void onCreate(Bundle bundle) {

		super.onCreate(bundle);
		setContentView(R.layout.findparking);

		myContext = getBaseContext();

		/** 
		 * Extract from intent whether this activity was launched 
		 * only for viewing downtown spots.
		 */
		
		Intent intent = getIntent();
		if ((intent != null) && (intent.hasExtra(ParkingConstants.ParkingMapIntentParameters.LOCATION_FIXED_TO_DOWNTOWN)))
			locationFixedToDowntown = intent.getBooleanExtra(ParkingConstants.ParkingMapIntentParameters.LOCATION_FIXED_TO_DOWNTOWN,
					false);

		setUpMapView();
		setUpMapController();

		/** 
		 * Don't register for location updates if you are just displaying 
		 * downtown data.
		 */

		if (false == locationFixedToDowntown)
		{
			updateCurrentUserLocation();
		}

	}

	@Override
	public void onResume() {
		super.onResume();

		
		
		if (false == locationFixedToDowntown)
		{
			myLocOverlay.enableMyLocation();
			myLocOverlay.enableCompass();
			showDialog(0);
			Log.v(TAG, "here");
		}
		else if (locationFixedToDowntown && (false == staticDowntownMapPopulated))
		{
			Log.v(TAG, "getting overlay spots");
			showDialog(0);
			//overlayParkingSpots();
			staticDowntownMapPopulated = true;
		}
	}

    // Method to create a progress bar dialog of either spinner or horizontal type
    @Override
    protected Dialog onCreateDialog(int id) {
    		Log.v(TAG, "setting spinner");
    		progDialog = new ProgressDialog(this);
            progDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            
            progDialog.setMessage("Loading...");
            //Run the Async task from here
            Log.v(TAG, "besfor launching async task");
            overlayParkingSpots();
            
            return progDialog;
       }
    
    
	@SuppressWarnings("unchecked")
	private void overlayParkingSpots() {
		Log.v(TAG, "overlayParkingSpots");
		if (staticDowntownMapPopulated == false)
		{
			Log.v(TAG, "populate static map first time");
			List<ParkingLocationDataEntry> nLocList = new LinkedList<ParkingLocationDataEntry>();
			ParkingLocationDataEntry nLoc = new ParkingLocationDataEntry();
			nLoc.setLatitude((float) ParkingConstants.DOWNTOWN_FIXED_LATITUDE);
			nLoc.setLongitude((float) ParkingConstants.DOWNTOWN_FIXED_LONGITUDE);
		
			nLocList.add(nLoc);
	
			/** Center map to downtown */
			GeoPoint gp = 
					new GeoPoint((int)(nLoc.getLatitude()*1E6) ,
							(int)(nLoc.getLongitude()*1E6));
			
			/** Dont Center the map to downtown for current location **/
			if (true == locationFixedToDowntown)
			{
				mMapView.getController().setCenter(gp); 
				mMapView.getController().animateTo(gp);
			}

			staticDowntownMapPopulated=true;
			mGetLocationList = new GetLocationList(getBaseContext(), mMapView, this);
			mGetLocationList.execute(nLocList);
	
		}
		else
		{
			Log.v(TAG, "populate map everyothertime" + staticDowntownMapPopulated);
			
			
			GeoPoint mapCenter = mMapView.getMapCenter();
			
			if (mGetLocationList  == null){
				
				List<ParkingLocationDataEntry> nLocList = new LinkedList<ParkingLocationDataEntry>();
				ParkingLocationDataEntry nLoc = new ParkingLocationDataEntry();
				nLoc.setLatitude((float) ((float)mapCenter.getLatitudeE6()/1E6));
				nLoc.setLongitude((float) ((float)mapCenter.getLongitudeE6()/1E6));
				nLocList.add(nLoc);
				mGetLocationList = new GetLocationList(getBaseContext(), mMapView, FindParkingMapActivity.this);
				mGetLocationList.execute(nLocList);
			}
			else
				Log.v(TAG,"Async task still running");
	
		}
	}

	private void updateCurrentUserLocation() {

		
		Toast.makeText(getApplicationContext(), "Updating Location...",
				Toast.LENGTH_LONG).show();

		myLocOverlay = new MyLocationOverlay(this,
				mMapView);
		// Gives the blue 'google' location marker
		
		mMapView.getOverlays().add(myLocOverlay);
		myLocOverlay.runOnFirstFix(new Runnable() {
			public void run() {
				mMapView.getController().animateTo(myLocOverlay.getMyLocation());
				mMapView.postInvalidate();
			}
		});
		
		 
	}


	private void setUpMapView() {

		mMapView = (ParkMapView) findViewById(R.id.themap);
		mMapView.setBuiltInZoomControls(true);
		mMapView.setSatellite(false);
		// Add listeners
		mMapView.setOnChangeListener(new MapViewChangeListener());
		
//		itemizedOverlays.setOnTapListener(new MapViewTapListener());
		Log.e(ParkingConstants.TAG, "Map listenr set!");
	}

	private void setUpMapController() {
		
		mapController = mMapView.getController();
		mapController.setZoom(ParkingConstants.DEFAULT_ZOOM_LEVEL); // Zoom 1 is world view

	}


	@Override
	public void onDestroy() {
		super.onDestroy();
		if (locationFixedToDowntown == false)
		{
//			locationManager.removeUpdates(this);
		}
		Log.v(TAG,"Removing things on destroy");
		FindParkingTabs.parkingListfilled = false;
		FindParkingTabs.allParkingLocations.clear();
		
	}


	@Override
	public void onPause() {
		super.onPause();

		Log.v(TAG,"ACtivity Paused");
		
		if (false == locationFixedToDowntown)
		{
			myLocOverlay.disableMyLocation();
			myLocOverlay.disableCompass();
		}
	}
	  
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		
			Log.v(TAG,"Configuration changed");
	        super.onConfigurationChanged(newConfig);
	    }

	// Runnables
		Runnable mOnMapTap = new Runnable()
		{
			public void run()
			{
				// Notify
				Toast.makeText(FindParkingMapActivity.this, "Map Tap", Toast.LENGTH_SHORT).show();
			}
		};
		
		Runnable mOnMapZoom = new Runnable()
		{
			public void run()
			{
				// Notify
				FindParkingTabs.zoomlevel = mMapView.getZoomLevel();
				Log.v(TAG,"Map Zoom level: " + mMapView.getZoomLevel());
				Toast.makeText(FindParkingMapActivity.this, "Map Zoom", Toast.LENGTH_SHORT).show();
				if(!mMapView.getOverlays().isEmpty()) 
				{ 
				//mMapView.getOverlays().remove(itemizedOverlays);
				//mMapView.getOverlays().remove(itemizedOverlays2);
				//mMapView.getOverlays().remove(itemizedOverlays3);
				mMapView.getOverlays().clear(); 
				if (myLocOverlay != null)
				mMapView.getOverlays().add(myLocOverlay);
				mMapView.postInvalidate();
				/** Redisplay the spots based on zoom level **/
				overlayTappableParkingSpots();
				
				}
			}
		};
		
		Runnable mOnMapPan = new Runnable()
		{
			@SuppressWarnings("unchecked")
			public void run()
			{
				// Notify
				Toast.makeText(FindParkingMapActivity.this, "Map Pan", Toast.LENGTH_SHORT).show();
				if(!mMapView.getOverlays().isEmpty()) 
				{ 
				//mMapView.getOverlays().remove(itemizedOverlays);
				//mMapView.getOverlays().remove(itemizedOverlays2);
				//mMapView.getOverlays().remove(itemizedOverlays3);
				mMapView.getOverlays().clear(); 
				if (myLocOverlay != null)
				mMapView.getOverlays().add(myLocOverlay);
				mMapView.postInvalidate();				 
				} 
				showDialog(0);	
			
			}
		
		};
		
		Runnable mOnMapZoomPan = new Runnable()
		{
			public void run()
			{
				// Notify
				FindParkingTabs.zoomlevel = mMapView.getZoomLevel();
				Toast.makeText(FindParkingMapActivity.this, "Map Zoom + Pan", Toast.LENGTH_SHORT).show();
			}
		};

		private class MapViewChangeListener implements ParkMapView.OnChangeListener
		{

			@Override
			public void onChange(MapView view, GeoPoint newCenter, GeoPoint oldCenter, int newZoom, int oldZoom)
			{
				// Check values
				if ((!newCenter.equals(oldCenter)) && (newZoom != oldZoom))
				{
					mHandler.post(mOnMapZoomPan);
				}
				else if (!newCenter.equals(oldCenter))
				{
					mHandler.post(mOnMapPan);
				}
				else if (newZoom != oldZoom)
				{
					mHandler.post(mOnMapZoom);
					
				}
			}	
		}
		

	public static String convertPointToLocation(GeoPoint point) {
		Geocoder geoCoder = new Geocoder(myContext, Locale.getDefault());
		return LocationUtility.ConvertPointToLocation(point, geoCoder);
	}

	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}

	

	@SuppressWarnings("unchecked")
	@Override
	public void onLocationChanged(Location location) {
		int lat = (int) (location.getLatitude() * 1E6);
		int lng = (int) (location.getLongitude() * 1E6);
		GeoPoint point = new GeoPoint(lat, lng);
		//createMarker();
		Toast.makeText(getApplicationContext(), "Location changed called",
				Toast.LENGTH_LONG).show();		
		Log.v(TAG, "Loc change call back : " + location.getLatitude() +  location.getLongitude() );
		if (mGetLocationList  == null){
			mapController.animateTo(point); // mapController.setCenter(point);	
			List<ParkingLocationDataEntry> nLocList = new LinkedList<ParkingLocationDataEntry>();
			ParkingLocationDataEntry nLoc = new ParkingLocationDataEntry();
			nLoc.setLatitude((float)location.getLatitude());
			nLoc.setLongitude((float)location.getLongitude());
			nLocList.add(nLoc);
			mGetLocationList = new GetLocationList(getBaseContext(), mMapView, this);
			mGetLocationList.execute(nLocList);
		}
		else
			Log.v(TAG,"Async task still running");
		
		
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
	@Override
	public void notifyResult(boolean result) {
		//dismissDialog(0);
		removeDialog(0);
		progDialog = null;
		mGetLocationList = null;
		overlayTappableParkingSpots();
		
	}
	
	 private void overlayTappableParkingSpots() {

			String address = "No Associated Address";
			GeoPoint pSpotGeoPoint = null;
			OverlayItem overlayitem = null;
			String sPpotInfo = "No Info Available";
			Drawable drawable = myContext.getResources().getDrawable(
					R.drawable.blue_markerp);// map_marker_black);
			itemizedOverlays = new MapOverLays(drawable, myContext);
			Drawable drawable2 = myContext.getResources().getDrawable(
					R.drawable.darkgreen_markerp);// map_marker_black);
			itemizedOverlays2 = new MapOverLays(drawable2, myContext);
			
			Drawable drawable3 = myContext.getResources().getDrawable(
					R.drawable.red_markerp);// map_marker_black);
			itemizedOverlays3 = new MapOverLays(drawable3, myContext);
			
			int dataPoints = FindParkingTabs.parkingLocations.size();
			int displayPoints;
			if (FindParkingTabs.zoomlevel <=14)
				displayPoints = 30;
			else if (FindParkingTabs.zoomlevel == 15 || FindParkingTabs.zoomlevel == 16)
				displayPoints = dataPoints;
			else
				displayPoints = dataPoints; 
			int count=1;
			Log.v(TAG, "Parked item size" + FindParkingTabs.parkingLocations.size());
			for (ParkingLocationDataEntry parkingSpot : FindParkingTabs.parkingLocations) {
				
				if (count >= displayPoints)
					break;
				pSpotGeoPoint = parkingSpot.getGeoPoint();
				address = "empty";//convertPointToLocation(pSpotGeoPoint);
				sPpotInfo = LocationUtility.convertObjToString(parkingSpot);
				overlayitem = new OverlayItem(pSpotGeoPoint, address, sPpotInfo);
				//Log.v(TAG, "Spot type: " + parkingSpot.getType());
				if (parkingSpot.getType().contains("MultiMeterPost"))
				itemizedOverlays2.addOverlay(overlayitem);
				else if (parkingSpot.getType().contains("OffStreetParking"))
				itemizedOverlays3.addOverlay(overlayitem);	
				else 
				itemizedOverlays.addOverlay(overlayitem);
				
				count++;
			}
			itemizedOverlays.addDone();
			itemizedOverlays2.addDone();
			itemizedOverlays3.addDone();
			mMapView.getOverlays().add(itemizedOverlays);
			mMapView.getOverlays().add(itemizedOverlays2);
			mMapView.getOverlays().add(itemizedOverlays3);
			mMapView.postInvalidate();
		}


}
