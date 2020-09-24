package com.newrelic.lib;

import java.util.*;
import java.lang.*;

import java.io.Serializable;

public class AppConfigDependency implements Serializable
{
    public AppConfigDependency() 
    {
    }
    
	public String getId() {
		return this.id;
    }	
	public void setId(String id) {
		this.id = id;
	}

	public String[] getUrls() {
		return this.urls;
    }	
	public void setUrls(String[] urls) {
		this.urls = urls;
	}

	private String id;
	private String[] urls = {};
}
