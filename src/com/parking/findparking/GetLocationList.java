package com.parking.findparking;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import android.app.ProgressDialog;
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
	private ProgressDialog dialog;
	
	
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

		/** First time populate the list allParkingLocations and provide the subset
		 * Subsequent times only sort the list based on distance and return */
		if (FindParkingTabs.parkingListfilled == false) {
		
		DataBaseHelper myDbHelper = new DataBaseHelper(myContext);
		FindParkingTabs.allParkingLocations=mParkingLocationsAll.getAllParkingLocations(myDbHelper);
		/*
		FindParkingTabs.parkingLocations = mParkingLocationsAll.getParkingLocations(
				ParkingConstants.DISTANCE_RADIUS, 
				ParkingConstants.MAX_NUM_POINTS, 
				(float)nLoc.getLatitude(), 
				(float)nLoc.getLongitude(), 
				myDbHelper);
		*/
		Log.v(TAG,"Got "+FindParkingTabs.allParkingLocations.size()+" spots");
		FindParkingTabs.parkingListfilled = true;
		}
		
		List<ParkingLocationDataEntry> nLocList = arg0[0];
		ParkingLocationDataEntry nLoc;
		Log.v( TAG, "doInBackground()" + nLocList.size());
		nLoc = nLocList.get(0);
		
		Log.v(TAG,"Get Location List executing with lat: "+nLoc.getLatitude() +
			  " and longitude : "+nLoc.getLongitude());
		
		parkingListSort(nLoc);
		
		return true;
	}

	
	public void parkingListSort (ParkingLocationDataEntry nLoc){
		double dist;
		float currentLat = nLoc.getLatitude();
		float currentLon = nLoc.getLongitude();
		List<ParkingLocationDataEntry> tempParkingSet = new ArrayList<ParkingLocationDataEntry>();
		FindParkingTabs.parkingLocations.clear();
		Log.v(TAG,"ParkingListSort from total spots" + FindParkingTabs.allParkingLocations.size());
		for (ParkingLocationDataEntry parkingSpot : FindParkingTabs.allParkingLocations) {
			
			float dblat = parkingSpot.getLatitude();
			float dblon = parkingSpot.getLongitude();
			dist = distance((double)dblat, (double)dblon,currentLat,currentLon);
			parkingSpot.setDistance(dist);
			
			if (dist <= ParkingConstants.DISTANCE_RADIUS) {
				tempParkingSet.add(parkingSpot);
			}
		}
			 ParkingLocationDataEntry[] arrayOfLocation = tempParkingSet.toArray(new ParkingLocationDataEntry[]{});
		       
		        Arrays.sort(arrayOfLocation, new DistanceComparator());
		        int count=0;
		        for (int i = 0; i < arrayOfLocation.length; i++)
		        {
					if (count >= ParkingConstants.MAX_NUM_POINTS )
		        		break;
		        	FindParkingTabs.parkingLocations.add(arrayOfLocation[i]);
		        	count++;
		        }	
					       	
	}
	//Below functions are helper functions to calculate distance
	private double distance(double lat1, double lon1, double lat2, double lon2) {
	      double theta = lon1 - lon2;
	      double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));
	      dist = Math.acos(dist);
	      dist = rad2deg(dist);
	      dist = dist * 60 * 1.1515;
	       return (dist);
	    }
	   private double deg2rad(double deg) {
		      return (deg * Math.PI / 180.0);
		    }
		   private double rad2deg(double rad) {
		      return (rad * 180.0 / Math.PI);
		    }


	
	// -- gets called just before thread begins
	@Override
	protected void onPreExecute()
	{
		//this.dialog.setMessage("Progress start");
        //this.dialog.show();	
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
		 //if (dialog.isShowing()) {
          //   dialog.dismiss();
        // }
		 super.onPostExecute(result);
		 Log.v( TAG, "onPostExecute(): " + result );
		 notifier.notifyResult(result);
	 }
	 
		
}
class DistanceComparator implements Comparator<ParkingLocationDataEntry> {

	   @Override
	   public int compare(ParkingLocationDataEntry obj1, ParkingLocationDataEntry obj2) {

	      /*
	       * parameter are of type Object, so we have to downcast it
	       * to Employee objects
	       */

	      double dist1 = obj1.getDistance();
	      double dist2 = obj2.getDistance();

	      if (dist1 > dist2)
	         return 1;
	      else if (dist1 < dist2)
	         return -1;
	      else
	         return 0;
	   }

	}

