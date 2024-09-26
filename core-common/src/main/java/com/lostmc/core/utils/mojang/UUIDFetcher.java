package com.lostmc.core.utils.mojang;


import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import javax.annotation.ParametersAreNonnullByDefault;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class UUIDFetcher {

    private List<String> apis = new ArrayList<>();
    private final JsonParser parser = new JsonParser();

    public UUIDFetcher() {
        apis.add("https://api.mojang.com/users/profiles/minecraft/%s");
        apis.add("https://api.mcuuid.com/json/uuid/%s");
        apis.add("https://api.minetools.eu/uuid/%s");
    }

    private LoadingCache<String, UUID> cache = CacheBuilder.newBuilder().
            expireAfterAccess(10L, TimeUnit.MINUTES).build(new CacheLoader<String, UUID>() {
        @Override
        @ParametersAreNonnullByDefault
        public UUID load(String name) throws Exception {
            return request(name);
        }
    });

    private UUID request(String name) {
        return request(0, apis.get(0), name);
    }

    private UUID request(int idx, String api, String name) {
        try {
            URLConnection con = new URL(String.format(api, name)).openConnection();
            JsonElement element = parser.parse(new BufferedReader(new InputStreamReader(con.getInputStream(), StandardCharsets.UTF_8)));
            if (element instanceof JsonObject) {
                JsonObject object = (JsonObject) element;
                if (object.has("error") && object.has("errorMessage")) {
                    throw new Exception(object.get("errorMessage").getAsString());
                } else if (object.has("id")) {
                    return UUIDParser.parse(object.get("id"));
                } else if (object.has("uuid")) {
                    JsonObject uuid = object.getAsJsonObject("uuid");
                    if (uuid.has("formatted")) {
                        return UUIDParser.parse(object.get("formatted"));
                    }
                }
            }
        } catch (Exception e) {
            idx++;
            if (idx < apis.size()) {
                api = apis.get(idx);
                return request(idx, api, name);
            }
        }

        return null;
    }

    public UUID getUUIDOf(String name) {
        if (name != null && !name.isEmpty()) {
            try {
                return cache.get(name);
            } catch (Exception e) {
            }
        }
        return null;
    }

    public UUID getFixedOfflineUUID(String name) {
        return UUID.nameUUIDFromBytes(("OfflinePlayer:" + name.toLowerCase()).
                getBytes(StandardCharsets.UTF_8));
    }
}
