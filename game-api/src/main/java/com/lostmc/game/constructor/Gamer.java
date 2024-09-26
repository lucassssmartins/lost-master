package com.lostmc.game.constructor;

import lombok.Getter;

import java.util.*;

@Getter
public class Gamer {

    private UUID uniqueId;
    private String name;
    private Map<Integer, Kit> kits = new KitMap();
    private CombatLog combatLog;

    public Gamer(UUID uniqueId, String name) {
        this.uniqueId = uniqueId;
        this.name = name;
        this.combatLog = new CombatLog(uniqueId);
    }

    private class KitMap extends HashMap<Integer, Kit> {

        @Override
        public Collection<Kit> values() {
            return new KitCollection(super.values());
        }
    }

    private class KitCollection extends HashSet<Kit> {

        public KitCollection(Collection<Kit> copy) {
            super(copy);
        }

        @Override
        public String toString() {
            if (isEmpty())
                return "(Nenhum)";
            return super.toString().replace("[", "(").replace("]", ")");
        }
    }
}
