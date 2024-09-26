package com.lostmc.bungee.command.registry;

import com.lostmc.bungee.command.WrappedProxyCommand;
import com.lostmc.core.Commons;
import com.lostmc.core.command.WrappedCommandSender;
import com.lostmc.core.networking.PacketOutReloadTranslations;
import com.lostmc.core.networking.PacketType;
import com.lostmc.core.translate.Translator;
import redis.clients.jedis.Jedis;

public class ReloadTranslationsCommand extends WrappedProxyCommand {

    public ReloadTranslationsCommand() {
        super("reloadtranslations");
        this.setPermission("*");
        this.setAliases("rltranslations");
    }

    @Override
    public boolean runAsync() {
        return true;
    }

    @Override
    public void execute(WrappedCommandSender sender, String label, String[] args) {
        Translator.reloadTranslations();
        try (Jedis jedis = Commons.getRedisBackend().getPool().getResource()) {
            jedis.publish(PacketType.RELOAD_TRANSLATIONS.toString(),
                    Commons.getGson().toJson(new PacketOutReloadTranslations()));
        }
        sender.tlMessage("command.rltranslations.reloaded");
    }
}
