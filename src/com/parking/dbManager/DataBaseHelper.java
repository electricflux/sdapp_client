package com.parking.dbManager;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.R.string;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.google.android.maps.GeoPoint;
import com.parking.datamanager.ParkingLocationDataEntry;

public class DataBaseHelper extends SQLiteOpenHelper {

   private static final String TAG = "DataBaseHelper";

   //The Android's default system path of your application database.
   private static final String DB_PATH = "/data/data/com.parking.dashboard/databases/";
   private static final String DB_NAME = "parking_info.db";
   private static final String outfilename = DB_PATH + DB_NAME;

   private SQLiteDatabase myDataBase;
   private final Context myContext;

   public DataBaseHelper(Context context) {
      super(context, DB_NAME, null, 1);
      this.myContext = context;
   }

   /**
    *  Creates a empty database on the system and rewrites it with your own database.
    */
   public boolean createDataBase() throws IOException {

      boolean dbExist = checkDataBase();
      boolean retVal = true;

      //TODO Uncomment, commented so that the db can be replaced
      //      if (dbExist) {
      //         Log.v(TAG, "Database Exists, we can proceed");
      //      } else {

      try {
         //Create empty DB & copy our database to the empty DB
         this.getReadableDatabase();
         retVal = copyDataBase();

      } catch (IOException e) {
         retVal = false;
         throw new Error("Error copying database");
      }
      //      }

      return retVal;
   }

   /**
    * Check if the database already exist to avoid re-copying the file each time you open the application.
    * @return true if it exists, false if it doesn't
    */
   private boolean checkDataBase() {

      SQLiteDatabase checkDB = null;

      try {
         String myPath = DB_PATH + DB_NAME;
         checkDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
         Log.v(TAG, "checkDataBase::0 ");

      } catch (SQLiteException e) {
         //database does't exist yet.
         Log.v(TAG, "checkDataBase:: 1 database does't exist yet.");
      }

      if (checkDB != null) {
         Log.v(TAG, "checkDataBase NOT NULL " + checkDB.getPath());
         checkDB.close();
      }

      //Log.v(TAG, "checkDataBase::2 ");
      return checkDB != null ? true : false;
   }

   /**
    * Copies your database from your local assets-folder to the just created empty database in the
    * system folder, from where it can be accessed and handled.
    * This is done by transfering bytestream.
    * */
   private boolean copyDataBase() throws IOException {

      InputStream myinput = null;
      OutputStream myoutput = null;
      boolean retVal1 = false;
      boolean retVal2 = false;
      int length;

      try {
         myinput = myContext.getAssets().open(DB_NAME);
         retVal1 = true;
      } catch (IOException e1) {
         Log.e(TAG, "Error accessing the bundled database!");
         e1.printStackTrace();
      }

      try {
         myoutput = new FileOutputStream(outfilename.toString());
         retVal2 = true;

      } catch (FileNotFoundException e) {
         Log.v(TAG, "Database not found!");
         e.printStackTrace();
      }

      if (retVal1 && retVal2) {
         byte[] buffer = new byte[1024];

         while ((length = myinput.read(buffer)) > 0)
         {
            myoutput.write(buffer, 0, length);
         }

         myoutput.flush();
      }

      if (retVal1) {
         myinput.close();
      }
      if (retVal2) {
         myoutput.close();
      }

      return (retVal1 || retVal2);
   }

   public void openDataBase() throws SQLException {

      //Open the database
      Log.v(TAG, "openDataBase::1 ");
      String myPath = DB_PATH + DB_NAME;
      myDataBase = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
      //Log.v(TAG, "openDataBase 2 ");

      if (myDataBase != null)
         Log.v(TAG, "openDataBase NOT NULL ");

   }

   @Override
   public synchronized void close() {

      Log.v(TAG, "Closing DB");
      if (myDataBase != null)
         myDataBase.close();
      super.close();

   }

   @Override
   public void onCreate(SQLiteDatabase db) {

   }

   @Override
   public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

   }

   public List<ParkingLocationDataEntry> dbquery(GeoPoint gp, List<ParkingLocationDataEntry> parkingLocations) {

      int lat, lng;
      long meterId;

      Log.v(TAG, "dbquery 0 ");
      String selection = "Lat LIKE '34.1%'";
      Cursor parkingSpotsCursor = myDataBase.query("parking_info", null, selection, null, null, null, null);
      if (parkingSpotsCursor != null)
      {

         if (parkingSpotsCursor.moveToFirst()) {
            do {
               ParkingLocationDataEntry pSpotInfo = new ParkingLocationDataEntry();

               //Lat Lon
               lat = (int) (parkingSpotsCursor.getDouble(parkingSpotsCursor.getColumnIndexOrThrow("Lat")) * 1E6);
               lng = (int) (parkingSpotsCursor.getDouble(parkingSpotsCursor.getColumnIndexOrThrow("Lon")) * 1E6);
               Log.e(TAG, " Lat: " + lat + " Lon: " + lng);

               GeoPoint geoPoint = new GeoPoint(lat, lng);
               pSpotInfo.setGeoPoint(geoPoint);
               pSpotInfo.setLatitude(lat);
               pSpotInfo.setLongitude(lng);

               //Meter Id
               meterId = (long) parkingSpotsCursor.getInt(parkingSpotsCursor.getColumnIndexOrThrow("MeterId"));
               pSpotInfo.setMeterID(meterId);

               parkingLocations.add(pSpotInfo);

            } while (parkingSpotsCursor.moveToNext());
            parkingSpotsCursor.close();
         }
         

         Log.v(TAG, "dbquery 1 > " + parkingSpotsCursor.getColumnCount() + parkingSpotsCursor.getCount());

      }

      return parkingLocations;

   }

	public void dbquery(int limit, List<ParkingLocationDataEntry> parkingLocations ){
		Log.v(TAG, "dbquery 1 ");
		Cursor cursor = getReadableDatabase().rawQuery("select * from parking_info where _id LIMIT "+limit, null);
		ParkingLocationDataEntry tLocationObj = null; 
		tLocationObj = new ParkingLocationDataEntry();
		if (cursor != null)
		{
			//"(_id INTEGER PRIMARY KEY, BlockName TEXT, Terminal_I TEXT, Lat FLOAT, Lon FLOAT);");
			Log.v(TAG, "dbquery 1 ::" + cursor.getColumnCount() );
			/* Check if our result was valid. */
			if (cursor != null) {
				/* Check if at least one Result was returned. */
				if (cursor.moveToFirst()) {
					int i = 0;
					/* Loop through all Results */
					do {
						i++;
						/* Retrieve the values of the Entry
						 * the Cursor is pointing to. */
						Long Id = (long) cursor.getInt(cursor.getColumnIndexOrThrow("_id"));
						float Lat = cursor.getFloat(cursor.getColumnIndexOrThrow("Lat"));
						float Lon = cursor.getFloat(cursor.getColumnIndexOrThrow("Lon"));
						Log.v(TAG, "dbquery :: " + Id + " Lat:: " + Lat + " Lon:: " + Lon);
						tLocationObj.setId(Id);
						tLocationObj.setLatitude(Lat);
						tLocationObj.setLongitude(Lon);
						parkingLocations.add(tLocationObj);

					} while (cursor.moveToNext());
					cursor.close();
				}
			}
		}
   // Add your public helper methods to access and get content from the database.
   // You could return cursors by doing "return myDataBase.query(....)" so it'd be easy
   // to you to create adapters for your views.
	}
	
	public void dbquery(int distance,
            double mlat, double mlon, int limit,
            List<ParkingLocationDataEntry> parkingLocations) {
        Log.v(TAG, "dbquery by distance 3 " + distance + "miles");
        
        //mlat = (float) Math.toRadians(mlat);
        //mlon = (float) Math.toRadians(mlon);
        ParkingLocationDataEntry tLocationObj = null;
        tLocationObj = new ParkingLocationDataEntry();
        List<ParkingLocationDataEntry> tempParkingSet = new ArrayList<ParkingLocationDataEntry>();
        int iParkingObjs = 0;
        double dist;
        Cursor cursor = getReadableDatabase().rawQuery(
                    //"select * from parking_info where _id > "+start+ " and _id < "+ end, null);
            		"select * from parking_info", null);
        if (cursor != null) {
                Log.v(TAG, "dbquery got 500 elements :: " + cursor.getColumnCount());
                /* Check if our result was valid. */
                   if (cursor.moveToFirst()) {
                      /* Loop through all Results */
                        do {
        
                            /*
                             * Retrieve the values of the Entry the Cursor is
                             * pointing to.
                             */
                        	
                            Long Id = (long) cursor.getInt(cursor
                                    .getColumnIndexOrThrow("_id"));
                            float dbLat = cursor.getFloat(cursor
                                    .getColumnIndexOrThrow("Lat"));
                            float dbLon = cursor.getFloat(cursor
                                    .getColumnIndexOrThrow("Lon"));
                            float dbRate = cursor.getFloat(cursor
                                    .getColumnIndexOrThrow("Rate"));
                            String dbType = cursor.getString(cursor
                                    .getColumnIndexOrThrow("Type"));
                            int dbQuantity = cursor.getInt(cursor
                                    .getColumnIndexOrThrow("Quantity"));
                            
                            //Log.v(TAG, "dbquery :: " + Id + " Lat:: " + dbLat+ " Lon:: " + dbLon);

                            dist = distance((double)dbLat, (double)dbLon,mlat,mlon);
                            //Log.v(TAG, "dbquery :: " + Id + " Dist1:: " + dist+ " Distance:: " + distance);
                            if (dist <= distance)
                            {
        
                                                              
                                int ilat = (int) (dbLat * 1E6);
        						int ilng = (int) (dbLon * 1E6);
        						GeoPoint point = new GeoPoint(ilat, ilng);
        						
        						ParkingLocationDataEntry tempLocationObj = null;
        				        tempLocationObj = new ParkingLocationDataEntry();
        				        tempLocationObj.setId(Id);
        				        tempLocationObj.setLatitude( dbLat);
        				        tempLocationObj.setLongitude(dbLon);
        				        tempLocationObj.setGeoPoint(point);
        				        tempLocationObj.setDistance(dist);
        				        tempLocationObj.setRate(dbRate);
        				        tempLocationObj.setType(dbType);
        				        tempLocationObj.setQuantity(dbQuantity);
                                //Log.v(TAG, "dbquery :: " + Id + " Lat:: " + dbLat+ " Lon:: " + dbLon+ " Dist:: " + dist);
                                tempParkingSet.add(tempLocationObj);
                                iParkingObjs++;
                            }
                            dist = 0;
                        } while (cursor.moveToNext()); 
                        cursor.close();
                    }
                }
        
        
        //Collections.sort(tempParkingSet, COMPARATOR);
        ParkingLocationDataEntry[] arrayOfLocation = tempParkingSet.toArray(new ParkingLocationDataEntry[]{});
       
        Arrays.sort(arrayOfLocation, new DistanceComparator());
        int count=0;
        for (int i = 0; i < arrayOfLocation.length; i++)
        {
        	//Log.v(TAG, "here 1" + arrayOfLocation[i].getDistance() );
        	
			if (count >= limit )
        		break;
        	parkingLocations.add(arrayOfLocation[i]);
        	count++;
        }
    }
	//Below functions are helper functions to calculate distance
	private double distance(double lat1, double lon1, double lat2, double lon2) {
	      double theta = lon1 - lon2;
	      double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));
	      dist = Math.acos(dist);
	      dist = rad2deg(dist);
	      dist = dist * 60 * 1.1515;
	       return (dist);
	    }

	   private double deg2rad(double deg) {
	      return (deg * Math.PI / 180.0);
	    }
	   private double rad2deg(double rad) {
	      return (rad * 180.0 / Math.PI);
	    }
	 /*  private static Comparator<ParkingLocationDataEntry> COMPARATOR = new Comparator<ParkingLocationDataEntry>() {
		   public double compare (ParkingLocationDataEntry obj1, ParkingLocationDataEntry obj2) {
		   return  (obj1.getDistance() - obj2.getDistance());
	   }
	   };
	   */
	   
	   
}


class DistanceComparator implements Comparator<ParkingLocationDataEntry> {

   @Override
   public int compare(ParkingLocationDataEntry obj1, ParkingLocationDataEntry obj2) {

      /*
       * parameter are of type Object, so we have to downcast it
       * to Employee objects
       */

      double dist1 = obj1.getDistance();
      double dist2 = obj2.getDistance();

      if (dist1 > dist2)
         return 1;
      else if (dist1 < dist2)
         return -1;
      else
         return 0;
   }

}
