package com.parking.billing;

import java.util.HashSet;
import java.util.Set;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.parking.billing.ParkingPayment.Managed;

/**
 * An adapter used for displaying a catalog of products.  If a product is
 * managed by Android Market and already purchased, then it will be "grayed-out" in
 * the list and not selectable.
 */
public class CatalogAdapter extends ArrayAdapter<String> {
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
