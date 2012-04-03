package com.parking.application;

import android.accounts.Account;
import android.app.Application;

public class ParkingApplication extends Application{
	
	private static Account accountLinkedToApplication = null;
	private static boolean userAuthenticated = false;
	private static String authToken = null;
	
	@Override
	public void onCreate()
	{
		super.onCreate();
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

}
