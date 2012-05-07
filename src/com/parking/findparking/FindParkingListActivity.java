package com.parking.findparking;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.OverlayItem;
import com.parking.billing.ParkingPayment;
import com.parking.dashboard.R;
import com.parking.dashboard.activity.DashboardActivity;
import com.parking.datamanager.ParkingLocationDataEntry;
import com.parking.dbManager.DataBaseHelper;
import com.parking.utils.LocationUtility;

public class FindParkingListActivity extends Activity{
   
   private static final String TAG = FindParkingListActivity.class.getSimpleName();
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
   
      
//      DataBaseHelper myDbHelper = new DataBaseHelper(DashboardActivity.myContext);
//      myDbHelper.openDataBase(); 
      //temp dummy
      //GeoPoint gp = new GeoPoint(0, 0);
      //myDbHelper.dbquery(gp, FindParkingTabs.parkingLocations);
      
      ListView psListView = (ListView) findViewById(R.id.pSlistView);
      psListView.setTextFilterEnabled(true);
      int resID = R.layout.pspotlistrowlayout;
      aa = new MyParkingArrayAdapter(this, resID, FindParkingTabs.parkingLocations);
      psListView.setAdapter(aa);
      psListView.setOnItemClickListener(new android.widget.AdapterView.OnItemClickListener() {
    	    public void onItemClick(AdapterView<?> parent, View view,
    	        int position, long id) {
    	      // When clicked, show a toast with the TextView text
    	    	//Toast.makeText(this, "Functionality coming soon" , Toast.LENGTH_SHORT).show();
    	    	Log.v( TAG, "List item clicked" );
    	    	ParkingLocationDataEntry item = aa.getItem(position); 
    	    	String parkingStrObj = LocationUtility.convertObjToString(item);
    			Intent pspotInfo = new Intent(getBaseContext(), ParkingSpotSelector.class); //ParkingSpotAndPaymentInformation.class);
    			pspotInfo.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    			pspotInfo.putExtra("info", parkingStrObj);
    			Log.v(TAG,"Passing snippet: "+ parkingStrObj);
    			DashboardActivity.myContext.startActivity(pspotInfo);


    	          
    	    }
    	  });   
      
  }

   


}