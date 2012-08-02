package com.gurugv.wonderboard;

import java.io.Serializable;
import java.util.HashMap;

import com.google.appengine.api.memcache.Expiration;
import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheService.SetPolicy;
import com.google.appengine.api.memcache.MemcacheServiceFactory;

public class SharedClipboardItems implements Serializable {

	private static final SetPolicy DEFAULT_SET_POLICY = SetPolicy.SET_ALWAYS;

	/**
	 * 
	 */
	private static final long serialVersionUID = -5246743764736031420L;

	private static final String MEMCACHE_SHAREDCLIPBOARD_PREFIX = "SCB.";
	private HashMap<String, HashMap<String, String>> sharedClipboards = new HashMap<String, HashMap<String, String>>();

	private static final int EXPIRATION_SECONDS = 60 * 60 * 4; // 4 hours

	private static final Expiration EXPIRE_BY_DELTA_SECONDS = Expiration.byDeltaSeconds(EXPIRATION_SECONDS);
	private MemcacheService memcache;

	private SharedClipboardItems() {
		memcache = MemcacheServiceFactory.getMemcacheService();
	}

	public static SharedClipboardItems getInstance() {
		return instance;
	}

	private final static SharedClipboardItems instance = new SharedClipboardItems();

	public HashMap<String, String> getSharedClipboards(String forUserId) {

		HashMap<String, String> clipsForUser = (HashMap<String, String>) memcache
				.get(MEMCACHE_SHAREDCLIPBOARD_PREFIX + forUserId);
		return clipsForUser;
	}

	public void addSharedClipboards(String forUserId, String fromUserId,
			String data) {
		HashMap<String, String> cbForUserMap = (HashMap<String, String>) memcache
				.get(MEMCACHE_SHAREDCLIPBOARD_PREFIX + forUserId);
		if (cbForUserMap == null) {
			cbForUserMap = new HashMap<String, String>();
		}
		cbForUserMap.put(fromUserId, data);
		memcache.put(MEMCACHE_SHAREDCLIPBOARD_PREFIX + forUserId, cbForUserMap,
				EXPIRE_BY_DELTA_SECONDS, DEFAULT_SET_POLICY);
	}

}
