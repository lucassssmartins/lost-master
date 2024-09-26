package com.lostmc.bungee.listener.registry;

import com.lostmc.bungee.listener.ProxyListener;
import com.lostmc.bungee.utils.NickAPI;
import com.lostmc.core.profile.Profile;
import com.lostmc.core.profile.data.DataType;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;

public class PostLoginListener extends ProxyListener {

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPostLogin(PostLoginEvent event) {
        ProxiedPlayer p = event.getPlayer();
        Profile profile = Profile.getProfile(p);
        String nickname = profile.getData(DataType.NICKNAME).getAsString();
        if (!nickname.isEmpty())
            NickAPI.changePlayerName(p, nickname);

    }
}
