package com.parking.dbManager;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import com.parking.datamanager.ParkingLocationDataEntry;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DataBaseHelper extends SQLiteOpenHelper{

	private static final String TAG = "DataBaseHelper";

	//The Android's default system path of your application database.
	private static String DB_PATH = "/data/data/com.parking.dashboard/databases/";
	private static String DB_NAME = "parking_info.db";

	private SQLiteDatabase myDataBase; 
	private final Context myContext;

	/**
	 * Constructor
	 * Takes and keeps a reference of the passed context in order to access to the application assets and resources.
	 * @param context
	 */
	public DataBaseHelper(Context context) {

		super(context, DB_NAME, null, 1);
		this.myContext = context;
		if (context != null)
			Log.v(TAG, "DataBaseHelper::1 context not null");
		//Log.v(TAG, "DataBaseHelper::2");
	}	

	/**
	 * Creates a empty database on the system and rewrites it with your own database.
	 * */
	public void createDataBase() throws IOException{

		//Log.v(TAG, "createDataBase::1 ");
		boolean dbExist = checkDataBase();

		if(dbExist){
			//do nothing - database already exist
			Log.v(TAG, "createDataBase::2 do nothing - database already exist");
		}else{

			//By calling this method and empty database will be created into the default system path
			//of your application so we are gonna be able to overwrite that database with our database.
			this.getReadableDatabase();
			//Log.v(TAG, "createDataBase 3 ");

			try {

				//Log.v(TAG, "createDataBase 4 ");
				copyDataBase();
				//Log.v(TAG, "createDataBase 5 ");

			} catch (IOException e) {
				throw new Error("Error copying database");
			}
		}

	}

	/**
	 * Check if the database already exist to avoid re-copying the file each time you open the application.
	 * @return true if it exists, false if it doesn't
	 */
	private boolean checkDataBase(){

		SQLiteDatabase checkDB = null;

		try{
			String myPath = DB_PATH + DB_NAME;
			checkDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
			Log.v(TAG, "checkDataBase::0 ");

		}catch(SQLiteException e){
			//database does't exist yet.
			Log.v(TAG, "checkDataBase:: 1 database does't exist yet.");
		}

		if(checkDB != null){
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
	private void copyDataBase() throws IOException{
		//Open your local db as the input stream
		Log.v(TAG, "copydatabase:: 1");
		InputStream myinput = null;
		try {
			myinput = myContext.getAssets().open(DB_NAME);
		} catch (IOException    e1) {
			// TODO Auto-generated catch block
			Log.v(TAG, "copydatabase error 0");
			e1.printStackTrace();
		}

		//Log.v(TAG, "copydatabase 2");
		// Path to the just created empty db
		String outfilename = DB_PATH + DB_NAME;

		//Log.v(TAG, "copydatabase 3");
		//Open the empty db as the output stream
		OutputStream myoutput = null;
		try {
			myoutput = new FileOutputStream(outfilename.toString());
		} catch (FileNotFoundException  e) {
			// TODO Auto-generated catch block
			Log.v(TAG, "copydatabase error 1");
			e.printStackTrace();
		}

		// transfer byte to inputfile to outputfile
		byte[] buffer = new byte[1024];
		int length;
		while ((length = myinput.read(buffer))>0)
		{
			myoutput.write(buffer,0,length);
		}

		//Close the streams
		myoutput.flush();
		myoutput.close();
		myinput.close();

	}

	public void openDataBase() throws SQLException{

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

		Log.v(TAG, "close 1");
		if(myDataBase != null)
			myDataBase.close();

		super.close();

	}

	@Override
	public void onCreate(SQLiteDatabase db) {

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	}

	public void dbquery(int limit){
		Log.v(TAG, "dbquery 0 ");
		Cursor cursor = getReadableDatabase().rawQuery("select * from parking_info where _id LIMIT "+limit, null);
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
						String strId = cursor.getString(cursor.getColumnIndexOrThrow("_id"));
						float Lat = cursor.getFloat(cursor.getColumnIndexOrThrow("Lat"));
						float Lon = cursor.getFloat(cursor.getColumnIndexOrThrow("Lon"));
						Log.v(TAG, "dbquery :: " + strId.toString() + " Lat:: " + Lat + " Lon:: " + Lon);

					} while (cursor.moveToNext());
				}
			}



		}

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
	}
	// Add your public helper methods to access and get content from the database.
	// You could return cursors by doing "return myDataBase.query(....)" so it'd be easy
	// to you to create adapters for your views.

}

