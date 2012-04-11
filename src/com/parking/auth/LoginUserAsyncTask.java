package com.parking.auth;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.parking.application.ParkingApplication;
import com.parking.utils.AppPreferences;
import com.parking.utils.ParkingConstants;
import com.sdapp.domain.json.LicensePlateJsonObject;

public class LoginUserAsyncTask extends AsyncTask<String, Void, Boolean> {


	DefaultHttpClient http_client = new DefaultHttpClient();
	private static final String TAG = LoginUserAsyncTask.class.getSimpleName();
	private boolean result = false;
	private AsyncTaskResultNotifierInterface notifier = null;
	/** progress dialog to show user that the backup is processing. */
	private ProgressDialog dialog;
	private Context context;

	private LoginUserAsyncTask() {};

	public LoginUserAsyncTask (AsyncTaskResultNotifierInterface loginNotifier, Context context)
	{
		this.notifier = loginNotifier;
		this.context = context;
		dialog = new ProgressDialog(this.context);
	}

	protected void onPreExecute() {
		this.dialog.setMessage("Authenticating...");
		this.dialog.show();
	}

	protected Boolean doInBackground(String... tokens) {
		try {
			/** Don't follow redirects */
			http_client.getParams().setBooleanParameter(ClientPNames.HANDLE_REDIRECTS, true);

			HttpPost httppost = 
					new HttpPost(
							ParkingConstants.serverUrl + 
							"/loginServlet");

			/** Add your data */
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
			nameValuePairs.add(new BasicNameValuePair("username", 
					ParkingApplication.getAccount().name));
			nameValuePairs.add(new BasicNameValuePair("isDevice", 
					"true"));
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

			HttpResponse response;
			response = http_client.execute(httppost);
			Log.v(TAG,"Respone is "+response.getStatusLine().getStatusCode());
			Log.v(TAG,response.getStatusLine().toString());

			if(response.getStatusLine().getStatusCode() == 200)
			{
				/** Extract JSON user message from user response */
				ObjectMapper objectMapper = new ObjectMapper();
				LicensePlateJsonObject licensePlate = 
						objectMapper.readValue(
								response.getEntity().getContent(), LicensePlateJsonObject.class);
				result = true;
				AppPreferences.getInstance().setLicensePlateString(
						licensePlate.getLicensePlateList().toUpperCase());
			}

		} catch (Exception e) {
			e.printStackTrace();
			result = false;
		} finally {
			http_client.getParams().setBooleanParameter(ClientPNames.HANDLE_REDIRECTS, true);
		}
		return result;
	}

	protected void onPostExecute(Boolean result) {

		if (dialog.isShowing())
			dialog.dismiss();

		if (result == true)
			Log.v(TAG,"Successfully authenticated.");
		else
			Log.v(TAG,"Could not authenticate.");
		/** Notify launching activity of login result */
		this.notifier.notifyResult(result);
	}
}
