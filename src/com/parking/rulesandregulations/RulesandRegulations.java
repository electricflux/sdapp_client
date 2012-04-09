package com.parking.rulesandregulations;


import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;

import com.parking.dashboard.R;
import com.parking.dashboard.activity.DashboardActivity;

public class RulesandRegulations extends Activity {
    
	private static final String TAG = RulesandRegulations.class.getSimpleName();
	@Override
    public void onCreate(Bundle savedInstanceState) {
		Log.v(TAG, "About to start webview");
		WebView mWebView;
    	super.onCreate(savedInstanceState);
		Log.v(TAG, "Done with saved instance");
    	setContentView(R.layout.rulesandregulations);
    	Log.v(TAG, "Done with setcontentview");
        mWebView = (WebView) findViewById(R.id.webview1);
        Log.v(TAG, "Done with findviewbyid");
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.loadUrl("http://www.sandiego.gov/parking/enforcement/");
        Log.v(TAG, "Done with loadurl");
    }
}
