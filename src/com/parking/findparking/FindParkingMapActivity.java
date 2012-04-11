package com.parking.findparking;

import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Vector;
import android.content.Context;
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
import com.google.android.maps.Overlay;
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
	private Vector<GeoPoint> geoPointsVector = new Vector<GeoPoint>();
	private GetLocationList mGetLocationList = null;
	private static long minTime = 1000;
	private static float minDistance = 1000;
	private static LocationManager locationManager = null;
	ParkingLocationsAll mParkingLocationsAll = new ParkingLocationsAll();

	@Override
	public void onCreate(Bundle bundle) {

		super.onCreate(bundle);
		setContentView(R.layout.findparking);

		myContext = getBaseContext();

		pLocationManger = new ParkingLocationManager(getApplicationContext());
		
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, minTime,
				minDistance, this);
		locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, minTime,
				minDistance, this);

		setUpLocationManager();
		setUpMapView();
		setUpMapController();

	}

	@SuppressWarnings({ "null", "unchecked" })
	private void overlayParkingSpots() {

		FindParkingTabs.parkingLocations = mParkingLocationsAll.getParkingLocations(2, 200, (float)32.71283, (float)-117.165695, myDbHelper);
		
		Log.v(TAG, "NULLLLL" + FindParkingTabs.parkingLocations.size());
		
		//overlayTappableParkingSpots();

		//Async activity to get the parking stops from db

		List<ParkingLocationDataEntry> nLocList = new LinkedList<ParkingLocationDataEntry>();
		ParkingLocationDataEntry nLoc = new ParkingLocationDataEntry();
		//mGetLocationList = new GetLocationList();
		nLoc.setLatitude((float)32.71283);
		nLoc.setLongitude((float) -117.165695);
		nLocList.add(nLoc);
//<<<<<<< HEAD:src/com/parking/findparking/FindParkingMap.java
//		//mGetLocationList.execute(nLocList);
//
//=======
//		GetLocationList mGetLocationList = new GetLocationList();
//		mGetLocationList.execute(nLocList);
//>>>>>>> 1659d79f5dd0a7fd02b2ee0e1d3fef612188247e:src/com/parking/findparking/FindParkingMapActivity.java
	}

	private void updateCurrentUserLocation() {

		List<Overlay> mapsOverLays = mapView.getOverlays();

		Toast.makeText(getApplicationContext(), "Updating Location...",
				Toast.LENGTH_LONG).show();
		Drawable userLocationBlueMarker = this.getResources().getDrawable(
				R.drawable.map_marker_blue);// map_marker_black);
		// private Drawable parkingLocationMarkers =
		// this.getResources().getDrawable(R.drawable.parking_spot_marker);

		itemizedOverlays = new MapOverLays(userLocationBlueMarker, this);
		// updateMapWithLastKnownLocation(pLocationManger.getLastKnownLocation());

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
		// parkingSpotsCursor.close();
		locationManager.removeUpdates(this);

	}

	@Override
	public void onResume() {
		super.onResume();
		// parkingSpotsCursor.requery();
		updateCurrentUserLocation();
		
		/** Open database connection here */
		myDbHelper = new DataBaseHelper(myContext);
		myDbHelper.openDataBase();
		
		overlayParkingSpots();


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
		String address = "";
		Geocoder geoCoder = new Geocoder(myContext, Locale.getDefault());
		return LocationUtility.ConvertPointToLocation(point, geoCoder);
		// return address;
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

		//Async activity to get the parking stops from db

		Log.v(TAG, "Loc" + location.getLatitude() +  location.getLongitude() );

		List<ParkingLocationDataEntry> nLocList = new LinkedList<ParkingLocationDataEntry>();
		ParkingLocationDataEntry nLoc = new ParkingLocationDataEntry();
		nLoc.setLatitude((float)location.getLatitude());
		nLoc.setLongitude((float)location.getLongitude());
		nLocList.add(nLoc);

		if (mGetLocationList  == null){
			mGetLocationList = new GetLocationList(getBaseContext(), mapView, this);
			Log.v(TAG, "Created mGetLocationList ");

			Log.v(TAG, "Did not start Aynctask "+mGetLocationList.getStatus()   );
			//	if (mGetLocationList.getStatus().equals(AsyncTask.Status.FINISHED))
			//	{
			mGetLocationList.execute(nLocList);
			//	}
			//	else
			//{
			// wait until it's done.
			//Log.v(TAG, "Did not start Aynctask"  );
			//}
		}
	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub

	}
//<<<<<<< HEAD:src/com/parking/findparking/FindParkingMap.java

	@Override
	public void notifyResult(boolean result) {
		mGetLocationList = null;
	}

}
//=======
//}
//>>>>>>> 1659d79f5dd0a7fd02b2ee0e1d3fef612188247e:src/com/parking/findparking/FindParkingMapActivity.java
