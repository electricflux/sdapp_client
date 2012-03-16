/*
 * Copyright © 2011 QUALCOMM Incorporated. All rights reserved.
 *
 * This software is the confidential and proprietary information of
 * QUALCOMM Incorporated ("Proprietary Information"). You shall not
 * disclose such Proprietary Information, and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with QUALCOMM Incorporated.
 */
package com.parking.dashboard.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import com.parking.add.AddCapture;
import com.parking.dashboard.R;
import com.parking.manage.Manage;
import com.parking.personalbests.PersonalBests;
import com.parking.view.ViewAll;

public class DashboardActivity extends Activity{
   
   
   @Override
   protected void onCreate(Bundle savedInstanceState) {
      // TODO Auto-generated method stub
      super.onCreate(savedInstanceState);
      setContentView(R.layout.dashboard);
      
    //attach event handler to dash buttons
      DashboardClickListener dBClickListener = new DashboardClickListener();
      findViewById(R.id.dashboard_button_add).setOnClickListener(dBClickListener);
      findViewById(R.id.dashboard_button_viewall).setOnClickListener(dBClickListener);
      findViewById(R.id.dashboard_button_manage).setOnClickListener(dBClickListener);
      findViewById(R.id.dashboard_button_personalbests).setOnClickListener(dBClickListener);
      
   }

   private class DashboardClickListener implements OnClickListener {
      @Override
      public void onClick(View v) {
          Intent i = null;
          switch (v.getId()) {
              case R.id.dashboard_button_add:
                  i = new Intent(DashboardActivity.this, AddCapture.class);
                  break;
              case R.id.dashboard_button_viewall:
                  i = new Intent(DashboardActivity.this, ViewAll.class);
                  break;
              case R.id.dashboard_button_manage:
                  i = new Intent(DashboardActivity.this, Manage.class);
                  break;
              case R.id.dashboard_button_personalbests:
                  i = new Intent(DashboardActivity.this, PersonalBests.class);
                  break;
              default:
                  break;
          }
          if(i != null) {
              startActivity(i);
          }
      }
  }
   

}
