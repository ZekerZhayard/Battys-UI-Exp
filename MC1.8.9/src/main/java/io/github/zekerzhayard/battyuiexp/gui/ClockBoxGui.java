package io.github.zekerzhayard.battyuiexp.gui;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang3.reflect.FieldUtils;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import batty.ui.BattyUI;
import io.github.zekerzhayard.battyuiexp.BattyUIExp;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import scala.Tuple3;

public class ClockBoxGui extends GuiScreen {
    private Map.Entry<ArrayList<String>, ArrayList<Field>> fields;

    public ClockBoxGui() throws NoSuchFieldException {
        fields = Maps.immutableEntry(Lists.newArrayList("Timer.shade", "Timer.colours.Stopped", "Timer.colours.Running"), Lists.newArrayList(FieldUtils.getDeclaredField(BattyUI.class, "shadedTimer", true), FieldUtils.getDeclaredField(BattyUI.class, "myTimerStopText", true), FieldUtils.getDeclaredField(BattyUI.class, "myTimerRunText", true)));
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        GuiHelper.drawString(this, this.fontRendererObj, this.fields.getKey(), "Timer", 0);
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick) {
        GuiHelper.mouseClickMove(mouseX, mouseY, clickedMouseButton, "clock");
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        try {
            GuiHelper.changeButton(button.id == 0, button, this.fields.getValue().get(button.id));
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void initGui() {
        try {
            for (int i = 0; i < this.fields.getValue().size(); i++) {
                this.buttonList.add(GuiHelper.initButton(i == 0, i, this, this.fields.getValue(), 0));
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onGuiClosed() {
        try {
            Properties properties = (Properties) BattyUIExp.instance.fields.get("propts").get(BattyUIExp.instance.battyUI);
            for (int i = 0; i < this.fields.getKey().size(); i++) {
                properties.setProperty(this.fields.getKey().get(i), i == 0 ? String.valueOf(this.fields.getValue().get(i).getBoolean(BattyUIExp.instance.battyUI)) : GuiHelper.getColorName(this.fields.getValue().get(i).getInt(BattyUIExp.instance.battyUI)));
            }
            properties.store(new FileOutputStream(new File(Minecraft.getMinecraft().mcDataDir, "BatMod.properties")), null);
            BattyUIExp.instance.config.save();
        } catch (IllegalAccessException | IOException e) {
            e.printStackTrace();
        }
    }
}
