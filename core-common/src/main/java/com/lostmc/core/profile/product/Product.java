package com.lostmc.core.profile.product;

import com.google.common.base.MoreObjects;
import lombok.Getter;

import java.util.UUID;

@Getter
public abstract class Product<T> {

    private T object;
    private String name;
    private UUID senderUniqueId;
    private String senderName;
    private boolean paid;
    private long receivedTime;
    private long expirationTime;

    public Product(T object, String name, UUID senderUniqueId, String senderName, boolean paid, long expirationTime) {
        this.object = object;
        this.name = name;
        this.senderUniqueId = senderUniqueId;
        this.senderName = senderName;
        this.paid = paid;
        this.receivedTime = System.currentTimeMillis();
        this.expirationTime = expirationTime;
    }

    public boolean isLifetime() {
        return this.expirationTime == -1;
    }

    public boolean isExpired() {
        return !isLifetime() && this.expirationTime < System.currentTimeMillis();
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("object", this.object)
                .add("name", this.name)
                .add("senderUniqueId", this.senderUniqueId)
                .add("senderName", this.senderName)
                .add("paid", this.paid)
                .add("received", this.receivedTime)
                .add("expirationTime", this.expirationTime)
                .toString();
    }
}
