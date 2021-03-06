
package com.parking.paymenthistory;

import android.app.ListActivity;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import com.parking.billing.PurchaseDatabase;
import com.parking.dashboard.R;
import com.parking.dashboard.activity.DashboardActivity;
import com.parking.utils.AppPreferences;

public class PaymentHistory extends ListActivity{
   
   private PurchaseDatabase historyTable;
   private Cursor historyCursor;
   ListView listView; 
   
   @Override
   protected void onCreate(Bundle savedInstanceState) {
      
      super.onCreate(savedInstanceState);
      setContentView(R.layout.paymenthistory);
      historyTable = new PurchaseDatabase(DashboardActivity.myContext);
      
      queryHistory();
      linkToCursorAdapter();
      
      historyTable.close();
   }

   private void linkToCursorAdapter() {
      String[] from = new String[] {PurchaseDatabase.HISTORY_ADDRESS_COL, 
                                    PurchaseDatabase.HISTORY_STARTTIME,
                                    PurchaseDatabase.HISTORY_DURATION_COL,
                                    PurchaseDatabase.HISTORY_AMOUNT_PAID_COL 
                                    };
      int[] to = new int[] {R.id.parkedAddress,
                            R.id.parkedTime,
                            R.id.parkedDuration, 
                            R.id.parkedAmount};
      
      SimpleCursorAdapter myCursorAdapter = 
    		  new SimpleCursorAdapter(this,R.layout.paymenthistoryfragment,
                                      historyCursor, from, to);
      this.setListAdapter(myCursorAdapter);
   }

   private void queryHistory() {
      
	   if (true == AppPreferences.getInstance().getGuestLogin())
		   historyCursor = null;
	   else
		   historyCursor = historyTable.queryAllHistoryItems();
      startManagingCursor(historyCursor);
   }
   
}
