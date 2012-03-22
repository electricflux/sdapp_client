/*
 * Copyright © 2011 QUALCOMM Incorporated. All rights reserved.
 *
 * This software is the confidential and proprietary information of
 * QUALCOMM Incorporated ("Proprietary Information"). You shall not
 * disclose such Proprietary Information, and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with QUALCOMM Incorporated.
 */
package com.parking.findparking;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class FindParkingList extends Activity{
   public void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);

      TextView textview = new TextView(this);
      textview.setText("This is the Artists tab");
      setContentView(textview);
  }
}