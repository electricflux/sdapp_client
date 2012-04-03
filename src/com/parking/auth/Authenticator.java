package com.parking.auth;

import android.content.Context;
import android.content.Intent;

import com.parking.application.ParkingApplication;

public class Authenticator {

	public static void authenticate(Context context)
	{
		if (ParkingApplication.getAccount() == null)
		{
			/** Launch account list activity */
			Intent intent = new Intent(context, GetAccountListActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(intent);
		}
		else
		{
			/** Launch application info activity */
			Intent intent = new Intent(context, GetAuthTokenActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(intent);
		}
	}
}
