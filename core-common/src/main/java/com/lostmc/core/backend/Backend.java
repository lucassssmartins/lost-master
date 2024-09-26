package com.lostmc.core.backend;

public interface Backend {

    void connect() throws Throwable;

    void disconnect() throws Throwable;

    boolean isConnected();
}
