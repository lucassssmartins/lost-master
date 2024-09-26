package com.lostmc.game.kit;

import lombok.Getter;

@Getter
public enum KitMenuType {

    PRIMARY(1),
    SECONDARY(2);

    private int id;

    KitMenuType(int id) {
        this.id = id;
    }
}
