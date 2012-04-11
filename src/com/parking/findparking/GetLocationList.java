package com.parking.findparking;

import java.util.List;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.google.android.maps.OverlayItem;
import com.parking.auth.AsyncTaskResultNotifierInterface;
import com.parking.dashboard.R;
import com.parking.datamanager.ParkingLocationDataEntry;
import com.parking.datamanager.ParkingLocationsAll;
import com.parking.dbManager.DataBaseHelper;
import com.parking.utils.LocationUtility;

public class GetLocationList extends AsyncTask<List<ParkingLocationDataEntry>, Void, Boolean >
{
	private static final String TAG = GetLocationList.class.getSimpleName();
	private static Context myContext  = null;
	private ParkingLocationsAll mParkingLocationsAll = new ParkingLocationsAll();
	private static MapOverLays itemizedOverlays;
	private MapView mapView = null;
	private AsyncTaskResultNotifierInterface notifier = null;
	
	public GetLocationList(Context context, MapView mapView,  AsyncTaskResultNotifierInterface notifier)
	{
		myContext = context;
		this.mapView = mapView;
		this.notifier = notifier;
	}

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
		FindParkingTabs.parkingLocations = mParkingLocationsAll.getParkingLocations(50, 200, (float)nLoc.getLatitude(), (float)nLoc.getLongitude(), myDbHelper);
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
	 @Override
	 protected void onPostExecute( Boolean result )
	 {
		 super.onPostExecute(result);
		 Log.v( TAG, "onPostExecute(): " + result );
		 notifier.notifyResult(result);
	 }
	 
	 private void overlayTappableParkingSpots() {

			String address = "No Associated Address";
			GeoPoint pSpotGeoPoint = null;
			OverlayItem overlayitem = null;
			String sPpotInfo = "No Info Available";
			Drawable drawable = myContext.getResources().getDrawable(
					R.drawable.map_marker_blue);// map_marker_black);
			itemizedOverlays = new MapOverLays(drawable, myContext);

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

	
}

