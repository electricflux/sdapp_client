package com.parking.findparking;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.maps.GeoPoint;
import com.parking.dashboard.R;
import com.parking.dashboard.activity.DashboardActivity;
import com.parking.datamanager.ParkingLocationDataEntry;
import com.parking.utils.LocationUtility;
import com.parking.utils.ParkingConstants;

public class MyParkingArrayAdapter extends ArrayAdapter<ParkingLocationDataEntry>{
   
   private int resource;
   private List<ParkingLocationDataEntry> parkingData = new ArrayList<ParkingLocationDataEntry>(); 
   private ParkingLocationDataEntry parkingSingle = new ParkingLocationDataEntry(); 
   
   public MyParkingArrayAdapter(Context context, int textViewResourceId, List<ParkingLocationDataEntry> objects) {
      super(context, textViewResourceId, objects);
      resource = textViewResourceId;
      this.parkingData = (List<ParkingLocationDataEntry>) objects;
      
      
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
      
      parkingSingle = parkingData.get(position);
      
      String address = "Address not found";
      Geocoder geoCoder = new Geocoder(DashboardActivity.myContext, Locale.getDefault());
      address = LocationUtility.ConvertPointToLocation(parkingSingle.getGeoPoint(), geoCoder);
      
      addressText.setText(address);
      infoText.setText(Float.toString(parkingSingle.getRate()));
      
      return parkingSpotView;
      
   }
   
   

}
