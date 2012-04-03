package com.parking.auth;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.message.BasicNameValuePair;

import com.parking.application.ParkingApplication;
import com.parking.dashboard.R;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class RegisterUserActivity extends Activity implements AsyncTaskResultNotifierInterface {
	
	private static final String TAG = RegisterUserActivity.class.getSimpleName();
	private EditText userNameText = null;
	private EditText fullNameText = null;
	private EditText licensePlateNumbersText = null;
	private Button clearButton = null;
	private Button submitButton = null;
	
	private String deviceId;
	
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.register_user);
		
		TelephonyManager tManager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
		deviceId = tManager.getDeviceId();
		
		userNameText = (EditText) findViewById(R.id.nUsername);
		userNameText.setText(ParkingApplication.getAccount().name);
		userNameText.setEnabled(false);
		
		fullNameText = (EditText) findViewById(R.id.nFullName);
		licensePlateNumbersText = (EditText) findViewById(R.id.nLicensePlates);
		
		clearButton = (Button) findViewById(R.id.nClear);
		clearButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				userNameText.setText("");
				fullNameText.setText("");
				licensePlateNumbersText.setText("");
			}
		});
		
		submitButton = (Button) findViewById(R.id.nRegister);
		submitButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				List<BasicNameValuePair> nameValuePairs = new ArrayList<BasicNameValuePair>();
				nameValuePairs.add(new BasicNameValuePair("username", 
						ParkingApplication.getAccount().name));
				nameValuePairs.add(new BasicNameValuePair("deviceId", 
						deviceId));
				nameValuePairs.add(new BasicNameValuePair("authToken", 
						ParkingApplication.getAuthToken()));
				nameValuePairs.add(new BasicNameValuePair("isDevice", 
						"true"));
				nameValuePairs.add(new BasicNameValuePair("license", 
						licensePlateNumbersText.getText().toString()));
				new RegisterUserAsyncTask(
						RegisterUserActivity.this,RegisterUserActivity.this).
						execute(nameValuePairs);
			}
		});
	}
	
	public void onResume()
	{
		super.onResume();
	}

	@Override
	public void notifyResult(boolean result) {
		if (result == true)
		{
			Log.v(TAG,"User registration successful.");
			/** 
			 * Send the result back. This way using the result, we can kill the 
			 * previous activity on the stack.
			 */
			if (getParent() == null) {
			    setResult(Activity.RESULT_OK, this.getIntent());
			} else {
			    getParent().setResult(Activity.RESULT_OK, this.getIntent());
			}
			finish();
		}
		else
		{
			clearButton.performClick();
		}
		
	}
}
