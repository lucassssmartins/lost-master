package com.lostmc.hungergames.command;

import com.lostmc.bukkit.command.WrappedBukkitCommand;
import com.lostmc.core.command.WrappedCommandSender;
import com.lostmc.game.manager.Management;
import com.lostmc.hungergames.HungerGames;
import com.lostmc.hungergames.manager.FeastManager;
import com.lostmc.hungergames.stage.GameStage;
import com.lostmc.hungergames.structure.Feast;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class FeastCommand extends WrappedBukkitCommand {

    public FeastCommand() {
        super("feast");
    }

    @Override
    public void execute(WrappedCommandSender sender, String label, String[] args) {
        if (sender.getHandle() instanceof Player) {
            if (((HungerGames) HungerGames.getInstance()).getGameStage().isGametime()) {
                Location feastLocation = null;
                Feast feast = null;
                for (Feast pending : Management.getManagement(FeastManager.class).getPendingFeasts()) {
                    if (pending.isDefault()) {
                        if (pending.isSpawned()) {
                            feastLocation = pending.getCentralLocation();
                            feast = pending;
                            break;
                        }
                    } else if (pending.isSpawned()) {
                        feastLocation = pending.getCentralLocation();
                        feast = pending;
                        break;
                    }
                }
                if (feastLocation != null) {
                    ((Player) sender.getHandle()).setCompassTarget(feastLocation);
                    sender.sendMessage("§cBússola apontando para o " + (feast.isDefault() ? "feast!" : "feast #" + feast.getId()));
                } else {
                    sender.sendMessage("§cNenhum feast encontrado.");
                }
            } else {
                sender.sendMessage("§cComando indisponível no estágio atual.");
            }
        } else {

        }
    }
}
