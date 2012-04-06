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
import java.util.List;

import android.database.Cursor;
import android.database.DataSetObserver;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.RectF;
import android.util.Log;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.Projection;
import com.parking.datamanager.ParkingLocationDataEntry;

public class NearbyParkingSpotsOverlay extends Overlay{

   private static final String TAG = "ParkingApp";
   private List<ParkingLocationDataEntry> parkingLocationsList = null;
   
   
   public NearbyParkingSpotsOverlay(List<ParkingLocationDataEntry> parkingLocations){
      super();
      parkingLocationsList = parkingLocations;
           
   }
   
   @Override
   public void draw(Canvas canvas, MapView mapView, boolean shadow) {
      int radius = 5;
      Projection projection = mapView.getProjection();
      //super.draw(arg0, mapView, arg2);
      
      //Create and setup a paintbrush
      Paint paint = new Paint();
      paint.setARGB(250, 255, 0, 0);
      paint.setAntiAlias(true);
      paint.setFakeBoldText(true);
      
      if(shadow == true)
      {
         //Draw all the nearby spots
         for(ParkingLocationDataEntry pSpotInfo : parkingLocationsList)
         {
            Point myPoint = new Point();
            projection.toPixels(pSpotInfo.getGeoPoint(), myPoint);
            
            RectF oval = new RectF( myPoint.x - radius, myPoint.y - radius, 
                                    myPoint.x + radius, myPoint.y + radius);
            
            canvas.drawOval(oval, paint);
            
         }
         
      }
   }

   @Override
   public boolean onTap(GeoPoint arg0, MapView arg1) {
      return super.onTap(arg0, arg1);
      
   }
   

}
