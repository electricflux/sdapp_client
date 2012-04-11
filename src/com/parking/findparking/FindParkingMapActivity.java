package com.parking.findparking;

import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Vector;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
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
import com.parking.dashboard.R;
import com.parking.datamanager.DBInterface;
import com.parking.datamanager.ParkingLocationDataEntry;
import com.parking.datamanager.ParkingLocationsAll;
import com.parking.dbManager.DataBaseHelper;
import com.parking.location.ParkingLocationManager;
import com.parking.location.ParkingSpots;
import com.parking.utils.LocationUtility;
import com.parking.utils.ParkingConstants;

public class FindParkingMapActivity extends MapActivity {

	private static final String TAG = "FindParkingMap";
	private static MapView mapView;
	private static MapController mapController;
	private static MapOverLays itemizedOverlays;
	private static Context myContext;

	private ParkingLocationManager pLocationManger = null;
	private DataBaseHelper myDbHelper = null;

	private static Vector<ParkingSpots> parkingSpotsVector = new Vector<ParkingSpots>();
	private Vector<GeoPoint> geoPointsVector = new Vector<GeoPoint>();
	ParkingLocationsAll mParkingLocationsAll = new ParkingLocationsAll();

	@Override
	public void onCreate(Bundle bundle) {

		super.onCreate(bundle);
		setContentView(R.layout.findparking);

		myContext = getBaseContext();

		pLocationManger = new ParkingLocationManager(getApplicationContext());


		setUpLocationManager();
		setUpMapView();
		setUpMapController();

	}

	@SuppressWarnings({ "null", "unchecked" })
	private void overlayParkingSpots() {

		FindParkingTabs.parkingLocations = mParkingLocationsAll.getParkingLocations(2, 200, (float)32.71283, (float)-117.165695, myDbHelper);
		
		Log.v(TAG, "NULLLLL" + FindParkingTabs.parkingLocations.size());
		
		overlayTappableParkingSpots();

		//Async activity to get the parking stops from db

		List<ParkingLocationDataEntry> nLocList = new LinkedList<ParkingLocationDataEntry>();
		ParkingLocationDataEntry nLoc = new ParkingLocationDataEntry();
		nLoc.setLatitude((float)32.71283);
		nLoc.setLongitude((float) -117.165695);
		nLocList.add(nLoc);
		GetLocationList mGetLocationList = new GetLocationList();
		mGetLocationList.execute(nLocList);
	}

	private void overlayTappableParkingSpots() {

		String address = "No Associated Address";
		GeoPoint pSpotGeoPoint = null;
		OverlayItem overlayitem = null;
		String sPpotInfo = "No Info Available";
		Drawable drawable = this.getResources().getDrawable(
				R.drawable.map_marker_blue);// map_marker_black);
		itemizedOverlays = new MapOverLays(drawable, this);

		Log.v(TAG, "here ..4");
		for (ParkingLocationDataEntry parkingSpot : FindParkingTabs.parkingLocations) {


			pSpotGeoPoint = parkingSpot.getGeoPoint();
			address = "empty";//convertPointToLocation(pSpotGeoPoint);
			sPpotInfo = LocationUtility.convertObjToString(parkingSpot);
			//sPpotInfo = "empty string";
			overlayitem = new OverlayItem(pSpotGeoPoint, address, sPpotInfo);
			itemizedOverlays.addOverlay(overlayitem);
		}
		mapView.getOverlays().add(itemizedOverlays);
		// Toast.makeText(getBaseContext(), address, Toast.LENGTH_SHORT).show();
		mapView.postInvalidate();


	}

	// private String convertObjToString(ParkingLocationDataEntry parkingSpot) {
	// String pSpotInfo = Float.toString(parkingSpot.getLatitude()) + ","
	// + Float.toString(parkingSpot.getLongitude()) + ","
	// + parkingSpot.getMeterID();
	// return pSpotInfo;
	// }

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

	protected class GetLocationList extends AsyncTask<List<ParkingLocationDataEntry>, Void, Boolean >
	{

		@Override
		protected Boolean doInBackground(
				List<ParkingLocationDataEntry>... arg0) {
			Log.v( TAG, "doInBackground()" );

			/** Add your data */
			List<ParkingLocationDataEntry> nLocList = arg0[0];
			ParkingLocationDataEntry nLoc;

			Log.v( TAG, "doInBackground()" + nLocList.size());
			nLoc = nLocList.get(0);

			DataBaseHelper myDbHelper = new DataBaseHelper(myContext);
			//FindParkingTabs.parkingLocations = mParkingLocationsAll.getParkingLocations(2, 200, (float)32.71283, (float)-117.165695, myDbHelper);
			FindParkingTabs.parkingLocations = mParkingLocationsAll.getParkingLocations(2, 200, (float)nLoc.getLatitude(), (float)nLoc.getLongitude(), myDbHelper);
			Log.v(TAG, "NULLLLL" + FindParkingTabs.parkingLocations.size());

			overlayTappableParkingSpots();
			return true;
		}

		// -- gets called just before thread begins
		@Override
		protected void onPreExecute()
		{
			Log.v( TAG, "onPreExecute()" );
			super.onPreExecute();

		}

		// -- called if the cancel button is pressed
		@Override
		protected void onCancelled()
		{
			super.onCancelled();
			Log.i( TAG, "onCancelled()" );
		}

		// // -- called as soon as doInBackground method completes
		// // -- notice that the third param gets passed to this method
		// @Override
		// protected void onPostExecute( String result )
		// {
		// super.onPostExecute(result);
		// Log.v( TAG, "onPostExecute(): " + result );
		// }


	}
}
