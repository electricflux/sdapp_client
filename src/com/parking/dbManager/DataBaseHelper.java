package com.parking.dbManager;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

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

      if (dbExist) {
         Log.v(TAG, "Database Exists, we can proceed");
      } else {
         
            try {
            //Create empty DB & copy our database to the empty DB
            this.getReadableDatabase();
            retVal = copyDataBase();

            } catch (IOException e) {
               retVal = false;
               throw new Error("Error copying database");
            }
      }

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
         
         if(parkingSpotsCursor.moveToFirst()){
            do{
               ParkingLocationDataEntry pSpotInfo = new ParkingLocationDataEntry();
               
               //Lat Lon
               lat = (int) (parkingSpotsCursor.getDouble(parkingSpotsCursor.getColumnIndexOrThrow("Lat")) * 1E6);
               lng = (int) (parkingSpotsCursor.getDouble(parkingSpotsCursor.getColumnIndexOrThrow("Lon")) * 1E6);
               Log.e(TAG, " Lat: "+lat + " Lon: "+lng);
               
               GeoPoint geoPoint = new GeoPoint(lat, lng);
               pSpotInfo.setGeoPoint(geoPoint);
               pSpotInfo.setlatitude(lat);
               pSpotInfo.setlongitude(lng);
               
               //Meter Id
               meterId = (long) parkingSpotsCursor.getInt(parkingSpotsCursor.getColumnIndexOrThrow("MeterId"));
               pSpotInfo.setMeterId(meterId);
               
               
               parkingLocations.add(pSpotInfo);
               
            }while(parkingSpotsCursor.moveToNext());
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
						tLocationObj.setid(Id);
						tLocationObj.setlatitude(Lat);
						tLocationObj.setlongitude(Lon);
						parkingLocations.add(tLocationObj);

					} while (cursor.moveToNext());
				}
			}
		}
   // Add your public helper methods to access and get content from the database.
   // You could return cursors by doing "return myDataBase.query(....)" so it'd be easy
   // to you to create adapters for your views.
	}
}



