package com.lostmc.core;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.lostmc.core.profile.data.DataType;
import org.slf4j.Logger;

import java.nio.file.Path;
import java.util.UUID;
import java.util.concurrent.ThreadFactory;

public interface CommonsPlatform {

    String getName();

    Path getPath();

    boolean isPluginInstalled(String paramString);

    String getNameOf(UUID uuid);

    UUID getUUIDOf(String name);

    void updateData(UUID id, DataType dataType, Object data);

    void runAsync(Runnable command);
}
