/*
 * Copyright © 2011 QUALCOMM Incorporated. All rights reserved.
 *
 * This software is the confidential and proprietary information of
 * QUALCOMM Incorporated ("Proprietary Information"). You shall not
 * disclose such Proprietary Information, and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with QUALCOMM Incorporated.
 */
package com.parking.billing;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.widget.Toast;

import com.android.vending.billing.IMarketBillingService;
import com.parking.billing.BillingConstants.ResponseCode;
import com.parking.dashboard.activity.DashboardActivity;

public class ParkingBillingService extends Service implements ServiceConnection {

   private static final String TAG = "ParkingBillingService";
   boolean serviceBound = false;
   private IMarketBillingService mBillingService = null;

   public ParkingBillingService() {
      super();
   }

   @Override
   public void onCreate() {

      super.onCreate();

      serviceBound = bindService(new Intent("com.android.vending.billing.IMarketBillingService.BIND"), this, Context.BIND_AUTO_CREATE);
      if (serviceBound) {
         Toast.makeText(DashboardActivity.myContext, "Billing Connected", Toast.LENGTH_SHORT).show();
      } else
      {
         Toast.makeText(DashboardActivity.myContext, "Could not connect", Toast.LENGTH_SHORT).show();
      }
   }

   @Override
   public IBinder onBind(Intent arg0) {
      return null;
   }

   public void setContext(Context context) {
      attachBaseContext(context);

   }

   @Override
   public void onServiceConnected(ComponentName name, IBinder service) {
      Toast.makeText(DashboardActivity.myContext, "Billing Service Connected", Toast.LENGTH_SHORT).show();
      mBillingService = IMarketBillingService.Stub.asInterface(service);
   }

   @Override
   public void onServiceDisconnected(ComponentName name) {
      Toast.makeText(DashboardActivity.myContext, "Billing Service Disconnected", Toast.LENGTH_SHORT).show();

   }
   
   public int isInAppBillingSupported(){
      
      int responseCode;
      
      Bundle request = makeRequestBundle("CHECK_BILLING_SUPPORTED");
      Bundle response = null;
      try {
         response = mBillingService.sendBillingRequest(request);
      } catch (RemoteException e) {
         Log.e(TAG, "Cannot talk to the billing service!");
         e.printStackTrace();
      }
      
      responseCode = response.containsKey(BillingConstants.BILLING_RESPONSE_RESPONSE_CODE) ? 
                     response.getInt(BillingConstants.BILLING_RESPONSE_RESPONSE_CODE) :
                     ResponseCode.RESULT_BILLING_UNAVAILABLE.ordinal();
                     
      return responseCode;
      
   }

   private Bundle makeRequestBundle(String method) {

      Bundle request = new Bundle();
      request.putString(BillingConstants.BILLING_REQUEST_METHOD, method);
      request.putInt(BillingConstants.BILLING_REQUEST_API_VERSION, 1);
      request.putString(BillingConstants.BILLING_REQUEST_PACKAGE_NAME, getPackageName());
      
      return request;
   }

}
