package io.github.zekerzhayard.battyuiexp.gui;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang3.reflect.FieldUtils;
import org.lwjgl.input.Mouse;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import batty.ui.BattyUI;
import io.github.zekerzhayard.battyuiexp.BattyUIExp;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;

public class CoordBoxGui extends GuiScreen {
    private Map.Entry<ArrayList<String>, ArrayList<Field>> fields;
    private GuiTextField[] gtf = new GuiTextField[2];

    private int base = 110;

    public CoordBoxGui() throws NoSuchFieldException {
        this.fields = Maps.immutableEntry(Lists.newArrayList("Coords.shade", "Coords.chars.Increase", "Coords.chars.Decrease", "Coords.colours.TitleText", "Coords.colours.PosCoordText", "Coords.colours.NegCoordText", "Coords.colours.PosChunkText", "Coords.colours.NegChunkText", "Coords.colours.CompassText", "Coords.colours.BiomeText", "Coords.copy.tpFormat"),
                Lists.newArrayList(FieldUtils.getDeclaredField(BattyUI.class, "shadedCoords", true), FieldUtils.getDeclaredField(BattyUI.class, "myChevronUp", true), FieldUtils.getDeclaredField(BattyUI.class, "myChevronDown", true), FieldUtils.getDeclaredField(BattyUI.class, "myTitleText", true), FieldUtils.getDeclaredField(BattyUI.class, "myPosCoordText", true), FieldUtils.getDeclaredField(BattyUI.class, "myNegCoordText", true), FieldUtils.getDeclaredField(BattyUI.class, "myPosChunkText", true), FieldUtils.getDeclaredField(BattyUI.class, "myNegChunkText", true), FieldUtils.getDeclaredField(BattyUI.class, "myCompassText", true), FieldUtils.getDeclaredField(BattyUI.class, "myBiomeText", true), FieldUtils.getDeclaredField(BattyUI.class, "coordsCopyTPFormat", true)));
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        GuiHelper.drawString(this, this.fontRendererObj, this.fields.getKey(), "Coords", this.base);
        Arrays.stream(this.gtf).forEach(tf -> tf.drawTextBox());
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        for (int i = 0; i < this.gtf.length; i++) {
            this.gtf[i].textboxKeyTyped(typedChar, keyCode);
            try {
                this.fields.getValue().get(i + 1).set(BattyUIExp.instance.battyUI, gtf[i].getText());
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        super.keyTyped(typedChar, keyCode);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        Arrays.stream(this.gtf).forEach(tf -> tf.mouseClicked(mouseX, mouseY, mouseButton));
    }

    @Override
    protected void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick) {
        GuiHelper.mouseClickMove(mouseX, mouseY, clickedMouseButton, "coord");
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        try {
            GuiHelper.changeButton(button.id == 0 || button.id == 10, button, this.fields.getValue().get(button.id));
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void initGui() {
        try {
            for (int i = 0; i < this.fields.getValue().size(); i++) {
                if (i == 1 || i == 2) {
                    this.gtf[i - 1] = new GuiTextField(i, this.fontRendererObj, this.width / 2 - 60, this.height / 2 - (this.fields.getValue().size() - 1) / 2 * 22 - 6 + this.base + i * 22, 200, 20);
                    this.gtf[i - 1].setMaxStringLength(1);
                    this.gtf[i - 1].setText((String) this.fields.getValue().get(i).get(BattyUIExp.instance.battyUI));
                } else {
                    this.buttonList.add(GuiHelper.initButton(i == 0 || i == 10, i, this, this.fields.getValue(), this.base));
                }
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void handleMouseInput() throws IOException {
        super.handleMouseInput();
        int distance = Mouse.getDWheel() / 10;
        if ((this.base < -44 && distance < 0) || (this.base > 110 && distance > 0)) {
            return;
        }
        if (distance != 0) {
            this.base += distance;
            this.buttonList.forEach(button -> button.yPosition += distance);
            Arrays.stream(this.gtf).forEach(tf -> tf.yPosition += distance);
        }
    }

    @Override
    public void onGuiClosed() {
        try {
            Properties properties = (Properties) BattyUIExp.instance.fields.get("propts").get(BattyUIExp.instance.battyUI);
            for (int i = 0; i < this.fields.getKey().size(); i++) {
                properties.setProperty(this.fields.getKey().get(i), i == 1 || i == 2 ? (String) this.fields.getValue().get(i).get(BattyUIExp.instance.battyUI) : (i == 0 || i == 10 ? String.valueOf(this.fields.getValue().get(i).getBoolean(BattyUIExp.instance.battyUI)) : GuiHelper.getColorName(this.fields.getValue().get(i).getInt(BattyUIExp.instance.battyUI))));
            }
            properties.store(new FileOutputStream(new File(Minecraft.getMinecraft().mcDataDir, "BatMod.properties")), null);
            BattyUIExp.instance.config.save();
        } catch (IllegalAccessException | IOException e) {
            e.printStackTrace();
        }
    }
}
