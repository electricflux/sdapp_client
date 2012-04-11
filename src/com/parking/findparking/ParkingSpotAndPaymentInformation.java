package com.parking.findparking;

import java.util.HashSet;
import java.util.Set;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.parking.billing.BillingConstants;
import com.parking.billing.BillingConstants.PurchaseState;
import com.parking.billing.BillingConstants.ResponseCode;
import com.parking.billing.BillingService;
import com.parking.billing.BillingService.RequestPurchase;
import com.parking.billing.BillingService.RestoreTransactions;
import com.parking.billing.PurchaseDatabase;
import com.parking.billing.PurchaseObserver;
import com.parking.billing.ResponseHandler;
import com.parking.dashboard.R;
import com.parking.dashboard.activity.DashboardActivity;

public class ParkingSpotAndPaymentInformation extends Activity{

   int billingSupported = 0;
   private static final String DB_INITIALIZED = "db_initialized";

   private ParkingPurchaseObserver mParkingPurchaseObserver;
   private Handler mHandler;

   private BillingService mBillingService;
   private Button mPayButton;
   private TextView mLogTextView;
   private PurchaseDatabase mPurchaseDatabase;
   private Cursor mOwnedItemsCursor;
   private Set<String> mOwnedItems = new HashSet<String>();

   private CatalogAdapter mCatalogAdapter;
   
   /**
    * The developer payload that is sent with subsequent
    * purchase requests.
    */
   private static final int DIALOG_CANNOT_CONNECT_ID = 1;
   private static final int DIALOG_BILLING_NOT_SUPPORTED_ID = 2;

   /**
    * Each product in the catalog is either MANAGED or UNMANAGED.  MANAGED
    * means that the product can be purchased only once per user (such as a new
    * level in a game). The purchase is remembered by Android Market and
    * can be restored if this application is uninstalled and then
    * re-installed. UNMANAGED is used for products that can be used up and
    * purchased multiple times (such as poker chips). It is up to the
    * application to keep track of UNMANAGED products for the user.
    */
   private enum Managed { MANAGED, UNMANAGED }

   private static class CatalogEntry {
      public String sku;
      public int nameId;
      public Managed managed;

      public CatalogEntry(String sku, int nameId, Managed managed) {
          this.sku = sku;
          this.nameId = nameId;
          this.managed = managed;
      }
  }

  /** An array of product list entries for the products that can be purchased. */
  private static final CatalogEntry[] CATALOG = new CatalogEntry[] {
      new CatalogEntry("sword_001", 1, Managed.MANAGED),
      new CatalogEntry("potion_001", 2, Managed.UNMANAGED),
      new CatalogEntry("android.test.purchased", 3, Managed.UNMANAGED),
      new CatalogEntry("android.test.canceled", 4, Managed.UNMANAGED),
      new CatalogEntry("android.test.refunded", 5, Managed.UNMANAGED),
      new CatalogEntry("android.test.item_unavailable", 6, Managed.UNMANAGED),
  };

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      
      super.onCreate(savedInstanceState);
      setContentView(R.layout.parkingpayment);
      Intent starterIntent = getIntent();
      Bundle bundle = starterIntent.getExtras();
      String all = bundle.getString("info");
      
      TextView textAll = (TextView) findViewById(R.id.textViewAll);
      textAll.setText(all);
      
      mHandler = new Handler();
      mParkingPurchaseObserver = new ParkingPurchaseObserver(mHandler);
      mBillingService = new BillingService();
      mBillingService.setContext(this);

      mPurchaseDatabase = new PurchaseDatabase(DashboardActivity.myContext);
      setupWidgets();

      // Check if billing is supported.
      ResponseHandler.register(mParkingPurchaseObserver);
      if (!mBillingService.checkBillingSupported()) {
          showDialog(DIALOG_CANNOT_CONNECT_ID);
      }

      
      Button payButton = (Button) findViewById(R.id.payButton);
      payButton.setOnClickListener(new OnClickListener(){
         @Override
         public void onClick(View arg0) {
            
            //billingSupported = mParkingBillingService.isInAppBillingSupported();
            
            if(billingSupported == ResponseCode.RESULT_OK.ordinal())
            {
               Toast.makeText(DashboardActivity.myContext, "SUPPORTED YAY", Toast.LENGTH_SHORT).show();
               
            }
            else
            {
               Toast.makeText(DashboardActivity.myContext, "NAY :(", Toast.LENGTH_SHORT).show();
            }
         }
      });
      
   }
   
   
   private void setupWidgets() {
      mPayButton = (Button) findViewById(R.id.payButton);
      mPayButton.setEnabled(false);
      
   }


   /**
    * A {@link PurchaseObserver} is used to get callbacks when Android Market sends
    * messages to this application so that we can update the UI.
    */
   private class ParkingPurchaseObserver extends PurchaseObserver {
       public ParkingPurchaseObserver(Handler handler) {
           super(ParkingSpotAndPaymentInformation.this, handler);
       }

       @Override
       public void onBillingSupported(boolean supported) {
           if (BillingConstants.DEBUG) {
               Log.i(TAG, "supported: " + supported);
           }
           if (supported) {
               restoreDatabase();
               mPayButton.setEnabled(true);
               //mEditPayloadButton.setEnabled(true);
           } else {
               showDialog(DIALOG_BILLING_NOT_SUPPORTED_ID);
           }
       }

       /**
        * If the database has not been initialized, we send a
        * RESTORE_TRANSACTIONS request to Android Market to get the list of purchased items
        * for this user. This happens if the application has just been installed
        * or the user wiped data. We do not want to do this on every startup, rather, we want to do
        * only when the database needs to be initialized.
        */
       private void restoreDatabase() {
           SharedPreferences prefs = getPreferences(MODE_PRIVATE);
           boolean initialized = prefs.getBoolean(DB_INITIALIZED, false);
           if (!initialized) {
               mBillingService.restoreTransactions();
               Toast.makeText(DashboardActivity.myContext, "Restoring Transactions", Toast.LENGTH_LONG).show();
           }
       }

      @Override
       public void onPurchaseStateChange(PurchaseState purchaseState, String itemId,
               int quantity, long purchaseTime, String developerPayload) {
           if (BillingConstants.DEBUG) {
               Log.i(TAG, "onPurchaseStateChange() itemId: " + itemId + " " + purchaseState);
           }

           if (developerPayload == null) {
               logProductActivity(itemId, purchaseState.toString());
           } else {
               logProductActivity(itemId, purchaseState + "\n\t" + developerPayload);
           }

           if (purchaseState == PurchaseState.PURCHASED) {
               mOwnedItems.add(itemId);
           }
           mCatalogAdapter.setOwnedItems(mOwnedItems);
           mOwnedItemsCursor.requery();
       }

       @Override
       public void onRequestPurchaseResponse(RequestPurchase request,
               ResponseCode responseCode) {
           if (BillingConstants.DEBUG) {
               Log.d(TAG, request.mProductId + ": " + responseCode);
           }
           if (responseCode == ResponseCode.RESULT_OK) {
               if (BillingConstants.DEBUG) {
                   Log.i(TAG, "purchase was successfully sent to server");
               }
               logProductActivity(request.mProductId, "sending purchase request");
           } else if (responseCode == ResponseCode.RESULT_USER_CANCELED) {
               if (BillingConstants.DEBUG) {
                   Log.i(TAG, "user canceled purchase");
               }
               logProductActivity(request.mProductId, "dismissed purchase dialog");
           } else {
               if (BillingConstants.DEBUG) {
                   Log.i(TAG, "purchase failed");
               }
               logProductActivity(request.mProductId, "request purchase returned " + responseCode);
           }
       }

       @Override
       public void onRestoreTransactionsResponse(RestoreTransactions request,
               ResponseCode responseCode) {
           if (responseCode == ResponseCode.RESULT_OK) {
               if (BillingConstants.DEBUG) {
                   Log.d(TAG, "completed RestoreTransactions request");
               }
               // Update the shared preferences so that we don't perform
               // a RestoreTransactions again.
               SharedPreferences prefs = getPreferences(Context.MODE_PRIVATE);
               SharedPreferences.Editor edit = prefs.edit();
               edit.putBoolean(DB_INITIALIZED, true);
               edit.commit();
           } else {
               if (BillingConstants.DEBUG) {
                   Log.d(TAG, "RestoreTransactions error: " + responseCode);
               }
           }
       }
   }

   private void logProductActivity(String product, String activity) {
      SpannableStringBuilder contents = new SpannableStringBuilder();
      contents.append(Html.fromHtml("<b>" + product + "</b>: "));
      contents.append(activity);
      prependLogEntry(contents);
  }
   
   private void prependLogEntry(CharSequence cs) {
      SpannableStringBuilder contents = new SpannableStringBuilder(cs);
      contents.append('\n');
      contents.append(mLogTextView.getText());
      mLogTextView.setText(contents);
  }

   private static class CatalogAdapter extends ArrayAdapter<String> {
      private CatalogEntry[] mCatalog;
      private Set<String> mOwnedItems = new HashSet<String>();

      public CatalogAdapter(Context context, CatalogEntry[] catalog) {
          super(context, android.R.layout.simple_spinner_item);
          mCatalog = catalog;
          for (CatalogEntry element : catalog) {
              add(context.getString(element.nameId));
          }
          setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
      }

      public void setOwnedItems(Set<String> ownedItems) {
          mOwnedItems = ownedItems;
          notifyDataSetChanged();
      }

      @Override
      public boolean areAllItemsEnabled() {
          // Return false to have the adapter call isEnabled()
          return false;
      }

      @Override
      public boolean isEnabled(int position) {
          // If the item at the given list position is not purchasable,
          // then prevent the list item from being selected.
          CatalogEntry entry = mCatalog[position];
          if (entry.managed == Managed.MANAGED && mOwnedItems.contains(entry.sku)) {
              return false;
          }
          return true;
      }

      @Override
      public View getDropDownView(int position, View convertView, ViewGroup parent) {
          // If the item at the given list position is not purchasable, then
          // "gray out" the list item.
          View view = super.getDropDownView(position, convertView, parent);
          view.setEnabled(isEnabled(position));
          return view;
      }
  }

   
}
