package me.lukasabbe.voicechatgroupmsg.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import de.maxhenkel.voicechat.api.Group;
import me.lukasabbe.voicechatgroupmsg.util.VoiceChatUtil;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.MessageArgumentType;
import net.minecraft.network.message.MessageType;
import net.minecraft.network.message.SentMessage;
import net.minecraft.network.message.SignedMessage;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.List;

public class MsgVcCommand {
    public static void CreateGroupMsgCommand(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess commandRegistryAccess, CommandManager.RegistrationEnvironment registrationEnvironment) {
        dispatcher.register(
                CommandManager
                        .literal("msgvc")
                        .requires(ServerCommandSource::isExecutedByPlayer)
                        .then(
                                CommandManager
                                        .argument("message", MessageArgumentType.message())
                                        .executes(MsgVcCommand::runCommand)));
    }

    private static int runCommand(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        final ServerCommandSource source = ctx.getSource();

        final ServerPlayerEntity player = source.getPlayer();
        if(!VoiceChatUtil.isPlayerInGroup(player)){
            source.sendError(Text.literal("You need to be in a voice chat group to use this command"));
            return 0;
        }

        Group group = VoiceChatUtil.getPlayerGroup(player);

        List<ServerPlayerEntity> players = VoiceChatUtil.GroupPlayers(group.getId(), source.getWorld());

        MessageArgumentType.getSignedMessage(ctx, "message", signedMessage -> {
            sendMessage(player, signedMessage, MessageType.TEAM_MSG_COMMAND_OUTGOING, source, group);
            players.forEach(voiceChatMember -> {
                if(voiceChatMember.getUuid().equals(player.getUuid())) return;
                sendMessage(voiceChatMember, signedMessage, MessageType.TEAM_MSG_COMMAND_INCOMING, source, group);
            });
        });
        return 1;
    }

    private static void sendMessage(ServerPlayerEntity player, SignedMessage signedMessage, RegistryKey<MessageType> teamMsgCommandOutgoing, ServerCommandSource source, Group group) {
        player.sendChatMessage(
                SentMessage.of(signedMessage),
                true,
                MessageType.params(teamMsgCommandOutgoing, source)
                        .withTargetName(Text.of(group.getName()))
        );
    }
}
