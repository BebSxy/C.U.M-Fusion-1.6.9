
package me.cum.fusion.features.gui.alts;

import net.minecraft.client.*;
import java.net.*;
import com.mojang.authlib.yggdrasil.*;
import com.mojang.authlib.*;
import net.minecraft.util.*;
import me.cum.fusion.*;
import java.lang.reflect.*;
import java.util.*;

public class AltManager
{
    private static final List<AltEntry> alts;
    private static Minecraft mc;
    
    public static YggdrasilUserAuthentication logIn(final String email, final String password, final boolean setSession) {
        final YggdrasilUserAuthentication auth = (YggdrasilUserAuthentication)new YggdrasilAuthenticationService(Proxy.NO_PROXY, "").createUserAuthentication(Agent.MINECRAFT);
        auth.setUsername(email);
        auth.setPassword(password);
        final YggdrasilUserAuthentication yggdrasilUserAuthentication = null;
        final Session[] session = { null };
        final YggdrasilUserAuthentication yggdrasilUserAuthentication2;
        final Object o;
        new Thread(() -> {
            try {
                yggdrasilUserAuthentication2.logIn();
                if (setSession) {
                    o[0] = new Session(yggdrasilUserAuthentication2.getSelectedProfile().getName(), yggdrasilUserAuthentication2.getSelectedProfile().getId().toString(), yggdrasilUserAuthentication2.getAuthenticatedToken(), "mojang");
                    if (o[0] != null) {
                        setSession(o[0]);
                    }
                }
            }
            catch (Exception exception) {
                exception.printStackTrace();
            }
            return;
        }).start();
        return auth;
    }
    
    public static List<AltEntry> getAlts() {
        return AltManager.alts;
    }
    
    public static void setSession(final Session newSession) {
        final Class<? extends Minecraft> mc = Minecraft.getMinecraft().getClass();
        try {
            Field session = null;
            for (final Field field : mc.getDeclaredFields()) {
                if (field.getType().isInstance(newSession)) {
                    session = field;
                    Fusion.LOGGER.info("Attempting Injection into Session.");
                }
            }
            if (session == null) {
                throw new IllegalStateException("No field of type " + Session.class.getCanonicalName() + " declared.");
            }
            session.setAccessible(true);
            session.set(Minecraft.getMinecraft(), newSession);
            session.setAccessible(false);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    static {
        alts = new ArrayList<AltEntry>();
        AltManager.mc = Minecraft.getMinecraft();
    }
}
