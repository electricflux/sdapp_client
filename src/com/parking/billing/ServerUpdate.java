package com.parking.billing;

import com.parking.datamanager.ParkingLocationDataEntry;

public class ServerUpdate {

   private IServerUpdateListener callbackHandler = null;
   private int responseCode;
   private ParkingLocationDataEntry spotWhereParked;
   
   public void updatePayment(ParkingLocationDataEntry parkingLocationObj) {
      this.spotWhereParked = parkingLocationObj;
      
   }

   public void registerListener(IServerUpdateListener serverUpdateListener) {
      this.callbackHandler = serverUpdateListener;
      
   }
   
   public void respondWithServerResponse(){
      callbackHandler.handleCallback(responseCode);
   }

   
}
