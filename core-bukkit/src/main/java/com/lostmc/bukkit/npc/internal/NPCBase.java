/*
 * Copyright (c) 2018 Jitse Boonstra
 */

package com.lostmc.bukkit.npc.internal;

import com.lostmc.bukkit.npc.api.NPC;
import com.lostmc.bukkit.npc.api.events.NPCHideEvent;
import com.lostmc.bukkit.npc.api.events.NPCShowEvent;
import com.lostmc.bukkit.npc.api.skin.Skin;
import com.lostmc.bukkit.npc.api.state.NPCAnimation;
import com.lostmc.bukkit.npc.api.state.NPCSlot;
import com.lostmc.bukkit.npc.api.state.NPCState;
import com.lostmc.bukkit.npc.NpcController;
import com.lostmc.bukkit.npc.utilities.MathUtil;
import com.lostmc.bukkit.utils.entity.EntityUtils;
import com.lostmc.core.property.IProperty;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public abstract class NPCBase implements NPC, NPCPacketHandler {

    protected final int entityId = EntityUtils.next();
    protected final Set<UUID> hasTeamRegistered = new HashSet<>();
    protected final Set<NPCState> activeStates = EnumSet.noneOf(NPCState.class);

    private final Set<UUID> shown = new HashSet<>();
    private final Set<UUID> autoHidden = new HashSet<>();

    protected double cosFOV = Math.cos(Math.toRadians(60));
    // 12/4/20, JMB: Changed the UUID in order to enable LabyMod Emotes:
    // This gives a format similar to: 528086a2-4f5f-2ec2-0000-000000000000
    protected UUID uuid = new UUID(new Random().nextLong(), 0);
    protected String name = "§8" + uuid.toString().replace("-", "").substring(0, 8);
    protected GameProfile gameProfile = new GameProfile(uuid, name);
    protected boolean created = false;

    protected NpcController instance;
    protected Location location;
    protected Skin skin;

    @Getter
    @Setter
    private InteractHandler interactHandler;

    protected final Map<NPCSlot, ItemStack> items = new EnumMap<>(NPCSlot.class);

    public NPCBase(NpcController instance) {
        this.instance = instance;
        NPCList.add(this);
    }

    public NpcController getInstance() {
        return instance;
    }

    @Override
    public UUID getUniqueId() {
        return uuid;
    }

    @Override
    public String getId() {
        return name;
    }

    @Override
    public NPC setSkin(Skin skin) {
        this.skin = skin;

        gameProfile.getProperties().get("textures").clear();
        if (skin != null)
            gameProfile.getProperties().put("textures", new Property("textures", skin.getValue(), skin.getSignature()));

        return this;
    }

    @Override
    public void destroy() {
        NPCList.getAllNPCs().removeIf(npc -> npc.entityId == this.entityId);

        // Destroy NPC for every player that is still seeing it.
        for (UUID uuid : shown) {
            if (autoHidden.contains(uuid)) continue;
            Player player = Bukkit.getPlayer(uuid);
            if (player != null) hide(player, true); // which will also destroy the holograms
        }
    }

    public void disableFOV() {
        this.cosFOV = 0;
    }

    public void setFOV(double fov) {
        this.cosFOV = Math.cos(Math.toRadians(fov));
    }

    public Set<UUID> getShown() {
        return shown;
    }

    public Set<UUID> getAutoHidden() {
        return autoHidden;
    }

    @Override
    public Location getLocation() {
        return location;
    }

    @Override
    public World getWorld() {
        return location != null ? location.getWorld() : null;
    }

    public int getEntityId() {
        return entityId;
    }

    @Override
    public boolean isShown(Player player) {
        Objects.requireNonNull(player, "Player object cannot be null");
        return shown.contains(player.getUniqueId()) && !autoHidden.contains(player.getUniqueId());
    }

    @Override
    public NPC setLocation(Location location) {
        this.location = location;
        return this;
    }

    @Override
    public NPC create() {
        createPackets();
        this.created = true;
        return this;
    }

    @Override
    public boolean isCreated() {
        return created;
    }

    public void onLogout(Player player) {
        getAutoHidden().remove(player.getUniqueId());
        getShown().remove(player.getUniqueId()); // Don't need to use NPC#hide since the entity is not registered in the NMS server.
        hasTeamRegistered.remove(player.getUniqueId());
    }

    public boolean inRangeOf(Player player) {
        if (player == null) return false;
        if (!player.getWorld().equals(location.getWorld())) {
            // No need to continue our checks, they are in different worlds.
            return false;
        }

        // If Bukkit doesn't track the NPC entity anymore, bypass the hiding distance variable.
        // This will cause issues otherwise (e.g. custom skin disappearing).
        double hideDistance = instance.getAutoHideDistance();
        double distanceSquared = player.getLocation().distanceSquared(location);
        double bukkitRange = Bukkit.getViewDistance() << 4;

        return distanceSquared <= MathUtil.square(hideDistance) && distanceSquared <= MathUtil.square(bukkitRange);
    }

    public boolean inViewOf(Player player) {
        /**
         * Vector dir = location.toVector().subtract(player.getEyeLocation().toVector()).normalize();
         *         return dir.dot(player.getEyeLocation().getDirection()) >= cosFOV;
         */
        return true;
    }

    @Override
    public void show(Player player) {
        show(player, false);
    }

    public void show(Player player, boolean auto) {
        NPCShowEvent event = new NPCShowEvent(this, player, auto);
        Bukkit.getServer().getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            return;
        }

        if (isShown(player)) {
            throw new IllegalArgumentException("NPC is already shown to player");
        }

        if (auto) {
            sendShowPackets(player);
            sendMetadataPacket(player);
            sendEquipmentPackets(player);

            // NPC is auto-shown now, we can remove the UUID from the set.
            autoHidden.remove(player.getUniqueId());
        } else {
            // Adding the UUID to the set.
            shown.add(player.getUniqueId());

            if (inRangeOf(player) && inViewOf(player)) {
                // The player can see the NPC and is in range, send the packets.
                sendShowPackets(player);
                sendMetadataPacket(player);
                sendEquipmentPackets(player);
            } else {
                // We'll wait until we can show the NPC to the player via auto-show.
                autoHidden.add(player.getUniqueId());
            }
        }
    }

    @Override
    public void hide(Player player) {
        hide(player, false);
    }

    public void hide(Player player, boolean auto) {
        NPCHideEvent event = new NPCHideEvent(this, player, auto);
        Bukkit.getServer().getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            return;
        }

        if (!shown.contains(player.getUniqueId())) {
            throw new IllegalArgumentException("NPC cannot be hidden from player before calling NPC#show first");
        }

        if (auto) {
            if (autoHidden.contains(player.getUniqueId())) {
                throw new IllegalStateException("NPC cannot be auto-hidden twice");
            }

            sendHidePackets(player);

            // NPC is auto-hidden now, we will add the UUID to the set.
            autoHidden.add(player.getUniqueId());
        } else {
            // Removing the UUID from the set.
            shown.remove(player.getUniqueId());

            if (inRangeOf(player)) {
                // The player is in range of the NPC, send the packets.
                sendHidePackets(player);
            } else {
                // We don't have to send any packets, just don't let it auto-show again by removing the UUID from the set.
                autoHidden.remove(player.getUniqueId());
            }
        }
    }

    @Override
    public boolean getState(NPCState state) {
        return activeStates.contains(state);
    }

    @Override
    public NPC toggleState(NPCState state) {
        if (activeStates.contains(state)) {
            activeStates.remove(state);
        } else {
            activeStates.add(state);
        }

        // Send a new metadata packet to all players that can see the NPC.
        for (UUID shownUuid : shown) {
            Player player = Bukkit.getPlayer(shownUuid);
            if (player != null && isShown(player)) {
                sendMetadataPacket(player);
            }
        }
        return this;
    }

    @Override
    public void playAnimation(NPCAnimation animation) {
        for (UUID shownUuid : shown) {
            Player player = Bukkit.getPlayer(shownUuid);
            if (player != null && isShown(player)) {
                sendAnimationPacket(player, animation);
            }
        }
    }

    @Override
    public ItemStack getItem(NPCSlot slot) {
        Objects.requireNonNull(slot, "Slot cannot be null");

        return items.get(slot);
    }

    @Override
    public NPC setItem(NPCSlot slot, ItemStack item) {
        Objects.requireNonNull(slot, "Slot cannot be null");

        items.put(slot, item);

        for (UUID shownUuid : shown) {
            Player player = Bukkit.getPlayer(shownUuid);
            if (player != null && isShown(player)) {
                sendEquipmentPacket(player, slot, false);
            }
        }
        return this;
    }

    @Override
    public void lookAt(Location location) {
        sendHeadRotationPackets(location);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o instanceof NPCBase) {
            NPCBase that = (NPCBase) o;
            return that.entityId == this.entityId;
        }
        return false;
    }
}
