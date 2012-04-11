/*
 * Copyright (C) 2010 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.parking.billing;


import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.maps.GeoPoint;
import com.parking.application.ParkingApplication;
import com.parking.auth.AsyncTaskResultNotifierInterface;
import com.parking.billing.BillingConstants.PurchaseState;
import com.parking.billing.BillingConstants.ResponseCode;
import com.parking.billing.BillingService.RequestPurchase;
import com.parking.billing.BillingService.RestoreTransactions;
import com.parking.dashboard.R;
import com.parking.dashboard.activity.DashboardActivity;
import com.parking.datamanager.ParkingLocationDataEntry;
import com.parking.utils.AppPreferences;
import com.parking.utils.LocationUtility;

public class ParkingPayment extends Activity implements OnClickListener,
OnItemSelectedListener {
	private static final String TAG = ParkingPayment.class.getSimpleName();

	/**
	 * The SharedPreferences key for recording whether we initialized the
	 * database.  If false, then we perform a RestoreTransactions request
	 * to get all the purchases for this user.
	 */
	private static final String DB_INITIALIZED = "db_initialized";

	private String mItemName;
	private String mSku;
	private int mDurationTotalTimeMinutes;
	private int mTimePeriods;

	private CatalogAdapter mCatalogAdapter;

	//Parking data that comes in from the previous activity
	public static ParkingLocationDataEntry parkingLocationObj = null;
	public int nLeastCountTime = 15;

	private ParkingPurchaseObserver mParkingPurchaseObserver;
	private Handler mHandler;

	private BillingService mBillingService;
	private Button mBuyButton;
	private Spinner mSelectItemSpinner;
	private Spinner mSelectLicensePlateSpinner;
	private PurchaseDatabase mPurchaseDatabase;
	private Set<String> mOwnedItems = new HashSet<String>();

	/**
	 * The developer payload that is sent with subsequent
	 * purchase requests.
	 */
	private String mPayloadContents = null;

	private static final int DIALOG_CANNOT_CONNECT_ID = 1;
	private static final int DIALOG_BILLING_NOT_SUPPORTED_ID = 2;

	/**
	 * Each product in the catalog is either MANAGED or UNMANAGED.  MANAGED
	 * means that the product can be purchased only once per user (such as a new
	 * level in a game). The purchase is remembered by Android Market and
	 * can be restored if this application is uninstalled and then
	 * re-installed. UNMANAGED is used for products that can be used up and
	 * purchased multiple times (such as poker chips). It is up to the
	 * application to keep track of UNMANAGED products for the user.
	 */
	public enum Managed { MANAGED, UNMANAGED }

	/**
	 * A {@link PurchaseObserver} is used to get callbacks when Android Market sends
	 * messages to this application so that we can update the UI.
	 */
	private class ParkingPurchaseObserver extends PurchaseObserver 
	implements AsyncTaskResultNotifierInterface {
		public ParkingPurchaseObserver(Handler handler) {
			super(ParkingPayment.this, handler);
		}

		@Override
		public void onBillingSupported(boolean supported) {
			if (BillingConstants.DEBUG) {
				Log.i(TAG, "supported: " + supported);
			}
			if (supported) {
				restoreDatabase();
				mBuyButton.setEnabled(true);
				//mEditPayloadButton.setEnabled(true);
			} else {
				showDialog(DIALOG_BILLING_NOT_SUPPORTED_ID);
			}
		}

		@Override
		public void onPurchaseStateChange(PurchaseState purchaseState, String itemId,
				int quantity, long purchaseTime, String developerPayload) {
			if (BillingConstants.DEBUG) {
				Log.i(TAG, "onPurchaseStateChange() itemId: " + itemId + " " + purchaseState);
			}

			if (developerPayload == null) {
				//TODO: print purchase state
				//logProductActivity(itemId, purchaseState.toString());
			} else {
				//TODO: print purchase state
				//logProductActivity(itemId, purchaseState + "\n\t" + developerPayload);
			}

			if (purchaseState == PurchaseState.PURCHASED) {
				mOwnedItems.add(itemId);

				//Update Server with the Payment Info!
				updateServer();

			}
			mCatalogAdapter.setOwnedItems(mOwnedItems);
		}

		private void updateServer() {

			/** Parameters for payment submission */
			List<BasicNameValuePair> nameValuePairs = new ArrayList<BasicNameValuePair>();
			nameValuePairs.add(new BasicNameValuePair("username", 
					ParkingApplication.getAccount().name));
			nameValuePairs.add(new BasicNameValuePair("deviceId", 
					ParkingApplication.getDeviceId()));
			nameValuePairs.add(new BasicNameValuePair("isDevice", 
					"true"));
			nameValuePairs.add(new BasicNameValuePair("amountPaid",
					Float.toString(parkingLocationObj.getAmountPaid())));
			nameValuePairs.add(new BasicNameValuePair("startTimestamp",
					parkingLocationObj.getStartTimestampMs()+""));
			nameValuePairs.add(new BasicNameValuePair("endTimestamp",
					parkingLocationObj.getEndTimestampMs()+""));
			nameValuePairs.add(new BasicNameValuePair("parkingSpotId",
					parkingLocationObj.getId()+""));
			nameValuePairs.add(new BasicNameValuePair("licensePlateNumber", 
					(String) mSelectLicensePlateSpinner.getSelectedItem()));
			new UpdateServerAsyncTask(
					ParkingPurchaseObserver.this,ParkingPayment.this).
					execute(nameValuePairs);
		}


		@Override
		public void notifyResult(boolean result) {
			if (result == true)
				Log.v(TAG,"Submit payment succeeded.");
			else
				Log.v(TAG,"Submit payment failed.");
		}

		@Override
		public void onRequestPurchaseResponse(RequestPurchase request,
				ResponseCode responseCode) {
			if (BillingConstants.DEBUG) {
				Log.d(TAG, request.mProductId + ": " + responseCode);
			}
			if (responseCode == ResponseCode.RESULT_OK) {
				if (BillingConstants.DEBUG) {
					Log.i(TAG, "purchase was successfully sent to server");
				}
			} else if (responseCode == ResponseCode.RESULT_USER_CANCELED) {
				if (BillingConstants.DEBUG) {
					Log.i(TAG, "user canceled purchase");
				}
			} else {
				if (BillingConstants.DEBUG) {
					Log.i(TAG, "purchase failed");
				}
			}
		}

		@Override
		public void onRestoreTransactionsResponse(RestoreTransactions request,
				ResponseCode responseCode) {
			if (responseCode == ResponseCode.RESULT_OK) {
				if (BillingConstants.DEBUG) {
					Log.d(TAG, "completed RestoreTransactions request");
				}
				// Update the shared preferences so that we don't perform
				// a RestoreTransactions again.
				SharedPreferences prefs = getPreferences(Context.MODE_PRIVATE);
				SharedPreferences.Editor edit = prefs.edit();
				edit.putBoolean(DB_INITIALIZED, true);
				edit.commit();
			} else {
				if (BillingConstants.DEBUG) {
					Log.d(TAG, "RestoreTransactions error: " + responseCode);
				}
			}
		}

	}

	/** An array of product list entries for the products that can be purchased. */
	private static final CatalogEntry[] CATALOG = new CatalogEntry[] {
		new CatalogEntry("android.test.purchased", R.string.min15, Managed.UNMANAGED),
		new CatalogEntry("android.test.purchased", R.string.min30, Managed.UNMANAGED),
		new CatalogEntry("android.test.purchased", R.string.min45, Managed.UNMANAGED),
		new CatalogEntry("android.test.purchased", R.string.hr1, Managed.UNMANAGED),
		new CatalogEntry("android.test.purchased", R.string.hr1min15, Managed.UNMANAGED),
		new CatalogEntry("android.test.purchased", R.string.hr1min30, Managed.UNMANAGED),
		new CatalogEntry("android.test.purchased", R.string.hr1min45, Managed.UNMANAGED),
		new CatalogEntry("android.test.purchased", R.string.hr2, Managed.UNMANAGED),

		new CatalogEntry("android.test.canceled", R.string.android_test_canceled, Managed.UNMANAGED),
		new CatalogEntry("android.test.refunded", R.string.android_test_refunded, Managed.UNMANAGED),
		new CatalogEntry("android.test.item_unavailable", R.string.android_test_item_unavailable, Managed.UNMANAGED),
	};

	private static final long NUM_MILLIS_IN_A_MINUTE = 60*1000;

	private static final float FLAT_PARKING_SPOT_RATE_PER_MINUTE = 
			0.16f;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.parkingpayment);

		mHandler = new Handler();
		mParkingPurchaseObserver = new ParkingPurchaseObserver(mHandler);
		mBillingService = new BillingService();
		mBillingService.setContext(this);

		mPurchaseDatabase = new PurchaseDatabase(DashboardActivity.myContext);

		//Get on the spot selected 
		Intent starterIntent = getIntent();
		Bundle bundle = starterIntent.getExtras();

		TextView textAll = (TextView) findViewById(R.id.parkingAllDetailsTextView);

		/** Create parkingLocationObj */
		String all = bundle.getString("info");
		parkingLocationObj = LocationUtility.convertStringToObject(all);

		/** Use the object to populate fields */
		String locAddress = "Address unavailable";
		Geocoder geoCoder = new Geocoder(DashboardActivity.myContext, Locale.getDefault());
		GeoPoint gp = new GeoPoint((int)(parkingLocationObj.getLatitude()*1E6),(int)(parkingLocationObj.getLongitude()*1E6));
		locAddress = LocationUtility.ConvertPointToLocation(gp, geoCoder);
		parkingLocationObj.setAddress(locAddress);

		String addressToDisplay  = 
				(parkingLocationObj.getParkingType() == null) ? "Not known" : ""+parkingLocationObj.getParkingType();

		parkingLocationObj = LocationUtility.convertStringToObject(all);
		textAll.setText( "Details...\n"
				+ "MeterId: " + parkingLocationObj.getMeterID() + "\n"
				+ "Address: " + parkingLocationObj.getAddress() + "\n"
				+ "Type:" + addressToDisplay + "\n"
				);

		setupWidgets();

		// Check if billing is supported.
		ResponseHandler.register(mParkingPurchaseObserver);
		if (!mBillingService.checkBillingSupported()) {
			showDialog(DIALOG_CANNOT_CONNECT_ID);
		}
	}

	/**
	 * Called when this activity becomes visible.
	 */
	@Override
	protected void onStart() {
		super.onStart();
		ResponseHandler.register(mParkingPurchaseObserver);
		initializeOwnedItems();
	}

	/**
	 * Called when this activity is no longer visible.
	 */
	@Override
	protected void onStop() {
		super.onStop();
		ResponseHandler.unregister(mParkingPurchaseObserver);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mPurchaseDatabase.close();
		mBillingService.unbind();
	}

	/**
	 * Save the context of the log so simple things like rotation will not
	 * result in the log being cleared.
	 */
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
	}

	/**
	 * Restore the contents of the log if it has previously been saved.
	 */
	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case DIALOG_CANNOT_CONNECT_ID:
			return createDialog(R.string.cannot_connect_title,
					R.string.cannot_connect_message);
		case DIALOG_BILLING_NOT_SUPPORTED_ID:
			return createDialog(R.string.billing_not_supported_title,
					R.string.billing_not_supported_message);
		default:
			return null;
		}
	}

	private Dialog createDialog(int titleId, int messageId) {
		String helpUrl = replaceLanguageAndRegion(getString(R.string.help_url));
		if (BillingConstants.DEBUG) {
			Log.i(TAG, helpUrl);
		}
		final Uri helpUri = Uri.parse(helpUrl);

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(titleId)
		.setIcon(android.R.drawable.stat_sys_warning)
		.setMessage(messageId)
		.setCancelable(false)
		.setPositiveButton(android.R.string.ok, null)
		.setNegativeButton(R.string.learn_more, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				Intent intent = new Intent(Intent.ACTION_VIEW, helpUri);
				startActivity(intent);
			}
		});
		return builder.create();
	}

	/**
	 * Replaces the language and/or country of the device into the given string.
	 * The pattern "%lang%" will be replaced by the device's language code and
	 * the pattern "%region%" will be replaced with the device's country code.
	 *
	 * @param str the string to replace the language/country within
	 * @return a string containing the local language and region codes
	 */
	private String replaceLanguageAndRegion(String str) {
		// Substitute language and or region if present in string
		if (str.contains("%lang%") || str.contains("%region%")) {
			Locale locale = Locale.getDefault();
			str = str.replace("%lang%", locale.getLanguage().toLowerCase());
			str = str.replace("%region%", locale.getCountry().toLowerCase());
		}
		return str;
	}

	/**
	 * Sets up the UI.
	 */
	private void setupWidgets() {
		mBuyButton = (Button) findViewById(R.id.buy_button);
		mBuyButton.setEnabled(false);
		mBuyButton.setOnClickListener(this);

		mSelectLicensePlateSpinner = (Spinner) findViewById(R.id.license_plate_choices);
		ArrayAdapter<String> spinnerArrayAdapter = 
				new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, 
						AppPreferences.getInstance().getLicensePlateList());
		spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		mSelectLicensePlateSpinner.setAdapter(spinnerArrayAdapter);

		mSelectItemSpinner = (Spinner) findViewById(R.id.item_choices);
		mCatalogAdapter = new CatalogAdapter(this, CATALOG);
		mSelectItemSpinner.setAdapter(mCatalogAdapter);
		mSelectItemSpinner.setOnItemSelectedListener(this);
	}

	/**
	 * If the database has not been initialized, we send a
	 * RESTORE_TRANSACTIONS request to Android Market to get the list of purchased items
	 * for this user. This happens if the application has just been installed
	 * or the user wiped data. We do not want to do this on every startup, rather, we want to do
	 * only when the database needs to be initialized.
	 */
	private void restoreDatabase() {
		SharedPreferences prefs = getPreferences(MODE_PRIVATE);
		boolean initialized = prefs.getBoolean(DB_INITIALIZED, false);
		if (!initialized) {
			mBillingService.restoreTransactions();
			Toast.makeText(this, R.string.restoring_transactions, Toast.LENGTH_LONG).show();
		}
	}

	/**
	 * Creates a background thread that reads the database and initializes the
	 * set of owned items.
	 */
	private void initializeOwnedItems() {
		new Thread(new Runnable() {
			public void run() {
				doInitializeOwnedItems();
			}
		}).start();
	}

	/**
	 * Reads the set of purchased items from the database in a background thread
	 * and then adds those items to the set of owned items in the main UI
	 * thread.
	 */
	private void doInitializeOwnedItems() {
		Cursor cursor = mPurchaseDatabase.queryAllPurchasedItems();
		if (cursor == null) {
			return;
		}

		final Set<String> ownedItems = new HashSet<String>();
		try {
			int productIdCol = cursor.getColumnIndexOrThrow(
					PurchaseDatabase.PURCHASED_PRODUCT_ID_COL);
			while (cursor.moveToNext()) {
				String productId = cursor.getString(productIdCol);
				ownedItems.add(productId);
			}
		} finally {
			cursor.close();
		}

		// We will add the set of owned items in a new Runnable that runs on
		// the UI thread so that we don't need to synchronize access to
		// mOwnedItems.
		mHandler.post(new Runnable() {
			public void run() {
				mOwnedItems.addAll(ownedItems);
				mCatalogAdapter.setOwnedItems(mOwnedItems);
			}
		});
	}

	/**
	 * Called when a button is pressed.
	 */
	public void onClick(View v) {
		if (v == mBuyButton) {
			if (BillingConstants.DEBUG) {
				Log.d(TAG, "buying: " + mItemName + " sku: " + mSku);
			}

			/** Update the object */
			parkingLocationObj.setAmountPaid(
					mDurationTotalTimeMinutes*FLAT_PARKING_SPOT_RATE_PER_MINUTE);
			parkingLocationObj.setDuration(mDurationTotalTimeMinutes);
			parkingLocationObj.setRate(FLAT_PARKING_SPOT_RATE_PER_MINUTE);
			parkingLocationObj.setQuantity(mTimePeriods);
			parkingLocationObj.setStartTimestampMs(System.currentTimeMillis());
			parkingLocationObj.setEndTimestampMs(System.currentTimeMillis() + 
					mDurationTotalTimeMinutes*NUM_MILLIS_IN_A_MINUTE);

			if (!mBillingService.requestPurchase(mSku, mPayloadContents)) {
				showDialog(DIALOG_BILLING_NOT_SUPPORTED_ID);
			}
		} 
	}

	/**
	 * Called when an item in the spinner is selected.
	 */
	public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
		//1 hr 15 min
		mItemName = getString(CATALOG[position].nameId);
		//android.test.purchased
		mSku = CATALOG[position].sku;
		//0 = 15 min, 1 = 30 min, 2 = 45 etc...
		//total time =  (0+1)* 15
		mDurationTotalTimeMinutes = ( position + 1 ) * nLeastCountTime;
		mTimePeriods = position +1;
	}

	public void onNothingSelected(AdapterView<?> arg0) {
	}
}
