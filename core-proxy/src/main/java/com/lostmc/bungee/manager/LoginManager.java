package com.lostmc.bungee.manager;

import com.lostmc.bungee.ProxyPlugin;
import com.lostmc.core.utils.DateUtils;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class LoginManager extends ProxyManager {

    private Map<String, List<AuthToken>> tokenMap = new HashMap<>();

    public LoginManager(ProxyPlugin plugin) {
        super(plugin);
    }

    @Override
    public void onEnable() {
        getProxy().getScheduler().schedule(getPlugin(), () -> {
            for (String ipAddress : new HashSet<>(tokenMap.keySet())) {
                List<AuthToken> tokenList = tokenMap.get(ipAddress);
                if (tokenList != null) {
                    tokenList.removeIf(token -> !token.isValid());
                }
            }
        }, 10, 10, TimeUnit.SECONDS);
    }

    public void addToken(String ipAddress, String name) {
        if (hasToken(ipAddress, name)) {
            List<AuthToken> tokenList = tokenMap.get(ipAddress);
            if (tokenList != null)
                tokenList.removeIf(token -> !token.isValid());
            tokenList.add(new AuthToken(name));
        } else {
            tokenMap.computeIfAbsent(ipAddress, v -> new ArrayList<>())
                    .add(new AuthToken(name));
        }
    }


    public boolean hasToken(String ipAddress, String name) {
        List<AuthToken> tokenList = tokenMap.get(ipAddress);
        if (tokenList != null)
            for (AuthToken authToken : tokenList)
                if (authToken.hasValidChild(name))
                    return true;
        return false;
    }

    @Override
    public void onDisable() {

    }

    public static class AuthToken {

        private String name;
        private long expiresIn;

        public AuthToken(String name) {
            this.name = name;
            try {
                this.expiresIn = DateUtils.parseDateDiff("10h", true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public boolean hasValidChild(String name) {
            return this.name.equalsIgnoreCase(name) && isValid();
        }

        public boolean isValid() {
            return expiresIn >= System.currentTimeMillis();
        }
    }
}
