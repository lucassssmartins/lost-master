package com.lostmc.core.networking;

import com.lostmc.core.server.ProxiedServer;
import com.lostmc.core.server.ServerType;
import com.lostmc.core.utils.JsonBuilder;
import com.lostmc.core.Commons;

public class PacketServerInfo extends Packet{

    public PacketServerInfo(ServerType serverType, ProxiedServer proxiedServer) {
        super(new JsonBuilder().addProperty("serverType", serverType.toString())
                .addProperty("networkServer", Commons.getGson().toJson(proxiedServer)).build());
    }

    public ServerType getServerType() {
        return ServerType.valueOf(this.json.get("serverType").getAsString());
    }

    public ProxiedServer getNetworkServer(Class<? extends ProxiedServer> clazz) {
        return Commons.getGson().fromJson(this.json.get("networkServer").getAsString(), clazz);
    }
}
