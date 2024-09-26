package com.lostmc.bukkit.menu;

import com.lostmc.bukkit.api.item.ItemBuilder;
import com.lostmc.bukkit.api.menu.MenuClickHandler;
import com.lostmc.bukkit.api.menu.MenuInventory;
import com.lostmc.core.Commons;
import com.lostmc.core.networking.PacketType;
import com.lostmc.core.networking.PacketUpdateSingleData;
import com.lostmc.core.profile.Profile;
import com.lostmc.core.profile.data.DataType;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

import static com.lostmc.core.translate.Translator.tl;

public class SocialMediaMenu extends MenuInventory {

    public static GameProfile twitterProfile, youtubeProfile, instagramProfile, twitchProfile, tiktokProfile,
            discordProfile;

    static {
        twitterProfile = createSkinProfile("ewogICJ0aW1lc3RhbXAiIDogMTY0MTk4MzM0MDE3MywKICAicHJvZmlsZUlkIiA6ICIzNzNiZmY5NzQwMmY0N2IzOTViOTZlNzc3NmNmOGFhMiIsCiAgInByb2ZpbGVOYW1lIiA6ICJjdXRlcGluazczIiwKICAic2lnbmF0dXJlUmVxdWlyZWQiIDogdHJ1ZSwKICAidGV4dHVyZXMiIDogewogICAgIlNLSU4iIDogewogICAgICAidXJsIiA6ICJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlLzNiYTQ2NjMxZWIxZGUwM2FhZWUxY2VlZjMwMmY0MDAzYTYyMDU5OGUxNDk0YmFhNDY1YWYzYjU5MGExMTE1NGMiLAogICAgICAibWV0YWRhdGEiIDogewogICAgICAgICJtb2RlbCIgOiAic2xpbSIKICAgICAgfQogICAgfQogIH0KfQ==",
                "l6hohZWOcrfQQOj7YryVYVF29qwcC+XLOGEgAd0pI3kaVmyO2Ls3D1VWfWyVc5776MEZD4w+zTzOTnfry4nKfZilBAT0M9nDmLYohxKTFEqNGaM+oLXAzLTy3mIQKV7Q3+F3CvQbqCBs0lsnbg3bDeCLxdtdzrNPcW1PVwhGiSWRMLQ02hutk2cT9iORVsIHJOGfOMYHd2Sl/Aw1qaHBsO3cYOWgIrQbNhBPPWO/UdxmH0AFwL2RvnPRVeAplU45teMzMJwQPFh5HzKDs7cmJVbQE+cb2x4UX/qCYNQKlopkN5ZmekDM5GlF7J4D9owlDSPKcFlWkS2YtxfSfRcOMtjKMPmXC9kad3HMpEAhYyq78rve7AUBB11g2bynMKdbt5wlVp0plNRuVxRMXXM7jM5mqpukGa0IaaL2vR8QPpQr0pmnt8uShfHxlUw79oBbz1LZYmLWr7mmn2bDfBYIcgg/6k32wUrpmtCowBXzrgnzUFxYCZeHjW7k09bN+SXUSKQUOHJT4XV9wql2XuXJqGZdm0kwIiV/YEBn7MhfxvE//mw6LA6rZ5dxwGYp2wuDLFHRSXlA1kClMnOlxXix84L7bMKmERTvwJ8Ok0NH5nr10nyk+t/5RKD/gyUBD9ntz8jpZ6ATmoKISQAoB/Quy13YxHpfVszOkM2p8C61IwA=");
        youtubeProfile = createSkinProfile("ewogICJ0aW1lc3RhbXAiIDogMTY1MTg4NjEzMzQ0NSwKICAicHJvZmlsZUlkIiA6ICJmMjc0YzRkNjI1MDQ0ZTQxOGVmYmYwNmM3NWIyMDIxMyIsCiAgInByb2ZpbGVOYW1lIiA6ICJIeXBpZ3NlbCIsCiAgInNpZ25hdHVyZVJlcXVpcmVkIiA6IHRydWUsCiAgInRleHR1cmVzIiA6IHsKICAgICJTS0lOIiA6IHsKICAgICAgInVybCIgOiAiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS9mOTE2MTQ3NDYzZGZjMzMwODZkYjU2NDAzMjkxMTVjMTI3ZDExMTI4MTNkYWMzNzQwOGFlZTc0MzAyM2JlMzUyIgogICAgfQogIH0KfQ==",
                "Nlqz4AItLNYk9DXvD+Ar75cD6zqL20tFtF78UyAA5tH6ViAj7OvUPK9En0Us31D95gW507UaGQwALgKjOmrNrCkw1faS4i4leffLF0A89/BHTwLfbTKH1ek0QIToF2vlCNgkIM0o9y78lXn4INql8owmx7Yka5c//qNPAU3kwHZw7py9ddVZmk0T0SGoylDcOCvSQBttPqj5XTG63m96hE80RgcW2WFP7pieYlUtxubxqkhO0IyfeNBZqM58pdlVkDmH3lVs7T4+c1ErSacRxlZcluBcJ2HH2guD4OUBRUiSMpkxt7W96eL10H5pjaccRfWVF2YNycRs/kWlZHR1XM+KGC67XgI8GHdGfi7BRcZFnS6kAguVf2jAl3nFCYFPt1/tj/Dg1YWqwojoTBWdOdsLvsQiwlVeIVAiWIYSE0X6RqSuLdGb2KWphzgOHsdAcKbgDOvEy09tprOxTIjci71NSREKOEZ34wMKH45uq/mSJjYeWs8BxQTsC5UQiUPrasBlTGJa3LWZktndQz7nt/s4aXE5nZ9zhdY1BkD2bEfgywdyVsoJAbAKnHpzp8xcCv5LOVtoUxOqSH0B8Ry0uithQQ+K0bjPsKhwSJ0vIvCA03oF8svFv8rzMsm02gcuuzxw6kr3MGrBsjc8AVIN7Ts7UlMYGp73qfs6gyA78aA=");
        instagramProfile = createSkinProfile("ewogICJ0aW1lc3RhbXAiIDogMTY1MTg4NjI4MDg5MiwKICAicHJvZmlsZUlkIiA6ICI2NDU4Mjc0MjEyNDg0MDY0YTRkMDBlNDdjZWM4ZjcyZSIsCiAgInByb2ZpbGVOYW1lIiA6ICJUaDNtMXMiLAogICJzaWduYXR1cmVSZXF1aXJlZCIgOiB0cnVlLAogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZTg3MzViYWVhYTJhYjIyMmZkMDYxMGQ0NzhlZTE3Y2VjZjVlY2Y4NDhjYjFhZTExNWQ4NGM4NTNjNDFmN2RhMyIKICAgIH0KICB9Cn0=",
                "lcnwfJTV7JWPmwuu1JrUz49w+ELO18RmBGrQa7myFzCvPGaVKB57TYptJAKp0AsuHxDk+NSUFB9W3Vs8MMuOMigs3VOEUDqFMrzWwtbWDf3ABJgKvmjdCsmu7xTdtYxTQ/ypS6p6rzUO4GQb6wQZe1cBXVx8DtEgaGYKUv5BUGXTVuYAw8HJzvnKKLW6jKTd7/L1tt2VCSE/bsP3jOqhEctbLbUtBAMkO149GDSY55rEyfseosJs/irmhNhvMV6OEPcDZ8kx2sBpM23NKBFQcMVzyOKwtSKIzOaS7cToeJdlY692creRKn1D19Ptf/8G4R7Yf4c0qU5XwY+I1OFb3zNKSlZL+gCCQhHxYSy8I9AyUcNwwfASvpedWzhF4DxtzHtlLSCZBfSYmfSNeWflKchU4+ELialhZEcwYu7gQcL+05BZ1+6KQnf8kUxQDAyv9k/39rZez4vk2Ey9uOQmia6CdlMluBREDHRwxy/FXMydG37zf2eW5OYJhVHHzOLoKw2j+EiV+HgiyeBEPi+0fgi7/NCUCBoIXWrOH63zOgpNzSb4rsoWaS7PQXqB55A3ay67nxmGJd0PIAjr6LXeR0ckfUs5YgGpvENCn2R3StbGBTWPzmqXTWPKHSOgwdHIzC8vWApWuUD/vMDS3YgmmzkPUTaq6a1lDIK/uFZp/Hw=");
        twitchProfile = createSkinProfile("ewogICJ0aW1lc3RhbXAiIDogMTYxMDgwNDgxNTk3NywKICAicHJvZmlsZUlkIiA6ICIyM2YxYTU5ZjQ2OWI0M2RkYmRiNTM3YmZlYzEwNDcxZiIsCiAgInByb2ZpbGVOYW1lIiA6ICIyODA3IiwKICAic2lnbmF0dXJlUmVxdWlyZWQiIDogdHJ1ZSwKICAidGV4dHVyZXMiIDogewogICAgIlNLSU4iIDogewogICAgICAidXJsIiA6ICJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlLzFmYTMzN2M0ZDAyOWFmMTFjMDYwYjExZmUzMTdmYzRkMjg3OWU1MGQ2MTRlMjYwMTIxMmIyMThkODNlMzEzM2IiCiAgICB9CiAgfQp9",
                "RC5Xp8cHbIK1JXFsHwQ/bN9V4Va+OiG4ASHgZbcKlqN/rdMHt86zqaCqEtFEnoso2GJen0NL8x0FZzm6HfAdSluyPQbOk4Fas1fG34WN/rBl7yQ1gh3zUu7L1l7+RDTvcZUr49SGRGH0VNgl9nEEMTi1xTnL/VwWN4r9v+XW1xIoWZusmFuFRV6GRe/a6Mhfs50UwlRiVgns5chtVZxroctxBqUw+9GnwmBpzi/WpgGsai7+q1oOmZg5hVOoPIs0v+pjktQXNIbKDhAgpzWtIhnYkaq+xyU0Q4nMci2hFZNRxvEXtXNEgXs8//CLkneTJVIsPrKFwPFDe0t/2wdxB1MUFRAfmvGziSgmaGZ+c2f75QkgYvOPU8NQaT07ztwCI0fy9/Dm/48Q4GS+kEa6qIx74b1gF+As6uxVVVWPhEEw8K8aMuBG5+UbdITc3923JxtU/9kTePFCioj8qx5es8+uNX+bzMNdEHbobsYPwoXnvmKJj11MFWSA3/EEkZq1pw+DP6INfvONTMTjWZKAFksEKJ9chf+dV+0lUIF0Gzi2KqydmycD5hiwwrrG6FF43DDAopWRnSjOr5ZBO42IueqFk+BxsQS5tKphgLMo2wbbDuqwcz2d4s49F45vMySDHnC4olseSqL4yiNv8pq6dWxi6/N7MHI+Pp7bzXiPcoA=");
        tiktokProfile = createSkinProfile("ewogICJ0aW1lc3RhbXAiIDogMTU5NzMxODcxMTIxMiwKICAicHJvZmlsZUlkIiA6ICI1NjY3NWIyMjMyZjA0ZWUwODkxNzllOWM5MjA2Y2ZlOCIsCiAgInByb2ZpbGVOYW1lIiA6ICJUaGVJbmRyYSIsCiAgInNpZ25hdHVyZVJlcXVpcmVkIiA6IHRydWUsCiAgInRleHR1cmVzIiA6IHsKICAgICJTS0lOIiA6IHsKICAgICAgInVybCIgOiAiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS9hYzM3ZTMwNjk3NjQzMGY3ODNmYTg3ZDQ5MzFjOTRkZjVhNTc1ZDUxMTVjNDk1MzA1NzVkMTI4Nzc4NDQzNDIzIgogICAgfQogIH0KfQ==",
                "fGQXus4xDKuflCKoikxQ4OHuBzhdvmysnNld8b6jrXov6HmQTP38gLCLMEI/6EXzi+XhVVWa6R6Ml2gPnH9YwbHvCnPkZloaVGBek6O1vfmp8wPVV784c23vnQxE7DjfbZCmr4N+O/Q9He2V4rAko4JD6yLFDUHKlRC4evoIcFfvw5SIl2aiFopQ/smdRr4OfsQIljOcKRP/6bkTTm+Us/D5LpUhj22IqZdxWFiwtXd8yVaBNtZHPj8tN0NnZDnKvGpHZM1Veekk5dTjOYh6sYdFghM2ePdMoXRUmmdNXWyLXttqgthySmvXm/A62Q2PQ2A4QmJ3Xat1pl3si85zeAS2chZxPxYLZPx+eJIHeAGKWHnWNwN+OfPh5OSYRoIrjVXPi5KGVmuhANHAKNZFPcJ3MkgIBtVUiN1i0Ray2/aumzwi5yWNLIZr6qNIsvt2w/fkW8Xk7BJh+CqY4bmVwsOeTpAaBjMgkdVo9A3/zPe/Y4kfECgfsiQPzmdsPcnruNneJWEUKtk6SEakipz7FJxrCxeLBkO+KgxLq2nJcR29xAlo5yTaquPwA43OndyMjeiHqs3P66k7LmEzsUsdniO4gPLNostQMBX+6p30DtwIQ47WI0DtzZ9Q1Z/wKV3+CPChdtAgxJ4QuDqUfAjTMqTive+XUyT6ch3rgZrL1bQ=");
        discordProfile = createSkinProfile("ewogICJ0aW1lc3RhbXAiIDogMTYxOTMyNTA5NjExMiwKICAicHJvZmlsZUlkIiA6ICJhNzdkNmQ2YmFjOWE0NzY3YTFhNzU1NjYxOTllYmY5MiIsCiAgInByb2ZpbGVOYW1lIiA6ICIwOEJFRDUiLAogICJzaWduYXR1cmVSZXF1aXJlZCIgOiB0cnVlLAogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZDM0MDEzN2RjZDY3NzEyOGU4YWRhNDM0MDRlMTcyMThmODEwZTk1NzZjM2JmNzI5NDdlMzE5MTgyNzJlNTJiMSIKICAgIH0KICB9Cn0=",
                "MBq9TqrLLY7UZmqYDNO0cvsRI+RD4wD/CWpraSR1x04K3ICcKT4J49E0jP3XM95TjpIox/4B/Q+qkgYJZBzQQZiP4NZUcXx8uxqkidzqDBXMXLXeZEtK+pfZM8RXPKHSmppjy3o8nvtWUyv9vcDu/8k+72vx3bQ5Kbarx2LRtPBszEKMU0vdaggxTzZmVXX560RuvZwGirKTz5n4LiAyLhTyVRCplkW09K1I3f6N94+1tYBD9ST6rTqZcbRTS8bKgtbZIfSIAhGE8H5YbK2Z5BYhaGdjcTL3SHlSGZiz7acxVfD60uGI0o6mFRNxbIAATzx8aF92oDxCPXXKFY0NP9y0sDM9yZd28DCE0hq3X4UIYShfZ51ToioP6+TDpc3+qshGYaipfs46/eahf0ffgBlri/qit+u6aExDL2onb+bKBujt9+C1PeH31RXMlghWCjR7hnxu4dE5ldK7Xw2jhTlHhU0250BuDi4EQ9lGwYHQBlLPBYM55+PnfNnFtPz/3sk5ivspygl01d89beBJ3dK0ZtD6y51X8w3UMQPY3IAfR+rz2az/U9ZUPVq+CYDMnDr2JM8i4ABy58QfREwmF9LvegMVSn+GMjZWaVzqsqjE2827BMoJfzyKG4EoGfy/5qTQXZ6shqUSuenSTWSrNKWWoIG61Ga371YFBLAeCrA=");
    }

    public SocialMediaMenu(Profile target, Profile opener, boolean readyOnly) {
        super(6 * 9, tl(opener.getLocale(), "menu.social-media.title-name", target.getName()));

        for (int i = 9; i <= 17; i++)
            setItem(i, new ItemBuilder(Material.STAINED_GLASS_PANE).setDurability(1).setName("§6-").build(), new MenuClickHandler() {
                @Override
                public void onClick(Player p, Inventory inv, ClickType type, ItemStack stack, int slot) {

                }
            });

        if (!readyOnly) {
            String publicLinks = tl(target.getLocale(), "menu.social-media.public-links");

            String leftClickToEdit = tl(target.getLocale(), "menu.social-media.left-click-edit");
            String rightClickToEdit = tl(target.getLocale(), "menu.social-media.right-click-edit");

            String twitter = target.getData(DataType.TWITTER_LINK).getAsString();
            String twitterLink = twitter.isEmpty() ? tl(target.getLocale(), "menu.social-media.no-link") : twitter;
            String currentTwitter = tl(target.getLocale(), "menu.social-media.current") + "\n" + twitterLink;

            setItem(20, new ItemBuilder(Material.SKULL_ITEM).setDurability(3).setPlayerHead(twitterProfile)
                    .setName("§bTwitter").setLoreText(currentTwitter + "\n\n" + leftClickToEdit + "\n" + rightClickToEdit + "\n\n" +
                            publicLinks).build(), new MenuClickHandler() {
                @Override
                public void onClick(Player p, Inventory inv, ClickType type, ItemStack stack, int slot) {
                    Profile profile = Profile.getProfile(p);
                    if (type == ClickType.RIGHT) {
                        if (!profile.getData(DataType.TWITTER_LINK).getAsString().isEmpty()) {
                            profile.setData(DataType.TWITTER_LINK, "");
                            profile.save();
                            Commons.getRedisBackend().publish(PacketType.UPDATE_IN_SINGLE_DATA.toString(),
                                    Commons.getGson().toJson(new PacketUpdateSingleData(p.getUniqueId(), DataType.TWITTER_LINK, profile.getData(DataType.TWITTER_LINK))));
                            p.sendMessage(tl(profile.getLocale(), "menu.social-media.removed-twitter"));
                        } else {
                            p.sendMessage(tl(profile.getLocale(), "menu.social-media.not-linked", "Twitter"));
                        }
                    } else {

                    }
                }
            });

            String youtube = target.getData(DataType.YOUTUBE_LINK).getAsString();
            String youtubeLink = youtube.isEmpty() ? tl(target.getLocale(), "menu.social-media.no-link") : youtube;
            String currentYoutube = tl(target.getLocale(), "menu.social-media.current") + "\n" + youtubeLink;

            setItem(21, new ItemBuilder(Material.SKULL_ITEM).setDurability(3).setPlayerHead(youtubeProfile)
                    .setName("§cYouTube").setLoreText(currentYoutube + "\n\n" + leftClickToEdit + "\n" + rightClickToEdit + "\n\n" +
                            publicLinks).build(), new MenuClickHandler() {
                @Override
                public void onClick(Player p, Inventory inv, ClickType type, ItemStack stack, int slot) {
                    Profile profile = Profile.getProfile(p);
                    if (type == ClickType.RIGHT) {
                        if (!profile.getData(DataType.YOUTUBE_LINK).getAsString().isEmpty()) {
                            profile.setData(DataType.YOUTUBE_LINK, "");
                            profile.save();
                            Commons.getRedisBackend().publish(PacketType.UPDATE_IN_SINGLE_DATA.toString(),
                                    Commons.getGson().toJson(new PacketUpdateSingleData(p.getUniqueId(), DataType.YOUTUBE_LINK, profile.getData(DataType.YOUTUBE_LINK))));
                            p.sendMessage(tl(profile.getLocale(), "menu.social-media.removed-youtube"));
                        } else {
                            p.sendMessage(tl(profile.getLocale(), "menu.social-media.not-linked", "YouTube"));
                        }
                    } else {

                    }
                }
            });

            String instagram = target.getData(DataType.INSTAGRAM_LINK).getAsString();
            String instagramLink = instagram.isEmpty() ? tl(target.getLocale(), "menu.social-media.no-link") : instagram;
            String currentInstagram = tl(target.getLocale(), "menu.social-media.current") + "\n" + instagramLink;

            setItem(22, new ItemBuilder(Material.SKULL_ITEM).setDurability(3).setPlayerHead(instagramProfile)
                    .setName("§dInstagram").setLoreText(currentInstagram + "\n\n" + leftClickToEdit + "\n" + rightClickToEdit + "\n\n" +
                            publicLinks).build(), new MenuClickHandler() {
                @Override
                public void onClick(Player p, Inventory inv, ClickType type, ItemStack stack, int slot) {
                    Profile profile = Profile.getProfile(p);
                    if (type == ClickType.RIGHT) {
                        if (!profile.getData(DataType.INSTAGRAM_LINK).getAsString().isEmpty()) {
                            profile.setData(DataType.INSTAGRAM_LINK, "");
                            profile.save();
                            Commons.getRedisBackend().publish(PacketType.UPDATE_IN_SINGLE_DATA.toString(),
                                    Commons.getGson().toJson(new PacketUpdateSingleData(p.getUniqueId(), DataType.INSTAGRAM_LINK, profile.getData(DataType.INSTAGRAM_LINK))));
                            p.sendMessage(tl(profile.getLocale(), "menu.social-media.removed-instagram"));
                        } else {
                            p.sendMessage(tl(profile.getLocale(), "menu.social-media.not-linked", "Instagram"));
                        }
                    } else {

                    }
                }
            });

            String twitch = target.getData(DataType.TWITCH_LINK).getAsString();
            String twitchLink = twitch.isEmpty() ? tl(target.getLocale(), "menu.social-media.no-link") : twitch;
            String currentTwitch = tl(target.getLocale(), "menu.social-media.current") + "\n" + twitchLink;

            setItem(23, new ItemBuilder(Material.SKULL_ITEM).setDurability(3).setPlayerHead(twitchProfile)
                    .setName("§5Twitch").setLoreText(currentTwitch + "\n\n" + leftClickToEdit + "\n" + rightClickToEdit + "\n\n" +
                            publicLinks).build(), new MenuClickHandler() {
                @Override
                public void onClick(Player p, Inventory inv, ClickType type, ItemStack stack, int slot) {
                    Profile profile = Profile.getProfile(p);
                    if (type == ClickType.RIGHT) {
                        if (!profile.getData(DataType.TWITCH_LINK).getAsString().isEmpty()) {
                            profile.setData(DataType.TWITCH_LINK, "");
                            profile.save();
                            Commons.getRedisBackend().publish(PacketType.UPDATE_IN_SINGLE_DATA.toString(),
                                    Commons.getGson().toJson(new PacketUpdateSingleData(p.getUniqueId(), DataType.TWITCH_LINK, profile.getData(DataType.TWITCH_LINK))));
                            p.sendMessage(tl(profile.getLocale(), "menu.social-media.removed-twitch"));
                        } else {
                            p.sendMessage(tl(profile.getLocale(), "menu.social-media.not-linked", "Twitch"));
                        }
                    } else {

                    }
                }
            });

            String tiktok = target.getData(DataType.TIKTOK_LINK).getAsString();
            String tiktokLink = tiktok.isEmpty() ? tl(target.getLocale(), "menu.social-media.no-link") : tiktok;
            String currentTiktok = tl(target.getLocale(), "menu.social-media.current") + "\n" + tiktokLink;

            setItem(24, new ItemBuilder(Material.SKULL_ITEM).setDurability(3).setPlayerHead(tiktokProfile)
                    .setName("§8TikTok").setLoreText(currentTiktok + "\n\n" + leftClickToEdit + "\n" + rightClickToEdit + "\n\n" +
                            publicLinks).build(), new MenuClickHandler() {
                @Override
                public void onClick(Player p, Inventory inv, ClickType type, ItemStack stack, int slot) {
                    Profile profile = Profile.getProfile(p);
                    if (type == ClickType.RIGHT) {
                        if (!profile.getData(DataType.TIKTOK_LINK).getAsString().isEmpty()) {
                            profile.setData(DataType.TIKTOK_LINK, "");
                            profile.save();
                            Commons.getRedisBackend().publish(PacketType.UPDATE_IN_SINGLE_DATA.toString(),
                                    Commons.getGson().toJson(new PacketUpdateSingleData(p.getUniqueId(), DataType.TIKTOK_LINK, profile.getData(DataType.TIKTOK_LINK))));
                            p.sendMessage(tl(profile.getLocale(), "menu.social-media.removed-tiktok"));
                        } else {
                            p.sendMessage(tl(profile.getLocale(), "menu.social-media.not-linked", "TikTok"));
                        }
                    } else {

                    }
                }
            });

            String discord = target.getData(DataType.DISCORD_LINK).getAsString();
            String discordLink = discord.isEmpty() ? tl(target.getLocale(), "menu.social-media.no-link") : discord;
            String currentDiscord = tl(target.getLocale(), "menu.social-media.current") + "\n" + discordLink;

            setItem(31, new ItemBuilder(Material.SKULL_ITEM).setDurability(3).setPlayerHead(discordProfile)
                    .setName("§3Discord").setLoreText(currentDiscord + "\n\n" + leftClickToEdit + "\n" + rightClickToEdit + "\n\n" +
                            publicLinks).build(), new MenuClickHandler() {
                @Override
                public void onClick(Player p, Inventory inv, ClickType type, ItemStack stack, int slot) {
                    Profile profile = Profile.getProfile(p);
                    if (type == ClickType.RIGHT) {
                        if (!profile.getData(DataType.DISCORD_LINK).getAsString().isEmpty()) {
                            profile.setData(DataType.DISCORD_LINK, "");
                            profile.save();
                            Commons.getRedisBackend().publish(PacketType.UPDATE_IN_SINGLE_DATA.toString(),
                                    Commons.getGson().toJson(new PacketUpdateSingleData(p.getUniqueId(), DataType.DISCORD_LINK, profile.getData(DataType.DISCORD_LINK))));
                            p.sendMessage(tl(profile.getLocale(), "menu.social-media.removed-discord"));
                        } else {
                            p.sendMessage(tl(profile.getLocale(), "menu.social-media.not-linked", "Discord"));
                        }
                    } else {

                    }
                }
            });
        } else {

        }
    }

    public static GameProfile createSkinProfile(String value, String signature) {
        GameProfile profile = new GameProfile(UUID.randomUUID(), null);
        profile.getProperties().put("textures", new Property("textures", value, signature));
        return profile;
    }
}
