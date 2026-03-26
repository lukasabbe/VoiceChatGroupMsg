package me.lukasabbe.voicechatgroupmsg.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import de.maxhenkel.voicechat.api.Group;
import me.lukasabbe.voicechatgroupmsg.util.VoiceChatUtil;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.MessageArgument;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.OutgoingChatMessage;
import net.minecraft.network.chat.PlayerChatMessage;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;

import java.util.List;

public class MsgVcCommand {
    public static void CreateGroupMsgCommand(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext commandRegistryAccess, Commands.CommandSelection registrationEnvironment) {
        dispatcher.register(
                Commands
                        .literal("msgvc")
                        .requires(CommandSourceStack::isPlayer)
                        .then(
                                Commands
                                        .argument("message", MessageArgument.message())
                                        .executes(MsgVcCommand::runCommand)));
    }

    private static int runCommand(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        final CommandSourceStack source = ctx.getSource();

        final ServerPlayer player = source.getPlayer();
        if(!VoiceChatUtil.isPlayerInGroup(player)){
            source.sendFailure(Component.literal("You need to be in a voice chat group to use this command"));
            return 0;
        }

        Group group = VoiceChatUtil.getPlayerGroup(player);

        List<ServerPlayer> players = VoiceChatUtil.GroupPlayers(group.getId(), source.getLevel());

        MessageArgument.resolveChatMessage(ctx, "message", signedMessage -> {
            sendMessage(player, signedMessage, ChatType.TEAM_MSG_COMMAND_OUTGOING, source, group);
            players.forEach(voiceChatMember -> {
                if(voiceChatMember.getUUID().equals(player.getUUID())) return;
                sendMessage(voiceChatMember, signedMessage, ChatType.TEAM_MSG_COMMAND_INCOMING, source, group);
            });
        });
        return 1;
    }

    private static void sendMessage(ServerPlayer player, PlayerChatMessage signedMessage, ResourceKey<ChatType> teamMsgCommandOutgoing, CommandSourceStack source, Group group) {
        player.sendChatMessage(
                OutgoingChatMessage.create(signedMessage),
                true,
                ChatType.bind(teamMsgCommandOutgoing, source)
                        .withTargetName(Component.nullToEmpty(group.getName()))
        );
    }
}
