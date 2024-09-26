package com.lostmc.hungergames.constructor;

import com.lostmc.bukkit.api.item.InteractHandler;
import com.lostmc.bukkit.api.item.ItemBuilder;
import com.lostmc.bukkit.vanish.VanishController;
import com.lostmc.core.profile.Profile;
import com.lostmc.game.constructor.Gamer;
import com.lostmc.game.constructor.Kit;
import com.lostmc.game.manager.Management;
import com.lostmc.hungergames.HungerGames;
import com.lostmc.hungergames.HungerGamesMode;
import com.lostmc.hungergames.listener.InventoryListener;
import com.lostmc.hungergames.manager.GamerManager;
import com.lostmc.hungergames.menu.AlivePlayersMenu;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

@Getter
@Setter
public class HungerGamer extends Gamer {

    public static ItemStack alivePlayers = new ItemBuilder(Material.COMPASS).setName("§aJogadores §7(Clique)")
            .build(new InteractHandler() {
                @Override
                public boolean onInteract(Player player, ItemStack item, Action action, Block clicked) {
                    if (!getGamer(player).isAlive())
                        player.openInventory(new AlivePlayersMenu());
                    return true;
                }
            });

    private GamerState gamerState;
    private boolean spectators = true;
    private int matchKills = 0;
    private boolean hiddingSpecs = true;

    public HungerGamer(UUID uniqueId, String name) {
        super(uniqueId, name);
    }

    public void setGamerState(GamerState gamerState) {
        this.gamerState = gamerState;
        Player p = Bukkit.getPlayer(getUniqueId());
        if (p != null) {
            switch (gamerState) {
                case ALIVE:
                    for (Player ps : Bukkit.getOnlinePlayers()) {
                        if (!ps.equals(p)) {
                            ps.showPlayer(p);
                            if (!isHiddingSpecs()) {
                                if (getGamer(ps).isNotPlaying()) {
                                    if (HungerGames.getControl().getController(VanishController.class)
                                            .isVanished(ps)) {
                                        if (Profile.getProfile(ps).getRank().ordinal() <
                                                Profile.getProfile(p).getRank().ordinal())
                                            continue;
                                    }
                                    p.showPlayer(ps);
                                }
                            } else if (getGamer(ps).isNotPlaying()) {
                                p.hidePlayer(ps);
                            }
                            if (getGamer(ps).isAlive() && !p.canSee(ps))
                                p.showPlayer(ps);
                        }
                    }
                    p.getInventory().clear();
                    if (p.getAllowFlight())
                        p.setAllowFlight(false);
                    p.setGameMode(GameMode.ADVENTURE);
                    p.getInventory().remove(alivePlayers);
                    HungerGamesMode hgMode = (HungerGamesMode) ((HungerGames) HungerGames.getInstance())
                            .getGameMode();
                    if (((HungerGames) hgMode.getMain()).getGameStage().isPregame()) {
                        if (!p.getInventory().contains(InventoryListener.singleChest))
                            p.getInventory().addItem(InventoryListener.singleChest);
                        if (hgMode.isDoubleKit()) {
                            if (!p.getInventory().contains(InventoryListener.doubleChest))
                                p.getInventory().addItem(InventoryListener.doubleChest);
                        }
                        if (!p.getInventory().contains(InventoryListener.kitStore))
                            p.getInventory().setItem(8, InventoryListener.kitStore);
                    } else {
                        for (Kit kit : getKits().values()) {
                            if (kit.getItems() != null) {
                                p.getInventory().addItem(kit.getItems());
                            }
                        }
                        if (!p.getInventory().contains(Material.COMPASS)) {
                            p.getInventory().addItem(new ItemStack(Material.COMPASS));
                        }
                    }
                    p.updateInventory();
                    break;
                case GAMEMAKER:
                case SPECTATOR:
                    hidePlayerFromAlive(p);
                    if (!p.getAllowFlight()) {
                        p.setAllowFlight(true);
                        p.setFlying(true);
                    }
                    p.getInventory().clear();
                    p.getInventory().setItem(5, alivePlayers);
                    p.updateInventory();
            }
        }
    }

    private void hidePlayerFromAlive(Player p) {
        for (Player ps : Bukkit.getOnlinePlayers()) {
            if (!ps.equals(p)) {
                HungerGamer gamer = getGamer(ps);
                if (gamer.isAlive() && gamer.isHiddingSpecs()) {
                    ps.hidePlayer(p);
                }
            }
        }
    }

    public void setPrimaryKit(Kit kit) {
        getKits().put(1, kit);
    }

    public void setSecondaryKit(Kit kit) {
        getKits().put(2, kit);
    }

    public Kit getPrimaryKit() {
        return getKits().get(1);
    }

    public Kit getSecondaryKit() {
        return getKits().get(2);
    }

    public boolean hasPrimaryKit() {
        Kit kit = getKits().get(1);
        return kit != null && !kit.getName().equals("Nenhum");
    }

    public boolean hasSecondaryKit() {
        Kit kit = getKits().get(2);
        return kit != null && !kit.getName().equals("Nenhum");
    }

    public boolean isSpectator() {
        return gamerState == GamerState.SPECTATOR;
    }

    public boolean isAlive() {
        return gamerState == GamerState.ALIVE;
    }

    public boolean isGamemaker() {
        return gamerState == GamerState.GAMEMAKER;
    }

    public boolean isNotPlaying() {
        return isSpectator() || isGamemaker();
    }

    public void remove() {
        Management.getManagement(GamerManager.class).removeGamer(getUniqueId());
    }

    public static HungerGamer getGamer(Player p) {
        return getGamer(p.getUniqueId());
    }

    public static HungerGamer getGamer(UUID uuid) {
        return Management.getManagement(GamerManager.class).getGamer(uuid);
    }

    public enum GamerState {

        ALIVE, SPECTATOR, GAMEMAKER;
    }
}
