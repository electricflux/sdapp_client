package com.parking.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class AppPreferences {
     private static final String APP_SHARED_PREFS = "com.parking"; //  Name of the file -.xml
     private static SharedPreferences appSharedPrefs;
     
     private AppPreferences() { }
     
     public static SharedPreferences getInstance()
     {
    	 return appSharedPrefs;
     }

     public void initialize(Context context)
     {
         appSharedPrefs = context.getSharedPreferences(APP_SHARED_PREFS, Activity.MODE_PRIVATE);
     }

     public String getLicensePlateString() 
     {
         return appSharedPrefs.getString("license_plate_string", "");
     }

     public void setLicensePlateString(String text) {
    	 Editor prefsEditor;
    	 prefsEditor = appSharedPrefs.edit();
         prefsEditor.putString("license_plate_string", text);
         prefsEditor.commit();
     }
}
