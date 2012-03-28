package com.parking.datamanager;

public class ParkingLocationDataEntry {
   
	private enum ParkingType {
		PRIVATELOT, MULTISPOT, STREET
	};

	// PrimaryKey
	private Long id;

	// Persistent
	private float latitude;

	// Persistent
	private float longitude;

	//parkingType
	private ParkingType parkingType;
	
	// Persistent
	private int rate;

	// Persistent
	private int duration;

	// Persistent
	private int type;

	// Persistent
	private int quantity;

	// Persistent
	private String address;

	// Persistent
	private String attendent;

	// Persistent
	private String contact;
}
