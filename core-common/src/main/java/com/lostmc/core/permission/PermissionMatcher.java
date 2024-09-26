package com.lostmc.core.permission;

import java.util.concurrent.ExecutionException;
import java.util.regex.Pattern;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.LoadingCache;

public class PermissionMatcher {
	
	private LoadingCache<String, Pattern> patternCache;

	public PermissionMatcher() {
		patternCache = CacheBuilder.newBuilder().maximumSize(500).build(new RegexCacheLoader());
	}

	public boolean isMatches(String expression, String permission) {
		try {
			return patternCache.get(expression).matcher(permission).matches();
		} catch (ExecutionException e) {
			e.printStackTrace();
			return false;
		}
	}
}
