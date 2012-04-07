package com.parking.findparking;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.parking.dashboard.R;
import com.parking.dashboard.activity.DashboardActivity;
import com.parking.datamanager.ParkingLocationDataEntry;

public class MyParkingArrayAdapter extends ArrayAdapter<ParkingLocationDataEntry>{
   
   int resource;
   
   public MyParkingArrayAdapter(Context context, int textViewResourceId, List<ParkingLocationDataEntry> objects) {
      super(context, textViewResourceId, objects);
      resource = textViewResourceId;
      
   }

   @Override
   public View getView(int position, View convertView, ViewGroup parent) {

      LinearLayout parkingSpotView = null; //new LinearLayout(DashboardActivity.myContext);
      ParkingLocationDataEntry parkingDataItem = getItem(position);
      
      if(convertView == null){
         
         parkingSpotView = new LinearLayout(DashboardActivity.myContext);
         LayoutInflater li = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
         li.inflate(resource, parkingSpotView, true);
         
      }else{
         
         parkingSpotView = (LinearLayout) convertView;
      }
      
      TextView addressText = (TextView) parkingSpotView.findViewById(R.id.parkingSpotListAddress);
      TextView infoText = (TextView) parkingSpotView.findViewById(R.id.parkingSpotListInfo1);
      
      addressText.setText("Dadar Mumbai 28");
      infoText.setText("1 mi");
      
      return parkingSpotView;
      
   }
   
   

}
