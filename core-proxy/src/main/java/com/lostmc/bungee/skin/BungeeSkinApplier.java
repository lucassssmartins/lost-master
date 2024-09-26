package com.lostmc.bungee.skin;

import com.lostmc.bungee.utils.reflection.BungeeReflection;
import com.lostmc.core.Commons;
import com.lostmc.core.networking.PacketType;
import com.lostmc.core.networking.PacketUpdateOutSkin;
import com.lostmc.core.property.IProperty;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.connection.InitialHandler;
import net.md_5.bungee.connection.LoginResult;
import net.md_5.bungee.protocol.Property;

public class BungeeSkinApplier {

	public static void applySkin(IProperty property, InitialHandler handler) throws Exception {
		applyWithProperty(handler, new Property("textures", property.getValue(), property.getSignature()));
	}

	private static void applyWithProperty(InitialHandler handler, Property textures) throws Exception {
		LoginResult profile = handler.getLoginProfile();

		if (profile == null) {
			profile = new LoginResult(null, null, new Property[] { textures });
		}

		Property[] newProps = new Property[] { textures };

		profile.setProperties(newProps);
		try {
			BungeeReflection.setObject(InitialHandler.class, handler, "loginProfile", profile);
		} catch (Exception e) {
			throw e;
		}
	}

	public static void sendUpdatePacket(ProxiedPlayer player, IProperty property) {
		Commons.getRedisBackend().publish(PacketType.UPDATE_OUT_SKIN.toString(),
				new PacketUpdateOutSkin(player.getUniqueId(), property).toJson());
	}
}
