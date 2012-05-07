package com.parking.reminder;

import java.io.IOException;

import com.parking.dashboard.R;
import com.parking.timewidget.TimeActivity;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

public class ParkingReminder extends Activity {
	private MediaPlayer mMediaPlayer; 
	 
	 @Override
	    public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
	        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
	                WindowManager.LayoutParams.FLAG_FULLSCREEN);
	        setContentView(R.layout.alarm);
	        Button stopAlarm = (Button) findViewById(R.id.stopAlarm);
	        stopAlarm.setOnClickListener(new OnClickListener() {
	            public void onClick(View v) {
	                
	            	Toast.makeText(ParkingReminder.this, "Alarm cancel", Toast.LENGTH_LONG).show();
	            	mMediaPlayer.stop();
	                finish();
	            }
	        });
	        playSound(this, getAlarmUri());
	 }
	  private void playSound(Context context, Uri alert) {
	        mMediaPlayer = new MediaPlayer();
	        try {
	            mMediaPlayer.setDataSource(context, alert);
	            final AudioManager audioManager = (AudioManager) context
	                    .getSystemService(Context.AUDIO_SERVICE);
	            if (audioManager.getStreamVolume(AudioManager.STREAM_ALARM) != 0) {
	                mMediaPlayer.setAudioStreamType(AudioManager.STREAM_ALARM);
	                mMediaPlayer.prepare();
	                mMediaPlayer.start();
	            }
	        } catch (IOException e) {
	            System.out.println("OOPS");
	        }
	    }
	 
	        //Get an alarm sound. Try for an alarm. If none set, try notification, 
	        //Otherwise, ringtone.
	    private Uri getAlarmUri() {
	        Uri alert = RingtoneManager
	                .getDefaultUri(RingtoneManager.TYPE_ALARM);
	        if (alert == null) {
	            alert = RingtoneManager
	                    .getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
	            if (alert == null) {
	                alert = RingtoneManager
	                        .getDefaultUri(RingtoneManager.TYPE_RINGTONE);
	            }
	        }
	        return alert;
	    }
}
