package com.lostmc.login.command;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.lostmc.bukkit.command.WrappedBukkitCommand;
import com.lostmc.core.command.WrappedCommandSender;
import com.lostmc.core.profile.Profile;
import com.lostmc.core.profile.data.DataType;
import com.lostmc.login.Login;
import com.lostmc.login.manager.LoginManager;
import org.bukkit.entity.Player;

public class RegisterCommand extends WrappedBukkitCommand {

    public RegisterCommand() {
        super("register");
    }

    @Override
    public void execute(WrappedCommandSender sender, String label, String[] args) {
        if (sender.getHandle() instanceof Player) {
            Player p = (Player) sender.getHandle();
            if (args.length == 0) {
                p.sendMessage("§cUsage: /register <password>");
            } else {
                Profile profile = Profile.getProfile(p);
                String given = args[0];
                if (profile.getData(DataType.ACCOUNT_PASSWORD).getAsString().isEmpty()) {
                    if (given.length() >= 8 && given.length() <= 20) {
                        Login.getControl().getController(LoginManager.class)
                                        .onLoginSuccess(p);
                        ByteArrayDataOutput out = ByteStreams.newDataOutput();
                        out.writeUTF("REGISTRY_REQUEST");
                        out.writeUTF(given);
                        p.sendPluginMessage(Login.getInstance(), "BungeeCord", out.toByteArray());
                    } else {
                        p.sendMessage("§cA senha precisa ser maior que 7 e menor que 20.");
                    }
                } else {
                    p.sendMessage("§cVocê já está registrado! Use /login <password>");
                }
            }
        } else {
            sender.sendInGameMessage();
        }
    }
}
