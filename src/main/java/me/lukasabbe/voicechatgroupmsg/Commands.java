package me.lukasabbe.voicechatgroupmsg;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import de.maxhenkel.voicechat.api.Group;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.MessageArgumentType;
import net.minecraft.network.message.MessageType;
import net.minecraft.network.message.SentMessage;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.List;

public class Commands {
    public static void CreateGroupMsgCommand(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess commandRegistryAccess, CommandManager.RegistrationEnvironment registrationEnvironment) {
        dispatcher.register(
                CommandManager
                        .literal("msgvc")
                        .then(
                                CommandManager
                                        .argument("message", MessageArgumentType.message())
                                        .executes(Commands::runCommand)));
    }

    private static int runCommand(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        final ServerCommandSource source = ctx.getSource();
        if(!source.isExecutedByPlayer()) {
            source.sendError(Text.literal("You can't send msgvc as the console"));
            return 0;
        }
        final ServerPlayerEntity player = source.getPlayer();
        if(!VoiceChatGroupMsg.isPlayerInGroup(player)){
            source.sendError(Text.literal("You need to be in a voice chat group to use this command"));
            return 0;
        }
        Group group = VoiceChatGroupMsg.getPlayerGroup(player);
        List<ServerPlayerEntity> players = VoiceChatGroupMsg.GroupPlayers(group.getId(), source.getWorld());
        MessageArgumentType.getSignedMessage(ctx, "message", signedMessage -> {
            player.sendChatMessage(SentMessage.of(signedMessage),true, MessageType.params(MessageType.TEAM_MSG_COMMAND_OUTGOING,source).withTargetName(Text.of(group.getName())));
            players.forEach(player1 -> {
                if(!player1.getUuid().equals(source.getPlayer().getUuid())){
                    player1.sendChatMessage(SentMessage.of(signedMessage),true, MessageType.params(MessageType.TEAM_MSG_COMMAND_INCOMING,source).withTargetName(Text.of(group.getName())));
                }
            });
        });
        return 1;
    }
}
