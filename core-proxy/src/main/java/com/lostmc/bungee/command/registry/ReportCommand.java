package com.lostmc.bungee.command.registry;

import com.lostmc.bungee.ProxyPlugin;
import com.lostmc.bungee.command.WrappedProxyCommand;
import com.lostmc.core.Commons;
import com.lostmc.core.command.WrappedCommandSender;
import com.lostmc.core.profile.Profile;
import com.lostmc.core.profile.data.DataType;
import com.lostmc.core.report.Report;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.command.ConsoleCommandSender;
import net.md_5.bungee.event.EventHandler;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static com.lostmc.core.translate.Translator.tl;

public class ReportCommand extends WrappedProxyCommand implements Listener {

    private Map<UUID, Long> cooldown = new HashMap<>();

    public ReportCommand() {
        super("report");
        this.setAliases("rp", "denunciar");
        ProxyServer.getInstance().getPluginManager().registerListener(ProxyPlugin.getInstance(), this);
    }

    @Override
    public boolean runAsync() {
        return true;
    }

    @Override
    public void execute(WrappedCommandSender sender, String label, String[] args) {
        if (sender.getHandle() instanceof ConsoleCommandSender) {
            sender.sendInGameMessage();
            return;
        }

        ProxiedPlayer player = (ProxiedPlayer) sender.getHandle();
        if (args.length == 2) {
            switch (args[0].toLowerCase()) {
                case "remove":
                case "delete":
                case "excluir": {
                    Report report = Report.fromName(args[1]);
                    if (report == null) {
                        sender.tlMessage("command.report.not-found");
                        return;
                    }

                    report.remove();
                    sender.tlMessage("command.report.removed-successfully", report.getPlayerName());
                    return;
                }
            }
        }

        if (args.length >= 2) {
            ProxiedPlayer target = ProxyServer.getInstance().getPlayer(args[0]);
            String reason = createArgs(1, args);
            if (target == null) {
                sender.sendMessage("§cJogador não encontrado.");
                return;
            }

            if (player.getUniqueId().equals(target.getUniqueId())) {
                sender.sendMessage("§cIndique outro jogador para denunciar!");
                return;
            }

            if (cooldown.containsKey(player.getUniqueId()) && cooldown.get(player.getUniqueId()) >= System.currentTimeMillis()) {
                sender.sendMessage("§cAguarde para denunciar novamente.");
                return;
            }

            cooldown.put(player.getUniqueId(), (30000L + System.currentTimeMillis()));

            Report report;
            if (Commons.getReportMap().containsKey(target.getUniqueId())) {
                report = Commons.getReportMap().get(target.getUniqueId());
            } else {
                report = new Report(target.getUniqueId(), target.getName(), target.getServer().getInfo().getName());
                Commons.getReportMap().put(target.getUniqueId(), report);
            }

            report.addReport(new Report.ReportInfo(player.getUniqueId(), player.getName(), reason));
            sender.tlMessage("command.report.report-successfully", target.getName());

            ProxyServer.getInstance().getPlayers().stream().filter(p ->
                    p.hasPermission("core.cmd.reports")
            ).forEach(p -> {
                Profile profile = Profile.getProfile(p);
                if (profile.getData(DataType.AC_ALERTS).getAsBoolean()) {
                    p.sendMessage(tl(profile.getLocale(), "command.report.broadcast"));
                }
            });
            return;
        }

        sender.tlMessage("command.report.usage");
    }

    private String createArgs(int begin, String[] args) {
        StringBuilder sb = new StringBuilder();
        for (int i = begin; i < args.length; i++)
            sb.append(args[i]).append(i + 1 >= args.length ? "" : " ");
        return sb.toString();
    }
    
    @EventHandler
    public void onDisconnect(PlayerDisconnectEvent event) {
        cooldown.remove(event.getPlayer().getUniqueId());
    }
}
