package com.parking.findparking;

import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Vector;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
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
import com.parking.datamanager.DBInterface;
import com.parking.datamanager.ParkingLocationDataEntry;
import com.parking.datamanager.ParkingLocationsAll;
import com.parking.dbManager.DataBaseHelper;
import com.parking.location.ParkingLocationManager;
import com.parking.location.ParkingSpots;
import com.parking.utils.LocationUtility;
import com.parking.utils.ParkingConstants;

public class FindParkingMapActivity extends MapActivity implements LocationListener, AsyncTaskResultNotifierInterface {

	private static final String TAG = "FindParkingMap";
	private static MapView mapView;
	private static MapController mapController;
	private static MapOverLays itemizedOverlays;
	private static Context myContext;

	private ParkingLocationManager pLocationManger = null;
	private DataBaseHelper myDbHelper = null;

	private static Vector<ParkingSpots> parkingSpotsVector = new Vector<ParkingSpots>();
	private GetLocationList mGetLocationList = null;
	private static long minTime = 1000;
	private static float minDistance = 1000;
	private static LocationManager locationManager = null;
	ParkingLocationsAll mParkingLocationsAll = new ParkingLocationsAll();

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
		if ((intent != null) && 
				(intent.hasExtra(
						ParkingConstants.ParkingMapIntentParameters.LOCATION_FIXED_TO_DOWNTOWN)))
			locationFixedToDowntown = intent.getBooleanExtra(
					ParkingConstants.ParkingMapIntentParameters.LOCATION_FIXED_TO_DOWNTOWN,
					false);

		pLocationManger = new ParkingLocationManager(getApplicationContext());

		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

		/** 
		 * Don't register for location updates if you are just displaying 
		 * downtown data.
		 */
		if (false == locationFixedToDowntown)
		{
			locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, minTime,
					minDistance, this);
			locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, minTime,
					minDistance, this);
		}

		setUpLocationManager();
		setUpMapView();
		setUpMapController();

	}

	private void overlayParkingSpots() {
		List<ParkingLocationDataEntry> nLocList = new LinkedList<ParkingLocationDataEntry>();
		ParkingLocationDataEntry nLoc = new ParkingLocationDataEntry();
		nLoc.setLatitude((float) ParkingConstants.DOWNTOWN_FIXED_LATITUDE);
		nLoc.setLongitude((float) ParkingConstants.DOWNTOWN_FIXED_LONGITUDE);
		nLocList.add(nLoc);

		/** Center map to downtown */
		GeoPoint gp = 
				new GeoPoint((int)(nLoc.getLatitude()*1E6) ,
						(int)(nLoc.getLongitude()*1E6));
		mapView.getController().setCenter(gp); 
		mapView.getController().animateTo(gp);

		mGetLocationList = new GetLocationList(getBaseContext(), mapView, this);
		mGetLocationList.execute(nLocList);
	}

	private void updateCurrentUserLocation() {

		Toast.makeText(getApplicationContext(), "Updating Location...",
				Toast.LENGTH_LONG).show();
		Drawable userLocationBlueMarker = this.getResources().getDrawable(
				R.drawable.map_marker_blue);// map_marker_black);

		itemizedOverlays = new MapOverLays(userLocationBlueMarker, this);

		// Gives the blue 'google' location marker
		final MyLocationOverlay myLocOverlay = new MyLocationOverlay(this,
				mapView);
		mapView.getOverlays().add(myLocOverlay);
		myLocOverlay.enableCompass();
		myLocOverlay.enableMyLocation();
		myLocOverlay.runOnFirstFix(new Runnable() {
			public void run() {
				mapView.getController().animateTo(myLocOverlay.getMyLocation());
			}
		});

	}

	private void setUpLocationManager() {

		pLocationManger.setUpLocationServices();

	}

	private void setUpMapView() {

		mapView = (MapView) findViewById(R.id.mapview);
		mapView.setBuiltInZoomControls(true);
		mapView.setSatellite(false);
		mapView.setStreetView(true);

	}

	private void setUpMapController() {
		mapController = mapView.getController();
		mapController.setZoom(19); // Zoom 1 is world view

	}

	public static void updateMapWithLastKnownLocation(Location location) {

		Log.e(ParkingConstants.TAG, "Map Update!");

		int lat = (int) (location.getLatitude() * 1E6);
		int lng = (int) (location.getLongitude() * 1E6);
		GeoPoint point = new GeoPoint(lat, lng);
		createMarker();
		mapController.animateTo(point); // mapController.setCenter(point);

	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (locationFixedToDowntown)
		{
			locationManager.removeUpdates(this);
		}
	}

	@Override
	public void onResume() {
		super.onResume();

		/** Open database connection here */
		myDbHelper = new DataBaseHelper(myContext);

		if (false == locationFixedToDowntown)
			updateCurrentUserLocation();
		else if (locationFixedToDowntown && (false == staticDowntownMapPopulated))
		{
			overlayParkingSpots();
			staticDowntownMapPopulated = true;
		}
	}

	@Override
	public void onPause() {
		super.onPause();

		/** close database connection here */
		myDbHelper.close();
		myDbHelper = null;
	}

	private static void createMarker() {
		String address = "Not found";
		GeoPoint p = mapView.getMapCenter();
		address = convertPointToLocation(p);

		Log.v(TAG, "got the map center" + address);
		OverlayItem overlayitem = new OverlayItem(p, address, "My Location P ");

		itemizedOverlays.addOverlay(overlayitem);

		mapView.getOverlays().add(itemizedOverlays);
		mapView.postInvalidate();

	}

	public static String convertPointToLocation(GeoPoint point) {
		Geocoder geoCoder = new Geocoder(myContext, Locale.getDefault());
		return LocationUtility.ConvertPointToLocation(point, geoCoder);
	}

	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}

	public static void updateMapWithNewLocation(Location currentNewLocation) {

		// TODO Remove previous overlay, update with new one.
		// updateNewLocationOnMap(currentNewLocation);
		showNearbyParkingSpots(currentNewLocation);

	}

	private static void showNearbyParkingSpots(Location currentNewLocation) {

		parkingSpotsVector = DBInterface
				.getNearByParkingSpots(currentNewLocation);

	}

	private static void updateNewLocationOnMap(Location currentNewLocation) {
		Log.e(ParkingConstants.TAG, "Map Update!");
		int lat = (int) (currentNewLocation.getLatitude() * 1E6);
		int lng = (int) (currentNewLocation.getLongitude() * 1E6);
		GeoPoint point = new GeoPoint(lat, lng);
		createMarker();
		mapController.animateTo(point); // mapController.setCenter(point);

	}

	@SuppressWarnings("unchecked")
	@Override
	public void onLocationChanged(Location location) {
		int lat = (int) (location.getLatitude() * 1E6);
		int lng = (int) (location.getLongitude() * 1E6);
		GeoPoint point = new GeoPoint(lat, lng);
		//createMarker();
		mapController.animateTo(point); // mapController.setCenter(point);
		Log.v(TAG, "Loc" + location.getLatitude() +  location.getLongitude() );

		List<ParkingLocationDataEntry> nLocList = new LinkedList<ParkingLocationDataEntry>();
		ParkingLocationDataEntry nLoc = new ParkingLocationDataEntry();
		nLoc.setLatitude((float)location.getLatitude());
		nLoc.setLongitude((float)location.getLongitude());
		nLocList.add(nLoc);

		if (mGetLocationList  == null){
			mGetLocationList = new GetLocationList(getBaseContext(), mapView, this);
			mGetLocationList.execute(nLocList);
		}
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
		mGetLocationList = null;
	}
}
