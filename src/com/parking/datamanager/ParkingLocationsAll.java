package com.parking.datamanager;

import java.util.LinkedList;
import java.util.List;

import android.database.SQLException;

import com.parking.datamanager.ParkingLocationDataEntry;
import com.parking.dbManager.DataBaseHelper;

public class ParkingLocationsAll {

	private List<ParkingLocationDataEntry> parkingLocations = new LinkedList<ParkingLocationDataEntry>();

	public List<ParkingLocationDataEntry>  getAllParkingLocations(DataBaseHelper myDbHelper)
	{
		try {

			myDbHelper.openDataBase();
			myDbHelper.dbquery(parkingLocations);
			myDbHelper.close();

		} catch (SQLException sqle) {
			sqle.printStackTrace();
		}
		return parkingLocations;
	}

	public List<ParkingLocationDataEntry> getParkingLocations(int distance, int limit, float mlat, float mlon,
			DataBaseHelper myDbHelper) {

		try {
			myDbHelper.openDataBase();
			myDbHelper.dbquery(distance, mlat, mlon, limit, parkingLocations);
			myDbHelper.close();
		} catch (SQLException sqle) {
			sqle.printStackTrace();
		}
		return parkingLocations;
	}

}
