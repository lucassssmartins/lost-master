package com.lostmc.pvp.status;

import com.lostmc.core.profile.Profile;
import com.lostmc.core.profile.data.DataType;
import com.lostmc.game.constructor.Gamer;
import com.lostmc.pvp.constructor.PvPGamer;
import com.lostmc.pvp.warp.Warp;
import com.lostmc.pvp.warp.registry.ArenaWarp;
import com.lostmc.pvp.warp.registry.FPSWarp;
import com.lostmc.pvp.warp.registry.FightWarp;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class StatusHandler {

    public static void updateStatus(Player killer, Player loser) {
        Profile pKiller = Profile.getProfile(killer);
        Profile pLoser = Profile.getProfile(loser);

        int coinsGain = 15;

        Warp killerWarp = ((PvPGamer) pKiller.getResource(Gamer.class)).getWarp();
        DataType[] kdr = null;
        if (killerWarp instanceof ArenaWarp || killerWarp instanceof FPSWarp) {
            kdr = new DataType[]{DataType.PVP_GLOBAL_KILLS, DataType.PVP_GLOBAL_DEATHS, DataType.PVP_GLOBAL_KS,
                    DataType.PVP_GLOBAL_GREATER_KS};
        } else if (killerWarp instanceof FightWarp) {
            kdr = new DataType[]{DataType.FIGHT_1V1_KILLS, DataType.FIGHT_1V1_DEATHS, DataType.FIGHT_1V1_KS,
                    DataType.FIGHT_1V1_GREATER_KS};
        }

        if (kdr != null) {
            pKiller.setData(kdr[0], pKiller.getData(kdr[0]).getAsInt() + 1);
            pKiller.setData(kdr[2], pKiller.getData(kdr[2]).getAsInt() + 1);
            if (pKiller.getData(kdr[2]).getAsInt() >= pKiller.getData(kdr[3]).getAsInt())
                pKiller.setData(kdr[3], pKiller.getData(kdr[2]).getAsInt());
            pLoser.setData(kdr[1], pLoser.getData(kdr[1]).getAsInt() + 1);

            if (pLoser.getData(kdr[2]).getAsInt() >= 10) {
                coinsGain += 10;
                String broadcast = ChatColor.DARK_RED + "" + ChatColor.BOLD + "KILLSTREAK "
                        + ChatColor.DARK_BLUE + ChatColor.BOLD + loser.getName() + ChatColor.WHITE + " perdeu seu "
                        + ChatColor.GOLD + ChatColor.BOLD + "KILLSTREAK DE " + pLoser.getData(kdr[2]).getAsInt()
                        + ChatColor.WHITE + " para " + ChatColor.RED + ChatColor.BOLD + killer.getName();
                killerWarp.getPlayers().forEach(p -> p.sendMessage(broadcast));
            }

            if (pKiller.getData(kdr[2]).getAsInt() % 5 == 0) {
                coinsGain += 10;
                String broadcast = ChatColor.DARK_RED + "" + ChatColor.BOLD + "KILLSTREAK " + ChatColor.RED
                        + ChatColor.BOLD + killer.getName() + ChatColor.WHITE + " conseguiu um " + ChatColor.GOLD
                        + ChatColor.BOLD + "KILLSTREAK DE " + pKiller.getData(kdr[2]).getAsInt();
                killerWarp.getPlayers().forEach(p -> p.sendMessage(broadcast));
            }

            pLoser.setData(kdr[2], 0);
        }

        killer.sendMessage("§eVocê matou §6" + loser.getName());
        loser.sendMessage("§cVocê foi morto por " + killer.getName());

        if (killerWarp instanceof FightWarp) {
            coinsGain += 5;
        }

        pKiller.setData(DataType.COINS, (pKiller.getData(DataType.COINS).getAsInt() + coinsGain));
        killer.sendMessage("§6+" + coinsGain + " coins");

        pKiller.save();
        pLoser.save();
    }
}
