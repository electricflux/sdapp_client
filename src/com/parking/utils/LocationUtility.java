package com.parking.utils;

import java.io.IOException;
import java.util.List;

import android.location.Address;
import android.location.Geocoder;
import android.util.Log;

import com.google.android.maps.GeoPoint;
import com.parking.datamanager.ParkingLocationDataEntry;

public class LocationUtility {

   public static String convertObjToString(ParkingLocationDataEntry parkingSpot) {
      String pSpotInfo = 
         (parkingSpot.getId() == null ? 0 : parkingSpot.getId()) + ":"
       + parkingSpot.getAddress() + ":"
       + (parkingSpot.getMeterID() == null ? 0 : parkingSpot.getMeterID()) + ":"
       + parkingSpot.getAttendent() + ":"
       + parkingSpot.getContact() + ":"
       + (parkingSpot.getDuration() == 0 ? 0 : parkingSpot.getDuration()) + ":"
       + parkingSpot.getParkingType() + ":"
       + parkingSpot.getQuantity() + ":"
       + (parkingSpot.getRate()  == 0 ? 0 : parkingSpot.getRate()) + ":"
       + parkingSpot.getType() + ":"
       + Float.toString(parkingSpot.getLatitude()) + ":" 
       + Float.toString(parkingSpot.getLongitude()); 
      return pSpotInfo;
   }

   public static ParkingLocationDataEntry convertStringToObject(String sParkingSpot) {
   
      //Do not change the order! Look at the method above.
      
      //TODO - 
      //NULL Checks!
      //Convert between type and ordinal!!
      
      ParkingLocationDataEntry parkingObj = new ParkingLocationDataEntry();
      
      String[] parkingSpotValues = sParkingSpot.split(":");
      
      parkingObj.setId(parkingSpotValues[0] == "null" ? 0 : Long.parseLong(parkingSpotValues[0]));
      parkingObj.setAddress(parkingSpotValues[1]);
      parkingObj.setMeterID(parkingSpotValues[2] == "null" ? 0 : Long.parseLong(parkingSpotValues[2]));
      parkingObj.setAttendent(parkingSpotValues[3]);
      parkingObj.setContact(parkingSpotValues[4]);
      parkingObj.setDuration(parkingSpotValues[5] == "null" ? 0 : Integer.parseInt(parkingSpotValues[5]));
      //TODO!
      //parkingObj.setParkingType(parkingSpotValues[6]);
      parkingObj.setQuantity(parkingSpotValues[7] == "null" ? 0 : Integer.parseInt(parkingSpotValues[7]));
      parkingObj.setRate(parkingSpotValues[8] == "null" ? 0 : Integer.parseInt(parkingSpotValues[8]));
      parkingObj.setType(parkingSpotValues[9] == "null" ? 0 : Integer.parseInt(parkingSpotValues[9]));
      parkingObj.setLatitude(parkingSpotValues[10] == "null" ? 0 : Float.parseFloat(parkingSpotValues[10])); 
      parkingObj.setLongitude(parkingSpotValues[11] == "null" ? 0 : Float.parseFloat(parkingSpotValues[11]));
      
      return parkingObj;
   }

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
