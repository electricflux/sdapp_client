package com.parking.towingcontacts;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;

import com.parking.dashboard.R;
import com.parking.dashboard.activity.DashboardActivity;

public class TowingContacts extends Activity {
    
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
		
		WebView mWebView;
    	super.onCreate(savedInstanceState);
    	setContentView(R.layout.towingcontacts);
        mWebView = (WebView) findViewById(R.id.webview2);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.loadUrl("http://www.sandiego.gov/parking/enforcement/#impounds");
    }
}
