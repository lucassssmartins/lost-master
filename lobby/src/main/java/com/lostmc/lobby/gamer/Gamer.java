package com.lostmc.lobby.gamer;

import com.lostmc.core.profile.Profile;
import com.lostmc.core.profile.data.DataType;
import com.lostmc.core.translate.Translator;
import com.lostmc.lobby.collectables.gadget.Gadget;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

@Getter
public class Gamer {

	private final Profile profile;
	private final List<Gadget> gadgets = new ArrayList<>();

	public Gamer(final Profile profile) {
		this.profile = profile;
	}

	public void setFlying(boolean flight) {
		this.profile.setData(DataType.HUB_FLIGHT_MODE, flight);
		this.profile.save();
		Player p = Bukkit.getPlayer(this.profile.getUniqueId());
		if (p != null && p.isOnline()) {
			p.setAllowFlight(flight);
			p.setFlying(flight);
			p.sendMessage(Translator.tl(this.profile.getLocale(), flight ?
					"hub.command.flight.true" : "hub.command.flight.false"));
		}
	}

	public boolean isFlying() {
		return profile.getData(DataType.HUB_FLIGHT_MODE).getAsBoolean();
	}

	public Gadget getGadget(Class<? extends Gadget> clas) {
		return this.gadgets.stream().filter(gadget -> gadget.getClass().equals(clas)).findFirst().orElse(null);
	}
}
