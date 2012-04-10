package com.parking.utils;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import android.os.Environment;
import android.util.Log;

public class SdCardUtils {

	private static final String TAG = SdCardUtils.class.getSimpleName();
	
    public static void writeToSdCard(byte[] fileData, String fileName) {
        try {
            File root = Environment.getExternalStorageDirectory();
            File dir = 
            		new File (
            				root.getAbsolutePath() + 
            				"/" + 
            				ParkingConstants.PARKING_APP_DIRECOTRY);
            if (dir.exists())
            	dir.mkdirs();
            
            FileOutputStream f = new FileOutputStream(new File(dir, fileName));
            InputStream in = new ByteArrayInputStream(fileData);

            byte[] buffer = new byte[1024];
            int len1 = 0;
            while ((len1 = in.read(buffer)) > 0) {
                f.write(buffer, 0, len1);
            }
            f.close();
        } catch (Exception e) {
            Log.d(TAG, e.getMessage());
        }

    }
}
