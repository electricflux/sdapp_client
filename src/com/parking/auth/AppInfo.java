package com.parking.auth;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.DefaultHttpClient;

import com.parking.dashboard.R;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

public class AppInfo extends Activity {
	
	private static final String TAG = AppInfo.class.getSimpleName();
	private static final String ALL_SERVICES = "ah";
	
	DefaultHttpClient http_client = new DefaultHttpClient();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.appinfo);
	}

	@Override
	protected void onResume() {
		super.onResume();
		Intent intent = getIntent();
		AccountManager accountManager = AccountManager.get(getApplicationContext());
		Account account = (Account)intent.getExtras().get("account");
		accountManager.getAuthToken(account, ALL_SERVICES, false, new GetAuthTokenCallback(), null);
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
		new GetCookieTask().execute(auth_token);
	}

	private class GetCookieTask extends AsyncTask<String, Void, Boolean> {
		protected Boolean doInBackground(String... tokens) {
			try {
				/** Don't follow redirects */
				http_client.getParams().setBooleanParameter(ClientPNames.HANDLE_REDIRECTS, false);

				HttpGet http_get = new HttpGet("https://sdapp-parking.appspot.com/_ah/login?continue=http://localhost/&auth=" + tokens[0]);
				HttpResponse response;
				response = http_client.execute(http_get);
				Log.v(TAG,"Respone is "+response.getStatusLine().getStatusCode());
				
				if(response.getStatusLine().getStatusCode() != 302)
					return false;

				for(Cookie cookie : http_client.getCookieStore().getCookies()) {
					Log.v(TAG,"Cookie found: "+cookie.getExpiryDate().toLocaleString());
					Log.v(TAG,"Cookie found: "+cookie.getName());
					/** SACSID cookie because we are using https to authenticate with appspot */
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
	}
}