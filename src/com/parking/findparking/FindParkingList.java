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

import java.util.ArrayList;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ListView;

import com.parking.dashboard.R;
import com.parking.datamanager.ParkingLocationDataEntry;

public class FindParkingList extends Activity{
   
   private ArrayList<ParkingLocationDataEntry> parkingData = new ArrayList<ParkingLocationDataEntry>(); 
   private MyParkingArrayAdapter aa = null;
   
   public void onCreate(Bundle savedInstanceState) {
      
      super.onCreate(savedInstanceState);
      setContentView(R.layout.parkingspotslistview);
   
      ParkingLocationDataEntry test = new ParkingLocationDataEntry();
      test.setMeterID(100L);
      parkingData.add(test);
      
      ListView psListView = (ListView) findViewById(R.id.pSlistView);
      int resID = R.layout.pspotlistrowlayout;
      aa = new MyParkingArrayAdapter(this, resID, parkingData);
      psListView.setAdapter(aa);
      
      
  }

}