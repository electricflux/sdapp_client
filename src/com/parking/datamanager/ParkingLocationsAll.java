package com.parking.datamanager;

import java.util.LinkedList;
import java.util.List;

import android.database.SQLException;
import android.util.Log;

import com.parking.datamanager.ParkingLocationDataEntry;
import com.parking.dbManager.DataBaseHelper;

public class ParkingLocationsAll {

	private static final String TAG = "ParkingLocationsAll";
	private List<ParkingLocationDataEntry> parkingLocations = new LinkedList<ParkingLocationDataEntry>();

	public List<ParkingLocationDataEntry>  getParkingLocations(int limit, DataBaseHelper myDbHelper){
		try {

			myDbHelper.openDataBase();
			myDbHelper.dbquery(limit, parkingLocations);
			myDbHelper.close();

		} catch (SQLException sqle) {

			throw sqle;

		}
		int i = 0;
		ParkingLocationDataEntry mParkingLocationDataEntry = new ParkingLocationDataEntry();
		if (parkingLocations != null) {
			/* Check if at least one Result was returned. */
			while (i < limit) {
				mParkingLocationDataEntry = parkingLocations.get(i);
				i++;
				/* Loop through all Results */
				/* Retrieve the values of the Entry
				 * the Cursor is pointing to. */
				Long Id =  mParkingLocationDataEntry.getId();
				float Lat = mParkingLocationDataEntry.getLatitude();
				float Lon = mParkingLocationDataEntry.getLongitude();
				Log.v(TAG, i + "ParkingLocationsAll :: " + Id + " Lat:: " + Lat + " Lon:: " + Lon);

			}
		}
		return parkingLocations;
	}

}
