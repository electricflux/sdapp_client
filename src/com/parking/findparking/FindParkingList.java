package com.parking.findparking;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.SearchManager.OnCancelListener;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ListView;

import com.google.android.maps.GeoPoint;
import com.parking.dashboard.R;
import com.parking.dashboard.activity.DashboardActivity;
import com.parking.datamanager.ParkingLocationDataEntry;
import com.parking.dbManager.DataBaseHelper;

public class FindParkingList extends Activity implements OnClickListener{
   
   private ArrayList<ParkingLocationDataEntry> parkingData = new ArrayList<ParkingLocationDataEntry>(); 
   private MyParkingArrayAdapter aa = null;
   
   class updateList extends AsyncTask<Context, Integer, Integer>{

      @Override
      protected Integer doInBackground(Context... arg0) {
         // TODO Auto-generated method stub
         return null;
      }
      
   }
   
   public void onCreate(Bundle savedInstanceState) {
      
      super.onCreate(savedInstanceState);
      setContentView(R.layout.parkingspotslistview);
   
      
      DataBaseHelper myDbHelper = new DataBaseHelper(DashboardActivity.myContext);
      myDbHelper.openDataBase(); 
      //temp dummy
      GeoPoint gp = new GeoPoint(0, 0);
      myDbHelper.dbquery(gp, parkingData);
      
      ListView psListView = (ListView) findViewById(R.id.pSlistView);
      int resID = R.layout.pspotlistrowlayout;
      aa = new MyParkingArrayAdapter(this, resID, parkingData);
      psListView.setAdapter(aa);
      
      
  }

   @Override
   public void onClick(View arg0) {
      // TODO Auto-generated method stub
      
   }

}