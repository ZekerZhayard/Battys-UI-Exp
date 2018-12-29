package io.github.zekerzhayard.battyuiexp.gui;

import java.lang.reflect.Field;
import java.util.Arrays;

import org.apache.commons.lang3.ArrayUtils;

import batty.ui.BattyUI;
import io.github.zekerzhayard.battyuiexp.BattyUIExp;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.EnumChatFormatting;

class GuiHelper {
    static String getColorName(int color) {
        try {
            int index = ArrayUtils.indexOf((int[]) BattyUIExp.instance.fieldMyColourCodes.get(BattyUIExp.instance.battyUI), color);
            Field fieldMyColourList = BattyUI.class.getDeclaredField("myColourList");
            fieldMyColourList.setAccessible(true);
            return ((String[]) fieldMyColourList.get(fieldMyColourList))[index];
        } catch (IllegalAccessException | NoSuchFieldException e) {
            e.printStackTrace();
            return "";
        }
    }

    private static int changeColor(GuiButton button, Field field) throws IllegalAccessException {
        int[] myColourCodes = (int[]) BattyUIExp.instance.fieldMyColourCodes.get(BattyUIExp.instance.battyUI);
        int index = ArrayUtils.indexOf(myColourCodes, field.getInt(BattyUIExp.instance.battyUI)) + 1;
        if (index >= ArrayUtils.getLength(myColourCodes)) {
            index = 0;
        }
        field.setInt(BattyUIExp.instance.battyUI, myColourCodes[index]);
        button.displayString = GuiHelper.getColorName(myColourCodes[index]);
        button.packedFGColour = myColourCodes[index];
        return myColourCodes[index];
    }

    private static int changeBoolean(GuiButton button, Field field) throws IllegalAccessException {
        field.setBoolean(BattyUIExp.instance.battyUI, !field.getBoolean(BattyUIExp.instance.battyUI));
        button.displayString = field.getBoolean(BattyUIExp.instance.battyUI) ? EnumChatFormatting.GREEN + "true" : EnumChatFormatting.RED + "false";
        return -1;
    }

    static int changeButton(boolean isBoolean, GuiButton button, Field field) throws IllegalAccessException {
        return isBoolean ? GuiHelper.changeBoolean(button, field) : GuiHelper.changeColor(button, field);
    }

    private static int j;
    static void drawString(GuiScreen guiScreen, FontRenderer fontRenderer, String[] texts, String title, int base) {
        GuiHelper.j = (texts.length - 1) * 11 + 22 - base;
        guiScreen.drawCenteredString(fontRenderer, EnumChatFormatting.BOLD + "Batty's Coordinates PLUS! Mod", guiScreen.width / 2 + 30, guiScreen.height / 2 - GuiHelper.j - 44, 0x00FFFF55);
        guiScreen.drawCenteredString(fontRenderer, EnumChatFormatting.BOLD + title + " Settings", guiScreen.width / 2 + 30, guiScreen.height / 2 - GuiHelper.j - 31, 0x00FFFFFF);
        guiScreen.drawString(fontRenderer, EnumChatFormatting.DARK_AQUA + "Batty's UI Exp Mod v" + BattyUIExp.VERSION + " by ZekerZhayard", 0, guiScreen.height - 10, 0x00FFFFFF);
        Arrays.stream(texts).forEach(str -> guiScreen.drawString(fontRenderer, I18n.format(str), guiScreen.width / 2 - 70 - fontRenderer.getStringWidth(I18n.format(str)), guiScreen.height / 2 - (GuiHelper.j -= 22), 0x00FFFFFF));
    }

    static void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, String boxName, Field fieldBoxR, Field fieldBoxBase) {
        if (clickedMouseButton == 0) {
            try {
                Field fieldBoxW = BattyUI.class.getDeclaredField(boxName + "BoxW");
                fieldBoxW.setAccessible(true);
                Field fieldBoxH = BattyUI.class.getDeclaredField(boxName + "BoxH");
                fieldBoxH.setAccessible(true);
                int right = fieldBoxR.getInt(BattyUIExp.instance.battyUI);
                int left = right - fieldBoxW.getInt(BattyUIExp.instance.battyUI);
                int base = fieldBoxBase.getInt(BattyUIExp.instance.battyUI);
                int top = base - fieldBoxH.getInt(BattyUIExp.instance.battyUI);
                if (mouseX >= left && mouseX <= right && mouseY >= top && mouseY <= base) {
                    fieldBoxR.setInt(BattyUIExp.instance.battyUI, mouseX + fieldBoxW.getInt(BattyUIExp.instance.battyUI) / 2);
                    fieldBoxBase.setInt(BattyUIExp.instance.battyUI, mouseY + fieldBoxH.getInt(BattyUIExp.instance.battyUI) / 2);
                    BattyUIExp.instance.config.get(BattyUIExp.MODID, boxName + "BoxR", 0).set(fieldBoxR.getInt(BattyUIExp.instance.battyUI));
                    BattyUIExp.instance.config.get(BattyUIExp.MODID, boxName + "BoxBase", 0).set(fieldBoxBase.getInt(BattyUIExp.instance.battyUI));
                }
            } catch (IllegalAccessException | NoSuchFieldException e) {
                e.printStackTrace();
            }
        }
    }

    private static GuiButton initBooleanButton(int index, GuiScreen guiScreen, Field[] fields, int base) throws IllegalAccessException {
        return new GuiButton(index, guiScreen.width / 2 - 60, guiScreen.height / 2 - (fields.length - 1) / 2 * 22 - 6 + index * 22 + base, 200, 20, fields[index].getBoolean(BattyUIExp.instance.battyUI) ? EnumChatFormatting.GREEN + "true" : EnumChatFormatting.RED + "false");
    }

    private static GuiButton initColorButton(int index, GuiScreen guiScreen, Field[] fields, int base) throws IllegalAccessException {
        int color = fields[index].getInt(BattyUIExp.instance.battyUI);
        GuiButton button = new GuiButton(index, guiScreen.width / 2 - 60, guiScreen.height / 2 - (fields.length - 1) / 2 * 22 - 6 + index * 22 + base, 200, 20, GuiHelper.getColorName(color));
        button.packedFGColour = color;
        return button;
    }

    static GuiButton initButton(boolean isBoolean, int index, GuiScreen guiScreen, Field[] fields, int base) throws IllegalAccessException {
        return isBoolean ? GuiHelper.initBooleanButton(index, guiScreen, fields, base) : GuiHelper.initColorButton(index, guiScreen, fields, base);
    }
}
