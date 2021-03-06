package com.parking.billing;

import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import com.parking.auth.AsyncTaskResultNotifierInterface;
import com.parking.utils.ParkingConstants;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

public class UpdateServerAsyncTask extends AsyncTask<List<BasicNameValuePair>, Void, Boolean> {

	DefaultHttpClient http_client = new DefaultHttpClient();
	private static final String TAG = UpdateServerAsyncTask.class.getSimpleName();
	private boolean result = false;
	private Context context;
	private AsyncTaskResultNotifierInterface notifierInterface;
	/** progress dialog to show user that the backup is processing. */
	private ProgressDialog dialog;
	
	private UpdateServerAsyncTask() {};
	
	public UpdateServerAsyncTask(AsyncTaskResultNotifierInterface notifier, Context context)
	{
		this.notifierInterface = notifier;
		this.context = context;
		dialog = new ProgressDialog(this.context);
	}
	
	protected void onPreExecute() {
		this.dialog.setMessage("Registering your payment..");
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
							"/submitPaymentServlet");

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
			Log.v(TAG,"Successfully submitted payment.");
		else
			Log.v(TAG,"Could not submit payment.");
		/** Notify launching activity of login result */
		this.notifierInterface.notifyResult(result);
	}

}
