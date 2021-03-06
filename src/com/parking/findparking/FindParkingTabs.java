package com.parking.findparking;

import java.util.ArrayList;
import java.util.List;

import android.app.TabActivity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.widget.TabHost;
import android.widget.Toast;

import com.parking.dashboard.R;
import com.parking.dashboard.activity.DashboardActivity;
import com.parking.datamanager.ParkingLocationDataEntry;
import com.parking.findparking.FindParkingMapActivity;
import com.parking.utils.ParkingConstants;


public class FindParkingTabs extends TabActivity {

	public static List<ParkingLocationDataEntry> parkingLocations = new ArrayList<ParkingLocationDataEntry>();
	public static List<ParkingLocationDataEntry> allParkingLocations = new ArrayList<ParkingLocationDataEntry>();
	public static int zoomlevel=ParkingConstants.DEFAULT_ZOOM_LEVEL;
    public static boolean parkingListfilled = false;
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.map_list_tabs);

		/** Extract intent from the activity */
		Intent mapActivityIntent = getIntent();

		Resources res = getResources(); // Resource object to get Drawables
		TabHost tabHost = getTabHost(); // The activity TabHost
		TabHost.TabSpec spec; // Resusable TabSpec for each tab
		Intent intent; // Reusable Intent for each tab

		// Create an Intent to launch an Activity for the tab (to be reused)
		intent = new Intent().setClass(this, FindParkingMapActivity.class);
		if ((mapActivityIntent != null) && 
				(mapActivityIntent.hasExtra(
						ParkingConstants.ParkingMapIntentParameters.LOCATION_FIXED_TO_DOWNTOWN)))
			intent.putExtra(
					ParkingConstants.ParkingMapIntentParameters.LOCATION_FIXED_TO_DOWNTOWN,
					mapActivityIntent.getBooleanExtra(
							ParkingConstants.ParkingMapIntentParameters.LOCATION_FIXED_TO_DOWNTOWN, 
							false));
		// Initialize a TabSpec for each tab and add it to the TabHost
		spec = tabHost.newTabSpec("map").setIndicator("Map",
				res.getDrawable(R.drawable.location_map))
				.setContent(intent);
		tabHost.addTab(spec);

		// Do the same for the other tab
		intent = new Intent().setClass(this, FindParkingListActivity.class);   
		spec = tabHost.newTabSpec("list").setIndicator("List",
				res.getDrawable(R.drawable.tab_list))
				.setContent(intent);
		tabHost.addTab(spec);

		tabHost.setCurrentTab(0);
	}

	public void mapRefresh(View v){

		Toast.makeText(DashboardActivity.myContext, "Refreshing Map...", Toast.LENGTH_LONG).show();

	}

}
