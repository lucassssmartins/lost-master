package com.lostmc.core.permission;

public abstract class PermissionManager<T> {

	public PermissionMatcher matcher;
	
	public PermissionManager() {
		matcher = new PermissionMatcher();
	}
	
	public PermissionMatcher getMatcher() {
		return this.matcher;
	}
	
	public abstract void updatePermissions(T player);
	
	public abstract void clearPermissions(T player);
}
