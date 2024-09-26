package com.lostmc.bukkit.command.registry;

import com.lostmc.bukkit.command.WrappedBukkitCommand;
import com.lostmc.core.Commons;
import com.lostmc.core.command.WrappedCommandSender;
import com.lostmc.core.networking.PacketInUpdateMedal;
import com.lostmc.core.networking.PacketType;
import com.lostmc.core.profile.Profile;
import com.lostmc.core.profile.medal.Medal;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class MedalCommand extends WrappedBukkitCommand {

    public MedalCommand() {
        super("medal");
        setAliases("medalha", "medalhas");
    }

    @Override
    public void execute(WrappedCommandSender sender, String label, String[] args) {
        if (sender.getHandle() instanceof Player) {
            List<Medal> medals = getMedalsOf((Player) sender.getHandle());
            if (args.length == 0) {
                int max = medals.size();
                if (max == 0) {
                    sender.sendMessage("§cVocê não possui nenhuma medalha! Adquira em §oloja.lostmc.com.br");
                    return;
                }

                TextComponent component = new TextComponent("§aSuas medalhas:" + " ");

                int i = 0;
                for (Medal medal : medals) {
                    TextComponent next = new TextComponent(medal.getColor() + medal.getSymbol());
                    next.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/medal " + medal.toString()));
                    component.addExtra(next);

                    if (++i < max) {
                        component.addExtra(new TextComponent("§f, "));
                    }
                }

                ((Player) sender.getHandle()).spigot().sendMessage(component);
                sender.sendMessage("§cUse '/medal remove' para remover sua medalha!");
            } else {
                Profile account = Profile.getProfile(sender.getHandle());
                if (args[0].equalsIgnoreCase("remove") || args[0].equalsIgnoreCase("remover")) {
                    if (account.getMedal() != null) {
                        account.setMedal(null);
                        account.save();
                        sender.sendMessage("§aMedalha removida com sucesso!");
                    } else {
                        sender.sendMessage("§cVocê não está utilizando nenhuma medalha.");
                    }
                } else {
                    Medal medal = Medal.fromString(args[0]);
                    if (medal != null && medals.contains(medal)) {
                        if (!medal.equals(account.getMedal())) {
                            account.setMedal(medal);
                            account.save();
                            Commons.getRedisBackend().publish(PacketType.UPDATE_IN_MEDAL.toString(),
                                    new PacketInUpdateMedal(((Player) sender.getHandle()).getUniqueId(),
                                            medal).toJson());
                            sender.sendMessage("§aSelecionado a medalha: " + medal.getColor() + medal.getSymbol());
                        } else {
                            sender.sendMessage("§cVocê já está com esta medalha.");
                        }
                    } else {
                        sender.sendMessage("§cA medalha '" + args[0] + "' não foi encontrada.");
                    }
                }
            }
        } else {
            sender.sendInGameMessage();
        }
    }

    private List<Medal> getMedalsOf(Player p) {
        List<Medal> list = new ArrayList<>();

        for (Medal tag : Medal.values()) {
            if (!p.hasPermission("medal." + tag.toString().toLowerCase()))
                continue;
            list.add(tag);
        }

        return list;
    }
}
