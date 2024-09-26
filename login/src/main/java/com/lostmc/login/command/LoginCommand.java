package com.lostmc.login.command;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.lostmc.bukkit.BukkitPlugin;
import com.lostmc.bukkit.command.WrappedBukkitCommand;
import com.lostmc.core.command.WrappedCommandSender;
import com.lostmc.core.profile.Profile;
import com.lostmc.core.profile.data.DataType;
import com.lostmc.login.Login;
import com.lostmc.login.manager.LoginManager;
import org.bukkit.entity.Player;

public class LoginCommand extends WrappedBukkitCommand {

    public LoginCommand() {
        super("login");
    }

    @Override
    public void execute(WrappedCommandSender sender, String label, String[] args) {
        if (sender.getHandle() instanceof Player) {
            Player p = (Player) sender.getHandle();
            if (args.length == 0) {
                p.sendMessage("§cUsage: /login <password>");
            } else {
                Profile profile = Profile.getProfile(p);
                String accPassword = profile.getData(DataType.ACCOUNT_PASSWORD).getAsString();
                if (!accPassword.isEmpty()) {
                    if (args[0].equals(accPassword)) {
                        Login.getControl().getController(LoginManager.class)
                                .onLoginSuccess(p);
                        ByteArrayDataOutput out = ByteStreams.newDataOutput();
                        out.writeUTF("LOGIN_REQUEST");
                        p.sendPluginMessage(Login.getInstance(), "BungeeCord", out.toByteArray());
                    } else {
                        p.sendMessage("§cSenha incorreta! Tente novamente.");
                    }
                } else {
                    p.sendMessage("§cVocê não está registrado! Use /register");
                }
            }
        } else {
            sender.sendInGameMessage();
        }
    }
}
