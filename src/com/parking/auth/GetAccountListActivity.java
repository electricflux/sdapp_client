package com.parking.auth;

import com.parking.application.ParkingApplication;
import com.parking.dashboard.R;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class GetAccountListActivity extends ListActivity {
	protected AccountManager accountManager;
	protected Intent intent;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		accountManager = AccountManager.get(getApplicationContext());
		Account[] accounts = accountManager.getAccountsByType("com.google");
		this.setListAdapter(new ArrayAdapter(this, R.layout.listitem, accounts));        
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		Account account = (Account)getListView().getItemAtPosition(position);
		ParkingApplication.setAccount(account);
		Intent intent = new Intent(this, GetAuthTokenActivity.class);
		startActivity(intent);
		finish();
	}
}
