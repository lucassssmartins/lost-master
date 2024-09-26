package com.lostmc.pvp.constructor;

import com.lostmc.game.constructor.Gamer;
import com.lostmc.pvp.warp.Warp;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

public class PvPGamer extends Gamer {

    @Getter
    @Setter
    private Warp warp;

    public PvPGamer(UUID uniqueId, String name) {
        super(uniqueId, name);
    }
}
