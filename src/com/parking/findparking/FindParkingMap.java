package com.parking.findparking;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;
import com.parking.dashboard.R;

public class FindParkingMap extends MapActivity {

   private MapView mapView;
   private MapController mapController;
   private LocationManager locationManager;
   private MapOverLays itemizedOverlays;

   public void onCreate(Bundle bundle) {

      super.onCreate(bundle);
      setContentView(R.layout.findparking);

      setUpMapView();
      setUpMapController();
      setUpLocationManager();

      List<Overlay> mapsOverLays = mapView.getOverlays();
      Drawable drawable = this.getResources().getDrawable(R.drawable.map_marker_blue);

      itemizedOverlays = new MapOverLays(drawable, this);
      createMarker();
   }

   public String ConvertPointToLocation(GeoPoint point) {
      String address = "";
      Geocoder geoCoder = new Geocoder(getBaseContext(), Locale.getDefault());
      try {
         List<Address> addresses = geoCoder.getFromLocation(point.getLatitudeE6() / 1E6, point.getLongitudeE6() / 1E6, 1);

         if (addresses.size() > 0) {
            for (int index = 0; index < addresses.get(0).getMaxAddressLineIndex(); index++)
               address += addresses.get(0).getAddressLine(index) + " ";
         }
      }

      catch (IOException e) {
         e.printStackTrace();
      }
      return address;
   }

   private void createMarker() {

      String address = "Not found";
      GeoPoint p = mapView.getMapCenter();
      address = ConvertPointToLocation(p);

      OverlayItem overlayitem = new OverlayItem(p, address, "My Location");
      itemizedOverlays.addOverlay(overlayitem);
      mapView.getOverlays().add(itemizedOverlays);

   }

   private void setUpLocationManager() {

      locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
      locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, new GeoUpdateHandler());

   }

   private void setUpMapController() {
      mapController = mapView.getController();
      mapController.setZoom(16); // Zoom 1 is world view

   }

   private void setUpMapView() {

      mapView = (MapView) findViewById(R.id.mapview);
      mapView.setBuiltInZoomControls(true);
      mapView.setSatellite(false);
      mapView.setStreetView(true);

   }

   @Override
   protected boolean isRouteDisplayed() {
      return false;
   }

   public class GeoUpdateHandler implements LocationListener {

      public void onProviderDisabled(String provider) {
      }

      public void onProviderEnabled(String provider) {
      }

      public void onStatusChanged(String provider, int status, Bundle extras) {
      }

      @Override
      public void onLocationChanged(Location location) {

         int lat = (int) (location.getLatitude() * 1E6);
         int lng = (int) (location.getLongitude() * 1E6);
         GeoPoint point = new GeoPoint(lat, lng);
         createMarker();
         mapController.animateTo(point); // mapController.setCenter(point);

      }
   }

}
