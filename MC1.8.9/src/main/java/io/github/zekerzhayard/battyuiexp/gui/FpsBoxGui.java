package io.github.zekerzhayard.battyuiexp.gui;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Properties;

import batty.ui.BattyUI;
import io.github.zekerzhayard.battyuiexp.BattyUIExp;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;

public class FpsBoxGui extends GuiScreen {
    /** boolean */
    private Field fieldShadedFPS;
    /** int */
    private Field fieldMyFPSText;
    private Field[] fields;
    private String[] stringtext = new String[] { "FPS.shade", "FPS.colours.Text" };

    public FpsBoxGui() throws NoSuchFieldException {
        this.fieldShadedFPS = BattyUI.class.getDeclaredField("shadedFPS");
        this.fieldMyFPSText = BattyUI.class.getDeclaredField("myFPSText");
        Arrays.stream(this.fields = new Field[] { this.fieldShadedFPS, this.fieldMyFPSText }).forEach(f -> f.setAccessible(true));
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        GuiHelper.drawString(this, this.fontRendererObj, this.stringtext, "Fps", 11);
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick) {
        GuiHelper.mouseClickMove(mouseX, mouseY, clickedMouseButton, "fps", BattyUIExp.instance.fieldFpsBoxR, BattyUIExp.instance.fieldFpsBoxBase);
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        try {
            GuiHelper.changeButton(button.id == 0, button, this.fields[button.id]);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void initGui() {
        try {
            for (int i = 0; i < fields.length; i++) {
                this.buttonList.add(GuiHelper.initButton(i == 0, i, this, this.fields, 0));
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onGuiClosed() {
        try {
            Properties properties = (Properties) BattyUIExp.instance.fieldOptionsPro.get(BattyUIExp.instance.battyUI);
            for (int i = 0; i < this.fields.length; i++) {
                properties.setProperty(this.stringtext[i], i == 0 ? String.valueOf(this.fields[i].getBoolean(BattyUIExp.instance.battyUI)) : GuiHelper.getColorName(this.fields[i].getInt(BattyUIExp.instance.battyUI)));
            }
            properties.store(new FileOutputStream(new File(Minecraft.getMinecraft().mcDataDir, "BatMod.properties")), null);
            BattyUIExp.instance.config.save();
        } catch (IllegalAccessException | IOException e) {
            e.printStackTrace();
        }
    }
}
