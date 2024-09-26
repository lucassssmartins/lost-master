package com.lostmc.hungergames.structure;

import com.lostmc.hungergames.structure.items.FeastType;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;

public class Feast {

    @Getter
    private static int feastCount = 0;
    @Getter
    private int id;
    @Getter
    private String name;
    private FeastStructure structure;
    @Getter
    private FeastChestStructure chestStructure;
    @Getter
    @Setter
    private int counter = 0;
    @Getter
    private Location centralLocation;
    @Getter
    private boolean spawned = false;
    @Getter
    private boolean chestsSpawned = false;
    @Getter
    @Setter
    private boolean silently = false;

    public Feast(String name, int radius, int maxSpawnDistance, FeastType type, Location centralLocation,
                 int counter) {
        this.id = ++feastCount;
        this.name = name;
        this.structure = new FeastStructure(radius, maxSpawnDistance);
        this.chestStructure = new FeastChestStructure(type);
        this.centralLocation = centralLocation;
        this.counter = counter;
    }

    public boolean isDefault() {
        return id == 1;
    }

    public Feast(String name, int radius, int maxSpawnDistance, FeastType type, int counter) {
        this.id = ++feastCount;
        this.name = name;
        this.structure = new FeastStructure(radius, maxSpawnDistance);
        this.chestStructure = new FeastChestStructure(type);
        this.centralLocation = structure.findPlace();
        this.counter = counter;
    }

    public void spawn() {
        if (spawned)
            return;
        structure.place(centralLocation);
        spawned = true;
    }

    public void spawnChests() {
        if (chestsSpawned)
            return;
        chestStructure.place(centralLocation);
        chestsSpawned = true;
    }

    public boolean count() {
        return --counter <= 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
       if (o instanceof Feast) {
           Feast that = (Feast) o;
           return that.id == this.id && that.name.equals(this.name);
       }
       return false;
    }
}
