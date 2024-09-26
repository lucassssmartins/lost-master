package com.lostmc.core.profile.collect;

import com.lostmc.core.profile.rank.Rank;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Getter
@AllArgsConstructor
public enum Collectable {

    //GADGETS
    DEFAULT_TRAMPOLINE("default_trampoline", "hub.gadget.default_trampoline", CollectCategory.GADGET,
            CollectRarity.LEGENDARY, Rank.LOST),
    FIREWORK("firework", "hub.gadget.firework", CollectCategory.GADGET, CollectRarity.EPIC, Rank.VIP);

    private String name;
    private String permission;
    private CollectCategory category;
    private CollectRarity rarity;
    private Rank requiredRank;

    public static List<Collectable> getByCategory(CollectCategory category) {
        return Stream.of(values()).filter(a -> a.getCategory().equals(category)).collect(Collectors.toList());
    }

    public static List<Collectable> getByCategoryAndRarity(CollectCategory category
            , CollectRarity rarity) {
        return Stream.of(values()).filter(a -> a.getCategory().equals(category) && a.getRarity().equals(rarity)).collect(Collectors.toList());
    }
}
