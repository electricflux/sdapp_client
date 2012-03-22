/*
 * Copyright © 2011 QUALCOMM Incorporated. All rights reserved.
 *
 * This software is the confidential and proprietary information of
 * QUALCOMM Incorporated ("Proprietary Information"). You shall not
 * disclose such Proprietary Information, and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with QUALCOMM Incorporated.
 */
package com.parking.findparking;

import android.app.TabActivity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.widget.TabHost;

import com.parking.dashboard.R;

public class FindParkingTabs extends TabActivity {

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
            res.getDrawable(R.drawable.ic_tab_artists))
            .setContent(intent);
      tabHost.addTab(spec);

      // Do the same for the other tab
      intent = new Intent().setClass(this, FindParkingList.class);
      spec = tabHost.newTabSpec("list").setIndicator("List",
            res.getDrawable(R.drawable.ic_tab_artists))
            .setContent(intent);
      tabHost.addTab(spec);

      tabHost.setCurrentTab(0);
   }
}
