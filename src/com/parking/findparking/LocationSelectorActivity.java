package com.parking.findparking;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.parking.dashboard.R;
import com.parking.dashboard.activity.DashboardActivity;

public class LocationSelectorActivity extends Activity{
   
   @Override
   public void onCreate(Bundle savedInstanceState) {
       super.onCreate(savedInstanceState);
       setContentView(R.layout.locationselectorfragment);

       Spinner spinner = (Spinner) findViewById(R.id.spinner);
       ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
               this, R.array.locations_array, android.R.layout.simple_spinner_item);
       adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
       spinner.setAdapter(adapter);
       
       spinner.setOnItemSelectedListener(new MyOnItemSelectedListener());
   }
   
   public class MyOnItemSelectedListener implements OnItemSelectedListener {

      public void onItemSelected(AdapterView<?> parent,
          View view, int pos, long id) {
        
        if(0 != pos){
           Toast.makeText(parent.getContext(), "Dataset Not Available for " +
                 parent.getItemAtPosition(pos).toString(), Toast.LENGTH_LONG).show();
        }else{
           Intent i = new Intent(LocationSelectorActivity.this, FindParkingTabs.class);
           if(i != null)
           {
              startActivity(i);
           }
        }
        
        
        
      }

      public void onNothingSelected(AdapterView parent) {
        // Do nothing.
      }
  }

}
