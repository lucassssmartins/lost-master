package com.lostmc.core.wrapper;

import lombok.Getter;
import lombok.Setter;
import org.inventivetalent.reflection.accessor.FieldAccessor;
import org.inventivetalent.reflection.resolver.ClassResolver;
import org.inventivetalent.reflection.resolver.FieldResolver;
import org.inventivetalent.reflection.resolver.minecraft.NMSClassResolver;
import org.inventivetalent.reflection.resolver.minecraft.OBCClassResolver;
import org.inventivetalent.reflection.resolver.wrapper.ClassWrapper;

import java.lang.reflect.Method;

public class AbstractWrapper {

    protected static final ClassResolver CLASS_RESOLVER     = new ClassResolver();
    protected static final NMSClassResolver NMS_CLASS_RESOLVER = new NMSClassResolver();
    protected static final OBCClassResolver OBC_CLASS_RESOLVER = new OBCClassResolver();

    @Getter
    private final Class<?> handleType;
    @Getter
    @Setter
    private Object         handle;
    @Getter
    private final ClassWrapper classWrapper;
    private final Type type;
    protected final FieldResolver fieldResolver;

    public AbstractWrapper(Class<?> handleType) {
        this.handleType = handleType;
        this.classWrapper = new ClassWrapper<>(handleType);
        this.type = Type.fromPackage(handleType.getName());
        this.fieldResolver = new FieldResolver(handleType);
    }

    public <T> T getFieldValue(String... names) {
        FieldAccessor accessor = getFieldResolver().resolveAccessor(names);
        return accessor != null ? accessor.get(getHandle()) : null;
    }

    public <T> void setFieldValue(T value, String... names) {
        FieldAccessor accessor = getFieldResolver().resolveAccessor(names);
        if (accessor != null) { accessor.set(getHandle(), value); }
    }

    public Type getType() {
        return type;
    }

    public FieldResolver getFieldResolver() {
        return fieldResolver;
    }

    public Method resolveMethod(String name, Class<?>... parameterTypes) {
        if (this.handle == null)
            throw new IllegalStateException("handle is not defined");
        Method found = null;
        Class<?> search = this.handle.getClass();
        try {
            found = search.getDeclaredMethod(name, parameterTypes);
        } catch (Exception err) {
            while ((search = search.getSuperclass()) != null && found == null) {
                try {
                    (found = search.getDeclaredMethod(name, parameterTypes)).setAccessible(true);
                } catch (Exception err2) {
                    continue;
                }
            }
        }
        return found;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;

        if (obj instanceof AbstractWrapper) {
            AbstractWrapper that = (AbstractWrapper) obj;
            return this.handle.equals(that.handle);
        }

        return false;
    }

    @Override
    public int hashCode() {
        return this.handle.hashCode();
    }

    @Override
    public String toString() {
        return getClass().getName() + "[handle=" + this.handle + "]";
    }

    public enum Type {
        GENERAL,
        NMS,
        OBC;

        public static Type fromPackage(String className) {
            if (className.startsWith("net.minecraft.server")) { return NMS; }
            if (className.startsWith("org.bukkit.craftbukkit")) { return OBC; }
            return GENERAL;
        }

        public ClassResolver getClassResolver() {
            switch (this) {
                case GENERAL:
                    return CLASS_RESOLVER;
                case NMS:
                    return NMS_CLASS_RESOLVER;
                case OBC:
                    return OBC_CLASS_RESOLVER;
                default:
                    throw new IllegalStateException();
            }

        }
    }
}
