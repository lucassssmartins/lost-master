package com.lostmc.bukkit.command.registry;

import com.lostmc.bukkit.BukkitPlugin;
import com.lostmc.bukkit.command.WrappedBukkitCommand;
import com.lostmc.bukkit.nametag.NametagController;
import com.lostmc.core.command.WrappedCommandSender;
import com.lostmc.core.profile.Profile;
import com.lostmc.core.profile.tag.Tag;
import com.lostmc.core.translate.Translator;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class TagCommand extends WrappedBukkitCommand {

    public TagCommand() {
        super("tag");
        this.setAliases("nametag", "ntag");
    }

    @Override
    public void execute(WrappedCommandSender sender, String label, String[] args) {
        if (sender.getHandle() instanceof Player) {
            List<Tag> tags = getTagsOf((Player) sender.getHandle());
            if (args.length == 0) {
                TextComponent component = new TextComponent(
                        Translator.tl(sender.getLocale(), "command.tag.your-tags") + " ");
                int max = tags.size();
                int i = 0;
                for (Tag tag : tags) {
                    TextComponent next = new TextComponent(tag.getColouredName(false));
                    next.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tag " + tag.toString()));
                    component.addExtra(next);

                    if (++i < max) {
                        component.addExtra(new TextComponent("§f, "));
                    }
                }

                ((Player) sender.getHandle()).spigot().sendMessage(component);
            } else {
                Tag tag = Tag.fromName(args[0]);
                if (tag != null && tags.contains(tag)) {
                    Profile account = Profile.getProfile(sender.getHandle());
                    if (!tag.equals(account.getTag())) {
                        BukkitPlugin.getControl().getController(NametagController.class)
                                .setNametag((Player) sender.getHandle(), tag);
                        sender.sendMessage(Translator.tl(sender.getLocale(), "command.tag.tag-changed-success",
                                tag.getFormattedName().toUpperCase()));
                    } else {
                        sender.sendMessage(
                                Translator.tl(sender.getLocale(), "command.tag.already-with-that-tag", tag.getFormattedName()));
                    }
                } else {
                    sender.sendMessage(Translator.tl(sender.getLocale(), "command.tag.unknow-tag", args[0]));
                }
            }
        } else {
            sender.sendInGameMessage();
        }
    }

    public List<Tag> getTagsOf(Player p) {
        List<Tag> list = new ArrayList<>();
        for (Tag tag : Tag.values()) {
            if (!p.hasPermission("tag." + tag.toString().toLowerCase()) && !tag.equals(Tag.DEFAULT))
                continue;
            list.add(tag);
        }
        return list;
    }
}
