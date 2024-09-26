package com.lostmc.bukkit.menu;

import com.lostmc.bukkit.api.item.ItemBuilder;
import com.lostmc.bukkit.api.menu.MenuClickHandler;
import com.lostmc.bukkit.api.menu.MenuInventory;
import com.lostmc.bukkit.utils.string.StringLoreUtils;
import com.lostmc.core.Commons;
import com.lostmc.core.profile.Profile;
import com.lostmc.core.report.Report;
import com.lostmc.core.utils.DateUtils;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import static com.lostmc.core.translate.Translator.tl;

public class ReportMenu extends MenuInventory {

    private static final int itemsPerPage = 21;

    public ReportMenu(Profile opener, Player player, int page) {
        super(6 * 9, tl(opener.getLocale(), "menu.reports.title"));

        List<Report> reports = new ArrayList<>(Commons.getReportMap().values())
                .stream().filter(report -> !report.isExpired())
                .collect(Collectors.toList());
        Iterator<Report> iterator = reports.iterator();

        int pageStart = 0;
        int pageEnd = itemsPerPage;

        if (page > 1) {
            pageStart = ((page - 1) * itemsPerPage);
            pageEnd = (page * itemsPerPage);
        }

        if (pageEnd > reports.size()) {
            pageEnd = reports.size();
        }

        int w = 10;

        if (reports.isEmpty())
            setItem(13, new ItemBuilder(Material.BARRIER)
                    .setName(tl(opener.getLocale(), "menu.reports.not-found"))
                    .setLore(StringLoreUtils.formatForLore(tl(opener.getLocale(), "menu.reports.not-found-lore")))
                    .build());
        else
            for (int i = pageStart; i < pageEnd; i++) {
                Report report = reports.get(i);

                setItem(w, new ItemBuilder(new ItemStack(Material.SKULL_ITEM, 1, (byte) 3))
                        .setName("§a" + report.getPlayerName())
                        .setLoreText(tl(opener.getLocale(), "menu.reports.report-lore", report.getLastReport().getReporterName(),
                                report.getLastReport().getReason(), report.getReports().size(), report.getLevel().getColor() + report.getLevel().name(),
                                report.getPlayerServer().toUpperCase(),
                                new SimpleDateFormat("dd/MM/yyyy - HH:mm").format(new Date(report.getLastReport().getTime())),
                                DateUtils.getTime(report.getLastReport().getExpires())))
                        .build(), new ReportClickHandler(report, this));

                if (w % 9 == 7) {
                    w += 3;
                    continue;
                }

                w += 1;
            }

        if (page != 1)
            setItem(45, new ItemBuilder(Material.ARROW)
                            .setName(tl(opener.getLocale(), "page") + " " + (page - 1))
                            .build(),
                    (p, inventory, clickType, item, slot) -> p.openInventory(new ReportMenu(opener, player, page - 1)));

        if (Math.ceil(reports.size() / itemsPerPage) + 1 > page)
            setItem(53, new ItemBuilder(Material.ARROW)
                            .setName(tl(opener.getLocale(), "page") + " " + (page + 1))
                            .build(),
                    (p, inventory, clickType, item, slot) -> player.openInventory(new ReportMenu(opener, player, page + 1)));
    }

    private static class ReportClickHandler implements MenuClickHandler {

        private final Report report;

        public ReportClickHandler(Report report, MenuInventory topInventory) {
            this.report = report;
        }

        @Override
        public void onClick(Player p, Inventory inv, ClickType type, ItemStack stack, int slot) {
            p.openInventory(new ReportInfoMenu(Objects.requireNonNull(Profile.getProfile(p)), p, report));
        }
    }

    private static class ReportInfoMenu extends MenuInventory {

        public ReportInfoMenu(Profile opener, Player player, Report report) {
            super(6 * 9, tl(opener.getLocale(), "menu.report-info.title"));

            setItem(13, new ItemBuilder(new ItemStack(Material.SKULL_ITEM, 1, (byte) 3))
                    .setName("§a" + report.getPlayerName())
                    .build());
            setItem(29, new ItemBuilder(Material.ANVIL)
                            .setName(tl(opener.getLocale(), "menu.report-info.punish"))
                            .setLoreText(tl(opener.getLocale(), "menu.report-info.punish-lore"))
                            .build(),
                    new MenuClickHandler() {
                        @Override
                        public void onClick(Player p, Inventory inv, ClickType type, ItemStack stack, int slot) {
                            p.closeInventory();

                            TextComponent textComponent = new TextComponent(tl(opener.getLocale(), "command.report.punish"));
                            textComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText(tl(opener.getLocale(), "command.report.punish-hover"))));
                            textComponent.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/p ban <category> n " + report.getPlayerName() + " <reason>"));
                            p.spigot().sendMessage(textComponent);
                        }
                    });
            setItem(31, new ItemBuilder(Material.EYE_OF_ENDER)
                            .setName(tl(opener.getLocale(), "menu.report-info.teleport"))
                            .setLoreText(tl(opener.getLocale(), "menu.report-info.teleport-lore"))
                            .build(),
                    new MenuClickHandler() {
                        @Override
                        public void onClick(Player p, Inventory inv, ClickType type, ItemStack stack, int slot) {
                            p.closeInventory();
                            p.chat("/tp " + report.getPlayerName());
                        }
                    });
            setItem(33, new ItemBuilder(Material.BARRIER)
                            .setName(tl(opener.getLocale(), "menu.report-info.remove"))
                            .setLoreText(tl(opener.getLocale(), "menu.report-info.remove-lore"))
                            .build(),
                    new MenuClickHandler() {
                        @Override
                        public void onClick(Player p, Inventory inv, ClickType type, ItemStack stack, int slot) {
                            player.openInventory(new ReportConfirmMenu(Objects.requireNonNull(Profile.getProfile(p)), p, report));
                        }
                    });
            setItem(49, new ItemBuilder(Material.ARROW)
                            .setName(tl(opener.getLocale(), "menus.back.name"))
                            .setLoreText(tl(opener.getLocale(), "menus.back.lore"))
                            .build(),
                    new MenuClickHandler() {
                        @Override
                        public void onClick(Player p, Inventory inv, ClickType type, ItemStack stack, int slot) {
                            player.openInventory(new ReportMenu(Objects.requireNonNull(Profile.getProfile(p)), p, 1));
                        }
                    });
        }
    }

    private static class ReportConfirmMenu extends MenuInventory {

        public ReportConfirmMenu(Profile opener, Player player, Report report) {
            super(3 * 9, tl(opener.getLocale(), "menu.report-confirm.title"));

            setItem(12, new ItemBuilder(new ItemStack(Material.INK_SACK, 1, (byte) 10))
                            .setName(tl(opener.getLocale(), "menu.report-confirm.confirm"))
                            .build(),
                    new MenuClickHandler() {
                        @Override
                        public void onClick(Player p, Inventory inv, ClickType type, ItemStack stack, int slot) {
                            p.closeInventory();
                            report.remove();
                            player.sendMessage(tl(opener.getLocale(), "command.report.removed-successfully", report.getPlayerName()));
                        }
                    });
            setItem(14, new ItemBuilder(new ItemStack(Material.INK_SACK, 1, (byte) 1))
                            .setName(tl(opener.getLocale(), "menu.report-confirm.cancel"))
                            .build(),
                    new MenuClickHandler() {
                        @Override
                        public void onClick(Player p, Inventory inv, ClickType type, ItemStack stack, int slot) {
                            player.openInventory(new ReportInfoMenu(opener, p, report));
                        }
                    });

            player.openInventory(this);
        }
    }
}
