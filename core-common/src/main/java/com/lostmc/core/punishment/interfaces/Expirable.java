package com.lostmc.core.punishment.interfaces;

public interface Expirable {

    long getExpiresIn();

    default boolean isLifetime() {
        return getExpiresIn() == -1;
    }

    default boolean isExpired() {
        return !isLifetime() && getExpiresIn() < System.currentTimeMillis();
    }
}
