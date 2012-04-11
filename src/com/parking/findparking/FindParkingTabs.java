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


public class FindParkingTabs extends TabActivity {
	
	public static List<ParkingLocationDataEntry> parkingLocations = new ArrayList<ParkingLocationDataEntry>();

   public void onCreate(Bundle savedInstanceState) {

      super.onCreate(savedInstanceState);
      setContentView(R.layout.map_list_tabs);

      Resources res = getResources(); // Resource object to get Drawables
      TabHost tabHost = getTabHost(); // The activity TabHost
      TabHost.TabSpec spec; // Resusable TabSpec for each tab
      Intent intent; // Reusable Intent for each tab

      // Create an Intent to launch an Activity for the tab (to be reused)
      intent = new Intent().setClass(this, FindParkingMap.class);

      // Initialize a TabSpec for each tab and add it to the TabHost
      spec = tabHost.newTabSpec("map").setIndicator("Map",
            res.getDrawable(R.drawable.location_map))
            .setContent(intent);
      tabHost.addTab(spec);

      // Do the same for the other tab
      intent = new Intent().setClass(this, FindParkingList.class);   
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
