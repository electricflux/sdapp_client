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
import android.graphics.drawable.Drawable;

import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.OverlayItem;

public class MapOverLays extends ItemizedOverlay<OverlayItem> {

   private Context mContext;
   private static int maxNum = 3;
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
      AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
      dialog.setTitle(item.getTitle());
      dialog.setMessage(item.getSnippet());
      dialog.show();
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

}
