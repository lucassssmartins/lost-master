package com.lostmc.core.punishment;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import static com.lostmc.core.punishment.Punishment.Type.*;

import java.util.Objects;
import java.util.UUID;

@Getter
@RequiredArgsConstructor
public class Punishment {

    private final String id;
    private final String reason;
    private final String serverId;
    private final PlayerData playerData, actorData;
    private final long createdIn;
    private final Type type;

    public enum Type {

        /**
         * Baniment by unique id.
         */
        BAN,

        /**
         * Baniment by ip address.
         */
        IP_BAN,

        /**
         * Silent by unique id.
         */
        SILENT;
    }

    @AllArgsConstructor
    @Getter
    public enum Category {

        /**
         * Cheating punishment category
         */
        CHEATING(BAN, "Uso de Trapaças", "-1"),

        /**
         * Chargeback punishment category
         */
        CHARGEBACK(BAN, "Chargeback", "-1"),

        /**
         * Community punishment category
         */
        COMMUNITY(BAN, "Violação das diretrizes da comunidade", "-1"),

        /**
         * Blacklist punishment category
         */
        BLACKLIST(BAN, "", "-1", false),

        ;

        private final Type type;
        private final String defaultReason;
        private final String timeFormat;
        private final boolean pardonnable;

        Category(Type type, String defaultReason, String timeFormat) {
            this(type, defaultReason, timeFormat, true);
        }
    }

    @Getter
    @AllArgsConstructor
    public static class PlayerData {

        private final UUID uniqueId;
        private final String name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o instanceof Punishment) {
            Punishment that = (Punishment) o;
            return that.id.equals(this.id);
        }
        return false;
    }

    @Override
    public String toString() {
        return '#' + this.id;
    }
}
