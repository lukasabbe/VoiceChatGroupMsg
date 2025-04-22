package me.lukasabbe.voicechatgroupmsg;

import me.lukasabbe.voicechatgroupmsg.command.MsgVcCommand;
import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;

public class VoiceChatGroupMsg implements DedicatedServerModInitializer {

    public final static String MOD_ID = "voicechatgroupmsg";

    @Override
    public void onInitializeServer() {
        registerCommands();
    }

    private static void registerCommands() {
        CommandRegistrationCallback.EVENT.register(MsgVcCommand::CreateGroupMsgCommand);
    }
}
