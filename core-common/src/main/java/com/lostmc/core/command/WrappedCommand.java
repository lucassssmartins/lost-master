package com.lostmc.core.command;

import com.lostmc.core.wrapper.AbstractWrapper;
import org.inventivetalent.reflection.accessor.FieldAccessor;
import org.inventivetalent.reflection.resolver.wrapper.MethodWrapper;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

public abstract class WrappedCommand extends AbstractWrapper {

    public WrappedCommand(Class<?> handleType) {
        super(handleType);
    }

    public String getName() {
        return getFieldResolver().resolveAccessor("name").get(getHandle());
    }

    public String getPermission() {
        return getFieldResolver().resolveAccessor("permission").get(getHandle());
    }

    public void setPermission(String name) {
        getFieldResolver().resolveAccessor("permission").set(getHandle(), name);
    }

    public String getPermissionMessage() {
        return getFieldResolver().resolveAccessor("permissionMessage").get(getHandle());
    }

    public void setPermissionMessage(String message) {
        getFieldResolver().resolveAccessor("permissionMessage").set(getHandle(), message);
    }

    public void setAliases(String... aliases) {
        Field field = getFieldResolver().resolveSilent("aliases");
        if (List.class.equals(field.getType())) {
            new MethodWrapper<>(resolveMethod("setAliases", List.class))
                    .invokeSilent(getHandle(), Arrays.asList(aliases));
        } else {
            new FieldAccessor(field).set(getHandle(), aliases);
        }
    }

    public boolean runAsync() {
        return false;
    }

    public abstract void execute(WrappedCommandSender sender, String label, String[] args);
}
