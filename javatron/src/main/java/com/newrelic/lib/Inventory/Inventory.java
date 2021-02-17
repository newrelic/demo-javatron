package com.newrelic.lib.Inventory;

import java.io.Serializable;

public class Inventory implements Serializable {

	private String id;
	private String item;
	private String price;
	private String sku;

	public Inventory() {
	}

	public String getId() {
		return this.id;
    }	
	public void setId(String id) {
		this.id = id;
	}

	public String getItem() {
		return this.item;
    }	
	public void setItem(String item) {
		this.item = item;
	}

	public String getPrice() {
		return this.price;
    }	
	public void setPrice(String price) {
		this.price = price;
	}

	public String getSku() {
		return this.sku;
    }	
	public void setSku(String sku) {
		this.sku = sku;
	}
}
