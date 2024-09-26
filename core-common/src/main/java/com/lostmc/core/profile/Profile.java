package com.lostmc.core.profile;

import com.google.gson.reflect.TypeToken;
import com.lostmc.core.networking.PacketType;
import com.lostmc.core.networking.PacketUpdateSingleData;
import com.lostmc.core.profile.collect.CollectCategory;
import com.lostmc.core.profile.collect.CollectRarity;
import com.lostmc.core.profile.collect.Collectable;
import com.lostmc.core.profile.data.DataType;
import com.lostmc.core.profile.medal.Medal;
import com.lostmc.core.profile.product.PermissionProduct;
import com.lostmc.core.profile.product.RankProduct;
import com.lostmc.core.profile.rank.Rank;
import com.lostmc.core.profile.tag.Tag;
import com.lostmc.core.Commons;
import com.google.gson.JsonElement;
import com.lostmc.core.property.IProperty;
import com.lostmc.core.property.SkinSource;
import com.lostmc.core.server.ServerType;
import lombok.Getter;
import lombok.Setter;
import org.inventivetalent.reflection.resolver.FieldResolver;
import org.inventivetalent.reflection.util.AccessUtil;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

/**
 * The @link ProfileHolder holds all players statistics, datas and other informations.
 */
public class Profile {

    public static final FieldResolver FIELD_RESOLVER = new FieldResolver(Profile.class);
    private static final Type ARRAY_LIST_TYPE = new TypeToken<ArrayList<String>>(){}.getType();

    @Getter
    private final UUID uniqueId;
    @Getter
    private final String name;

    @Getter
    @Setter
    private Locale locale = Commons.DEFAULT_LOCALE;
    @Getter
    @Setter
    private Tag tag = Tag.DEFAULT;
    @Getter
    @Setter
    private Medal medal;

    @Getter
    @Setter
    private IProperty property;
    @Getter
    @Setter
    private SkinSource skinSource;

    @Getter
    private final ArrayList<Collectable> collectables = new ArrayList<>();

    @Getter
    private final ArrayList<RankProduct> ranks = new ArrayList<>();
    @Getter
    private final ArrayList<PermissionProduct> permissions = new ArrayList<>();

    private HashMap<DataType, JsonElement> datas = new HashMap<>();

    private transient Map<Class<?>, Object> resources;

    public Profile(final UUID uniqueId, final String name) {
        this.uniqueId = uniqueId;
        this.name = name;
        checkForPendingDatas();
    }

    public List<Collectable> getCollectables(CollectCategory category) {
        return this.collectables.stream().filter(a -> a.getCategory().equals(category)).
                collect(Collectors.toList());
    }

    public List<Collectable> getCollectables(CollectCategory category, CollectRarity rarity) {
        return this.collectables.stream().filter(a -> a.getCategory().equals(category) && a.getRarity().equals(rarity)).
                collect(Collectors.toList());
    }

    private void initResourcesMap() {
        if (this.resources == null) { this.resources = new HashMap<>(); }
    }

    public <T> T getResource(Class<T> tClass) {
        initResourcesMap();
        return tClass.cast(this.resources.get(tClass));
    }

    public void addResource(Object src) {
        addResource(src.getClass(), src);
    }

    public void addResource(Class<?> clazz, Object src) {
        initResourcesMap();
        this.resources.put(clazz, src);
    }

    public void checkForPendingDatas() {
        for (DataType type : DataType.values()) {
            if (!datas.containsKey(type)) {
                datas.put(type, Commons.getGson().toJsonTree(type.generateNewValue()));
            }
        }
    }

    public JsonElement getData(DataType type) {
        return this.datas.get(type);
    }

    public List<String> getDataArray(DataType type) {
        return Commons.getGson().fromJson(this.datas.get(type).toString(), ARRAY_LIST_TYPE);
    }

    public synchronized void setData(DataType type, Object value) {
        JsonElement jsonElement = Commons.getGson().toJsonTree(value);
        this.datas.put(type, jsonElement);

        if (Commons.getProxyHandler().getLocal().getServerType() == ServerType.PROXY) {
            Commons.getRedisBackend().publish(PacketType.UPDATE_OUT_SINGLE_DATA.toString(),
                    new PacketUpdateSingleData(getUniqueId(), type, jsonElement).toJson());
        } else {
            Commons.getPlatform().updateData(getUniqueId(), type, value);
        }
    }

    public void setDataElement(DataType type, JsonElement data) {
        this.datas.put(type, data);
    }

    public Rank getRank() {
        Rank general = Rank.DEFAULT;
        for (Rank rank : getRanksObjects()) {
            if (rank.ordinal() < general.ordinal()) {
                general = rank;
            }
        }
        return general;
    }

    public void addRank(RankProduct product) {
        this.ranks.add(product);
    }

    public Collection<Rank> getRanksObjects() {
        Collection<Rank> collect = new CopyOnWriteArrayList<>();
        Iterator<RankProduct> it = this.ranks.iterator();
        while (it.hasNext()) {
            RankProduct next = it.next();
            if (next.isExpired())
                continue;
            if (collect.contains(next.getObject()))
                continue;
            collect.add(next.getObject());
        }
        return collect;
    }

    public RankProduct removeRank(int idx) {
        try {
            return this.ranks.remove(idx);
        } catch (Exception indexOutOfBounds) {
            return null;
        }
    }

    public int removeRanks(Rank rank) {
        int j = 0;
        Iterator<RankProduct> it = this.ranks.iterator();
        while (it.hasNext()) {
            RankProduct next = it.next();
            if (!next.getObject().equals(rank))
                continue;
            it.remove();
            ++j;
        }
        return j;
    }

    public void addPermission(PermissionProduct product) {
        this.permissions.add(product);
    }

    public Collection<String> getPermissionsObjects() {
        Collection<String> collect = new CopyOnWriteArrayList<>();
        Iterator<PermissionProduct> it = this.permissions.iterator();
        while (it.hasNext()) {
            PermissionProduct next = it.next();
            if (next.isExpired())
                continue;
            if (collect.contains(next.getName()))
                continue;
            collect.add(next.getObject());
        }
        return collect;
    }

    public boolean isPermissionSet(String name) {
        Iterator<PermissionProduct> it = this.permissions.iterator();
        while (it.hasNext()) {
            PermissionProduct next = it.next();
            if (next.isExpired())
                continue;
            if (!next.getObject().equals(name))
                continue;
            return true;
        }
        return false;
    }

    public int removePermissions(String name) {
        int j = 0;
        Iterator<PermissionProduct> it = this.permissions.iterator();
        while (it.hasNext()) {
            PermissionProduct next = it.next();
            if (!next.getObject().equals(name))
                continue;
            it.remove();
            ++j;
        }
        return j;
    }

    public void clearPermissions() {
        this.permissions.clear();
    }

    public void save() {
        if (Commons.getProxyHandler().getLocal().getServerType() != ServerType.AUTH) {
            Commons.getStorageCommon().getAccountStorage().saveProfile(this);
        }
    }

    public static Profile fromUniqueId(UUID uniqueId) {
        return Commons.getProfileMap().get(uniqueId);
    }

    public static Profile getProfile(Object player) {
        try {
            Method method = AccessUtil.setAccessible(player.getClass().getMethod("getUniqueId"));
            return fromUniqueId((UUID) method.invoke(player));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o instanceof Profile)
            return this.uniqueId.equals(((Profile) o).uniqueId);
        return false;
    }
}
