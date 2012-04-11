package com.parking.application;

import com.parking.utils.AppPreferences;

import android.accounts.Account;
import android.app.Application;
import android.content.Context;
import android.telephony.TelephonyManager;

public class ParkingApplication extends Application{
	
	private static Account accountLinkedToApplication = null;
	private static boolean userAuthenticated = false;
	private static String authToken = null;
	private static String deviceId = null;
	
	@Override
	public void onCreate()
	{
		super.onCreate();
		TelephonyManager tManager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
		deviceId = tManager.getDeviceId();
		AppPreferences.initialize(getApplicationContext());
	}
	
	public static void setAccount(Account account)
	{
		accountLinkedToApplication = account;
	}
	
	public static boolean isUserAuthenticated() {
		return userAuthenticated;
	}

	public static void setUserAuthenticated(boolean userAuthenticated) {
		ParkingApplication.userAuthenticated = userAuthenticated;
	}

	public static Account getAccount()
	{
		return accountLinkedToApplication;
	}

	public static void setAuthToken(String authToken) {
		ParkingApplication.authToken  = authToken;
	}
	
	public static String getAuthToken()
	{
		return authToken;
	}

	public static String getDeviceId() {
		return deviceId;
	}
}
