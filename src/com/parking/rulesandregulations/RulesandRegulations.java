package com.parking.rulesandregulations;


import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;

import com.parking.dashboard.R;
import com.parking.dashboard.activity.DashboardActivity;

public class RulesandRegulations extends Activity {
    
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
		
		WebView mWebView;
    	super.onCreate(savedInstanceState);
    	setContentView(R.layout.rulesandregulations);
        mWebView = (WebView) findViewById(R.id.webview1);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.loadUrl("http://www.sandiego.gov/parking/enforcement/");
    }
}
