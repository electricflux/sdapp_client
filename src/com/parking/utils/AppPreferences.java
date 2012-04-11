package com.parking.utils;

import java.util.ArrayList;
import java.util.Arrays;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

public class AppPreferences {
     private static final String APP_SHARED_PREFS = "com.parking"; //  Name of the file -.xml
     private static SharedPreferences appSharedPrefs = null;
     private static AppPreferences instance = null;
     private static final String TAG = AppPreferences.class.getSimpleName();
     
     private AppPreferences() { }
     
     public static AppPreferences getInstance()
     {
    	 if (instance == null)
    		 instance = new AppPreferences();
    	 
    	 return instance;
     }

     public static void initialize(Context context)
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
         Log.v(TAG,"Set license plate string to : "+getLicensePlateString());
     }
     
     public ArrayList<String> getLicensePlateList()
     {
    	 String[] list = getLicensePlateString().split(";");
    	 ArrayList<String> returnList = new ArrayList<String>();
    	 for (String licensePlate: list)
    	 {
    		 if (licensePlate.length() > 5)
    			 returnList.add(licensePlate);
    	 }
    	 return returnList;
     }
}
