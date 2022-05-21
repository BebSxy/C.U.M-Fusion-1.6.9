
package me.cum.fusion.features.modules.client;

import me.cum.fusion.features.modules.*;
import net.minecraft.util.*;
import net.minecraft.item.*;
import me.cum.fusion.features.setting.*;
import net.minecraftforge.fml.common.gameevent.*;
import org.lwjgl.opengl.*;
import java.awt.*;
import net.minecraftforge.fml.common.eventhandler.*;
import me.cum.fusion.features.*;
import net.minecraft.client.gui.*;
import me.cum.fusion.*;
import com.mojang.realmsclient.gui.*;
import net.minecraft.client.*;
import net.minecraft.potion.*;
import net.minecraft.client.multiplayer.*;
import java.text.*;
import java.util.*;
import me.cum.fusion.util.*;
import net.minecraft.init.*;
import java.util.function.*;
import net.minecraft.client.renderer.*;
import net.minecraftforge.event.entity.player.*;
import me.cum.fusion.event.events.*;
import net.minecraft.network.play.server.*;
import net.minecraft.util.text.*;

public class HUD extends Module
{
    private static final ResourceLocation box;
    private static final ItemStack totem;
    private static HUD INSTANCE;
    public Setting<String> gameTitle;
    public Setting<Boolean> timestamp;
    private final Setting<Boolean> grayNess;
    private final Setting<Boolean> renderingUp;
    private final Setting<Boolean> waterMark;
    private final Setting<String> waterMarkName;
    private final Setting<Boolean> arrayList;
    private final Setting<Boolean> coords;
    private final Setting<Boolean> direction;
    private final Setting<Boolean> armor;
    private final Setting<Boolean> totems;
    private final Setting<Boolean> greeter;
    public Setting<Boolean> speed;
    public Setting<Boolean> potions;
    public Setting<Boolean> potionSync;
    private final Setting<Boolean> ping;
    private final Setting<Boolean> ms;
    private final Setting<Boolean> tps;
    private final Setting<Boolean> fps;
    private final Setting<Boolean> server;
    private final Setting<Boolean> lag;
    public Setting<Integer> rainbowSpeed;
    public Setting<Integer> rainbowSaturation;
    public Setting<Integer> rainbowBrightness;
    private final Timer timer;
    private Map<String, Integer> players;
    public Setting<Boolean> future;
    public Setting<Boolean> commandPrefix;
    public Setting<Boolean> rainbowPrefix;
    public Setting<String> command;
    public Setting<TextUtil.Color> commandColor;
    public Setting<Boolean> notifyToggles;
    public Setting<Boolean> fusionImage;
    public Setting<Integer> animationHorizontalTime;
    public Setting<Integer> animationVerticalTime;
    public Setting<RenderingMode> renderingMode;
    public Setting<Integer> waterMarkY;
    public Setting<Boolean> textRadar;
    public Setting<Integer> textRadarUpdates;
    public Setting<Boolean> time;
    public Setting<Integer> lagTime;
    public Setting<Boolean> colorSync;
    public Map<Integer, Integer> colorHeightMap;
    public Map<Integer, Integer> colorMap;
    private int color;
    private boolean shouldIncrement;
    private int hitMarkerTimer;
    public float hue;
    Gui gui;
    
    public HUD() {
        super("HUD", "HUD Elements rendered on your screen", Category.CLIENT, true, false, false);
        this.gameTitle = (Setting<String>)this.register(new Setting("AppTitle", (T)"C.U.M Fusion b1.6.9"));
        this.timestamp = (Setting<Boolean>)this.register(new Setting("TimeStamps", (T)Boolean.TRUE));
        this.grayNess = (Setting<Boolean>)this.register(new Setting("Gray", (T)Boolean.TRUE));
        this.renderingUp = (Setting<Boolean>)this.register(new Setting("RenderingUp", (T)Boolean.FALSE, "Orientation of the HUD-Elements."));
        this.waterMark = (Setting<Boolean>)this.register(new Setting("Watermark", (T)Boolean.FALSE, "displays watermark"));
        this.waterMarkName = (Setting<String>)this.register(new Setting("WaterMarkName", (T)"C.U.M Fusion b1.6.9", v -> this.waterMark.getValue()));
        this.arrayList = (Setting<Boolean>)this.register(new Setting("ActiveModules", (T)Boolean.FALSE, "Lists the active modules."));
        this.coords = (Setting<Boolean>)this.register(new Setting("Coords", (T)Boolean.FALSE, "Your current coordinates"));
        this.direction = (Setting<Boolean>)this.register(new Setting("Direction", (T)Boolean.FALSE, "The Direction you are facing."));
        this.armor = (Setting<Boolean>)this.register(new Setting("Armor", (T)Boolean.FALSE, "ArmorHUD"));
        this.totems = (Setting<Boolean>)this.register(new Setting("Totems", (T)Boolean.FALSE, "TotemHUD"));
        this.greeter = (Setting<Boolean>)this.register(new Setting("Welcomer", (T)Boolean.FALSE, "The time"));
        this.speed = (Setting<Boolean>)this.register(new Setting("Speed", (T)Boolean.FALSE, "Your Speed"));
        this.potions = (Setting<Boolean>)this.register(new Setting("Potions", (T)Boolean.FALSE, "Active potion effects"));
        this.potionSync = (Setting<Boolean>)this.register(new Setting("PotionSync", (T)Boolean.FALSE, v -> this.potions.getValue()));
        this.ping = (Setting<Boolean>)this.register(new Setting("Ping", (T)Boolean.FALSE, "Your response time to the server."));
        this.ms = (Setting<Boolean>)this.register(new Setting("ms", (T)false, v -> this.ping.getValue()));
        this.tps = (Setting<Boolean>)this.register(new Setting("TPS", (T)Boolean.FALSE, "Ticks per second of the server."));
        this.fps = (Setting<Boolean>)this.register(new Setting("FPS", (T)Boolean.FALSE, "Your frames per second."));
        this.server = (Setting<Boolean>)this.register(new Setting("Server", (T)false, "Shows the server"));
        this.lag = (Setting<Boolean>)this.register(new Setting("LagNotifier", (T)Boolean.FALSE, "The time"));
        this.rainbowSpeed = (Setting<Integer>)this.register(new Setting("PrefixSpeed", (T)20, (T)0, (T)100));
        this.rainbowSaturation = (Setting<Integer>)this.register(new Setting("Saturation", (T)255, (T)0, (T)255));
        this.rainbowBrightness = (Setting<Integer>)this.register(new Setting("Brightness", (T)255, (T)0, (T)255));
        this.timer = new Timer();
        this.players = new HashMap<String, Integer>();
        this.future = (Setting<Boolean>)this.register(new Setting("Clickgui Gear", (T)true));
        this.commandPrefix = (Setting<Boolean>)this.register(new Setting("CommandPrefix", (T)true));
        this.rainbowPrefix = (Setting<Boolean>)this.register(new Setting("RainbowPrefix", (T)true));
        this.command = (Setting<String>)this.register(new Setting("Command", (T)"C.U.M Fusion"));
        this.commandColor = (Setting<TextUtil.Color>)this.register(new Setting("NameColor", (T)TextUtil.Color.BLUE));
        this.notifyToggles = (Setting<Boolean>)this.register(new Setting("ChatNotify", (T)Boolean.FALSE, "notifys in chat"));
        this.fusionImage = (Setting<Boolean>)this.register(new Setting("Fusion", (T)false, "draws Fusion Image"));
        this.animationHorizontalTime = (Setting<Integer>)this.register(new Setting("AnimationHTime", (T)500, (T)1, (T)1000, v -> this.arrayList.getValue()));
        this.animationVerticalTime = (Setting<Integer>)this.register(new Setting("AnimationVTime", (T)50, (T)1, (T)500, v -> this.arrayList.getValue()));
        this.renderingMode = (Setting<RenderingMode>)this.register(new Setting("Ordering", (T)RenderingMode.ABC));
        this.waterMarkY = (Setting<Integer>)this.register(new Setting("WatermarkPosY", (T)2, (T)0, (T)20, v -> this.waterMark.getValue()));
        this.textRadar = (Setting<Boolean>)this.register(new Setting("TextRadar", (T)Boolean.FALSE, "A TextRadar"));
        this.textRadarUpdates = (Setting<Integer>)this.register(new Setting("TRUpdates", (T)500, (T)0, (T)1000));
        this.time = (Setting<Boolean>)this.register(new Setting("Time", (T)Boolean.FALSE, "The time"));
        this.lagTime = (Setting<Integer>)this.register(new Setting("LagTime", (T)1000, (T)0, (T)2000));
        this.colorSync = (Setting<Boolean>)this.register(new Setting("Sync", (T)Boolean.FALSE, "Universal colors for hud."));
        this.colorHeightMap = new HashMap<Integer, Integer>();
        this.colorMap = new HashMap<Integer, Integer>();
        this.gui = new Gui();
        this.setInstance();
    }
    
    public static HUD getInstance() {
        if (HUD.INSTANCE == null) {
            HUD.INSTANCE = new HUD();
        }
        return HUD.INSTANCE;
    }
    
    private void setInstance() {
        HUD.INSTANCE = this;
    }
    
    @Override
    public void onUpdate() {
        if (this.shouldIncrement) {
            ++this.hitMarkerTimer;
        }
        if (this.hitMarkerTimer == 10) {
            this.hitMarkerTimer = 0;
            this.shouldIncrement = false;
        }
        if (this.timer.passedMs(getInstance().textRadarUpdates.getValue())) {
            this.players = this.getTextRadarPlayers();
            this.timer.reset();
        }
    }
    
    @SubscribeEvent
    public void onTick(final TickEvent.ClientTickEvent event) {
        Display.setTitle((String)this.gameTitle.getValue());
        final int colorSpeed = 101 - this.rainbowSpeed.getValue();
        final float hue = System.currentTimeMillis() % (360L * colorSpeed) / (360.0f * colorSpeed);
        this.hue = hue;
        float tempHue = hue;
        for (int i = 0; i <= 510; ++i) {
            this.colorHeightMap.put(i, Color.HSBtoRGB(tempHue, this.rainbowSaturation.getValue() / 255.0f, this.rainbowBrightness.getValue() / 255.0f));
            tempHue += 0.0013071896f;
        }
    }
    
    @Override
    public void onRender2D(final Render2DEvent event) {
        if (Feature.fullNullCheck()) {
            return;
        }
        final int width = this.renderer.scaledWidth;
        final int height = this.renderer.scaledHeight;
        if (this.textRadar.getValue()) {
            this.drawTextRadar(0);
        }
        this.color = ColorUtil.toRGBA(ClickGui.getInstance().red.getValue(), ClickGui.getInstance().green.getValue(), ClickGui.getInstance().blue.getValue());
        if (this.waterMark.getValue()) {
            final String string = this.waterMarkName.getPlannedValue();
            if (ClickGui.getInstance().rainbow.getValue()) {
                if (ClickGui.getInstance().rainbowModeHud.getValue() == ClickGui.rainbowMode.Static) {
                    this.renderer.drawString(string, 2.0f, this.waterMarkY.getValue(), ColorUtil.rainbow(ClickGui.getInstance().rainbowHue.getValue()).getRGB(), true);
                }
                else {
                    final int[] arrayOfInt = { 1 };
                    final char[] stringToCharArray = string.toCharArray();
                    float f = 0.0f;
                    for (final char c : stringToCharArray) {
                        this.renderer.drawString(String.valueOf(c), 2.0f + f, this.waterMarkY.getValue(), ColorUtil.rainbow(arrayOfInt[0] * ClickGui.getInstance().rainbowHue.getValue()).getRGB(), true);
                        f += this.renderer.getStringWidth(String.valueOf(c));
                        ++arrayOfInt[0];
                    }
                }
            }
            else {
                this.renderer.drawString(string, 2.0f, this.waterMarkY.getValue(), this.color, true);
            }
        }
        final int[] counter1 = { 1 };
        int j = (Util.mc.currentScreen instanceof GuiChat && !this.renderingUp.getValue()) ? 14 : 0;
        if (this.arrayList.getValue()) {
            if (this.renderingUp.getValue()) {
                if (this.renderingMode.getValue() == RenderingMode.ABC) {
                    for (int k = 0; k < Fusion.moduleManager.sortedModulesABC.size(); ++k) {
                        final String str = Fusion.moduleManager.sortedModulesABC.get(k);
                        RenderUtil.drawRectangleCorrectly(width - 2 - this.renderer.getStringWidth(str) - 7, 2 + j * 10, Util.mc.fontRenderer.getStringWidth(str) + 8, Util.mc.fontRenderer.FONT_HEIGHT + 1, ColorUtil.toRGBA(0, 0, 0, 89));
                        RenderUtil.drawRectangleCorrectly(width - 2, 2 + j * 10, 3, Util.mc.fontRenderer.FONT_HEIGHT + 1, ColorUtil.toRGBA(255, 255, 255, 255));
                        this.renderer.drawString(str, (float)(width - 2 - this.renderer.getStringWidth(str) - 5), (float)(2 + j * 10), ((boolean)ClickGui.getInstance().rainbow.getValue()) ? ((ClickGui.getInstance().rainbowModeA.getValue() == ClickGui.rainbowModeArray.Up) ? ColorUtil.rainbow(counter1[0] * ClickGui.getInstance().rainbowHue.getValue()).getRGB() : ColorUtil.rainbow(ClickGui.getInstance().rainbowHue.getValue()).getRGB()) : this.color, true);
                        ++j;
                        ++counter1[0];
                    }
                }
                else {
                    for (int k = 0; k < Fusion.moduleManager.sortedModules.size(); ++k) {
                        final Module module = Fusion.moduleManager.sortedModules.get(k);
                        final String str2 = module.getDisplayName() + ChatFormatting.GRAY + ((module.getDisplayInfo() != null) ? (" [" + ChatFormatting.WHITE + module.getDisplayInfo() + ChatFormatting.GRAY + "]") : "");
                        RenderUtil.drawRectangleCorrectly(width - 2 - this.renderer.getStringWidth(str2) - 7, 2 + j * 10, Util.mc.fontRenderer.getStringWidth(str2) + 8, Util.mc.fontRenderer.FONT_HEIGHT + 1, ColorUtil.toRGBA(0, 0, 0, 89));
                        RenderUtil.drawRectangleCorrectly(width - 2, 2 + j * 10, 3, Util.mc.fontRenderer.FONT_HEIGHT + 1, ColorUtil.toRGBA(255, 255, 255, 255));
                        this.renderer.drawString(str2, (float)(width - 2 - this.renderer.getStringWidth(str2) - 5), (float)(2 + j * 10), ((boolean)ClickGui.getInstance().rainbow.getValue()) ? ((ClickGui.getInstance().rainbowModeA.getValue() == ClickGui.rainbowModeArray.Up) ? ColorUtil.rainbow(counter1[0] * ClickGui.getInstance().rainbowHue.getValue()).getRGB() : ColorUtil.rainbow(ClickGui.getInstance().rainbowHue.getValue()).getRGB()) : this.color, true);
                        ++j;
                        ++counter1[0];
                    }
                }
            }
            else if (this.renderingMode.getValue() == RenderingMode.ABC) {
                for (int k = 0; k < Fusion.moduleManager.sortedModulesABC.size(); ++k) {
                    final String str = Fusion.moduleManager.sortedModulesABC.get(k);
                    j += 10;
                    RenderUtil.drawRectangleCorrectly(width - 2 - this.renderer.getStringWidth(str) - 7, height - j, Util.mc.fontRenderer.getStringWidth(str) + 8, Util.mc.fontRenderer.FONT_HEIGHT + 1, ColorUtil.toRGBA(0, 0, 0, 89));
                    RenderUtil.drawRectangleCorrectly(width - 2, height - j, 3, Util.mc.fontRenderer.FONT_HEIGHT + 1, ColorUtil.toRGBA(255, 255, 255, 255));
                    this.renderer.drawString(str, (float)(width - 2 - this.renderer.getStringWidth(str) - 5), (float)(height - j), ((boolean)ClickGui.getInstance().rainbow.getValue()) ? ((ClickGui.getInstance().rainbowModeA.getValue() == ClickGui.rainbowModeArray.Up) ? ColorUtil.rainbow(counter1[0] * ClickGui.getInstance().rainbowHue.getValue()).getRGB() : ColorUtil.rainbow(ClickGui.getInstance().rainbowHue.getValue()).getRGB()) : this.color, true);
                    ++counter1[0];
                }
            }
            else {
                for (int k = 0; k < Fusion.moduleManager.sortedModules.size(); ++k) {
                    final Module module = Fusion.moduleManager.sortedModules.get(k);
                    final String str2 = module.getDisplayName() + ChatFormatting.GRAY + ((module.getDisplayInfo() != null) ? (" [" + ChatFormatting.WHITE + module.getDisplayInfo() + ChatFormatting.GRAY + "]") : "");
                    j += 10;
                    RenderUtil.drawRectangleCorrectly(width - 2 - this.renderer.getStringWidth(str2) - 7, height - j, Util.mc.fontRenderer.getStringWidth(str2) + 8, Util.mc.fontRenderer.FONT_HEIGHT + 1, ColorUtil.toRGBA(0, 0, 0, 89));
                    RenderUtil.drawRectangleCorrectly(width - 2, height - j, 3, Util.mc.fontRenderer.FONT_HEIGHT + 1, ColorUtil.toRGBA(255, 255, 255, 255));
                    this.renderer.drawString(str2, (float)(width - 2 - this.renderer.getStringWidth(str2) - 5), (float)(height - j), ((boolean)ClickGui.getInstance().rainbow.getValue()) ? ((ClickGui.getInstance().rainbowModeA.getValue() == ClickGui.rainbowModeArray.Up) ? ColorUtil.rainbow(counter1[0] * ClickGui.getInstance().rainbowHue.getValue()).getRGB() : ColorUtil.rainbow(ClickGui.getInstance().rainbowHue.getValue()).getRGB()) : this.color, true);
                    ++counter1[0];
                }
            }
        }
        final String grayString = this.grayNess.getValue() ? String.valueOf(ChatFormatting.GRAY) : "";
        int i = (Util.mc.currentScreen instanceof GuiChat && this.renderingUp.getValue()) ? 13 : (this.renderingUp.getValue() ? -2 : 0);
        if (this.renderingUp.getValue()) {
            if (this.potions.getValue()) {
                final List<PotionEffect> effects = new ArrayList<PotionEffect>(Minecraft.getMinecraft().player.getActivePotionEffects());
                for (final PotionEffect potionEffect : effects) {
                    final String str3 = Fusion.potionManager.getColoredPotionString(potionEffect);
                    i += 10;
                    this.renderer.drawString(str3, (float)(width - this.renderer.getStringWidth(str3) - 2), (float)(height - 2 - i), ((boolean)this.potionSync.getValue()) ? (ClickGui.getInstance().rainbow.getValue() ? ((ClickGui.getInstance().rainbowModeA.getValue() == ClickGui.rainbowModeArray.Up) ? ColorUtil.rainbow(counter1[0] * ClickGui.getInstance().rainbowHue.getValue()).getRGB() : ColorUtil.rainbow(ClickGui.getInstance().rainbowHue.getValue()).getRGB()) : (this.potionSync.getValue() ? this.color : potionEffect.getPotion().getLiquidColor())) : potionEffect.getPotion().getLiquidColor(), true);
                }
            }
            if (this.server.getValue()) {
                final String sText = grayString + "Server " + ChatFormatting.WHITE + (Util.mc.isSingleplayer() ? "SinglePlayer" : Objects.requireNonNull(Util.mc.getCurrentServerData()).serverIP);
                i += 10;
                this.renderer.drawString(sText, (float)(width - this.renderer.getStringWidth(sText) - 2), (float)(height - 2 - i), ((boolean)ClickGui.getInstance().rainbow.getValue()) ? ((ClickGui.getInstance().rainbowModeA.getValue() == ClickGui.rainbowModeArray.Up) ? ColorUtil.rainbow(counter1[0] * ClickGui.getInstance().rainbowHue.getValue()).getRGB() : ColorUtil.rainbow(ClickGui.getInstance().rainbowHue.getValue()).getRGB()) : this.color, true);
                ++counter1[0];
            }
            if (this.speed.getValue()) {
                final String str2 = grayString + "Speed " + ChatFormatting.WHITE + Fusion.speedManager.getSpeedKpH() + " km/h";
                i += 10;
                this.renderer.drawString(str2, (float)(width - this.renderer.getStringWidth(str2) - 2), (float)(height - 2 - i), ((boolean)ClickGui.getInstance().rainbow.getValue()) ? ((ClickGui.getInstance().rainbowModeA.getValue() == ClickGui.rainbowModeArray.Up) ? ColorUtil.rainbow(counter1[0] * ClickGui.getInstance().rainbowHue.getValue()).getRGB() : ColorUtil.rainbow(ClickGui.getInstance().rainbowHue.getValue()).getRGB()) : this.color, true);
                ++counter1[0];
            }
            if (this.time.getValue()) {
                final String str2 = grayString + "Time " + ChatFormatting.WHITE + new SimpleDateFormat("h:mm a").format(new Date());
                i += 10;
                this.renderer.drawString(str2, (float)(width - this.renderer.getStringWidth(str2) - 2), (float)(height - 2 - i), ((boolean)ClickGui.getInstance().rainbow.getValue()) ? ((ClickGui.getInstance().rainbowModeA.getValue() == ClickGui.rainbowModeArray.Up) ? ColorUtil.rainbow(counter1[0] * ClickGui.getInstance().rainbowHue.getValue()).getRGB() : ColorUtil.rainbow(ClickGui.getInstance().rainbowHue.getValue()).getRGB()) : this.color, true);
                ++counter1[0];
            }
            if (this.tps.getValue()) {
                final String str2 = grayString + "TPS " + ChatFormatting.WHITE + Fusion.serverManager.getTPS();
                i += 10;
                this.renderer.drawString(str2, (float)(width - this.renderer.getStringWidth(str2) - 2), (float)(height - 2 - i), ((boolean)ClickGui.getInstance().rainbow.getValue()) ? ((ClickGui.getInstance().rainbowModeA.getValue() == ClickGui.rainbowModeArray.Up) ? ColorUtil.rainbow(counter1[0] * ClickGui.getInstance().rainbowHue.getValue()).getRGB() : ColorUtil.rainbow(ClickGui.getInstance().rainbowHue.getValue()).getRGB()) : this.color, true);
                ++counter1[0];
            }
            final String fpsText = grayString + "FPS " + ChatFormatting.WHITE + Minecraft.debugFPS;
            final String sText2 = grayString + "Server " + ChatFormatting.WHITE + (Util.mc.isSingleplayer() ? "SinglePlayer" : Objects.requireNonNull(Util.mc.getCurrentServerData()).serverIP);
            final String str4 = grayString + "Ping " + ChatFormatting.WHITE + Fusion.serverManager.getPing() + (this.ms.getValue() ? "ms" : "");
            if (this.renderer.getStringWidth(str4) > this.renderer.getStringWidth(fpsText)) {
                if (this.ping.getValue()) {
                    i += 10;
                    this.renderer.drawString(str4, (float)(width - this.renderer.getStringWidth(str4) - 2), (float)(height - 2 - i), ((boolean)ClickGui.getInstance().rainbow.getValue()) ? ((ClickGui.getInstance().rainbowModeA.getValue() == ClickGui.rainbowModeArray.Up) ? ColorUtil.rainbow(counter1[0] * ClickGui.getInstance().rainbowHue.getValue()).getRGB() : ColorUtil.rainbow(ClickGui.getInstance().rainbowHue.getValue()).getRGB()) : this.color, true);
                    ++counter1[0];
                }
                if (this.fps.getValue()) {
                    i += 10;
                    this.renderer.drawString(fpsText, (float)(width - this.renderer.getStringWidth(fpsText) - 2), (float)(height - 2 - i), ((boolean)ClickGui.getInstance().rainbow.getValue()) ? ((ClickGui.getInstance().rainbowModeA.getValue() == ClickGui.rainbowModeArray.Up) ? ColorUtil.rainbow(counter1[0] * ClickGui.getInstance().rainbowHue.getValue()).getRGB() : ColorUtil.rainbow(ClickGui.getInstance().rainbowHue.getValue()).getRGB()) : this.color, true);
                    ++counter1[0];
                }
            }
            else {
                if (this.fps.getValue()) {
                    i += 10;
                    this.renderer.drawString(fpsText, (float)(width - this.renderer.getStringWidth(fpsText) - 2), (float)(height - 2 - i), ((boolean)ClickGui.getInstance().rainbow.getValue()) ? ((ClickGui.getInstance().rainbowModeA.getValue() == ClickGui.rainbowModeArray.Up) ? ColorUtil.rainbow(counter1[0] * ClickGui.getInstance().rainbowHue.getValue()).getRGB() : ColorUtil.rainbow(ClickGui.getInstance().rainbowHue.getValue()).getRGB()) : this.color, true);
                    ++counter1[0];
                }
                if (this.ping.getValue()) {
                    i += 10;
                    this.renderer.drawString(str4, (float)(width - this.renderer.getStringWidth(str4) - 2), (float)(height - 2 - i), ((boolean)ClickGui.getInstance().rainbow.getValue()) ? ((ClickGui.getInstance().rainbowModeA.getValue() == ClickGui.rainbowModeArray.Up) ? ColorUtil.rainbow(counter1[0] * ClickGui.getInstance().rainbowHue.getValue()).getRGB() : ColorUtil.rainbow(ClickGui.getInstance().rainbowHue.getValue()).getRGB()) : this.color, true);
                    ++counter1[0];
                }
            }
        }
        else {
            if (this.potions.getValue()) {
                final List<PotionEffect> effects = new ArrayList<PotionEffect>(Minecraft.getMinecraft().player.getActivePotionEffects());
                for (final PotionEffect potionEffect : effects) {
                    final String str3 = Fusion.potionManager.getColoredPotionString(potionEffect);
                    this.renderer.drawString(str3, (float)(width - this.renderer.getStringWidth(str3) - 2), (float)(2 + i++ * 10), ((boolean)this.potionSync.getValue()) ? (ClickGui.getInstance().rainbow.getValue() ? ((ClickGui.getInstance().rainbowModeA.getValue() == ClickGui.rainbowModeArray.Up) ? ColorUtil.rainbow(counter1[0] * ClickGui.getInstance().rainbowHue.getValue()).getRGB() : ColorUtil.rainbow(ClickGui.getInstance().rainbowHue.getValue()).getRGB()) : (this.potionSync.getValue() ? this.color : potionEffect.getPotion().getLiquidColor())) : potionEffect.getPotion().getLiquidColor(), true);
                }
            }
            if (this.server.getValue()) {
                final String sText = grayString + "Server " + ChatFormatting.WHITE + (Util.mc.isSingleplayer() ? "SinglePlayer" : Objects.requireNonNull(Util.mc.getCurrentServerData()).serverIP);
                this.renderer.drawString(sText, (float)(width - this.renderer.getStringWidth(sText) - 2), (float)(2 + i++ * 10), ((boolean)ClickGui.getInstance().rainbow.getValue()) ? ((ClickGui.getInstance().rainbowModeA.getValue() == ClickGui.rainbowModeArray.Up) ? ColorUtil.rainbow(counter1[0] * ClickGui.getInstance().rainbowHue.getValue()).getRGB() : ColorUtil.rainbow(ClickGui.getInstance().rainbowHue.getValue()).getRGB()) : this.color, true);
                ++counter1[0];
            }
            if (this.speed.getValue()) {
                final String str2 = grayString + "Speed " + ChatFormatting.WHITE + Fusion.speedManager.getSpeedKpH() + " km/h";
                this.renderer.drawString(str2, (float)(width - this.renderer.getStringWidth(str2) - 2), (float)(2 + i++ * 10), ((boolean)ClickGui.getInstance().rainbow.getValue()) ? ((ClickGui.getInstance().rainbowModeA.getValue() == ClickGui.rainbowModeArray.Up) ? ColorUtil.rainbow(counter1[0] * ClickGui.getInstance().rainbowHue.getValue()).getRGB() : ColorUtil.rainbow(ClickGui.getInstance().rainbowHue.getValue()).getRGB()) : this.color, true);
                ++counter1[0];
            }
            if (this.time.getValue()) {
                final String str2 = grayString + " Time " + ChatFormatting.WHITE + new SimpleDateFormat("h:mm a").format(new Date());
                this.renderer.drawString(str2, (float)(width - this.renderer.getStringWidth(str2) - 2), (float)(2 + i++ * 10), ((boolean)ClickGui.getInstance().rainbow.getValue()) ? ((ClickGui.getInstance().rainbowModeA.getValue() == ClickGui.rainbowModeArray.Up) ? ColorUtil.rainbow(counter1[0] * ClickGui.getInstance().rainbowHue.getValue()).getRGB() : ColorUtil.rainbow(ClickGui.getInstance().rainbowHue.getValue()).getRGB()) : this.color, true);
                ++counter1[0];
            }
            if (this.tps.getValue()) {
                final String str2 = grayString + "TPS " + ChatFormatting.WHITE + Fusion.serverManager.getTPS();
                this.renderer.drawString(str2, (float)(width - this.renderer.getStringWidth(str2) - 2), (float)(2 + i++ * 10), ((boolean)ClickGui.getInstance().rainbow.getValue()) ? ((ClickGui.getInstance().rainbowModeA.getValue() == ClickGui.rainbowModeArray.Up) ? ColorUtil.rainbow(counter1[0] * ClickGui.getInstance().rainbowHue.getValue()).getRGB() : ColorUtil.rainbow(ClickGui.getInstance().rainbowHue.getValue()).getRGB()) : this.color, true);
                ++counter1[0];
            }
            final String fpsText = grayString + "FPS " + ChatFormatting.WHITE + Minecraft.debugFPS;
            final String str5 = grayString + "Ping " + ChatFormatting.WHITE + Fusion.serverManager.getPing() + (this.ms.getValue() ? "ms" : "");
            if (this.renderer.getStringWidth(str5) > this.renderer.getStringWidth(fpsText)) {
                if (this.ping.getValue()) {
                    this.renderer.drawString(str5, (float)(width - this.renderer.getStringWidth(str5) - 2), (float)(2 + i++ * 10), ((boolean)ClickGui.getInstance().rainbow.getValue()) ? ((ClickGui.getInstance().rainbowModeA.getValue() == ClickGui.rainbowModeArray.Up) ? ColorUtil.rainbow(counter1[0] * ClickGui.getInstance().rainbowHue.getValue()).getRGB() : ColorUtil.rainbow(ClickGui.getInstance().rainbowHue.getValue()).getRGB()) : this.color, true);
                    ++counter1[0];
                }
                if (this.fps.getValue()) {
                    this.renderer.drawString(fpsText, (float)(width - this.renderer.getStringWidth(fpsText) - 2), (float)(2 + i++ * 10), ((boolean)ClickGui.getInstance().rainbow.getValue()) ? ((ClickGui.getInstance().rainbowModeA.getValue() == ClickGui.rainbowModeArray.Up) ? ColorUtil.rainbow(counter1[0] * ClickGui.getInstance().rainbowHue.getValue()).getRGB() : ColorUtil.rainbow(ClickGui.getInstance().rainbowHue.getValue()).getRGB()) : this.color, true);
                    ++counter1[0];
                }
            }
            else {
                if (this.fps.getValue()) {
                    this.renderer.drawString(fpsText, (float)(width - this.renderer.getStringWidth(fpsText) - 2), (float)(2 + i++ * 10), ((boolean)ClickGui.getInstance().rainbow.getValue()) ? ((ClickGui.getInstance().rainbowModeA.getValue() == ClickGui.rainbowModeArray.Up) ? ColorUtil.rainbow(counter1[0] * ClickGui.getInstance().rainbowHue.getValue()).getRGB() : ColorUtil.rainbow(ClickGui.getInstance().rainbowHue.getValue()).getRGB()) : this.color, true);
                    ++counter1[0];
                }
                if (this.ping.getValue()) {
                    this.renderer.drawString(str5, (float)(width - this.renderer.getStringWidth(str5) - 2), (float)(2 + i++ * 10), ((boolean)ClickGui.getInstance().rainbow.getValue()) ? ((ClickGui.getInstance().rainbowModeA.getValue() == ClickGui.rainbowModeArray.Up) ? ColorUtil.rainbow(counter1[0] * ClickGui.getInstance().rainbowHue.getValue()).getRGB() : ColorUtil.rainbow(ClickGui.getInstance().rainbowHue.getValue()).getRGB()) : this.color, true);
                    ++counter1[0];
                }
            }
        }
        final boolean inHell = Util.mc.world.getBiome(Util.mc.player.getPosition()).getBiomeName().equals("Hell");
        final int posX = (int)Util.mc.player.posX;
        final int posY = (int)Util.mc.player.posY;
        final int posZ = (int)Util.mc.player.posZ;
        final float nether = inHell ? 8.0f : 0.125f;
        final int hposX = (int)(Util.mc.player.posX * nether);
        final int hposZ = (int)(Util.mc.player.posZ * nether);
        i = ((Util.mc.currentScreen instanceof GuiChat) ? 14 : 0);
        final String coordinates = ChatFormatting.RESET + String.valueOf(ChatFormatting.WHITE) + posX + ChatFormatting.GRAY + " [" + hposX + "], " + ChatFormatting.WHITE + posY + ChatFormatting.GRAY + ", " + ChatFormatting.WHITE + posZ + ChatFormatting.GRAY + " [" + hposZ + "]";
        final String direction = this.direction.getValue() ? Fusion.rotationManager.getDirection4D(false) : "";
        final String coords = this.coords.getValue() ? coordinates : "";
        i += 10;
        if (ClickGui.getInstance().rainbow.getValue()) {
            final String rainbowCoords = this.coords.getValue() ? (posX + " [" + hposX + "], " + posY + ", " + posZ + " [" + hposZ + "]") : "";
            if (ClickGui.getInstance().rainbowModeHud.getValue() == ClickGui.rainbowMode.Static) {
                this.renderer.drawString(direction, 2.0f, (float)(height - i - 11), ColorUtil.rainbow(ClickGui.getInstance().rainbowHue.getValue()).getRGB(), true);
                this.renderer.drawString(rainbowCoords, 2.0f, (float)(height - i), ColorUtil.rainbow(ClickGui.getInstance().rainbowHue.getValue()).getRGB(), true);
            }
            else {
                final int[] counter2 = { 1 };
                final char[] stringToCharArray2 = direction.toCharArray();
                float s = 0.0f;
                for (final char c2 : stringToCharArray2) {
                    this.renderer.drawString(String.valueOf(c2), 2.0f + s, (float)(height - i - 11), ColorUtil.rainbow(counter2[0] * ClickGui.getInstance().rainbowHue.getValue()).getRGB(), true);
                    s += this.renderer.getStringWidth(String.valueOf(c2));
                    ++counter2[0];
                }
                final int[] counter3 = { 1 };
                final char[] stringToCharArray3 = rainbowCoords.toCharArray();
                float u = 0.0f;
                for (final char c3 : stringToCharArray3) {
                    this.renderer.drawString(String.valueOf(c3), 2.0f + u, (float)(height - i), ColorUtil.rainbow(counter3[0] * ClickGui.getInstance().rainbowHue.getValue()).getRGB(), true);
                    u += this.renderer.getStringWidth(String.valueOf(c3));
                    ++counter3[0];
                }
            }
        }
        else {
            this.renderer.drawString(direction, 2.0f, (float)(height - i - 11), this.color, true);
            this.renderer.drawString(coords, 2.0f, (float)(height - i), this.color, true);
        }
        if (this.armor.getValue()) {
            this.renderArmorHUD(true);
        }
        if (this.totems.getValue()) {
            this.renderTotemHUD();
        }
        if (this.lag.getValue()) {
            this.renderLag();
        }
    }
    
    public Map<String, Integer> getTextRadarPlayers() {
        return EntityUtil.getTextRadarPlayers();
    }
    
    public void renderGreeter() {
        final int width = this.renderer.scaledWidth;
        String text = "";
        if (this.greeter.getValue()) {
            text = text + MathUtil.getTimeOfDay() + Util.mc.player.getDisplayNameString();
        }
        if (ClickGui.getInstance().rainbow.getValue()) {
            if (ClickGui.getInstance().rainbowModeHud.getValue() == ClickGui.rainbowMode.Static) {
                this.renderer.drawString(text, width / 2.0f - this.renderer.getStringWidth(text) / 2.0f + 2.0f, 2.0f, ColorUtil.rainbow(ClickGui.getInstance().rainbowHue.getValue()).getRGB(), true);
            }
            else {
                final int[] counter1 = { 1 };
                final char[] stringToCharArray = text.toCharArray();
                float i = 0.0f;
                for (final char c : stringToCharArray) {
                    this.renderer.drawString(String.valueOf(c), width / 2.0f - this.renderer.getStringWidth(text) / 2.0f + 2.0f + i, 2.0f, ColorUtil.rainbow(counter1[0] * ClickGui.getInstance().rainbowHue.getValue()).getRGB(), true);
                    i += this.renderer.getStringWidth(String.valueOf(c));
                    ++counter1[0];
                }
            }
        }
        else {
            this.renderer.drawString(text, width / 2.0f - this.renderer.getStringWidth(text) / 2.0f + 2.0f, 2.0f, this.color, true);
        }
    }
    
    public void renderLag() {
        final int width = this.renderer.scaledWidth;
        if (Fusion.serverManager.isServerNotResponding()) {
            final String text = ChatFormatting.RED + "Server not responding " + MathUtil.round(Fusion.serverManager.serverRespondingTime() / 1000.0f, 1) + "s.";
            this.renderer.drawString(text, width / 2.0f - this.renderer.getStringWidth(text) / 2.0f + 2.0f, 20.0f, this.color, true);
        }
    }
    
    public void renderTotemHUD() {
        final int width = this.renderer.scaledWidth;
        final int height = this.renderer.scaledHeight;
        int totems = Util.mc.player.inventory.mainInventory.stream().filter(itemStack -> itemStack.getItem() == Items.TOTEM_OF_UNDYING).mapToInt(ItemStack::getCount).sum();
        if (Util.mc.player.getHeldItemOffhand().getItem() == Items.TOTEM_OF_UNDYING) {
            totems += Util.mc.player.getHeldItemOffhand().getCount();
        }
        if (totems > 0) {
            GlStateManager.enableTexture2D();
            final int i = width / 2;
            final int iteration = 0;
            final int y = height - 55 - ((Util.mc.player.isInWater() && Util.mc.playerController.gameIsSurvivalOrAdventure()) ? 10 : 0);
            final int x = i - 189 + 180 + 2;
            GlStateManager.enableDepth();
            RenderUtil.itemRender.zLevel = 200.0f;
            RenderUtil.itemRender.renderItemAndEffectIntoGUI(HUD.totem, x, y);
            RenderUtil.itemRender.renderItemOverlayIntoGUI(Util.mc.fontRenderer, HUD.totem, x, y, "");
            RenderUtil.itemRender.zLevel = 0.0f;
            GlStateManager.enableTexture2D();
            GlStateManager.disableLighting();
            GlStateManager.disableDepth();
            this.renderer.drawStringWithShadow(totems + "", (float)(x + 19 - 2 - this.renderer.getStringWidth(totems + "")), (float)(y + 9), 16777215);
            GlStateManager.enableDepth();
            GlStateManager.disableLighting();
        }
    }
    
    public void renderArmorHUD(final boolean percent) {
        final int width = this.renderer.scaledWidth;
        final int height = this.renderer.scaledHeight;
        GlStateManager.enableTexture2D();
        final int i = width / 2;
        int iteration = 0;
        final int y = height - 55 - ((HUD.mc.player.isInWater() && HUD.mc.playerController.gameIsSurvivalOrAdventure()) ? 10 : 0);
        for (final ItemStack is : HUD.mc.player.inventory.armorInventory) {
            ++iteration;
            if (is.isEmpty()) {
                continue;
            }
            final int x = i - 90 + (9 - iteration) * 20 + 2;
            GlStateManager.enableDepth();
            RenderUtil.itemRender.zLevel = 200.0f;
            RenderUtil.itemRender.renderItemAndEffectIntoGUI(is, x, y);
            RenderUtil.itemRender.renderItemOverlayIntoGUI(HUD.mc.fontRenderer, is, x, y, "");
            RenderUtil.itemRender.zLevel = 0.0f;
            GlStateManager.enableTexture2D();
            GlStateManager.disableLighting();
            GlStateManager.disableDepth();
            final String s = (is.getCount() > 1) ? (is.getCount() + "") : "";
            this.renderer.drawStringWithShadow(s, (float)(x + 19 - 2 - this.renderer.getStringWidth(s)), (float)(y + 9), 16777215);
            if (!percent) {
                continue;
            }
            int dmg = 0;
            final int itemDurability = is.getMaxDamage() - is.getItemDamage();
            final float green = (is.getMaxDamage() - (float)is.getItemDamage()) / is.getMaxDamage();
            final float red = 1.0f - green;
            dmg = 100 - (int)(red * 100.0f);
            this.renderer.drawStringWithShadow(dmg + "", (float)(x + 8 - this.renderer.getStringWidth(dmg + "") / 2), (float)(y - 11), ColorUtil.toRGBA((int)(red * 255.0f), (int)(green * 255.0f), 0));
        }
        GlStateManager.enableDepth();
        GlStateManager.disableLighting();
    }
    
    @SubscribeEvent
    public void onUpdateWalkingPlayer(final AttackEntityEvent event) {
        this.shouldIncrement = true;
    }
    
    @Override
    public void onLoad() {
        Fusion.commandManager.setClientMessage(this.getCommandMessage());
    }
    
    @SubscribeEvent
    public void onSettingChange(final ClientEvent event) {
        if (event.getSetting() != null && this.equals(event.getSetting().getFeature())) {
            Fusion.commandManager.setClientMessage(this.getCommandMessage());
        }
    }
    
    @SubscribeEvent
    public void onPacketReceive(final PacketEvent.Receive event) {
        if (event.getStage() == 0 && event.getPacket() instanceof SPacketChat && this.timestamp.getValue()) {
            final String originalMessage = ((SPacketChat)event.getPacket()).chatComponent.getFormattedText();
            final String message = this.getTimeString(originalMessage) + originalMessage;
            ((SPacketChat)event.getPacket()).chatComponent = (ITextComponent)new TextComponentString(message);
        }
    }
    
    public String getTimeString(final String message) {
        final String date = new SimpleDateFormat("h:mm").format(new Date());
        final String timeString = "<" + date + "> ";
        final StringBuilder builder = new StringBuilder(timeString);
        builder.insert(0, ((boolean)this.rainbowPrefix.getValue()) ? "§+" : ChatFormatting.LIGHT_PURPLE);
        if (!message.contains(getInstance().getRainbowCommandMessage())) {
            builder.append("§r");
        }
        return builder.toString();
    }
    
    public String getTimeString2() {
        final String date = new SimpleDateFormat("h:mm").format(new Date());
        return "<" + date + ">";
    }
    
    public String getCommandMessage() {
        if (this.commandPrefix.getValue() || this.timestamp.getValue()) {
            final StringBuilder stringBuilder = new StringBuilder((this.timestamp.getValue() ? this.getTimeString2() : "") + (this.commandPrefix.getValue() ? ("<" + this.getRawCommandMessage() + ">") : ""));
            stringBuilder.insert(0, ((this.timestamp.getValue() || this.commandPrefix.getValue()) && this.rainbowPrefix.getValue()) ? "§+" : ChatFormatting.LIGHT_PURPLE);
            stringBuilder.append("§r ");
            return stringBuilder.toString();
        }
        return "";
    }
    
    public String getRainbowCommandMessage() {
        final StringBuilder stringBuilder = new StringBuilder(this.getRawCommandMessage());
        stringBuilder.insert(0, ((boolean)this.rainbowPrefix.getValue()) ? "§+" : ChatFormatting.LIGHT_PURPLE);
        stringBuilder.append("§r");
        return stringBuilder.toString();
    }
    
    public String getRawCommandMessage() {
        return this.command.getValue();
    }
    
    public void drawTextRadar(final int yOffset) {
        if (!this.players.isEmpty()) {
            int y = this.renderer.getFontHeight() + 7 + yOffset;
            for (final Map.Entry<String, Integer> player : this.players.entrySet()) {
                final String text = player.getKey() + " ";
                final int textheight = this.renderer.getFontHeight() + 1;
                this.renderer.drawString(text, 2.0f, (float)y, ((boolean)ClickGui.getInstance().rainbow.getValue()) ? ColorUtil.rainbow(ClickGui.getInstance().rainbowHue.getValue()).getRGB() : this.color, true);
                y += textheight;
            }
        }
    }
    
    static {
        box = new ResourceLocation("textures/gui/container/shulker_box.png");
        totem = new ItemStack(Items.TOTEM_OF_UNDYING);
        HUD.INSTANCE = new HUD();
    }
    
    public enum RenderingMode
    {
        Length, 
        ABC;
    }
}
