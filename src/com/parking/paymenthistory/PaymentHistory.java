
package com.parking.paymenthistory;

import android.app.ListActivity;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import com.parking.billing.PurchaseDatabase;
import com.parking.dashboard.R;
import com.parking.dashboard.activity.DashboardActivity;
import com.parking.utils.IDashoard;

public class PaymentHistory extends ListActivity implements IDashoard{
   
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
      String[] from = new String[] {PurchaseDatabase.HISTORY_DURATION_COL, PurchaseDatabase.HISTORY_AMOUNT_PAID_COL};
      int[] to = new int[] {R.id.parkedDuration, R.id.parkedAmount};      
      SimpleCursorAdapter myCursorAdapter = 
    		  new SimpleCursorAdapter(this,R.layout.paymenthistoryfragment,
                                      historyCursor, from, to);
      this.setListAdapter(myCursorAdapter);
   }

   private void queryHistory() {
      
      historyCursor = historyTable.queryAllHistoryItems();
      startManagingCursor(historyCursor);
   }
   
   public void openHomePage(View v){
      Log.e("HomeButton", "OK");
   }
}
