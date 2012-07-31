package com.gurugv.wonderboard;


import java.io.Serializable;
import java.util.Date;

import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;

public class ClipboardStorageService implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2385507138316648427L;
	private static final String COL_DATA = "DATA";
	private static final String CLIPBOARDSTORAGE_PREFIX = "CPSTORE_KEY";
	private static final String COL_TIME = "TS";
	private final String userId;
	private final Key lastItemKey;

	public ClipboardStorageService(String userId, String userPass) {
		super();
		this.userId = userId;
		lastItemKey = KeyFactory.createKey(CLIPBOARDSTORAGE_PREFIX, userId);
		initializeToken();
	}
	
	private String initializeToken(){
		String token = Math.random()+"";
		return token;
	}
	
	
	public void updateNewClipboardItem(String item){
		
		Entity clipboardentity = new Entity(lastItemKey);
		clipboardentity.setProperty(COL_DATA, item);
		clipboardentity.setProperty(COL_TIME, new Date());
		DatastoreServiceFactory.getAsyncDatastoreService().put(clipboardentity);
		System.out.println(lastItemKey+ " SAVED "+item+" for user "+userId);
	}
	
	public String get()  {
		Entity entity;
		try {
			entity = DatastoreServiceFactory.getDatastoreService().get(lastItemKey);
		} catch (EntityNotFoundException e) {
			e.printStackTrace();
			return "NOT FOUND";
		}
		return (String) entity.getProperty(COL_DATA);
	}
}
