package com.lostmc.pvp.command;

import com.lostmc.bukkit.command.WrappedBukkitCommand;
import com.lostmc.core.command.WrappedCommandSender;
import com.lostmc.core.profile.Profile;
import com.lostmc.game.constructor.Gamer;
import com.lostmc.pvp.constructor.PvPGamer;
import com.lostmc.pvp.util.Variable;
import com.lostmc.pvp.warp.Warp;
import com.lostmc.pvp.warp.registry.ArenaWarp;
import com.lostmc.pvp.warp.registry.SpawnWarp;
import org.bukkit.entity.Player;

public class SpawnCommand extends WrappedBukkitCommand {

    public SpawnCommand() {
        super("spawn");
    }

    @Override
    public void execute(WrappedCommandSender sender, String label, String[] args) {
        if (sender.getHandle() instanceof Player) {
            Player player = (Player) sender.getHandle();
            PvPGamer gamer = (PvPGamer) Profile.getProfile(player).getResource(Gamer.class);
            Variable<Warp> variable = new Variable<>(gamer.getWarp());
            boolean goToSpawn = false;
            if (variable.value != null) {
                if ((goToSpawn = (!(variable.value instanceof SpawnWarp)
                        && (variable.value instanceof ArenaWarp ? !variable.value.isProtected(player) :
                        variable.value.isProtected(player)))) || !variable.value.isProtected(player)) {
                    if (goToSpawn)
                        variable.value = variable.value.getController().getWarpByClass(SpawnWarp.class);
                    if (!gamer.getCombatLog().isLogged()) {
                        variable.value.joinPlayer(player, gamer.getWarp());
                        sender.tlMessage("pvp.command.spawn.teleported-to-spawn");
                    } else {
                        sender.tlMessage("pvp.command.spawn.in-combat");
                    }
                } else {
                    sender.tlMessage("pvp.command.spawn.already-in-spawn");
                }
            } else
                player.kickPlayer("§cERR_UNEXPECTED");
        } else {
            sender.sendInGameMessage();
        }
    }
}
