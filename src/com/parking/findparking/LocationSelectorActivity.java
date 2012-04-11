package com.parking.findparking;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.parking.dashboard.R;
import com.parking.dashboard.activity.DashboardActivity;
import com.parking.utils.ParkingConstants;

public class LocationSelectorActivity extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.locationselector);
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

			if (parent.getItemAtPosition(pos).toString().equals(
					"Available Locations")) 
			{
				//Do Nothing
			} 
			else if (parent.getItemAtPosition(pos).toString().equals("Current Location"))
			{
				Intent i = new Intent(LocationSelectorActivity.this, FindParkingTabs.class);
				i.putExtra(ParkingConstants.ParkingMapIntentParameters.LOCATION_FIXED_TO_DOWNTOWN,
						false);
				if (i != null)
				{
					Toast.makeText(parent.getContext(), "Loading map for current location", Toast.LENGTH_LONG).show();
					startActivity(i);
				}
			} 
			else if ( parent.getItemAtPosition(pos).toString().equals("Downtown, 225 BROADWAY, SUITE 1100"))
			{
				Intent i = new Intent(LocationSelectorActivity.this, FindParkingTabs.class);
				i.putExtra(ParkingConstants.ParkingMapIntentParameters.LOCATION_FIXED_TO_DOWNTOWN,
						true);
				if (i != null)
				{
					Toast.makeText(parent.getContext(), "Loading Map for Downtown district", Toast.LENGTH_LONG).show();
					startActivity(i);
				}
			}
			else 
			{
				Toast.makeText(parent.getContext(), "Not Available: " + parent.getItemAtPosition(pos).toString(), Toast.LENGTH_SHORT).show();

			}
		}

		public void onNothingSelected(AdapterView parent) {
			// Do nothing.
		}
	}

}
