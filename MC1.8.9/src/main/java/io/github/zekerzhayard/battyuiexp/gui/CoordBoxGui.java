package io.github.zekerzhayard.battyuiexp.gui;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Properties;

import org.lwjgl.input.Mouse;

import batty.ui.BattyUI;
import io.github.zekerzhayard.battyuiexp.BattyUIExp;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;

public class CoordBoxGui extends GuiScreen {
    /** boolean */
    private Field fieldShadedCoords;
    /** java.lang.String */
    private Field fieldMyChevronUp;
    /** java.lang.String */
    private Field fieldMyChevronDown;
    /** int */
    private Field fieldMyTitleText;
    /** int */
    private Field fieldMyPosCoordText;
    /** int */
    private Field fieldMyNegCoordText;
    /** int */
    private Field fieldMyPosChunkText;
    /** int */
    private Field fieldMyNegChunkText;
    /** int */
    private Field fieldMyCompassText;
    /** int */
    private Field fieldMyBiomeText;
    /** boolean */
    private Field fieldCoordsCopyTPFormat;
    private Field[] fields;
    private String[] stringtext = new String[] { "Coords.shade", "Coords.chars.Increase", "Coords.chars.Decrease", "Coords.colours.TitleText", "Coords.colours.PosCoordText", "Coords.colours.NegCoordText", "Coords.colours.PosChunkText", "Coords.colours.NegChunkText", "Coords.colours.CompassText", "Coords.colours.BiomeText", "Coords.copy.tpFormat" };
    private GuiTextField[] gtf = new GuiTextField[2];

    private int base = 110;

    public CoordBoxGui() throws NoSuchFieldException {
        this.fieldShadedCoords = BattyUI.class.getDeclaredField("shadedCoords");
        this.fieldMyChevronUp = BattyUI.class.getDeclaredField("myChevronUp");
        this.fieldMyChevronDown = BattyUI.class.getDeclaredField("myChevronDown");
        this.fieldMyTitleText = BattyUI.class.getDeclaredField("myTitleText");
        this.fieldMyPosCoordText = BattyUI.class.getDeclaredField("myPosCoordText");
        this.fieldMyNegCoordText = BattyUI.class.getDeclaredField("myNegCoordText");
        this.fieldMyPosChunkText = BattyUI.class.getDeclaredField("myPosChunkText");
        this.fieldMyNegChunkText = BattyUI.class.getDeclaredField("myNegChunkText");
        this.fieldMyCompassText = BattyUI.class.getDeclaredField("myCompassText");
        this.fieldMyBiomeText = BattyUI.class.getDeclaredField("myBiomeText");
        this.fieldCoordsCopyTPFormat = BattyUI.class.getDeclaredField("coordsCopyTPFormat");
        Arrays.stream(this.fields = new Field[] { this.fieldShadedCoords, this.fieldMyChevronUp, this.fieldMyChevronDown, this.fieldMyTitleText, this.fieldMyPosCoordText, this.fieldMyNegCoordText, this.fieldMyPosChunkText, this.fieldMyNegChunkText, this.fieldMyCompassText, this.fieldMyBiomeText, this.fieldCoordsCopyTPFormat }).forEach(f -> f.setAccessible(true));
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        GuiHelper.drawString(this, this.fontRendererObj, this.stringtext, "Coords", this.base);
        Arrays.stream(this.gtf).forEach(tf -> tf.drawTextBox());
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        for (int i = 0; i < this.gtf.length; i++) {
            this.gtf[i].textboxKeyTyped(typedChar, keyCode);
            try {
                this.fields[i + 1].set(BattyUIExp.instance.battyUI, gtf[i].getText());
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
        GuiHelper.mouseClickMove(mouseX, mouseY, clickedMouseButton, "coord", BattyUIExp.instance.fieldCoordBoxR, BattyUIExp.instance.fieldCoordBoxBase);
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        try {
            GuiHelper.changeButton(button.id == 0 || button.id == 10, button, this.fields[button.id]);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void initGui() {
        try {
            for (int i = 0; i < fields.length; i++) {
                if (i == 1 || i == 2) {
                    this.gtf[i - 1] = new GuiTextField(i, this.fontRendererObj, this.width / 2 - 60, this.height / 2 - (fields.length - 1) / 2 * 22 - 6 + this.base + i * 22, 200, 20);
                    this.gtf[i - 1].setMaxStringLength(1);
                    this.gtf[i - 1].setText((String) this.fields[i].get(BattyUIExp.instance.battyUI));
                } else {
                    this.buttonList.add(GuiHelper.initButton(i == 0 || i == 10, i, this, this.fields, this.base));
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
            Properties properties = (Properties) BattyUIExp.instance.fieldOptionsPro.get(BattyUIExp.instance.battyUI);
            for (int i = 0; i < this.fields.length; i++) {
                properties.setProperty(this.stringtext[i], i == 1 || i == 2 ? (String) this.fields[i].get(BattyUIExp.instance.battyUI) : (i == 0 || i == 10 ? String.valueOf(this.fields[i].getBoolean(BattyUIExp.instance.battyUI)) : GuiHelper.getColorName(this.fields[i].getInt(BattyUIExp.instance.battyUI))));
            }
            properties.store(new FileOutputStream(new File(Minecraft.getMinecraft().mcDataDir, "BatMod.properties")), null);
            BattyUIExp.instance.config.save();
        } catch (IllegalAccessException | IOException e) {
            e.printStackTrace();
        }
    }
}
