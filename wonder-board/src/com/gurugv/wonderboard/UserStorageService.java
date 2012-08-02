package com.gurugv.wonderboard;

import java.util.Date;

import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;

public class UserStorageService {

	private static final String USERTORAGE_PREFIX = "USERSTORE_KEY";
	private static UserStorageService instance;

	public static UserStorageService getInstance() {
		if (instance == null) {
			instance = new UserStorageService();
		}
		return instance;
	}

	private UserStorageService() {

	}

	public boolean checkIfExists(String userId) {

		Key userKey = getKey(userId);
		try {
			Entity ent = DatastoreServiceFactory.getDatastoreService().get(
					userKey);
			if (ent == null) {
				return false;
			}
		} catch (EntityNotFoundException e) {
		//	e.printStackTrace();
			return false;
		}
		return true;
	}

	Key getKey(String userId) {
		Key userKey = KeyFactory.createKey(USERTORAGE_PREFIX, userId);
		return userKey;
	}

	public boolean createAccount(String userId, String userPass) {
		Key userKey = getKey(userId);
		Entity entity = new Entity(userKey);
		entity.setProperty(Constants.PARAM_USERPASS, userPass);
		entity.setProperty("createdOn", new Date());
		DatastoreServiceFactory.getDatastoreService().put(entity);
		return true;
	}
	
	public boolean authenticate(String userId, String userPass) {
		Key userKey = getKey(userId);
		try {
			Entity useren = DatastoreServiceFactory.getDatastoreService().get(userKey);
			if(useren == null)
				return false;
			String storePass = (String) useren.getProperty(Constants.PARAM_USERPASS);
			if(storePass != null && storePass.equals(userPass)){
				return true;
			}
		} catch (EntityNotFoundException e) {
			e.printStackTrace();
			return false;
		}
		
		return false;	
	}

}
