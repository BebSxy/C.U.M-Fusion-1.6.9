
package me.cum.fusion.features.gui.alts;

import net.minecraft.client.*;
import com.mojang.authlib.yggdrasil.*;
import net.minecraft.util.*;
import org.lwjgl.opengl.*;
import net.minecraft.client.resources.*;
import com.mojang.authlib.minecraft.*;
import net.minecraft.client.gui.*;
import me.cum.fusion.util.*;
import java.util.*;

public class AltEntry implements GuiListExtended.IGuiListEntry
{
    private Minecraft mc;
    private String email;
    private String nick;
    private String password;
    private YggdrasilUserAuthentication auth;
    private ResourceLocation unknown;
    private ResourceLocation selected;
    
    public AltEntry(final String email, final String password) {
        this.mc = Minecraft.getMinecraft();
        this.unknown = new ResourceLocation("textures/misc/unknown_server.png");
        this.selected = new ResourceLocation("textures/gui/world_selection.png");
        this.email = email;
        this.nick = "";
        this.password = password;
        this.auth = AltManager.logIn(email, password, false);
    }
    
    public void updatePosition(final int slotIndex, final int x, final int y, final float partialTicks) {
    }
    
    public void drawEntry(final int slotIndex, final int x, final int y, final int listWidth, final int slotHeight, final int mouseX, final int mouseY, final boolean isSelected, final float partialTicks) {
        try {
            this.mc.fontRenderer.drawStringWithShadow(this.auth.getSelectedProfile().getName(), (float)(x + 36), (float)(y + 2), -1);
            this.mc.fontRenderer.drawStringWithShadow(this.email, (float)(x + 36), (float)(y + 12), -7829368);
            this.mc.fontRenderer.drawStringWithShadow("Premium", (float)(x + 36), (float)(y + 22), -11141291);
            GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
            ResourceLocation resourcelocation = DefaultPlayerSkin.getDefaultSkinLegacy();
            final Map<MinecraftProfileTexture.Type, MinecraftProfileTexture> map = (Map<MinecraftProfileTexture.Type, MinecraftProfileTexture>)this.mc.getSkinManager().loadSkinFromCache(this.auth.getSelectedProfile());
            if (map.containsKey(MinecraftProfileTexture.Type.SKIN)) {
                resourcelocation = this.mc.getSkinManager().loadSkin((MinecraftProfileTexture)map.get(MinecraftProfileTexture.Type.SKIN), MinecraftProfileTexture.Type.SKIN);
            }
            this.mc.getTextureManager().bindTexture(resourcelocation);
            GL11.glEnable(3042);
            Gui.drawModalRectWithCustomSizedTexture(x, y, 0.0f, 0.0f, 32, 32, 32.0f, 32.0f);
            GL11.glDisable(3042);
            if (isSelected) {
                this.mc.getTextureManager().bindTexture(this.selected);
                RenderUtil.drawRect((float)x, (float)y, 32.0f, 32.0f, -1601138544);
                GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
                Gui.drawModalRectWithCustomSizedTexture(x - 6, y + 3, 32.0f, 3.0f, 32, 32, 256.0f, 256.0f);
            }
        }
        catch (NullPointerException npe) {
            this.mc.fontRenderer.drawStringWithShadow("Unknown Account", (float)(x + 36), (float)(y + 2), -43691);
            GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
            this.mc.getTextureManager().bindTexture(this.unknown);
            GL11.glEnable(3042);
            Gui.drawModalRectWithCustomSizedTexture(x, y, 0.0f, 0.0f, 32, 32, 32.0f, 32.0f);
            GL11.glDisable(3042);
            if (isSelected) {
                this.mc.getTextureManager().bindTexture(this.selected);
                RenderUtil.drawRect((float)x, (float)y, 32.0f, 32.0f, -1601138544);
                GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
                Gui.drawModalRectWithCustomSizedTexture(x - 6, y + 3, 32.0f, 3.0f, 32, 32, 256.0f, 256.0f);
            }
        }
    }
    
    public boolean mousePressed(final int slotIndex, final int mouseX, final int mouseY, final int mouseEvent, final int relativeX, final int relativeY) {
        if (relativeX <= 32 && relativeX < 32) {
            AltManager.logIn(this.email, this.password, true);
            return true;
        }
        return false;
    }
    
    public void mouseReleased(final int slotIndex, final int x, final int y, final int mouseEvent, final int relativeX, final int relativeY) {
    }
    
    public String getName() {
        return this.auth.getSelectedProfile().getName();
    }
    
    public String getEmail() {
        return this.email;
    }
    
    public String getPassword() {
        return this.password;
    }
}
