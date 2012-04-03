package com.parking.auth;

import org.apache.http.impl.client.DefaultHttpClient;

import com.parking.application.ParkingApplication;
import com.parking.dashboard.R;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class GetAuthTokenActivity extends Activity implements AsyncTaskResultNotifierInterface{
	
	private static final String TAG = GetAuthTokenActivity.class.getSimpleName();
	private static final String ALL_SERVICES = "ah";
	private static final int RegisterActivityIdentifier = 55;
	
	DefaultHttpClient http_client = new DefaultHttpClient();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.appinfo);
	}

	@Override
	protected void onResume() {
		super.onResume();
		AccountManager accountManager = AccountManager.get(getApplicationContext());
		Account account = ParkingApplication.getAccount();
		accountManager.getAuthToken(account, 
				ALL_SERVICES, false, new GetAuthTokenCallback(), null);
	}

	private class GetAuthTokenCallback implements AccountManagerCallback<Bundle> {
		public void run(AccountManagerFuture<Bundle> result) {
			Bundle bundle;
			try {
				bundle = result.getResult();
				Intent intent = (Intent)bundle.get(AccountManager.KEY_INTENT);
				if(intent != null) {
					/** User input required */
					startActivity(intent);
				} else {
					onGetAuthToken(bundle);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	};

	protected void onGetAuthToken(Bundle bundle) {
		String auth_token = bundle.getString(AccountManager.KEY_AUTHTOKEN);
		Log.v(TAG,"Got auth token: "+auth_token);
		ParkingApplication.setAuthToken(auth_token);
		/**
		 * Verify if the user is registered
		 */
		new LoginUserAsyncTask(this,this).execute("");
		/**new GetCookieTask().execute(auth_token);*/
	}

	@Override
	public void notifyResult(boolean result) {
		ParkingApplication.setUserAuthenticated(result);
		if (result == true)
		{
			finish();
		}
		else
		{
			/** Launch registration activity here */
			Intent intent = new Intent(this.getBaseContext(), RegisterUserActivity.class);
			startActivityForResult(intent, RegisterActivityIdentifier);
		}
	}
	
	@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.v(TAG, "Got activity result " + resultCode); 
        /** Registration was successful. Kill this activity. */
        if (requestCode == RegisterActivityIdentifier)
        	finish();
    }

	/**
	 * Unused code as of now
	 */
/*	private class GetCookieTask extends AsyncTask<String, Void, Boolean> {
		protected Boolean doInBackground(String... tokens) {
			try {
				*//** Don't follow redirects *//*
				http_client.getParams().setBooleanParameter(ClientPNames.HANDLE_REDIRECTS, false);

				HttpGet http_get = 
						new HttpGet(
								ParkingConstants.serverUrl + 
								"/_ah/login?continue=http://localhost/&auth=" + 
								tokens[0]);
				HttpResponse response;
				response = http_client.execute(http_get);
				Log.v(TAG,"Respone is "+response.getStatusLine().getStatusCode());
				
				if(response.getStatusLine().getStatusCode() != 302)
					return false;

				for(Cookie cookie : http_client.getCookieStore().getCookies()) {
					Log.v(TAG,"Cookie found: "+cookie.getExpiryDate().toLocaleString());
					Log.v(TAG,"Cookie found: "+cookie.getName());
					*//** SACSID cookie because we are using https to authenticate with appspot *//*
					if(cookie.getName().equals("SACSID"))
					{
						return true;
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				http_client.getParams().setBooleanParameter(ClientPNames.HANDLE_REDIRECTS, true);
			}
			return false;
		}

		protected void onPostExecute(Boolean result) {
			if (result == true)
				Log.v(TAG,"Successfully authenticated and found cookie.");
			else
				Log.v(TAG,"Could not find the cookie.");
		}
	}*/
}