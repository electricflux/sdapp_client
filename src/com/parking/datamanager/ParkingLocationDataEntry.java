package com.parking.datamanager;

import com.google.android.maps.GeoPoint;

public class ParkingLocationDataEntry {

   private enum ParkingType {
      PRIVATELOT, MULTISPOT, STREET
   };

   // PrimaryKey
   private Long id;

   // Persistent
   private float latitude;

   // Persistent
   private float longitude;

   //parkingType
   private ParkingType parkingType;

   // Persistent
   private int rate;

   // Persistent
   private int duration;

   // Persistent
   private int type;

   // Persistent
   private int quantity;

   // Persistent
   private String address;

   // Persistent
   private String attendent;

   // Persistent
   private String contact;

   //Geopoint to simplify display
   private GeoPoint gpoint;
   
   public void setid(Long i) {
      id = i;
   }

   public void setlatitude(float lat) {
      latitude = lat;
   }

   public void setlongitude(float lon) {
      longitude = lon;
   }

   public Long getid() {
      return id;
   }

   public float getlatitude() {
      return latitude;
   }

   public float getlongitude() {
      return longitude;
   }

}