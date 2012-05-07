package com.parking.utils;

public class ParkingConstants {

	public static final String serverUrl = "https://sdapp-parking.appspot.com";
	public static final String TAG = "ParkingApp";
	public static final String MAP_LOCATION_UPDATE_INTENT = "com.parking.location.MAP_LOCATION_UPDATE_INTENT";
	public static final String PASSIVE_PROVIDER_PENDING_INTENT = "com.parking.location.PASSIVE_PROVIDER_PENDING_INTENT";
	public static final String PARKING_APP_DIRECOTRY = "easyPark";

	public static final class ParkingPaymentIntentParameters {
		public static final String amountPaid = "amountPaid";
		public static final String startTimestampMs = "startTimestampMs";
		public static final String endTimestampMs = "endTimestampMs";
		public static final String parkingSpotId ="parkingSpotId";
	}

	public static final class ParkingMapIntentParameters {
		public static final String LOCATION_FIXED_TO_DOWNTOWN = 
				"locationFixedToDowntown";
	}

	public static final float DOWNTOWN_FIXED_LATITUDE = 32.71283f;
	public static final float DOWNTOWN_FIXED_LONGITUDE = -117.165695f;
	public static final int DISTANCE_RADIUS = 5;
	public static final int MAX_NUM_POINTS = 300;
	public static final int DEFAULT_ZOOM_LEVEL = 17;

}