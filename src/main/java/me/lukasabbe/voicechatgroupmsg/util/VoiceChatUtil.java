package me.lukasabbe.voicechatgroupmsg.util;

import de.maxhenkel.voicechat.api.Group;
import de.maxhenkel.voicechat.api.VoicechatPlugin;
import de.maxhenkel.voicechat.api.VoicechatServerApi;
import de.maxhenkel.voicechat.api.events.EventRegistration;
import de.maxhenkel.voicechat.api.events.VoicechatServerStartedEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

import java.util.List;
import java.util.UUID;

import static me.lukasabbe.voicechatgroupmsg.VoiceChatGroupMsg.MOD_ID;

public class VoiceChatUtil implements VoicechatPlugin {

    private static VoicechatServerApi API = null;

    @Override
    public String getPluginId() {
        return MOD_ID;
    }

    @Override
    public void registerEvents(EventRegistration registration) {
        registration.registerEvent(VoicechatServerStartedEvent.class, this::onServerStarted);
    }

    public void onServerStarted(VoicechatServerStartedEvent event) {
        API = event.getVoicechat();
    }

    public static boolean isPlayerInGroup(ServerPlayer player){
        try{
            return API.getConnectionOf(player.getUUID()).isInGroup();
        }catch (NullPointerException ignore){
            return false;
        }
    }
    public static boolean isPlayerInGroup(ServerPlayer player, UUID groupID){
        try{
            if(!API.getConnectionOf(player.getUUID()).isInGroup()) return false;
            return API.getConnectionOf(player.getUUID()).getGroup().getId().equals(groupID);

        }catch (NullPointerException ignore){
            return false;
        }
    }
    public static Group getPlayerGroup(ServerPlayer player){
        return API.getConnectionOf(player.getUUID()).getGroup();
    }
    public static List<ServerPlayer> GroupPlayers(UUID groupUUID, ServerLevel serverWorld){
        return serverWorld.getPlayers(player -> isPlayerInGroup(player, groupUUID));
    }
}
