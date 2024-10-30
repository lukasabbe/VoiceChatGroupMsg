package me.lukasabbe.voiceChatGroupMsgPlugin;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MsgVCCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if(!(commandSender instanceof Player)){
            commandSender.sendMessage("only players can execute this command");
            return false;
        }
        if(strings.length == 0){
            commandSender.sendMessage("Make sure to write a message");
            return false;
        }
        final Player player = (Player) commandSender;
        if(!VoiceChatGroupMsgPlugin.isPlayerInGroup(player)){
            commandSender.sendMessage("You need to be in a voice chat group to use this command");
            return false;
        }

    }
}
