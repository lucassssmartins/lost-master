package com.lostmc.core.property;

import java.util.UUID;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class IProperty {

	private UUID id;
	private String value;
	private String signature;

	public IProperty(UUID id, String value, String signature) {
		this.id = id;
		this.value = value;
		this.signature = signature;
	}

	public IProperty() {}
	
	public UUID getId() {
		return this.id;
	}

	public String getValue() {
		return this.value;
	}

	public String getSignature() {
		return signature;
	}

	public boolean valuesFromJson(UUID id, JsonObject obj) {
		if (obj.has("properties")) {
			JsonArray properties = obj.getAsJsonArray("properties");
			if (properties.size() > 0) {
				JsonObject propertiesObject = properties.get(0).getAsJsonObject();

				this.id = id;
				this.signature = propertiesObject.get("signature").getAsString();
				this.value = propertiesObject.get("value").getAsString();

				return true;
			}
		}
		return false;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) return true;
		
		if (obj instanceof IProperty) {
			IProperty that = (IProperty) obj;
			return that.value.equals(this.value) && that.signature.equals(this.signature);
		}
		
		return false;
	}

	@Override
	public String toString() {
		return "{\"id\": \"" + this.id + "\", \"value\": \"" + this.value + "\", \"signature\": \"" + this.signature + "\"}";
	}
}
