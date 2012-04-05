package com.parking.utils;

import java.io.IOException;
import java.util.List;

import android.location.Address;
import android.location.Geocoder;
import android.util.Log;

import com.google.android.maps.GeoPoint;

public class LocationUtility {

   public static String ConvertPointToLocation(GeoPoint point, Geocoder geoCoder) {
      String address = "";
      try {
         List<Address> addresses = geoCoder.getFromLocation(point.getLatitudeE6() / 1E6, point.getLongitudeE6() / 1E6, 1);
         if (addresses.size() > 0) {
            for (int index = 0; index < addresses.get(0).getMaxAddressLineIndex(); index++)
               {
                  address += addresses.get(0).getAddressLine(index) + " ";
                  Log.e(ParkingConstants.TAG, "address: "+address);
               }
            
            
         }
      }

      catch (IOException e) {
         e.printStackTrace();
      }
      return address;
   }

   
}
