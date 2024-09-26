package com.lostmc.core.backend.redis;

import com.lostmc.core.backend.Backend;
import com.lostmc.core.utils.JsonUtils;
import com.lostmc.core.Commons;
import com.lostmc.core.profile.Profile;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisPubSub;

import java.util.UUID;

@Getter
@RequiredArgsConstructor
public class RedisBackend implements Backend {

    private final String hostname;
    private final String password;
    private final int port;

    private JedisPool pool;

    @Override
    public void connect() throws Throwable {
        if (!this.password.isEmpty())
            pool = new JedisPool(new JedisPoolConfig(), this.hostname, this.port, 0,
                    this.password);
        else {
            pool = new JedisPool(new JedisPoolConfig(), this.hostname, this.port, 0);
        }
    }

    public void publish(String channel, String message) {
        Commons.getPlatform().runAsync(()-> {
            try (Jedis j = this.pool.getResource()) {
                j.publish(channel, message);
            }
        });
    }

    public void saveRedisProfile(Profile profile) {
       Commons.getPlatform().runAsync(()-> {
           try (Jedis j = this.pool.getResource()) {
               j.hmset("profile:" + profile.getUniqueId().toString(), JsonUtils.objectToMap(profile));
           }
       });
    }

    public void saveSyncRedisProfile(Profile profile) {
        try (Jedis j = this.pool.getResource()) {
            j.hmset("profile:" + profile.getUniqueId().toString(), JsonUtils.objectToMap(profile));
        }
    }

    public void removeRedisProfile(UUID id) {
        Commons.getPlatform().runAsync(()-> {
            try (Jedis j = this.pool.getResource()) {
                if (j.exists("profile:" + id.toString())){
                    j.del("profile:" + id.toString());
                }
            }
        });
    }

    public Profile getRedisProfile(UUID id) {
        try (Jedis j = this.pool.getResource()) {
            if (!j.exists("profile:" + id.toString()))
                return null;
            return JsonUtils.mapToObject(j.hgetAll("profile:" + id.toString()), Profile.class);
        }
    }

    @Override
    public void disconnect() throws Throwable {
        if (this.pool != null) {
            this.pool.close();
        }
    }

    @Override
    public boolean isConnected() {
        return this.pool != null && !this.pool.isClosed();
    }

    public void registerPubSub(JedisPubSub pubSub, String... channels) {
        Commons.getPlatform().runAsync(new PubSubTask(pubSub, channels));
    }

    public class PubSubTask implements Runnable {

        private JedisPubSub jpsh;
        private final String[] channels;

        public PubSubTask(JedisPubSub s, String... channels) {
            this.jpsh = s;
            this.channels = channels;
        }

        @Override
        public void run() {
            boolean broken = false;
            try (Jedis rsc = pool.getResource()) {
                try {
                    rsc.subscribe(jpsh, channels);
                } catch (Throwable e) {
                    e.printStackTrace();
                    try {
                        jpsh.unsubscribe();
                    } catch (Throwable e1) {}
                    broken = true;
                }
            }
            if (broken) {
                run();
            }
        }

        public void addChannel(String... channel) {
            jpsh.subscribe(channel);
        }

        public void removeChannel(String... channel) {
            jpsh.unsubscribe(channel);
        }

        public void poison() {
            jpsh.unsubscribe();
        }
    }
}
