package com.parking.timewidget;

import java.util.Calendar;

import com.parking.billing.BillingConstants;
import com.parking.billing.ParkingPayment;
import com.parking.dashboard.R;
import com.parking.dashboard.activity.DashboardActivity;
import com.parking.datamanager.ParkingLocationDataEntry;
import com.parking.findparking.ParkingSpotSelector;
import com.parking.reminder.ParkingReminder;
import com.parking.reminder.ParkingReminderReceiver;
import com.parking.timewidget.OnWheelChangedListener;
import com.parking.timewidget.OnWheelClickedListener;
import com.parking.timewidget.OnWheelScrollListener;
import com.parking.timewidget.WheelView;
import com.parking.timewidget.adapters.NumericWheelAdapter;
//import com.parking.timewidget.adapters.NumericWheelAdapter;
import com.parking.utils.AppPreferences;
import com.parking.utils.LocationUtility;


import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TimePicker;
import android.widget.Toast;

public class TimeActivity extends Activity{
        // Time changed flag
        private boolean timeChanged = false;
        private static final String TAG = TimeActivity.class.getSimpleName();
    	public static ParkingLocationDataEntry parkingLocationObj =null;
    	String parkingObjString = "";
        // Time scrolled flag
        private boolean timeScrolled = false;
        private Button mBuyButton;
        private Button mRemindButton;
        public WheelView hours;
        public WheelView mins;
        public static int timeInSec;
        @Override
        public void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);

                setContentView(R.layout.time_layout);
            	
                Intent starterIntent = getIntent();
        		Bundle bundle = starterIntent.getExtras();
        		/** Create parkingLocationObj */
        		if (bundle != null) {
        		parkingLocationObj = new ParkingLocationDataEntry();
        		parkingObjString = bundle.getString("info");
        		parkingLocationObj = LocationUtility.convertStringToObject(parkingObjString);
        		Log.v(TAG,"Parking Obj is" + parkingObjString);
        		}


                hours = (WheelView) findViewById(R.id.hour);
                hours.setViewAdapter(new NumericWheelAdapter(this, 0, 23));
        
                mins = (WheelView) findViewById(R.id.mins);
                mins.setViewAdapter(new NumericWheelAdapter(this, 0, 59, "%02d"));
                mins.setCyclic(true);
        
                final TimePicker picker = (TimePicker) findViewById(R.id.time);
                picker.setIs24HourView(true);
        
                // set current time
                Calendar c = Calendar.getInstance();
                int curHours = c.get(Calendar.HOUR_OF_DAY);
                int curMinutes = c.get(Calendar.MINUTE);
        
                hours.setCurrentItem(curHours);
                mins.setCurrentItem(curMinutes);
        
                picker.setCurrentHour(curHours);
                picker.setCurrentMinute(curMinutes);
        
                // add listeners
                addChangingListener(mins, "min");
                addChangingListener(hours, "hour");
        
                OnWheelChangedListener wheelListener = new OnWheelChangedListener() {
                        public void onChanged(WheelView wheel, int oldValue, int newValue) {
                                if (!timeScrolled) {
                                        timeChanged = true;
                                        picker.setCurrentHour(hours.getCurrentItem());
                                        picker.setCurrentMinute(mins.getCurrentItem());
                                        timeChanged = false;
                                }
                        }
                };
                hours.addChangingListener(wheelListener);
                mins.addChangingListener(wheelListener);
                
                OnWheelClickedListener click = new OnWheelClickedListener() {
            public void onItemClicked(WheelView wheel, int itemIndex) {
                wheel.setCurrentItem(itemIndex, true);
            }
        };
        hours.addClickingListener(click);
        mins.addClickingListener(click);

                OnWheelScrollListener scrollListener = new OnWheelScrollListener() {
                        public void onScrollingStarted(WheelView wheel) {
                                timeScrolled = true;
                        }
                        public void onScrollingFinished(WheelView wheel) {
                                timeScrolled = false;
                                timeChanged = true;
                                picker.setCurrentHour(hours.getCurrentItem());
                                picker.setCurrentMinute(mins.getCurrentItem());
                                timeChanged = false;
                        }
                };
                
                hours.addScrollingListener(scrollListener);
                mins.addScrollingListener(scrollListener);
                
                picker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
                        public void onTimeChanged(TimePicker  view, int hourOfDay, int minute) {
                                if (!timeChanged) {
                                        hours.setCurrentItem(hourOfDay, true);
                                        mins.setCurrentItem(minute, true);
                                }
                        }
                });
                
                mRemindButton = (Button) findViewById(R.id.setToRemind);
        		mRemindButton.setEnabled(true);
        		mRemindButton.setOnClickListener(new View.OnClickListener() {
        			public void onClick(View v) {
        				if (v == mRemindButton) {
        					if (parkingLocationObj != null) {
        					/** Set latitude and longitude in application preferences */
        					AppPreferences.getInstance().setLastPaidLocationLatitude(
        							parkingLocationObj.getLatitude());
        					AppPreferences.getInstance().setLastPaidLocationLongitude(
        							parkingLocationObj.getLongitude());
        					Toast.makeText(TimeActivity.this, "Setting a reminder and parking location!", Toast.LENGTH_LONG).show();
        					}
        					else
        					Toast.makeText(TimeActivity.this, "Setting only a reminder!For parking loaction, please find a nearby spot to select and park", Toast.LENGTH_LONG).show();
        					setupAlarm(false);
        				}
        			}
        		}
        		);
        		
                mBuyButton = (Button) findViewById(R.id.setToPay);
        		mBuyButton.setEnabled(true);
        		mBuyButton.setOnClickListener(new View.OnClickListener() {
        			public void onClick(View v) {
        					if (v == mBuyButton) {
        						if (parkingLocationObj == null) {
        							Toast.makeText(TimeActivity.this, "You havent selected a spot yet. Please find a nearby spot to select and pay!", Toast.LENGTH_LONG).show();
        							//do nothing?
        							return ;
        						}
        						setupAlarm(true);
        						

        				}
        	    	}
        		}
        	);
        }

        private void setupAlarm(final boolean payActivityLaunch){
			
			Log.v(TAG, "Selected Time is: hours: " + hours.getCurrentItem() + " Mins: "+ mins.getCurrentItem());
			timeInSec = (hours.getCurrentItem()*60 + mins.getCurrentItem())*60;
			//final int timeInSec =  mins.getCurrentItem();
			//Create an offset from the current time in which the alarm will go off.
	        final Calendar cal = Calendar.getInstance();
	        cal.add(Calendar.SECOND, timeInSec);
			Intent intent = new Intent(TimeActivity.this, ParkingReminderReceiver.class);
			//If alarm does not already exist, schedule one, else, ask if previous one should be rescheduled!
			if (PendingIntent.getBroadcast(TimeActivity.this, 12345, intent, PendingIntent.FLAG_NO_CREATE) == null) {
				scheduleAlarm(intent, cal, payActivityLaunch);
			}
			else {
			
				//Popup to cancel previous alarm and set new one
				DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
				    @Override
				    public void onClick(DialogInterface dialog, int which) {
				        switch (which){
				        case DialogInterface.BUTTON_POSITIVE:
				            //Yes button clicked
				        	//Reschedule an alarm by canceling previously set alarm
				        	
				        	Log.v(TAG, "Yes clicked: ");
				        	Toast.makeText(TimeActivity.this, "Rescheduling alarm!", Toast.LENGTH_LONG).show();
				        	Intent intent = new Intent(TimeActivity.this, ParkingReminderReceiver.class);
				        	PendingIntent pendingIntent = PendingIntent.getBroadcast(TimeActivity.this, 12345, intent, PendingIntent.FLAG_CANCEL_CURRENT);
    	    				AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
    	    				alarmManager.cancel(pendingIntent);
    	    				scheduleAlarm(intent,cal,payActivityLaunch);
				            break;

				        case DialogInterface.BUTTON_NEGATIVE:
				            //No button clicked
				        	//Do nothing!
				        	Log.v(TAG, "No clicked: ");
				            break;
				        }
				    }
				};

				AlertDialog.Builder builder = new AlertDialog.Builder(TimeActivity.this);
				builder.setMessage("Do you want to re-schedule an alarm?").setPositiveButton("Yes", dialogClickListener)
				    .setNegativeButton("No", dialogClickListener).show();
			}
        }
        
        private void scheduleAlarm(Intent intent, Calendar cal, boolean payActivityLaunch) {
        	PendingIntent pendingIntent = PendingIntent.getBroadcast(TimeActivity.this, 12345, intent, PendingIntent.FLAG_CANCEL_CURRENT);
			AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
			alarmManager.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(),   pendingIntent);
			Toast.makeText(TimeActivity.this, "Alarm set to go after: " + timeInSec + "", Toast.LENGTH_LONG).show();//every 10 minutes i want to print the toast
			Log.v(TAG, "Wait time in seconds: " + timeInSec);
			 
			if (payActivityLaunch) {
				
				//Launch the payment activity
				Intent pay = new Intent(TimeActivity.this, ParkingPayment.class); //ParkingSpotAndPaymentInformation.class);
				pay.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				pay.putExtra("info", parkingObjString);
				pay.putExtra("time", Integer.toString(timeInSec));
				
				Log.v(TAG,"Passing snippet: ");
				if(pay != null) {
					Log.v(TAG, "About to start Activity");
					startActivity(pay);
				}
			}
        }

        /**
         * Adds changing listener for wheel that updates the wheel label
         * @param wheel the wheel
         * @param label the wheel label
         */
        private void addChangingListener(final WheelView wheel, final String label) {
                wheel.addChangingListener(new OnWheelChangedListener() {
                        public void onChanged(WheelView wheel, int oldValue, int newValue) {
                                //wheel.setLabel(newValue != 1 ? label + "s" : label);
                        }
                });
        }
}
