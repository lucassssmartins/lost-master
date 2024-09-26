package com.lostmc.pvp.warp.registry;

import com.lostmc.bukkit.api.item.InteractHandler;
import com.lostmc.bukkit.api.item.ItemBuilder;
import com.lostmc.bukkit.api.menu.MenuClickHandler;
import com.lostmc.bukkit.api.menu.MenuInventory;
import com.lostmc.bukkit.api.scoreboard.Scoreboard;
import com.lostmc.bukkit.api.scoreboard.ScoreboardHandler;
import com.lostmc.bukkit.event.timer.ServerTimerEvent;
import com.lostmc.bukkit.event.vanish.PlayerUnvanishEvent;
import com.lostmc.bukkit.event.vanish.PlayerVanishEvent;
import com.lostmc.bukkit.vanish.VanishController;
import com.lostmc.core.profile.Profile;
import com.lostmc.core.profile.data.DataType;
import com.lostmc.core.profile.division.DivisionHandler;
import com.lostmc.core.profile.division.EloCalculator;
import com.lostmc.game.constructor.Gamer;
import com.lostmc.pvp.PvP;
import com.lostmc.pvp.constructor.PvPGamer;
import com.lostmc.pvp.status.StatusHandler;
import com.lostmc.pvp.warp.Warp;
import com.lostmc.pvp.warp.WarpController;
import com.lostmc.pvp.warp.WarpScoreboardModel;
import com.lostmc.pvp.warp.event.PlayerDeathInWarpEvent;
import com.lostmc.pvp.warp.registry.system.FightLoc1Warp;
import com.lostmc.pvp.warp.registry.system.FightLoc2Warp;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

public class FightWarp extends Warp {

    private List<Fight> fights;
    private Queue<UUID> rankedQueue, unrankedQueue;
    private List<DefaultMatch> defaultMatchs;
    private List<CustomMatch> customMatchs;
    private Map<String, ItemStack> items;

    public FightWarp(WarpController controller) {
        super(controller);
        setName("1v1");
        setScoreboardModelClass(Spawn1v1ScoreboardModel.class);

        this.fights = new ArrayList<>();
        this.rankedQueue = new ConcurrentLinkedQueue<>();
        this.unrankedQueue = new ConcurrentLinkedQueue<>();
        this.defaultMatchs = new ArrayList<>();
        this.customMatchs = new ArrayList<>();

        this.items = new HashMap<>();

        InteractHandler normalHandler = (player, item, action, clicked) -> {
            if (!isInFight(player)) {
                if (unrankedQueue.contains(player.getUniqueId())) {
                    unrankedQueue.remove(player.getUniqueId());
                    player.setItemInHand(items.get("unranked-queue-off"));
                    player.sendMessage("§cVocê saiu da fila para 1v1 (Unranked)");
                } else {
                    if (rankedQueue.contains(player.getUniqueId()))
                        rankedQueue.remove(player.getUniqueId());
                    unrankedQueue.add(player.getUniqueId());
                    player.setItemInHand(items.get("unranked-queue-on"));
                    player.sendMessage("§aVocê entrou na fila para 1v1 (Unranked)");
                }
            }
            return true;
        };

        this.items.put("unranked-queue-off", new ItemBuilder(Material.INK_SACK)
                .setDurability(8).setName("§aProcurar partida §7(Unranked)")
                .build(normalHandler));
        this.items.put("unranked-queue-on", new ItemBuilder(Material.INK_SACK)
                .setDurability(10).setName("§aProcurando partidas §7(Unranked)")
                .build(normalHandler));

        InteractHandler rankedHandler = (player, item, action, clicked) -> {
            if (!isInFight(player)) {
                if (rankedQueue.contains(player.getUniqueId())) {
                    rankedQueue.remove(player.getUniqueId());
                    player.setItemInHand(items.get("ranked-queue-off"));
                    player.sendMessage("§cVocê saiu da fila para 1v1 (Ranked)");
                } else {
                    if (unrankedQueue.contains(player.getUniqueId()))
                        unrankedQueue.remove(player.getUniqueId());
                    rankedQueue.add(player.getUniqueId());
                    player.setItemInHand(items.get("ranked-queue-on"));
                    player.sendMessage("§aVocê entrou na fila para 1v1 (Ranked)");
                }
            }
            return true;
        };

        this.items.put("ranked-queue-off", new ItemBuilder(Material.INK_SACK).setDurability(8)
                .setName("§aProcurar partida §7(Ranked)")
                .build(rankedHandler));
        this.items.put("ranked-queue-on", new ItemBuilder(Material.INK_SACK).setDurability(10)
                .setName("§aProcurando partidas §7(Ranked)")
                .build(rankedHandler));

        this.items.put("normal-duel", new ItemBuilder(Material.BLAZE_ROD).setName("§aDesafiar 1v1")
                .build());
        this.items.put("custom-duel", new ItemBuilder(Material.IRON_FENCE).setName("§aDesafiar 1v1 Customizado")
                .build());
    }

    public boolean isInFight(Player p) {
        for (Fight fight : fights) {
            if (!fight.containsPlayer(p))
                continue;
            return true;
        }
        return false;
    }

    public Fight getFight(Player p) {
        for (Fight fight : fights) {
            if (!fight.containsPlayer(p))
                continue;
            return fight;
        }
        return null;
    }

    public boolean stopFight(Player p) {
        Iterator<Fight> it = fights.iterator();
        while (it.hasNext()) {
            Fight next = it.next();
            if (!next.containsPlayer(p))
                continue;
            it.remove();
            return true;
        }
        return false;
    }

    public CustomMatch getCustomMatch(Player owner, Player matched) {
        return customMatchs.stream().filter(m -> m.getOwner().equals(owner) &&
                m.getTarget().equals(matched)).findFirst().orElse(null);
    }

    public DefaultMatch getDefaultMatch(Player owner, Player matched) {
        return defaultMatchs.stream().filter(m -> m.getOwner().equals(owner) &&
                m.getTarget().equals(matched)).findFirst().orElse(null);
    }

    public void clearPendingMatches(Player p) {
        defaultMatchs.removeIf(m -> m.getOwner().equals(p) || m.getTarget().equals(p));
        customMatchs.removeIf(m -> m.getOwner().equals(p) || m.getTarget().equals(p));
    }

    public void startFight(Player p1, Player p2, CustomOption.Soup soupOption, CustomOption.Sharpness sharpnessOption,
                           CustomOption.Recraft recraftOption, CustomOption.Armor armorOption, CustomOption.Sword swordOption,
                           CustomOption.Speed speedOption, boolean ranked) {
        removeFromQueue(p1);
        removeFromQueue(p2);

        clearPendingMatches(p1);
        clearPendingMatches(p2);

        giveItems(p1, soupOption, sharpnessOption, recraftOption, armorOption, swordOption, speedOption);
        giveItems(p2, soupOption, sharpnessOption, recraftOption, armorOption, swordOption, speedOption);

        p1.sendMessage("§eVocê está lutando contra: §6" + p2.getName());
        p2.sendMessage("§eVocê está lutando contra: §6" + p1.getName());

        Location loc1 = getController().getWarpByClass(FightLoc1Warp.class).getLocation();
        if (loc1 != null)
            p1.teleport(loc1);
        Location loc2 = getController().getWarpByClass(FightLoc2Warp.class).getLocation();
        if (loc2 != null) {
            p2.teleport(loc2);
        }

        fights.add(new Fight(p1, p2, ranked, System.currentTimeMillis()));

        for (Player o : Bukkit.getOnlinePlayers()) {
            if (o.equals(p1)) {
                p2.showPlayer(o);
            } else if (o.equals(p2)) {
                p1.showPlayer(o);
            } else {
                p1.hidePlayer(o);
                p2.hidePlayer(o);
            }
        }

        setFightScoreboard(p1);
        setFightScoreboard(p2);
    }

    private void setFightScoreboard(Player p) {
        Scoreboard scoreboard = ScoreboardHandler.getInstance().getScoreboard(p);
        scoreboard.setModel(new Fighting1v1ScoreboardModel(scoreboard));
    }

    private void giveItems(Player p, CustomOption.Soup soupOption, CustomOption.Sharpness sharpnessOption,
                           CustomOption.Recraft recraftOption, CustomOption.Armor armorOption, CustomOption.Sword swordOption,
                           CustomOption.Speed speedOption) {
        p.getInventory().clear();

        ItemStack soup = new ItemBuilder(Material.MUSHROOM_SOUP).build();

        if (soupOption == CustomOption.Soup.FULL) {
            for (int i = 0; i <= 35; i++)
                p.getInventory().addItem(soup);
        } else {
            for (int i = 1; i <= 8; i++)
                p.getInventory().setItem(i, soup);
        }

        String type = "DIAMOND_";

        if (armorOption == CustomOption.Armor.EMPTY)
            type = null;
        else if (armorOption == CustomOption.Armor.IRON)
            type = "IRON_";
        else if (armorOption == CustomOption.Armor.LEATHER)
            type = "LEATHER_";
        else if (armorOption == CustomOption.Armor.CHAIN)
            type = "CHAINMAIL_";

        if (type != null) {
            p.getInventory().setArmorContents(new ItemStack[]
                    {new ItemStack(Material.getMaterial(type + "BOOTS")),
                            new ItemStack(Material.getMaterial(type + "LEGGINGS")),
                            new ItemStack(Material.getMaterial(type + "CHESTPLATE")),
                            new ItemStack(Material.getMaterial(type + "HELMET"))});
        }

        CustomOption.Sword sword = swordOption;
        type = "DIAMOND_";

        if (swordOption == CustomOption.Sword.IRON)
            type = "IRON_";
        else if (swordOption == CustomOption.Sword.STONE)
            type = "STONE_";
        else if (swordOption == CustomOption.Sword.WOOD)
            type = "WOOD_";
        else if (swordOption == CustomOption.Sword.GOLD) {
            type = "GOLD_";
        }

        boolean hasSharpness = sharpnessOption != CustomOption.Sharpness.EMPTY;
        ItemBuilder item = new ItemBuilder(Material.getMaterial(type + "SWORD")).setUnbreakable(true);

        if (hasSharpness) {
            if (sharpnessOption == CustomOption.Sharpness.I)
                item.addEnchant(Enchantment.DAMAGE_ALL, 1);
            else if (sharpnessOption == CustomOption.Sharpness.II)
                item.addEnchant(Enchantment.DAMAGE_ALL, 2);
            else if (sharpnessOption == CustomOption.Sharpness.III)
                item.addEnchant(Enchantment.DAMAGE_ALL, 3);
            else if (sharpnessOption == CustomOption.Sharpness.IV)
                item.addEnchant(Enchantment.DAMAGE_ALL, 4);
            else if (sharpnessOption == CustomOption.Sharpness.V) {
                item.addEnchant(Enchantment.DAMAGE_ALL, 5);
            }
        }

        p.getInventory().setItem(0, item.build());

        if (speedOption != CustomOption.Speed.EMPTY) {
            if (speedOption == CustomOption.Speed.I) {
                p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 100000 * 20, 0));
            } else if (speedOption == CustomOption.Speed.II) {
                p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 100000 * 20, 1));
            }
        }

        if (recraftOption != CustomOption.Recraft.EMPTY) {
            if (recraftOption == CustomOption.Recraft.MUSHROOM) {
                p.getInventory().setItem(13, new ItemBuilder(Material.BOWL).setAmount(64).build());
                p.getInventory().setItem(14, new ItemBuilder(Material.RED_MUSHROOM).setAmount(64)
                        .build());
                p.getInventory().setItem(15, new ItemBuilder(Material.BROWN_MUSHROOM).setAmount(64)
                        .build());
            } else if (recraftOption == CustomOption.Recraft.COCOA) {
                p.getInventory().setItem(13, new ItemBuilder(Material.BOWL).setAmount(64).build());
                p.getInventory().setItem(14, new ItemBuilder(Material.INK_SACK).setDurability(3)
                        .setAmount(64).build());
            }
        }

        p.updateInventory();
    }

    private void removeFromQueue(Player p) {
        rankedQueue.remove(p.getUniqueId());
        unrankedQueue.remove(p.getUniqueId());
    }

    @EventHandler
    public void onInteractListener(PlayerInteractEntityEvent e) {
        Player p = e.getPlayer();
        if (containsPlayer(p)) {
            if (e.getRightClicked() instanceof Player) {
                Player clicked = (Player) e.getRightClicked();
                if (containsPlayer(clicked)) {
                    if (!isInFight(p) && !isInFight(clicked)) {
                        if (p.getItemInHand() != null) {
                            ItemStack hand = p.getItemInHand();
                            if (hand.equals(items.get("normal-duel"))) {
                                e.setCancelled(true);
                                Match oldMatch = getDefaultMatch(clicked, p);
                                if (oldMatch != null) {
                                    startFight(p, clicked, oldMatch.getSoupOption(), oldMatch.getSharpnessOption(), oldMatch.getRecraftOption(),
                                            oldMatch.getArmorOption(), oldMatch.getSwordOption(), oldMatch.getSpeedOption(), false);
                                } else {
                                    DefaultMatch defaultMatch = getDefaultMatch(p, clicked);
                                    if (defaultMatch != null) {
                                        p.sendMessage("§cAguarde para enviar outro desafio NORMAL a este jogador!");
                                    } else {
                                        defaultMatchs.add(new DefaultMatch(p, clicked));
                                        clicked.sendMessage("§6" + p.getName() + "§e te desafiou para 1v1 normal!");
                                        p.sendMessage("§eVocê desafiou §6" + clicked.getName() + "§e para 1v1 normal!");
                                    }
                                }
                            } else if (hand.equals(items.get("custom-duel"))) {
                                e.setCancelled(true);
                                CustomMatch oldMatch = getCustomMatch(clicked, p);
                                if (oldMatch != null) {
                                    startFight(p, clicked, oldMatch.getSoupOption(), oldMatch.getSharpnessOption(), oldMatch.getRecraftOption(),
                                            oldMatch.getArmorOption(), oldMatch.getSwordOption(), oldMatch.getSpeedOption(), false);
                                } else {
                                    CustomMatch customMatch = getCustomMatch(p, clicked);
                                    if (customMatch != null) {
                                        p.sendMessage("§cAguarde para enviar outro desafio CUSTOMIZADO a este jogador!");
                                    } else {
                                        p.openInventory(new CustomMatchMenu(p, clicked));
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public class CustomMatchMenu extends MenuInventory {

        private CustomOption.Soup soupOption = CustomOption.Soup.HOTBAR;
        private CustomOption.Sharpness sharpnessOption = CustomOption.Sharpness.I;
        private CustomOption.Recraft recraftOption = CustomOption.Recraft.EMPTY;
        private CustomOption.Armor armorOption = CustomOption.Armor.IRON;
        private CustomOption.Sword swordOption = CustomOption.Sword.DIAMOND;
        private CustomOption.Speed speedOption = CustomOption.Speed.EMPTY;

        public CustomMatchMenu(Player owner, Player target) {
            super(54, "§8Configure a batalha!");

            for (int i = 0; i < getSize(); i++) {
                setItem(i, new ItemBuilder(Material.STAINED_GLASS_PANE).setName(" ").build(), new MenuClickHandler() {
                    @Override
                    public void onClick(Player p, Inventory inv, ClickType type, ItemStack stack, int slot) {
                        // Nope
                    }
                });
            }

            for (int i : new int[]{43, 44, 52, 53}) {
                setItem(i, new ItemBuilder(Material.WOOL).setName("§aEnviar desafio!")
                        .setDurability(5).build(), new MenuClickHandler() {
                    @Override
                    public void onClick(Player p, Inventory inv, ClickType type, ItemStack stack, int slot) {
                        p.closeInventory();
                        if (target != null && target.isOnline()) {
                            if (FightWarp.this.containsPlayer(target)) {
                                if (!FightWarp.this.isInFight(target)) {
                                    customMatchs.add(new CustomMatch(p, target, soupOption,
                                            sharpnessOption, recraftOption, armorOption, swordOption,
                                            speedOption));
                                    target.sendMessage("§6" + p.getName() + "§e te desafiou para 1v1 customizado!");
                                    p.sendMessage("§eVocê desafiou §6" + target.getName() + "§e para 1v1 customizado!");
                                } else {
                                    p.sendMessage("§cO jogador está em uma partida 1v1.");
                                }
                            } else {
                                p.sendMessage("§cO jogador saiu da warp 1v1.");
                            }
                        } else {
                            p.sendMessage("§cJogador inválido!");
                        }
                    }
                });
            }

            MenuClickHandler swordClick = new MenuClickHandler() {
                @Override
                public void onClick(Player p, Inventory inv, ClickType type, ItemStack stack, int slot) {
                    setItem(20, getItem(swordOption = swordOption.next()), this);
                }
            };
            setItem(20, getItem(swordOption), swordClick);

            MenuClickHandler armorClick = new MenuClickHandler() {
                @Override
                public void onClick(Player p, Inventory inv, ClickType type, ItemStack stack, int slot) {
                    setItem(21, getItem(armorOption = armorOption.next()), this);
                }
            };
            setItem(21, getItem(armorOption), armorClick);

            MenuClickHandler speedClick = new MenuClickHandler() {
                @Override
                public void onClick(Player p, Inventory inv, ClickType type, ItemStack stack, int slot) {
                    setItem(22, getItem(speedOption = speedOption.next()), this);
                }
            };
            setItem(22, getItem(speedOption), speedClick);

            MenuClickHandler soupClick = new MenuClickHandler() {
                @Override
                public void onClick(Player p, Inventory inv, ClickType type, ItemStack stack, int slot) {
                    setItem(23, getItem(soupOption = soupOption.next()), this);
                }
            };
            setItem(23, getItem(soupOption), soupClick);

            MenuClickHandler sharpClick = new MenuClickHandler() {
                @Override
                public void onClick(Player p, Inventory inv, ClickType type, ItemStack stack, int slot) {
                    setItem(24, getItem(sharpnessOption = sharpnessOption.next()), this);
                }
            };
            setItem(24, getItem(sharpnessOption), sharpClick);

            MenuClickHandler recraftClick = new MenuClickHandler() {
                @Override
                public void onClick(Player p, Inventory inv, ClickType type, ItemStack stack, int slot) {
                    setItem(29, getItem(recraftOption = recraftOption.next()), this);
                }
            };
            setItem(29, getItem(recraftOption), recraftClick);
        }

        private ItemStack getItem(Enum<?> e) {
            try {
                Method method = e.getClass().getDeclaredMethod("getIconMaterial");
                method.setAccessible(true);
                ItemBuilder builder = new ItemBuilder((Material) method.invoke(e));
                method = e.getClass().getDeclaredMethod("getData");
                method.setAccessible(true);
                builder.setDurability((int) method.invoke(e));
                method = e.getClass().getDeclaredMethod("getDescription");
                method.setAccessible(true);
                builder.setName((String) method.invoke(e));
                return builder.build();
            } catch (Exception ex) {
                ex.printStackTrace();
                return new ItemStack(Material.GLASS);
            }
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onEntityDamage(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player) || !(event.getDamager() instanceof Player))
            return;
        Player entity = (Player) event.getEntity();
        if (!containsPlayer(entity))
            return;
        Player damager = (Player) event.getDamager();
        if (isInFight(entity) && !isInFight(damager)) {
            event.setCancelled(true);
        } else if (!isInFight(entity) && isInFight(damager)) {
            event.setCancelled(true);
        } else if (isInFight(entity) && isInFight(damager)) {
            Fight eFight = getFight(entity);
            Fight dFight = getFight(damager);
            if (!eFight.equals(dFight)) {
                event.setCancelled(true);
            } else {
                event.setCancelled(false);
            }
        }
    }

    @EventHandler
    public void onCommandListener(PlayerCommandPreprocessEvent event) {
        if (isInFight(event.getPlayer())) {
            if (event.getMessage().toLowerCase().startsWith("/spawn")
                    || event.getMessage().toLowerCase().startsWith("/warp")) {
                event.setCancelled(true);
                event.getPlayer().sendMessage("§cTermine a batalha para usar este comando.");
            }
        } else if (unrankedQueue.contains(event.getPlayer().getUniqueId()) ||
                rankedQueue.contains(event.getPlayer().getUniqueId())) {
            if (event.getMessage().toLowerCase().startsWith("/spawn")
                    || event.getMessage().toLowerCase().startsWith("/warp")) {
                event.setCancelled(true);
                event.getPlayer().sendMessage("§cSaia da fila de 1v1 para usar este comando!");
            }
        }
    }

    @EventHandler
    public void onVanish(PlayerVanishEvent event) {
        Player p = event.getPlayer();
        Player viewer = event.getViewer();

        Fight fight = getFight(viewer);
        if (fight == null)
            return;

        if (!fight.containsPlayer(p))
            return;

        event.setCancelled(true);
    }

    @EventHandler
    public void onVanish(PlayerUnvanishEvent event) {
        Player p = event.getPlayer();
        Player viewer = event.getViewer();

        Fight fight = getFight(viewer);
        if (fight == null)
            return;

        if (fight.containsPlayer(p))
            return;

        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onJoinListener(PlayerJoinEvent event) {
        Player p = event.getPlayer();
        for (Fight f : fights) {
            f.getPlayer1().hidePlayer(p);
            f.getPlayer2().hidePlayer(p);
        }
    }

    @EventHandler
    public void onServerTimerListener(ServerTimerEvent event) {
        if (event.getCurrentTick() % 1 != 0)
            return;
        rankedQueue();
        //unrankedQueue();
    }

    private void rankedQueue() {
        if (this.rankedQueue.size() <= 1)
            return;

        Player player1 = Bukkit.getPlayer(rankedQueue.poll());
        Player player2 = Bukkit.getPlayer(rankedQueue.poll());

        if (player1 == null) {
            if (player2 != null) {
                this.rankedQueue.add(player2.getUniqueId());
                return;
            }
        } else if (player2 == null) {
            this.rankedQueue.add(player1.getUniqueId());
            return;
        } else {
            startFight(player1, player2, CustomOption.Soup.HOTBAR, CustomOption.Sharpness.I, CustomOption.Recraft.EMPTY,
                    CustomOption.Armor.IRON, CustomOption.Sword.DIAMOND, CustomOption.Speed.EMPTY, true);
        }
    }

    private void unrankedQueue() {
        if (this.unrankedQueue.size() <= 1)
            return;

        Player player1 = Bukkit.getPlayer(unrankedQueue.poll());
        Player player2 = Bukkit.getPlayer(unrankedQueue.poll());

        if (player1 == null) {
            if (player2 != null) {
                this.unrankedQueue.add(player2.getUniqueId());
                return;
            }
        } else if (player2 == null) {
            this.unrankedQueue.add(player1.getUniqueId());
            return;
        } else {
            startFight(player1, player2, CustomOption.Soup.HOTBAR, CustomOption.Sharpness.I, CustomOption.Recraft.EMPTY,
                    CustomOption.Armor.IRON, CustomOption.Sword.DIAMOND, CustomOption.Speed.EMPTY, false);
        }
    }

    @EventHandler
    public void onDeathInWarpListener(PlayerDeathInWarpEvent e) {
        if (e.getWarp().equals(this))
            e.setDropItems(false);
        Fight fight = getFight(e.getPlayer());
        if (fight != null) {
            onFightEnd(e.getPlayer(), fight);
        }
    }

    public void onFightEnd(Player loser, Fight fight) {
        Player winner = fight.getOponent(loser);

        String healthLeft = String.format("%.1f", winner.getHealth() / 2);
        int soupsLeft = 0;
        for (int i = 0; i <= 35; i++) {
            ItemStack stack = winner.getInventory().getItem(i);
            if (stack == null || stack.getType() == Material.AIR)
                continue;
            if (stack.getType() == Material.MUSHROOM_SOUP) {
                soupsLeft += stack.getAmount();
            }
        }
        loser.sendMessage("§c" + winner.getName() + " venceu o 1v1 com " + healthLeft
                + " corações e " + soupsLeft + " sopas restantes.");
        winner.sendMessage("§aVocê venceu o 1v1 contra " + loser.getName() + " com " + healthLeft
                + " corações e " + soupsLeft + " sopas restantes.");

        Profile pWinner = Profile.getProfile(winner);
        Profile pLoser = Profile.getProfile(loser);

        if (fight.isRanked()) {
            int winnerElo = pWinner.getData(DataType.FIGHT_1V1_ELO).getAsInt();
            int loserElo = pLoser.getData(DataType.FIGHT_1V1_ELO).getAsInt();

            EloCalculator calculator = DivisionHandler.getInstance().getEloCalculator();
            EloCalculator.Result result = calculator.calculate(winnerElo, loserElo);

            int eloGain = result.getWinnerGain();
            pWinner.setData(DataType.FIGHT_1V1_ELO, (winnerElo + eloGain));
            winner.sendMessage("§b+" + eloGain + " ELO");
            pWinner.save();

            int eloLost = Math.abs(result.getLoserGain());
            if (loserElo > 0) {
                pLoser.setData(DataType.FIGHT_1V1_ELO, (loserElo - eloLost <= 0 ? 0 : loserElo - eloLost));
                loser.sendMessage("§c-" + eloLost + " ELO");
                pLoser.save();
            }
        }

        stopFight(loser);
        joinPlayer(winner, this);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onQuitListener(PlayerQuitEvent e) {
        Player player = e.getPlayer();
        Fight fight = getFight(player);
        if (fight != null) {
            StatusHandler.updateStatus(player, fight.getOponent(player));
            onFightEnd(player, fight);
        }
    }

    @EventHandler
    public void onInventoryClickListener(InventoryClickEvent event) {
        if (event.getWhoClicked() instanceof Player) {
            Player p = (Player) event.getWhoClicked();
            if (containsPlayer(p) && !isInFight(p) && !PvP.getControl().getController(VanishController.class)
                    .isVanished(p)) {
                if (event.getAction() == InventoryAction.PICKUP_ONE ||
                        event.getAction() == InventoryAction.PICKUP_ALL ||
                        event.getAction() == InventoryAction.COLLECT_TO_CURSOR) {
                    event.setCancelled(true);
                }
            }
        }
    }

    @Override
    public void onPlayerJoin(Player player) {
        removeFromQueue(player);
        clearPendingMatches(player);
        //player.getInventory().setItem(1, this.items.get("unranked-queue-off"));
        player.getInventory().setItem(3, this.items.get("normal-duel"));
        player.getInventory().setItem(4, this.items.get("custom-duel"));
        player.getInventory().setItem(5, this.items.get("ranked-queue-off"));
        getController().getServer().getScheduler().runTask(getController().getPlugin(),
                () -> getController().getControl().getController(VanishController.class).updateVanishToPlayer(player));
    }

    @Override
    public void onProtectionLost(Player player) {

    }

    @Override
    public void onPlayerLeave(Player player) {
        removeFromQueue(player);
    }

    @EventHandler
    public void onTimerListener(ServerTimerEvent event) {
        if (event.getCurrentTick() % 20 != 0)
            return;
        defaultMatchs.removeIf(DefaultMatch::isExpired);
        customMatchs.removeIf(CustomMatch::isExpired);
    }

    @Getter
    public static class DefaultMatch extends Match {

        public DefaultMatch(Player owner, Player target) {
            super(owner, target, System.currentTimeMillis(), CustomOption.Soup.HOTBAR,
                    CustomOption.Sharpness.I, CustomOption.Recraft.EMPTY,
                    CustomOption.Armor.IRON, CustomOption.Sword.DIAMOND, CustomOption.Speed.EMPTY);
        }
    }

    @Getter
    public static class CustomMatch extends Match {

        public CustomMatch(Player owner, Player target, CustomOption.Soup soupOption,
                           CustomOption.Sharpness sharpnessOption, CustomOption.Recraft recraftOption,
                           CustomOption.Armor armorOption, CustomOption.Sword swordOption,
                           CustomOption.Speed speedOption) {
            super(owner, target, System.currentTimeMillis(), soupOption, sharpnessOption,
                    recraftOption, armorOption, swordOption, speedOption);
        }
    }

    @AllArgsConstructor
    @Getter
    public static abstract class Match {

        private final Player owner;
        private final Player target;
        private final long sentTime;

        private final CustomOption.Soup soupOption;
        private final CustomOption.Sharpness sharpnessOption;
        private final CustomOption.Recraft recraftOption;
        private final CustomOption.Armor armorOption;
        private final CustomOption.Sword swordOption;
        private final CustomOption.Speed speedOption;

        public boolean isExpired() {
            return this.sentTime + 10000L < System.currentTimeMillis();
        }
    }

    @Getter
    @AllArgsConstructor
    public class Fight {

        private Player player1, player2;
        private boolean ranked;
        private long startTime;

        public boolean containsPlayer(Player p) {
            return player1.equals(p) || player2.equals(p);
        }

        public Player getOponent(Player p) {
            if (p.equals(player1))
                return player2;
            else if (p.equals(player2))
                return player1;
            else
                return null;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o)
                return true;
            if (o instanceof Fight) {
                Fight that = (Fight) o;
                return that.player1.equals(this.player1) &&
                        that.player2.equals(this.player2) && that.ranked == this.ranked;
            }
            return false;
        }
    }

    @Getter
    @RequiredArgsConstructor
    public static class CustomDuelReceivedEvent extends Event {

        private static final HandlerList handlers = new HandlerList();

        @Override
        public HandlerList getHandlers() {
            return handlers;
        }

        public static HandlerList getHandlerList() {
            return handlers;
        }
    }

    public static class CustomOption {

        @AllArgsConstructor
        @Getter
        public enum Soup {
            FULL(Material.MUSHROOM_SOUP, 0, "§eInventário: §bFULL SOPA"),
            HOTBAR(Material.INK_SACK, 0, "§eInventário: §b1 HOTBAR");

            private Material iconMaterial;
            private int data;
            private String description;

            public Soup next() {
                if (ordinal() + 1 >= values().length)
                    return values()[0];
                return values()[ordinal() + 1];
            }
        }

        @AllArgsConstructor
        @Getter
        public enum Sharpness {
            EMPTY(Material.BOOK, 0, "§eAfiação da espada: §bDESATIVADO"),
            I(Material.ENCHANTED_BOOK, 0, "§eAfiação da espada: §b§l1"),
            II(Material.ENCHANTED_BOOK, 0, "§eAfiação da espada: §b§l2"),
            III(Material.ENCHANTED_BOOK, 0, "§eAfiação da espada: §b§l3"),
            IV(Material.ENCHANTED_BOOK, 0, "§eAfiação da espada: §b§l4"),
            V(Material.ENCHANTED_BOOK, 0, "§eAfiação da espada: §b§l5");

            private Material iconMaterial;
            private int data;
            private String description;

            public Sharpness next() {
                if (ordinal() + 1 >= values().length)
                    return values()[0];
                return values()[ordinal() + 1];
            }
        }

        @AllArgsConstructor
        @Getter
        public enum Recraft {
            EMPTY(Material.BOWL, 0, "§eRecraft: §bDESATIVADO"),
            COCOA(Material.INK_SACK, 3, "§eRecraft: §bCOCOA BEANS"),
            MUSHROOM(Material.RED_MUSHROOM, 0, "§eRecraft: §bCOGUMELOS");

            private Material iconMaterial;
            private int data;
            private String description;

            public Recraft next() {
                if (ordinal() + 1 >= values().length)
                    return values()[0];
                return values()[ordinal() + 1];
            }
        }

        @AllArgsConstructor
        @Getter
        public enum Armor {
            EMPTY(Material.GOLD_HELMET, 0, "§eArmaduras: §bDESATIVADO"),
            LEATHER(Material.LEATHER_CHESTPLATE, 0, "§eArmaduras: §bCOURO"),
            CHAIN(Material.CHAINMAIL_CHESTPLATE, 0, "§eArmaduras: §bMALHA"),
            IRON(Material.IRON_CHESTPLATE, 0, "§eArmaduras: §bFERRO"),
            DIAMOND(Material.DIAMOND_CHESTPLATE, 0, "§eArmaduras: §bDIAMANTE");

            private Material iconMaterial;
            private int data;
            private String description;

            public Armor next() {
                if (ordinal() + 1 >= values().length)
                    return values()[0];
                return values()[ordinal() + 1];
            }
        }

        @AllArgsConstructor
        @Getter
        public enum Sword {
            WOOD(Material.WOOD_SWORD, 0, "§eEspada: §bMADEIRA"),
            GOLD(Material.GOLD_SWORD, 0, "§eEspada: §bOURO"),
            STONE(Material.STONE_SWORD, 0, "§eEspada: §bPEDRA"),
            IRON(Material.IRON_SWORD, 0, "§eEspada: §bFERRO"),
            DIAMOND(Material.DIAMOND_SWORD, 0, "§eEspada: §bDIAMANTE");

            private Material iconMaterial;
            private int data;
            private String description;

            public Sword next() {
                if (ordinal() + 1 >= values().length)
                    return values()[0];
                return values()[ordinal() + 1];
            }
        }

        @AllArgsConstructor
        @Getter
        public enum Speed {
            EMPTY(Material.GLASS_BOTTLE, 0, "§ePoção de velocidade: §bDESATIVADO"),
            I(Material.POTION, 0, "§ePoção de velocidade: §bVELOCIDADE I"),
            II(Material.POTION, 0, "§ePoção de velocidade: §bVELOCIDADE II");

            private Material iconMaterial;
            private int data;
            private String description;

            public Speed next() {
                if (ordinal() + 1 >= values().length)
                    return values()[0];
                return values()[ordinal() + 1];
            }
        }
    }

    public static class Spawn1v1ScoreboardModel extends WarpScoreboardModel {

        public Spawn1v1ScoreboardModel(Scoreboard scoreboard) {
            super(scoreboard);
        }

        @Override
        public List<String> getModel(Player player) {
            getScoreboard().setDisplayName("§6§lPVP: 1V1");
            perPlayer.clear();

            Profile profile = Profile.getProfile(player);

            perPlayer.add("");
            perPlayer.add("§fVitórias: §7" + profile.getData(DataType.FIGHT_1V1_KILLS).getAsInt());
            perPlayer.add("§fDerrotas: §7" + profile.getData(DataType.FIGHT_1V1_DEATHS).getAsInt());
            perPlayer.add("§fWinstreak: §a" + profile.getData(DataType.FIGHT_1V1_KS).getAsInt());
            perPlayer.add("§fELO: §a" + profile.getData(DataType.FIGHT_1V1_ELO).getAsInt());
            perPlayer.add("");
            perPlayer.add("§fJogadores: §7" + Bukkit.getOnlinePlayers().size() + "/" + Bukkit.getMaxPlayers());
            perPlayer.add("");
            perPlayer.add("§ewww.lostmc.com.br");

            return perPlayer;
        }
    }

    public static class Fighting1v1ScoreboardModel extends WarpScoreboardModel {

        public Fighting1v1ScoreboardModel(Scoreboard scoreboard) {
            super(scoreboard);
        }

        @Override
        public List<String> getModel(Player player) {
            getScoreboard().setDisplayName("§6§lPVP: 1V1");
            perPlayer.clear();

            Profile profile = Profile.getProfile(player);

            perPlayer.add("");
            perPlayer.add("§fVitórias: §7" + profile.getData(DataType.FIGHT_1V1_KILLS).getAsInt());
            perPlayer.add("§fDerrotas: §7" + profile.getData(DataType.FIGHT_1V1_DEATHS).getAsInt());
            perPlayer.add("§fWinstreak: §a" + profile.getData(DataType.FIGHT_1V1_KS).getAsInt());
            perPlayer.add("§fELO: §a" + profile.getData(DataType.FIGHT_1V1_ELO).getAsInt());
            perPlayer.add("");
            perPlayer.add("§fJogadores: §7" + Bukkit.getOnlinePlayers().size() + "/" + Bukkit.getMaxPlayers());
            perPlayer.add("");
            perPlayer.add("§fLutando contra:");

            String oponentName = "NRE";
            Warp warp = ((PvPGamer) profile.getResource(Gamer.class)).getWarp();
            if (warp instanceof FightWarp) {
                Fight fight = ((FightWarp) warp).getFight(player);
                if (fight != null) {
                    oponentName = fight.getOponent(player).getName();
                }
            }

            perPlayer.add("§a" + oponentName);
            perPlayer.add("");
            perPlayer.add("§ewww.lostmc.com.br");

            return perPlayer;
        }
    }
}
