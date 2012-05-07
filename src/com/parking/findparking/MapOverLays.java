package com.parking.findparking;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapView;
import com.google.android.maps.OverlayItem;
import com.parking.billing.ParkingPayment;
import com.parking.dashboard.activity.DashboardActivity;

public class MapOverLays extends ItemizedOverlay<OverlayItem> {

	private static final String TAG = "MapOverLays";
	private Context mContext;
	private OnTapListener mTapListener;
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

	// ------------------------------------------------------------------------
	// LISTENER DEFINITIONS
	// ------------------------------------------------------------------------
	
	// Tap listener
	public interface OnTapListener
	{
		public void onTap(MapView v, GeoPoint geoPoint);
	}
	
	// Setters
	public void setOnTapListener(OnTapListener listener)
	{
		Log.v(TAG, "setting listener");
		mTapListener = listener;
	}

	// ------------------------------------------------------------------------
	// MEMBERS
	// ------------------------------------------------------------------------
	
	
	
	// ------------------------------------------------------------------------
	// EVENT HANDLERS
	// ------------------------------------------------------------------------
	/*
	@Override
	public boolean onTap(GeoPoint geoPoint, MapView mapView)
	{
		mTapListener.onTap(mapView, geoPoint);
		return super.onTap(geoPoint, mapView);
	}
*/
	
	@Override
	protected OverlayItem createItem(int i) {
		return overlays.get(i);

	}
	
	@Override
	protected boolean onTap(int index) {
		OverlayItem item = overlays.get(index);
		Intent pspotInfo = new Intent(mContext, ParkingSpotSelector.class); //ParkingSpotAndPaymentInformation.class);
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
		
		//populate();
	}
	
	public void addDone(){
		
		populate();
		setLastFocusedIndex(-1);
	}

	void onFocusChanged(ItemizedOverlay overlay, OverlayItem newFocus){
		Toast.makeText(mContext, "onFocusChanged", Toast.LENGTH_LONG).show();
	}

}
