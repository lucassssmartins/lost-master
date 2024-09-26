package com.lostmc.core;

import com.lostmc.core.backend.mysql.MySQLBackend;
import com.lostmc.core.backend.redis.RedisBackend;
import com.lostmc.core.profile.Profile;
import com.lostmc.core.report.Report;
import com.lostmc.core.server.ProxyHandler;
import com.lostmc.core.storage.StorageCommon;
import com.lostmc.core.utils.mojang.UUIDFetcher;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;
import lombok.Getter;
import lombok.Setter;

import java.lang.reflect.Modifier;
import java.nio.charset.StandardCharsets;
import java.util.*;

public final class Commons {

    public static final Locale[] AVAILABLE_LOCALES = new Locale[] {
            Locale.forLanguageTag("pt-BR"),
            Locale.forLanguageTag("en-US") };

    public static final UUID CONSOLE_UNIQUEID = UUID.nameUUIDFromBytes(("Console$ender").
            getBytes(StandardCharsets.UTF_8));
    public static final Locale DEFAULT_LOCALE = AVAILABLE_LOCALES[0];

    @Getter
    @Setter
    private static boolean systemReady = false;
    @Getter
    private static Gson gson;
    @Getter
    private static JsonParser jsonParser;
    @Getter
    private static CommonsPlatform platform;
    @Getter
    private static UUIDFetcher uuidFetcher;
    @Getter
    @Setter
    private static ProxyHandler proxyHandler;
    @Getter
    @Setter
    private static MySQLBackend mysqlBackend;
    @Getter
    @Setter
    private static RedisBackend redisBackend;
    @Getter
    private static StorageCommon storageCommon;
    @Getter
    private static Map<UUID, Profile> profileMap;
    @Getter
    private static Map<UUID, Report> reportMap;
    @Getter
    private static final Random random = new Random();

    public static void initialize(CommonsPlatform platform) {
        Locale.setDefault(DEFAULT_LOCALE);
        Commons.gson = new GsonBuilder().disableHtmlEscaping().
                excludeFieldsWithModifiers(Modifier.STATIC, Modifier.TRANSIENT).
                create();
        Commons.jsonParser = new JsonParser();
        Commons.platform = platform;
        Commons.uuidFetcher = new UUIDFetcher();
        Commons.storageCommon = new StorageCommon();
        Commons.profileMap = new HashMap<>();
        Commons.reportMap = new HashMap<>();
    }

    public static Profile searchProfile(String name) {
        UUID id = platform.getUUIDOf(name);
        Profile profile = null;

        if (id != null) {
            profile = profileMap.get(id);
        }

        if (profile == null) {
            if ((id = uuidFetcher.getUUIDOf(name)) == null) {
                id = uuidFetcher.getFixedOfflineUUID(name);
            }
        }

        if (profile == null) try {
            profile = redisBackend.getRedisProfile(id);
        } catch (Exception error) {
            error.printStackTrace();
        }

        if (profile == null) {
            try {
                profile = storageCommon.getAccountStorage().getProfile(id);
            } catch (Exception error) {
                error.printStackTrace();
            }
        }

        return profile;
    }
}
