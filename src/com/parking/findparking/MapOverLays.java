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

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.OverlayItem;
import com.parking.dashboard.activity.DashboardActivity;

public class MapOverLays extends ItemizedOverlay<OverlayItem> {

   private Context mContext;
   private static int maxNum = 10;
   private OverlayItem overlays[] = new OverlayItem[maxNum];
   private int index = 0;
   private boolean full = false;

   public MapOverLays(Drawable defaultMarker) {

      super(boundCenterBottom(defaultMarker));
   }

   public MapOverLays(Drawable defaultMarker, Context context) {

      super(boundCenterBottom(defaultMarker));
      mContext = context;
   }

   @Override
   protected OverlayItem createItem(int i) {
      return overlays[i];

   }

   @Override
   protected boolean onTap(int index) {

      
      OverlayItem item = overlays[index];
//      AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
//      dialog.setTitle(item.getTitle());
//      dialog.setMessage(item.getSnippet());
//      dialog.show();

      Intent pspotInfo = new Intent(DashboardActivity.myContext, ParkingSpotAndPaymentInformation.class);
      pspotInfo.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
      pspotInfo.putExtra("info", item.getSnippet());
      
      DashboardActivity.myContext.startActivity(pspotInfo);
      
      return true;
   }

   @Override
   public int size() {
      if (full) {
         return overlays.length;
      } else {
         return index;
      }

   }

   public void addOverlay(OverlayItem overlay) {
      if (index < maxNum) {
         overlays[index] = overlay;
      } else {
         index = 0;
         full = true;
         overlays[index] = overlay;
      }
      index++;
      populate();
   }

   void onFocusChanged(ItemizedOverlay overlay, OverlayItem newFocus){
      Toast.makeText(mContext, "onFocusChanged", Toast.LENGTH_LONG).show();
   }
   
}
