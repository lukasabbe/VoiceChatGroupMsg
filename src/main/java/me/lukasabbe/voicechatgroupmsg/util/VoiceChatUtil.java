package me.lukasabbe.voicechatgroupmsg.util;

import de.maxhenkel.voicechat.api.Group;
import de.maxhenkel.voicechat.api.VoicechatPlugin;
import de.maxhenkel.voicechat.api.VoicechatServerApi;
import de.maxhenkel.voicechat.api.events.EventRegistration;
import de.maxhenkel.voicechat.api.events.VoicechatServerStartedEvent;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;

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

    public static boolean isPlayerInGroup(ServerPlayerEntity player){
        try{
            return API.getConnectionOf(player.getUuid()).isInGroup();
        }catch (NullPointerException ignore){
            return false;
        }
    }
    public static boolean isPlayerInGroup(ServerPlayerEntity player, UUID groupID){
        try{
            if(!API.getConnectionOf(player.getUuid()).isInGroup()) return false;
            return API.getConnectionOf(player.getUuid()).getGroup().getId().equals(groupID);

        }catch (NullPointerException ignore){
            return false;
        }
    }
    public static Group getPlayerGroup(ServerPlayerEntity player){
        return API.getConnectionOf(player.getUuid()).getGroup();
    }
    public static List<ServerPlayerEntity> GroupPlayers(UUID groupUUID, ServerWorld serverWorld){
        return serverWorld.getPlayers(player -> isPlayerInGroup(player, groupUUID));
    }
}
