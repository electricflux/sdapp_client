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
import com.parking.utils.ParkingConstants;

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
		
		Log.v(TAG,"Get Location List executing with lat: "+nLoc.getLatitude() +
			  " and longitude : "+nLoc.getLongitude());

		DataBaseHelper myDbHelper = new DataBaseHelper(myContext);
		FindParkingTabs.parkingLocations = mParkingLocationsAll.getParkingLocations(
				ParkingConstants.DISTANCE_RADIUS, 
				ParkingConstants.MAX_NUM_POINTS, 
				(float)nLoc.getLatitude(), 
				(float)nLoc.getLongitude(), 
				myDbHelper);
		
		Log.v(TAG,"Got "+FindParkingTabs.parkingLocations.size()+" spots to render on map");
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
			Log.v(TAG, "Parked item size" + FindParkingTabs.parkingLocations.size());
			for (ParkingLocationDataEntry parkingSpot : FindParkingTabs.parkingLocations) {
				pSpotGeoPoint = parkingSpot.getGeoPoint();
				address = "empty";//convertPointToLocation(pSpotGeoPoint);
				sPpotInfo = LocationUtility.convertObjToString(parkingSpot);
				overlayitem = new OverlayItem(pSpotGeoPoint, address, sPpotInfo);
				itemizedOverlays.addOverlay(overlayitem);
			}
			mapView.getOverlays().add(itemizedOverlays);
			mapView.postInvalidate();
		}

	
}

