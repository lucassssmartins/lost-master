package com.lostmc.pvp.listener;

import com.lostmc.bukkit.listener.BukkitListener;
import com.lostmc.core.profile.Profile;
import com.lostmc.core.profile.data.DataType;
import com.lostmc.game.constructor.Gamer;
import com.lostmc.pvp.constructor.PvPGamer;
import com.lostmc.pvp.warp.Warp;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class SignListener extends BukkitListener {

    @SuppressWarnings("deprecation")
    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            if (event.getClickedBlock() != null) {
                if (event.getClickedBlock().getType() == Material.WALL_SIGN
                        || (event.getClickedBlock().getType() == Material.SIGN_POST)) {
                    Sign s = (Sign) event.getClickedBlock().getState();
                    String[] lines = s.getLines();
                    if (lines.length > 3) {
                        if (lines[0].equals("§b§lLOST") && lines[2].equals("§6§m>-----<") && lines[3].equals(" ")) {
                            if (lines[1].equals("§bSopas")) {
                                event.setCancelled(true);
                                Inventory soup = inv(54, "§bSopas");
                                for (int i = 0; i < 54; i++)
                                    soup.setItem(i, new ItemStack(Material.MUSHROOM_SOUP));
                                player.openInventory(soup);
                            } else if (lines[1].equals("§eRecraft")) {
                                event.setCancelled(true);
                                Inventory recraft = inv(9, "§eRecraft");
                                recraft.setItem(3, new ItemStack(Material.BOWL, 64));
                                recraft.setItem(4, new ItemStack(Material.RED_MUSHROOM, 64));
                                recraft.setItem(5, new ItemStack(Material.BROWN_MUSHROOM, 64));
                                player.openInventory(recraft);
                            } else if (lines[1].equals("§cCocoabean")) {
                                event.setCancelled(true);
                                Inventory cocoa = inv(9, "§cCocoabean");
                                cocoa.setItem(3, new ItemStack(Material.BOWL, 64));
                                cocoa.setItem(5, new ItemStack(Material.getMaterial(351), 64, (byte) 3));
                                player.openInventory(cocoa);
                            } else if (lines[1].equals("§aCactus")) {
                                event.setCancelled(true);
                                Inventory cactu = inv(9, "§aCactus");
                                cactu.setItem(3, new ItemStack(Material.BOWL, 64));
                                cactu.setItem(5, new ItemStack(Material.CACTUS, 64));
                                player.openInventory(cactu);
                            }
                        } else if (lines[0].equals("§b§lLAVA") && lines[2].equals("§6§m>-----<") && lines[3].equals(" ")) {
                            Profile account = Profile.getProfile(player);
                            PvPGamer gamer = (PvPGamer) account.getResource(Gamer.class);
                            Warp warp = gamer.getWarp();
                            if (lines[1].equals("§aFácil")) {
                                event.setCancelled(true);
                                warp.joinPlayer(player, warp);
                                account.setData(DataType.PVP_LAVA_EASY_COMPLETIONS,
                                        account.getData(DataType.PVP_LAVA_EASY_COMPLETIONS).getAsInt()
                                                + 1);
                                account.setData(DataType.COINS, account.getData(DataType.COINS).getAsInt()
                                        + 20);
                                account.save();
                                player.sendMessage("§aParabéns, você completou o nível Fácil!");
                                player.sendMessage("§6+" + 20 + " coins");
                            } else if (lines[1].equals("§eMédio")) {
                                event.setCancelled(true);
                                warp.joinPlayer(player, warp);
                                account.setData(DataType.PVP_LAVA_MEDIUM_COMPLETIONS,
                                        account.getData(DataType.PVP_LAVA_MEDIUM_COMPLETIONS).getAsInt()
                                                + 1);
                                account.setData(DataType.COINS, account.getData(DataType.COINS).getAsInt()
                                        + 50);
                                account.save();
                                player.sendMessage("§aParabéns, você completou o nível §eMédio§a!");
                                player.sendMessage("§6+" + 50 + " coins");
                            } else if (lines[1].equals("§cDifícil")) {
                                event.setCancelled(true);
                                warp.joinPlayer(player, warp);
                                account.setData(DataType.PVP_LAVA_HARD_COMPLETIONS,
                                        account.getData(DataType.PVP_LAVA_HARD_COMPLETIONS).getAsInt()
                                                + 1);
                                account.setData(DataType.COINS, account.getData(DataType.COINS).getAsInt()
                                        + 100);
                                account.save();
                                player.sendMessage("§aParabéns, você completou o nível §cDifícil§a!");
                                player.sendMessage("§6+" + 100 + "COINS");
                            } else if (lines[1].equals("§4Extreme")) {
                                event.setCancelled(true);
                                warp.joinPlayer(player, warp);
                                account.setData(DataType.PVP_LAVA_EXTREME_COMPLETIONS,
                                        account.getData(DataType.PVP_LAVA_EXTREME_COMPLETIONS).getAsInt()
                                                + 1);
                                account.setData(DataType.COINS, account.getData(DataType.COINS).getAsInt()
                                        + 300);
                                account.save();
                                player.sendMessage("§aParabéns, você completou o nível §4Extreme§a!");
                                player.sendMessage("§6+" + 300 + " coins");
                                Bukkit.broadcastMessage(ChatColor.RED + "" + ChatColor.BOLD
                                        + "LavaChallenge" + ChatColor.DARK_GRAY +
                                        ChatColor.BOLD + " >> " + ChatColor.GRAY + player.getName()
                                        + " completou o nivel extreme do Lava Challenge");
                            }
                        }
                    }
                }
            }
        }
    }

    protected Inventory inv(int size, String title) {
        return Bukkit.createInventory(null, size, title);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onSignChange(SignChangeEvent event) {
        if (event.getLine(0).contains("&")) {
            event.setLine(0, event.getLine(0).replace("&", "§"));
        }
        if (event.getLine(1).contains("&")) {
            event.setLine(1, event.getLine(1).replace("&", "§"));
        }
        if (event.getLine(2).contains("&")) {
            event.setLine(2, event.getLine(2).replace("&", "§"));
        }
        if (event.getLine(3).contains("&")) {
            event.setLine(3, event.getLine(3).replace("&", "§"));
        }

        String line = event.getLine(0);

        if (line.equalsIgnoreCase("extreme")) {
            event.setLine(0, "§b§lLAVA");
            event.setLine(1, "§4Extreme");
            event.setLine(2, "§6§m>-----<");
            event.setLine(3, " ");
        } else if (line.equalsIgnoreCase("hard")) {
            event.setLine(0, "§b§lLAVA");
            event.setLine(1, "§cDifícil");
            event.setLine(2, "§6§m>-----<");
            event.setLine(3, " ");
        } else if (line.equalsIgnoreCase("medium")) {
            event.setLine(0, "§b§lLAVA");
            event.setLine(1, "§eMédio");
            event.setLine(2, "§6§m>-----<");
            event.setLine(3, " ");
        } else if (line.equalsIgnoreCase("easy")) {
            event.setLine(0, "§b§lLAVA");
            event.setLine(1, "§aFácil");
            event.setLine(2, "§6§m>-----<");
            event.setLine(3, " ");
        } else if (line.equalsIgnoreCase("sopa") || line.equalsIgnoreCase("sopas")) {
            event.setLine(0, "§b§lLOST");
            event.setLine(1, "§bSopas");
            event.setLine(2, "§6§m>-----<");
            event.setLine(3, " ");
        } else if (line.equalsIgnoreCase("recraft") || line.equalsIgnoreCase("recrafts")) {
            event.setLine(0, "§b§lLOST");
            event.setLine(1, "§eRecraft");
            event.setLine(2, "§6§m>-----<");
            event.setLine(3, " ");
        } else if (line.equalsIgnoreCase("cocoa") || line.equalsIgnoreCase("cocoabean")) {
            event.setLine(0, "§b§lLOST");
            event.setLine(1, "§cCocoabean");
            event.setLine(2, "§6§m>-----<");
            event.setLine(3, " ");
        } else if (line.equalsIgnoreCase("cactu") || line.equalsIgnoreCase("cactus")) {
            event.setLine(0, "§b§lLOST");
            event.setLine(1, "§aCactus");
            event.setLine(2, "§6§m>-----<");
            event.setLine(3, " ");
        }
    }
}
