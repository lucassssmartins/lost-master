package com.lostmc.bukkit.command.registry;

import com.lostmc.bukkit.command.WrappedBukkitCommand;
import com.lostmc.core.Commons;
import com.lostmc.core.command.WrappedCommandSender;
import com.lostmc.core.networking.PacketType;
import com.lostmc.core.networking.PacketUpdateSingleData;
import com.lostmc.core.profile.Profile;
import com.lostmc.core.profile.data.DataType;
import org.bukkit.entity.Player;

public class BuildCommand extends WrappedBukkitCommand {

    public BuildCommand() {
        super("build");
        this.setPermission("core.cmd.build");
    }

    @Override
    public void execute(WrappedCommandSender sender, String label, String[] args) {
        if (sender.getHandle() instanceof Player) {
            Profile profile = Profile.getProfile(sender.getHandle());
            profile.setData(DataType.BUILD_MODE, !profile.getData(DataType.BUILD_MODE).getAsBoolean());
            profile.save();
            Commons.getRedisBackend().publish(PacketType.UPDATE_IN_SINGLE_DATA.toString(),
                    Commons.getGson().toJson(new PacketUpdateSingleData(profile.getUniqueId(), DataType.BUILD_MODE,
                            profile.getData(DataType.BUILD_MODE))));
            if (profile.getData(DataType.BUILD_MODE).getAsBoolean())
                sender.tlMessage("command.build.enabled");
            else
                sender.tlMessage("command.build.disabled");
        } else {
            sender.sendInGameMessage();
        }
    }
}
