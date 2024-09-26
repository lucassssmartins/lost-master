package com.lostmc.core.property;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import javax.annotation.ParametersAreNonnullByDefault;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.gson.JsonObject;
import com.lostmc.core.Commons;

public class IPropertyGetter {
	
	private static LoadingCache<String, IProperty> cache = CacheBuilder.newBuilder().expireAfterAccess(11L, TimeUnit.MINUTES)
			.build(new CacheLoader<String, IProperty>() {
				@Override
				@ParametersAreNonnullByDefault
				public IProperty load(String uuid) throws Exception {
					return getProperty(uuid, true);
				}
			});
	
	private static final String SKIN_URL = "https://api.minetools.eu/profile/%uuid%";
	private static final String SKIN_URL_MOJANG = "https://sessionserver.mojang.com/session/minecraft/profile/%uuid%?unsigned=false";
	private static final String SKIN_URL_BACKUP = "https://api.ashcon.app/mojang/v2/user/%uuid%";

	public static IProperty createProperty(UUID id, String value, String signature) {
		return new IProperty(id, value, signature);
	}

	public static IProperty getProperty(String uuid) {
		try {
			return cache.get(uuid);
		} catch (Exception e) {
			return null;
		}
	}

	private static IProperty getProperty(String uuid, boolean tryNext) {
		try {
			String output = readURL(SKIN_URL.replace("%uuid%", uuid));
			JsonObject obj = Commons.getGson().fromJson(output, JsonObject.class);

			if (obj.has("raw")) {
				JsonObject raw = obj.getAsJsonObject("raw");

				if (raw.has("status") && raw.get("status").getAsString().equalsIgnoreCase("ERR")) {
					return getPropertyMojang(uuid, true);
				}

				IProperty property = new IProperty();
				if (property.valuesFromJson(UUID.fromString(uuid), raw)) {
					return property;
				}
			}
		} catch (Exception e) {
			if (tryNext)
				return getPropertyMojang(uuid, true);
		}

		return null;
	}

	private static IProperty getPropertyMojang(String uuid, boolean tryNext) {
		try {
			String output = readURL(SKIN_URL_MOJANG.replace("%uuid%", uuid));
			JsonObject obj = Commons.getGson().fromJson(output, JsonObject.class);

			IProperty property = new IProperty();
			if (obj.has("properties") && property.valuesFromJson(UUID.fromString(uuid), obj)) {
				return property;
			}
		} catch (Exception e) {
			if (tryNext)
				return getPropertyBackup(uuid, true);
		}

		return null;
	}

	private static IProperty getPropertyBackup(String uuid, boolean tryNext) {
		try {
			String output = readURL(SKIN_URL_BACKUP.replace("%uuid%", uuid), 10000);
			JsonObject obj = Commons.getGson().fromJson(output, JsonObject.class);
			JsonObject textures = obj.get("textures").getAsJsonObject();
			JsonObject rawTextures = textures.get("raw").getAsJsonObject();

			return new IProperty(UUID.fromString(uuid), rawTextures.get("value").getAsString(),
					rawTextures.get("signature").getAsString());
		} catch (Exception e) {
		}

		return null;
	}

	private static String readURL(String url) throws IOException {
		return readURL(url, 5000);
	}

	private static String readURL(String url, int timeout) throws IOException {
		HttpURLConnection con = (HttpURLConnection) new URL(url).openConnection();

		con.setRequestMethod("GET");
		con.setRequestProperty("User-Agent", "LostMC");
		con.setConnectTimeout(timeout);
		con.setReadTimeout(timeout);
		con.setDoOutput(true);

		String line;
		StringBuilder output = new StringBuilder();
		BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));

		while ((line = in.readLine()) != null)
			output.append(line);

		in.close();
		return output.toString();
	}
}
