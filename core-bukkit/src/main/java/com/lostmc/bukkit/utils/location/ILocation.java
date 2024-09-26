package com.lostmc.bukkit.utils.location;

import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.World;

@Getter
public class ILocation {

    private double x;
    private double y;
    private double z;
    private float yaw;
    private float pitch;

    public ILocation(Location location) {
        this.x = location.getX();
        this.y = location.getY();
        this.z = location.getZ();
        this.yaw = location.getYaw();
        this.pitch = location.getPitch();
    }

    public Location toLocation(World world) {
        return new Location(world, this.x, this.y, this.z, this.yaw, this.pitch);
    }
}
