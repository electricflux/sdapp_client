package com.parking.auth;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.message.BasicNameValuePair;

import com.parking.application.ParkingApplication;
import com.parking.dashboard.R;
import com.parking.utils.AppPreferences;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class RegisterUserActivity extends Activity implements AsyncTaskResultNotifierInterface {
	
	private static final String TAG = RegisterUserActivity.class.getSimpleName();
	protected static final int MIN_LICENSE_PLATE_LENGTH = 7;
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
		
		deviceId = ParkingApplication.getDeviceId();
		
		userNameText = (EditText) findViewById(R.id.nUsername);
		userNameText.setText(ParkingApplication.getAccount().name);
		userNameText.setEnabled(false);
		
		fullNameText = (EditText) findViewById(R.id.nFullName);
		licensePlateNumbersText = (EditText) findViewById(R.id.nLicensePlates);
		
		clearButton = (Button) findViewById(R.id.nClear);
		clearButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				fullNameText.setText("");
				licensePlateNumbersText.setText("");
			}
		});
		
		submitButton = (Button) findViewById(R.id.nRegister);
		submitButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				String licensePlateNumbersString = 
						licensePlateNumbersText.getText().toString();
						
				/** Sanity for license plate number */
				if (licensePlateNumbersString.length() < 
						MIN_LICENSE_PLATE_LENGTH)
				{
					Toast.makeText(RegisterUserActivity.this,
							"Please enter valid license plate information",
							Toast.LENGTH_SHORT);
					clearButton.performClick();
					return;
				}
				AppPreferences.getInstance().setLicensePlateString(
						licensePlateNumbersText.getText().toString());
				
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
