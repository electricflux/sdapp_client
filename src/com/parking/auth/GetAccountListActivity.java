package com.parking.auth;

import java.util.ArrayList;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.parking.application.ParkingApplication;
import com.parking.dashboard.R;

public class GetAccountListActivity extends ListActivity{
	protected AccountManager accountManager;
	protected Intent intent;
	private Account[] accounts;
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.account_list);
 
        //add header to list
        ListView lv = getListView();
        LayoutInflater inflater = getLayoutInflater();
        View header = inflater.inflate(R.layout.account_list_header_fragment, (ViewGroup) findViewById(R.id.header_layout_root));
        lv.addHeaderView(header, null, false);
 
        
        accountManager = AccountManager.get(getApplicationContext());
		accounts = accountManager.getAccountsByType("com.google");
		ArrayList<String> accountNameArray = new ArrayList<String>(accounts.length);
		for(int i=0; i<accounts.length; i++)
		{
			accountNameArray.add(accounts[i].name);
		}
		this.setListAdapter(new ArrayAdapter(this, R.layout.listitem, accountNameArray)); 
    }

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		//We do position -1 to get the position in the array (array starts from zero).
		ParkingApplication.setAccount(accounts[position-1]);
		Intent intent = new Intent(this, GetAuthTokenActivity.class);
		startActivity(intent);
		finish();
	}


}
