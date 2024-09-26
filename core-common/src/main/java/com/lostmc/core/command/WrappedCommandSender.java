package com.lostmc.core.command;

import com.lostmc.core.Commons;
import com.lostmc.core.profile.Profile;
import com.lostmc.core.translate.Translator;
import com.lostmc.core.wrapper.AbstractWrapper;
import org.inventivetalent.reflection.resolver.wrapper.MethodWrapper;

import java.util.Locale;

public class WrappedCommandSender extends AbstractWrapper {

    public WrappedCommandSender(Class<?> handleType) {
        super(handleType);
    }

    public String getName() {
        return (String) new MethodWrapper<>(resolveMethod("getName")).invokeSilent(getHandle());
    }

    public void tlMessage(String key, Object... format) {
        sendMessage(Translator.tl(getLocale(), key, format));
    }

    public void sendMessage(String message) {
        new MethodWrapper<>(resolveMethod("sendMessage", String.class)).invokeSilent(getHandle(), message);
    }

    public void sendInGameMessage() {
        sendMessage("§4§lERRO§f Comando disponível apenas §c§lin-game!");
    }

    public Locale getLocale() {
        if (isCraftPlayerClass())
            return Profile.getProfile(getHandle()).getLocale();
       else {
            return Commons.DEFAULT_LOCALE;
        }
    }

    public boolean hasPermission(String name) {
        return new MethodWrapper<Boolean>(resolveMethod("hasPermission", String.class))
                .invokeSilent(getHandle(), name);
    }

    private boolean isCraftPlayerClass() {
        return getClassWrapper().getName().endsWith("CraftPlayer");
    }
}
