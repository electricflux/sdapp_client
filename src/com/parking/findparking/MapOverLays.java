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

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.OverlayItem;
import com.parking.billing.ParkingPayment;
import com.parking.dashboard.activity.DashboardActivity;

public class MapOverLays extends ItemizedOverlay<OverlayItem> {

	private static final String TAG = "MapOverLays";
	private Context mContext;
	private static int maxNum = 10;
	private ArrayList<OverlayItem> overlays ;
	private int index = 0;
	private boolean full = false;

	public MapOverLays(Drawable defaultMarker) {

		super(boundCenterBottom(defaultMarker));
	}

	public MapOverLays(Drawable defaultMarker, Context context) {

		super(boundCenterBottom(defaultMarker));
		overlays = new ArrayList<OverlayItem>();
		populate();
		mContext = context;
	}

	@Override
	protected OverlayItem createItem(int i) {
		return overlays.get(i);

	}
	@Override
	protected boolean onTap(int index) {
		OverlayItem item = overlays.get(index);
		Intent pspotInfo = new Intent(DashboardActivity.myContext, ParkingPayment.class); //ParkingSpotAndPaymentInformation.class);
		pspotInfo.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		pspotInfo.putExtra("info", item.getSnippet());
		Log.v(TAG,"Passing snippet: "+item.getSnippet());
		DashboardActivity.myContext.startActivity(pspotInfo);
		return true;
	}

	@Override
	public int size() {
		return overlays.size();	

	}

	public void addOverlay(OverlayItem overlay) {
		overlays.add(overlay);
		populate();
	}

	void onFocusChanged(ItemizedOverlay overlay, OverlayItem newFocus){
		Toast.makeText(mContext, "onFocusChanged", Toast.LENGTH_LONG).show();
	}

}
