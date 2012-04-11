package com.parking.utils;

import android.app.Activity;
import android.content.Intent;
import android.view.View;

import com.parking.dashboard.activity.DashboardActivity;

public class Utility{

   public static void openHomePage(View v){
      Intent intent = new Intent(DashboardActivity.myContext, DashboardActivity.class);
      intent.setFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
      DashboardActivity.myContext.startActivity(intent);
      
   }
   
}
