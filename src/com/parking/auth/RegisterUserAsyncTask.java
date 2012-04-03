package com.parking.auth;

import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import com.parking.utils.ParkingConstants;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

public class RegisterUserAsyncTask extends AsyncTask<List<BasicNameValuePair>, Void, Boolean> {

	DefaultHttpClient http_client = new DefaultHttpClient();
	private static final String TAG = LoginUserAsyncTask.class.getSimpleName();
	private boolean result = false;
	private Context context;
	private AsyncTaskResultNotifierInterface notifierInterface;
	/** progress dialog to show user that the backup is processing. */
	private ProgressDialog dialog;
	
	private RegisterUserAsyncTask() {};
	
	public RegisterUserAsyncTask(AsyncTaskResultNotifierInterface notifier, Context context)
	{
		this.notifierInterface = notifier;
		this.context = context;
		dialog = new ProgressDialog(this.context);
	}
	
	protected void onPreExecute() {
		this.dialog.setMessage("Processing registration...");
		this.dialog.show();
	}
	
	@Override
	protected Boolean doInBackground(List<BasicNameValuePair>... arg0) {
		try {
			/** Don't follow redirects */
			http_client.getParams().setBooleanParameter(ClientPNames.HANDLE_REDIRECTS, false);

			HttpPost httppost = 
					new HttpPost(
							ParkingConstants.serverUrl + 
							"/registerServlet");

			/** Add your data */
			List<BasicNameValuePair> nameValuePairs = arg0[0];
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

			HttpResponse response;
			response = http_client.execute(httppost);
			Log.v(TAG,"Respone is "+response.getStatusLine().getStatusCode());

			if(response.getStatusLine().getStatusCode() == 200)
				result = true;

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
			Log.v(TAG,"Successfully registered.");
		else
			Log.v(TAG,"Could not register.");
		/** Notify launching activity of login result */
		this.notifierInterface.notifyResult(result);
	}

}
