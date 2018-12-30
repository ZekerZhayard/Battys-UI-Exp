package io.github.zekerzhayard.battyuiexp;

import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;

public class BattyUIExpCommand extends CommandBase {
    @Override
    public int getRequiredPermissionLevel() {
        return 0;
    }

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender sender) {
        return true;
    }

    @Override
    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos) {
        return args.length == 1 ? CommandBase.getListOfStringsMatchingLastWord(args, "coords", "fps", "timer") : null;
    }

    @Override
    public String getCommandName() {
        return "battysui";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/battysui [coords | fps | timer]";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) throws CommandException {
        try {
            if (args[0].equalsIgnoreCase("coords")) {
                BattyUIExp.instance.showCoordGui = true;
            } else if (args[0].equalsIgnoreCase("fps")) {
                BattyUIExp.instance.showFpsGui = true;
            } else if (args[0].equalsIgnoreCase("timer")) {
                BattyUIExp.instance.showClockGui = true;
            } else {
                Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(this.getCommandUsage(sender)));
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(this.getCommandUsage(sender)));
        }
    }
}
