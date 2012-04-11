package com.parking.datamanager;

import com.google.android.maps.GeoPoint;

public class ParkingLocationDataEntry {

	private enum ParkingType {
		PRIVATELOT, MULTISPOT, STREET
	};

	// PrimaryKey
	private Long id;

	//Every meter has a meter id
	private Long meterID;

	// Persistent
	private float latitude;

	// Persistent
	private float longitude;

	//parkingType
	private ParkingType parkingType;

	// Persistent
	private float rate;

	// Persistent
	private int duration;

	private float amountPaid;


	private long startTimestampMs;

	private long endTimestampMs;

	// Persistent
	private String type;

	// Persistent
	private int quantity;

	// Persistent
	private String address;

	// Persistent
	private String attendent;

	// Persistent
	private String contact;

	//Geopoint to simplify display
	private GeoPoint gpoint;

	//Distance from users current location. This value is dynamically populated after reading from DB
	double distance;


	//   public void setid(Long i) {
	//      id = i;
	//   }
	//
	//   public void setlatitude(float lat) {
	//      latitude = lat;
	//   }
	//
	//   public void setlongitude(float lon) {
	//      longitude = lon;
	//   }
	//
	//   public Long getid() {
	//      return id;
	//   }
	//
	//   public float getlatitude() {
	//      return latitude;
	//   }
	//
	//   public float getlongitude() {
	//      return longitude;
	//   }
	//
	//   public void setGeoPoint(GeoPoint geoPoint) {
	//      gpoint = geoPoint;
	//      
	//   }
	//
	//   public GeoPoint getGeoPoint() {
	//      return gpoint;
	//   }
	//
	//   public void setMeterId(Long meterId) {
	//      this.meterID = meterId; 
	//      
	//   }
	//
	//   public Long getMeterId() {
	//      return meterID;
	//   }
	//   
	public long getStartTimestampMs() {
		return startTimestampMs;
	}

	public void setStartTimestampMs(long startTimestampMs) {
		this.startTimestampMs = startTimestampMs;
	}

	public long getEndTimestampMs() {
		return endTimestampMs;
	}

	public void setEndTimestampMs(long endTimestampMs) {
		this.endTimestampMs = endTimestampMs;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getMeterID() {
		return meterID;
	}

	public void setMeterID(Long meterID) {
		this.meterID = meterID;
	}

	public float getLatitude() {
		return latitude;
	}

	public void setLatitude(float latitude) {
		this.latitude = latitude;
	}

	public float getLongitude() {
		return longitude;
	}

	public void setLongitude(float longitude) {
		this.longitude = longitude;
	}

	public ParkingType getParkingType() {
		return parkingType;
	}

	public void setParkingType(ParkingType parkingType) {
		this.parkingType = parkingType;
	}

	public float getRate() {
		return rate;
	}

	public void setRate(float dbRate) {
		this.rate = dbRate;
	}

	public int getDuration() {
		return duration;
	}

	public void setDuration(int duration) {
		this.duration = duration;
	}

	public String getType() {
		return type;
	}

	public void setType(String dbType) {

		this.type = dbType;


	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getAttendent() {
		return attendent;
	}

	public void setAttendent(String attendent) {
		this.attendent = attendent;
	}

	public String getContact() {
		return contact;
	}

	public void setContact(String contact) {
		this.contact = contact;
	}

	public GeoPoint getGeoPoint() {
		return gpoint;
	}

	public void setGeoPoint(GeoPoint gpoint) {
		this.gpoint = gpoint;
	}

	public void setDistance(double distance){
		this.distance = distance;
	}
	
	public double getDistance (){
		return distance;
	}

	public float getAmountPaid() {
		return amountPaid;
	}

	public void setAmountPaid(float amountPaid) {
		this.amountPaid = amountPaid;
	}

}