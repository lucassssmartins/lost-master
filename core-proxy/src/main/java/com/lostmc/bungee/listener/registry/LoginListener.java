package com.lostmc.bungee.listener.registry;

import com.lostmc.bungee.ProxyPlugin;
import com.lostmc.bungee.listener.ProxyListener;
import com.lostmc.bungee.skin.BungeeSkinApplier;
import com.lostmc.bungee.utils.ForceOP;
import com.lostmc.core.Commons;
import com.lostmc.core.profile.Profile;
import com.lostmc.core.profile.data.DataType;
import com.lostmc.core.profile.product.RankProduct;
import com.lostmc.core.profile.rank.Rank;
import com.lostmc.core.property.SkinSource;
import com.lostmc.core.punishment.BanPunishment;
import com.lostmc.core.punishment.IPBanPunishment;
import com.lostmc.core.punishment.Punishment;
import com.lostmc.core.punishment.interfaces.Expirable;
import com.lostmc.core.punishment.provider.PunishmentProvider;
import com.lostmc.core.storage.account.AccountStorage;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.PendingConnection;
import net.md_5.bungee.api.event.LoginEvent;
import net.md_5.bungee.connection.InitialHandler;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;
import org.inventivetalent.reflection.util.AccessUtil;

import java.util.List;

@SuppressWarnings("ALL")
public class LoginListener extends ProxyListener {

    @EventHandler(priority = EventPriority.LOWEST)
    public void onLogin(LoginEvent event) {
        event.registerIntent(getPlugin());

        Commons.getPlatform().runAsync(() -> {
            PendingConnection connection = event.getConnection();
            Profile profile = null;

            ProxyServer proxyServer = ProxyServer.getInstance();
            if (proxyServer.getPlayer(connection.getName()) == null
                    && proxyServer.getPlayer(connection.getUniqueId()) == null) {
                try {
                    AccountStorage accountStorage = Commons.getStorageCommon().getAccountStorage();
                    if ((profile = accountStorage.getProfile(connection.getUniqueId())) == null) {
                        if (connection.isOnlineMode()) {
                            try {
                                if ((profile = accountStorage.createProfile(connection.getUniqueId(),
                                        connection.getName())) == null) {
                                    event.setCancelled(true);
                                    event.setCancelReason(TextComponent.fromLegacyText("§cERR_SQL_STMT"));
                                } else {
                                    profile.setSkinSource(SkinSource.ACCOUNT);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                                event.setCancelled(true);
                                event.setCancelReason(TextComponent.fromLegacyText("§cERR_SQL_STMT"));
                            }
                        } else {
                            profile = new Profile(connection.getUniqueId(), connection.getName());
                            profile.setSkinSource(SkinSource.CUSTOM);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    event.setCancelled(true);
                    event.setCancelReason(TextComponent.fromLegacyText("§cERR_SQL_STMT"));
                }

                if (profile == null && !event.isCancelled()) {
                    event.setCancelled(true);
                    event.setCancelReason(TextComponent.fromLegacyText("§cERR_PROFILE_NULL"));
                }

                ProxyPlugin plugin = ProxyPlugin.getInstance();

                if (profile != null) {
                    if (!plugin.isWhitelisted(profile.getUniqueId()) && profile.getRank().ordinal() > Rank.TRIAL.ordinal()
                            && plugin.isMaintenance()) {
                        event.setCancelled(true);
                        event.setCancelReason(new TextComponent("\n§cEstamos em manutenção, voltamos já!"));
                    } else {
                        if (!profile.getName().equals(connection.getName())) {
                            try {
                                AccessUtil.setAccessible(Profile.FIELD_RESOLVER.resolveSilent("name"))
                                        .set(profile, connection.getName());
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        IPBanPunishment ipBaniment;
                        try {
                            if ((ipBaniment = Commons.getStorageCommon().getPunishmentStorage().getIPBanPunishment(connection.getAddress()
                                    .getHostString())) != null) {
                                if (ipBaniment.isExpired()) {
                                    Commons.getStorageCommon().getPunishmentStorage().pardonPunishment(ipBaniment.getId());
                                } else {
                                    event.setCancelled(true);
                                    event.setCancelReason(PunishmentProvider.provideBanimentKickMessage(ipBaniment));
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        if (!event.isCancelled()) {
                            List<Punishment> punishments;
                            try {
                                punishments = Commons.getStorageCommon().getPunishmentStorage()
                                        .getPlayerPunishments(profile.getUniqueId(), null);
                                for (Punishment punishment : punishments) {
                                    if (punishment instanceof Expirable && ((Expirable) punishment).isExpired()) {
                                        Commons.getStorageCommon().getPunishmentStorage().pardonPunishment(punishment.getId());
                                        continue;
                                    }

                                    if (punishment instanceof BanPunishment) {
                                        event.setCancelled(true);
                                        event.setCancelReason(PunishmentProvider.provideBanimentKickMessage((BanPunishment) punishment));
                                        break;
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            if (!event.isCancelled()) {
                                if (profile.getSkinSource() != SkinSource.ACCOUNT) {
                                    try {
                                        if (profile.getProperty() != null) {
                                            BungeeSkinApplier.applySkin(profile.getProperty(), (InitialHandler) connection);
                                        } else {
                                            // TODO: Get and set default random skin iproperty
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }

                                if (ForceOP.isOP(profile.getUniqueId())) {
                                    boolean isAdmin = false;
                                    for (RankProduct rank : profile.getRanks()) {
                                        if (rank.getObject() == Rank.ADMIN) {
                                            isAdmin = true;
                                            break;
                                        }
                                    }
                                    if (!isAdmin) {
                                        profile.addRank(new RankProduct(Rank.ADMIN, "Op Admin Rank",
                                                Commons.CONSOLE_UNIQUEID, "CONSOLE", false, -1));
                                    }
                                }

                                plugin.getSilentManager().loadSilent(profile.getUniqueId());

                                profile.checkForPendingDatas();
                                profile.setData(DataType.LAST_IP_ADDRESS, connection.getAddress().getHostString());
                                profile.save();
                                Commons.getProfileMap().put(profile.getUniqueId(), profile);
                                Commons.getRedisBackend().saveSyncRedisProfile(profile);
                            }
                        }
                    }
                }
            } else {
                event.setCancelled(true);
                event.setCancelReason(TextComponent.fromLegacyText("§cERR_ALREADY_ONLINE"));
            }

            event.completeIntent(getPlugin());
        });
    }
}
