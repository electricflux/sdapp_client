package com.parking.billing;

import com.parking.billing.ParkingPayment.Managed;

public class CatalogEntry {
	public String sku;
	public int nameId;
	public Managed managed;

	public CatalogEntry(String sku, int nameId, Managed managed) {
		this.sku = sku;
		this.nameId = nameId;
		this.managed = managed;
	}
}
