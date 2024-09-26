package com.lostmc.core.property;

public enum SkinSource {

	ACCOUNT("skin-source.account"), 
	CUSTOM("skin-source.custom");
	
	private final String name;
	
	SkinSource(String name) {
		this.name = name;
	}
	
	public final String getName() {
		return this.name;
	}
}
