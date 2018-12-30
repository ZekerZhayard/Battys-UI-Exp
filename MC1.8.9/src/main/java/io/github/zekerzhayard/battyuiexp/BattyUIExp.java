package io.github.zekerzhayard.battyuiexp;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.reflect.FieldUtils;

import com.google.common.collect.Maps;

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
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

@Mod(modid = BattyUIExp.MODID, name = BattyUIExp.NAME, version = BattyUIExp.VERSION, acceptedMinecraftVersions = "[1.8.9]", dependencies = "required-after:Batty's Coordinates PLUS", clientSideOnly = true)
public class BattyUIExp {
    public static final String MODID = "battyuiexp";
    public static final String NAME = "Batty's Coordinates PLUS Exp";
    public static final String VERSION = "1.1";

    @Mod.Instance(value = BattyUIExp.MODID)
    public static BattyUIExp instance;

    public BattyUI battyUI;
    public boolean showClockGui = false;
    public boolean showCoordGui = false;
    public boolean showFpsGui = false;
    public Configuration config;
    public HashMap<String, Field> fields = Maps.newHashMap();

    public BattyUIExp() throws NoSuchFieldException {
        this.fields.put("clockBoxBase", FieldUtils.getDeclaredField(BattyUI.class, "clockBoxBase", true));
        this.fields.put("clockBoxR", FieldUtils.getDeclaredField(BattyUI.class, "clockBoxR", true));
        this.fields.put("coordBoxBase", FieldUtils.getDeclaredField(BattyUI.class, "coordBoxBase", true));
        this.fields.put("coordBoxR", FieldUtils.getDeclaredField(BattyUI.class, "coordBoxR", true));
        this.fields.put("fpsBoxBase", FieldUtils.getDeclaredField(BattyUI.class, "fpsBoxBase", true));
        this.fields.put("fpsBoxR", FieldUtils.getDeclaredField(BattyUI.class, "fpsBoxR", true));
        this.fields.put("propts", FieldUtils.getDeclaredField(BattyUI.class, "propts", true));
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
    public void postInit(FMLPostInitializationEvent event) throws IllegalAccessException, NoSuchFieldException {
        this.battyUI = (BattyUI) ((ConcurrentHashMap) FieldUtils.readDeclaredField(MinecraftForge.EVENT_BUS, "listeners", true)).keySet().stream().filter(o -> o instanceof BattyUI).findFirst().get();
        Arrays.asList("coordLocation", "fpsLocation", "timerLocation").stream().forEach(str -> {
            try {
                FieldUtils.writeDeclaredField(this.battyUI, str, 5, true);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        });

        this.fields.get("clockBoxR").setInt(this.battyUI, this.config.get(BattyUIExp.MODID, "clockBoxR", 13 + Minecraft.getMinecraft().fontRendererObj.getStringWidth((String) this.invokeBattyUIMethod("constructTimeString"))).getInt());
        this.fields.get("clockBoxBase").setInt(this.battyUI, this.config.get(BattyUIExp.MODID, "clockBoxBase", 11).getInt());

        boolean mode = (int) FieldUtils.readDeclaredField(this.battyUI, "showCoords", true) > 2;
        this.fields.get("coordBoxR").setInt(this.battyUI, this.config.get(BattyUIExp.MODID, "coordBoxR", mode ? 105 : 81).getInt());
        this.fields.get("coordBoxBase").setInt(this.battyUI, this.config.get(BattyUIExp.MODID, "coordBoxBase", mode ? 41 : 31).getInt());

        this.fields.get("fpsBoxR").setInt(this.battyUI, this.config.get(BattyUIExp.MODID, "fpsBoxR", 13 + Minecraft.getMinecraft().fontRendererObj.getStringWidth("000 FPS")).getInt());
        this.fields.get("fpsBoxBase").setInt(this.battyUI, this.config.get(BattyUIExp.MODID, "fpsBoxBase", 11).getInt());

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
    public void onRenderTick(TickEvent.RenderTickEvent event) {
        GuiScreen currentScreen = Minecraft.getMinecraft().currentScreen;
        if (currentScreen instanceof ClockBoxGui) {
            this.invokeBattyUIMethod("renderPlayerTimer");
        } else if (currentScreen instanceof CoordBoxGui) {
            this.invokeBattyUIMethod("renderPlayerCoords");
        } else if (currentScreen instanceof FpsBoxGui) {
            this.invokeBattyUIMethod("renderPlayerFPS");
        }
    }
    
    private Object invokeBattyUIMethod(String methodName) {
        try {
            Method method = BattyUI.class.getDeclaredMethod(methodName);
            method.setAccessible(true);
            return method.invoke(this.battyUI);
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
            return null;
        }
    }
}
