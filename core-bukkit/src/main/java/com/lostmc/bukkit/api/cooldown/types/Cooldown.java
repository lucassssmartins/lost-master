package com.lostmc.bukkit.api.cooldown.types;

import java.util.concurrent.TimeUnit;

public class Cooldown {

    private String name;
    private Long duration;
    private long startTime = System.currentTimeMillis();
    
    public Cooldown(String name, Long duration) {
    	this.name = name;
    	this.duration = duration;
    }
    
    public String getName() {
    	return name;
    }

    public double getPercentage() {
        return (getRemaining() * 100) / duration;
    }

    public double getRemaining() {
        long endTime = startTime + TimeUnit.SECONDS.toMillis(duration);
        return (-(System.currentTimeMillis() - endTime)) / 1000D;
    }

    public boolean expired() {
        return getRemaining() < 0D;
    }
}
