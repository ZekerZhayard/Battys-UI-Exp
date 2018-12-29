package io.github.zekerzhayard.battyuiexp;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang3.ArrayUtils;

import batty.ui.BattyBaseUI;
import batty.ui.BattyUI;
import io.github.zekerzhayard.battyuiexp.gui.ClockBoxGui;
import io.github.zekerzhayard.battyuiexp.gui.CoordBoxGui;
import io.github.zekerzhayard.battyuiexp.gui.FpsBoxGui;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.EventBus;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

@Mod(modid = BattyUIExp.MODID, name = BattyUIExp.NAME, version = BattyUIExp.VERSION, acceptedMinecraftVersions = "[1.8.9]", dependencies = "required-after:Batty's Coordinates PLUS", clientSideOnly = true)
public class BattyUIExp {
    public static final String MODID = "battyuiexp";
    public static final String NAME = "Batty's Coordinates PLUS Exp";
    public static final String VERSION = "1.0";

    @Mod.Instance(value = BattyUIExp.MODID)
    public static BattyUIExp instance;

    public BattyUI battyUI;
    public boolean showClockGui = false;
    public boolean showCoordGui = false;
    public boolean showFpsGui = false;
    public Configuration config;

    /** int */
    public Field fieldClockBoxBase;
    /** int */
    public Field fieldClockBoxR;
    /** int */
    public Field fieldCoordBoxBase;
    /** int */
    public Field fieldCoordBoxR;
    /** int */
    public Field fieldFpsBoxBase;
    /** int */
    public Field fieldFpsBoxR;
    /** int[] */
    public Field fieldMyColourCodes;
    /** java.util.Properties */
    public Field fieldOptionsPro;

    public BattyUIExp() throws NoSuchFieldException {
        this.fieldClockBoxBase = BattyUI.class.getDeclaredField("clockBoxBase");
        this.fieldClockBoxBase.setAccessible(true);

        this.fieldClockBoxR = BattyUI.class.getDeclaredField("clockBoxR");
        this.fieldClockBoxR.setAccessible(true);

        this.fieldCoordBoxBase = BattyUI.class.getDeclaredField("coordBoxBase");
        this.fieldCoordBoxBase.setAccessible(true);

        this.fieldCoordBoxR = BattyUI.class.getDeclaredField("coordBoxR");
        this.fieldCoordBoxR.setAccessible(true);

        this.fieldFpsBoxBase = BattyUI.class.getDeclaredField("fpsBoxBase");
        this.fieldFpsBoxBase.setAccessible(true);

        this.fieldFpsBoxR = BattyUI.class.getDeclaredField("fpsBoxR");
        this.fieldFpsBoxR.setAccessible(true);

        this.fieldMyColourCodes = BattyUI.class.getDeclaredField("myColourCodes");
        this.fieldMyColourCodes.setAccessible(true);

        this.fieldOptionsPro = BattyUI.class.getDeclaredField("propts");
        this.fieldOptionsPro.setAccessible(true);
    }

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        Minecraft.getMinecraft().gameSettings.keyBindings = ArrayUtils.removeElement(Minecraft.getMinecraft().gameSettings.keyBindings, BattyBaseUI.moveCoordScreenPos);
        Minecraft.getMinecraft().gameSettings.keyBindings = ArrayUtils.removeElement(Minecraft.getMinecraft().gameSettings.keyBindings, BattyBaseUI.moveFPSScreenPos);
        Minecraft.getMinecraft().gameSettings.keyBindings = ArrayUtils.removeElement(Minecraft.getMinecraft().gameSettings.keyBindings, BattyBaseUI.moveTimerScreenPos);

        this.config = new Configuration(new File(Minecraft.getMinecraft().mcDataDir + File.separator + BattyUIExp.MODID, BattyUIExp.MODID + ".cfg"));
        this.config.load();
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        ClientCommandHandler.instance.registerCommand(new BattyUIExpCommand());
        MinecraftForge.EVENT_BUS.register(this);
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) throws IllegalAccessException, InvocationTargetException, NoSuchFieldException, NoSuchMethodException {
        Field fieldListeners = EventBus.class.getDeclaredField("listeners");
        fieldListeners.setAccessible(true);
        for (Object object : ((ConcurrentHashMap) fieldListeners.get(MinecraftForge.EVENT_BUS)).keySet()) {
            if (object instanceof BattyUI) {
                this.battyUI = (BattyUI) object;
                break;
            }
        }

        Field fieldCoordLocation = BattyUI.class.getDeclaredField("coordLocation");
        fieldCoordLocation.setAccessible(true);
        fieldCoordLocation.setInt(this.battyUI, 4);

        Field fieldFpsLocation = BattyUI.class.getDeclaredField("fpsLocation");
        fieldFpsLocation.setAccessible(true);
        fieldFpsLocation.setInt(this.battyUI, 5);

        Field fieldTimerLocation = BattyUI.class.getDeclaredField("timerLocation");
        fieldTimerLocation.setAccessible(true);
        fieldTimerLocation.setInt(this.battyUI, 5);

        Method methodStoreRuntimeOptions = BattyUI.class.getDeclaredMethod("storeRuntimeOptions");
        methodStoreRuntimeOptions.setAccessible(true);
        methodStoreRuntimeOptions.invoke(this.battyUI);

        Method methodConstructTimeString = BattyUI.class.getDeclaredMethod("constructTimeString");
        methodConstructTimeString.setAccessible(true);
        this.fieldClockBoxR.setInt(this.battyUI, this.config.get(BattyUIExp.MODID, "clockBoxR", 13 + Minecraft.getMinecraft().fontRendererObj.getStringWidth((String) methodConstructTimeString.invoke(this.battyUI))).getInt());
        this.fieldClockBoxBase.setInt(this.battyUI, this.config.get(BattyUIExp.MODID, "clockBoxBase", 11).getInt());

        Field fieldShowCoords = BattyUI.class.getDeclaredField("showCoords");
        fieldShowCoords.setAccessible(true);
        int coordBoxR = 81;
        int coordBoxBase = 31;
        if (fieldShowCoords.getInt(this.battyUI) > 2) {
            coordBoxR = 105;
            coordBoxBase = 41;
        }
        this.fieldCoordBoxR.setInt(this.battyUI, this.config.get(BattyUIExp.MODID, "coordBoxR", coordBoxR).getInt());
        this.fieldCoordBoxBase.setInt(this.battyUI, this.config.get(BattyUIExp.MODID, "coordBoxBase", coordBoxBase).getInt());

        this.fieldFpsBoxR.setInt(this.battyUI, this.config.get(BattyUIExp.MODID, "fpsBoxR", 13 + Minecraft.getMinecraft().fontRendererObj.getStringWidth("000 FPS")).getInt());
        this.fieldFpsBoxBase.setInt(this.battyUI, this.config.get(BattyUIExp.MODID, "fpsBoxBase", 11).getInt());

        this.config.save();
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) throws NoSuchFieldException {
        if (this.showClockGui) {
            Minecraft.getMinecraft().displayGuiScreen(new ClockBoxGui());
            this.showClockGui = false;
        } else if (this.showCoordGui) {
            Minecraft.getMinecraft().displayGuiScreen(new CoordBoxGui());
            this.showCoordGui = false;
        } else if (this.showFpsGui) {
            Minecraft.getMinecraft().displayGuiScreen(new FpsBoxGui());
            this.showFpsGui = false;
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public void onRenderTick(TickEvent.RenderTickEvent event) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        GuiScreen currentScreen = Minecraft.getMinecraft().currentScreen;
        if (currentScreen instanceof ClockBoxGui) {
            Method methodRenderPlayerTimer = BattyUI.class.getDeclaredMethod("renderPlayerTimer");
            methodRenderPlayerTimer.setAccessible(true);
            methodRenderPlayerTimer.invoke(this.battyUI);
        } else if (currentScreen instanceof CoordBoxGui) {
            Method methodRenderPlayerCoords = BattyUI.class.getDeclaredMethod("renderPlayerCoords");
            methodRenderPlayerCoords.setAccessible(true);
            methodRenderPlayerCoords.invoke(this.battyUI);
        } else if (currentScreen instanceof FpsBoxGui) {
            Method methodRenderPlayerFPS = BattyUI.class.getDeclaredMethod("renderPlayerFPS");
            methodRenderPlayerFPS.setAccessible(true);
            methodRenderPlayerFPS.invoke(this.battyUI);
        }
    }
}
