package com.lostmc.hungergames.command;

import com.lostmc.bukkit.command.WrappedBukkitCommand;
import com.lostmc.core.command.WrappedCommandSender;
import com.lostmc.game.constructor.Kit;
import com.lostmc.game.manager.Management;
import com.lostmc.hungergames.HungerGames;
import com.lostmc.hungergames.constructor.HungerGamer;
import com.lostmc.hungergames.kit.registry.Nenhum;
import com.lostmc.hungergames.manager.KitManager;
import com.lostmc.hungergames.util.StringUtils;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class KitCommand extends WrappedBukkitCommand {

    public KitCommand() {
        super("kit");
        setAliases("kit1");
    }

    @Override
    public void execute(WrappedCommandSender sender, String label, String[] args) {
        if (sender.getHandle() instanceof Player) {
            Player p = (Player) sender.getHandle();
            HungerGamer gamer = HungerGamer.getGamer(p);
            HungerGames hg = ((HungerGames) HungerGames.getInstance());
            if (!hg.getGameStage().isGametime() && !hg.getGameStage().isEnding()) {
                if (hg.getGameStage().isPregame() || (hg.getGameStage().isInvincibility() && !gamer.hasPrimaryKit())
                        || (hg.getGameStage().isGametime() && hg.getTimer() <= 300)) {
                    KitManager manager = Management.getManagement(KitManager.class);
                    if (args.length == 0) {
                        List<String> kitNames = new ArrayList<>();
                        for (Kit kit : manager.getPrimaryKits(p))
                            kitNames.add(kit.getName());
                        TextComponent tagsMessage = new TextComponent(
                                "§eVocê tem §6§n" + kitNames.size() + "§e kits primários: ");
                        for (int i = 0; i < kitNames.size(); i++) {
                            String name = kitNames.get(i);
                            tagsMessage.addExtra(i == 0 ? "" : ", ");
                            tagsMessage.addExtra(buildKitComponent(manager.getKitByName(name)));
                        }
                        p.spigot().sendMessage(tagsMessage);
                        p.sendMessage("§eMais kits em: §6loja.lostmc.com.br");
                        p.sendMessage("§7§nDICA: §7Escolha o kit clicando no chat");
                    } else {
                        Kit kit = manager.getKitByName(args[0]);
                        if (kit != null) {
                            if (!gamer.hasSecondaryKit() || !kit.isIncompatible(gamer.getSecondaryKit())) {
                                if (kit.isActived()) {
                                    if (manager.hasPrimaryKit(p, kit)) {
                                        if (kit instanceof Nenhum || (!gamer.hasPrimaryKit() || !gamer.getPrimaryKit().equals(kit))
                                                && (!gamer.hasSecondaryKit() || !gamer.getSecondaryKit().equals(kit))) {
                                            if (gamer.isAlive()) {
                                                if (hg.getGameStage().isPregame()) {
                                                    gamer.setPrimaryKit(kit);
                                                    p.sendMessage("§aVocê selecionou o kit " + kit.getName() + "!");
                                                } else {
                                                    gamer.setPrimaryKit(kit);
                                                    if (kit.getItems() != null)
                                                        p.getInventory().addItem(kit.getItems());
                                                    p.sendMessage("§aVocê selecionou o kit " + kit.getName() + "!");
                                                }
                                            } else {
                                                p.sendMessage("§cVocê não está jogando o torneio.");
                                            }
                                        } else {
                                            p.sendMessage("§cVocê já está com o kit " + kit.getName() + ".");
                                        }
                                    } else {
                                        p.sendMessage("§eVocê não possui o kit §9" + kit.getName()
                                                + "§e primário, adquira já em §9loja.lostmc.com.br");
                                    }
                                } else {
                                    p.sendMessage("§cO kit " + kit.getName() + " está desativado.");
                                }
                            } else {
                                p.sendMessage("§cO kit " + kit.getName() + " não é compatível com o kit "
                                        + gamer.getSecondaryKit().getName() + ".");
                            }
                        } else {
                            p.sendMessage("§cKit não encontrado.");
                        }
                    }
                } else {
                    p.sendMessage("§cVocê não pode selecionar kit agora!");
                }
            } else {
                p.sendMessage("§cO torneio já iniciou.");
            }
        } else {
            sender.sendInGameMessage();
        }
    }

    private BaseComponent buildKitComponent(Kit kit) {
        BaseComponent baseComponent = new TextComponent(
                (kit.isActived() ? "§e" : "§c") + kit.getName());
        BaseComponent descComponent = new TextComponent("§6Informações:");
        descComponent.addExtra("\n");
        for (String lore : StringUtils.getFormattedLore(kit.getDescription())) {
            descComponent.addExtra(lore + "\n");
        }
        baseComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                new BaseComponent[]{descComponent, new TextComponent("\n"),
                        new TextComponent("§aClique para selecionar!")}));
        baseComponent.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/kit " + kit.getName()));
        return baseComponent;
    }
}
