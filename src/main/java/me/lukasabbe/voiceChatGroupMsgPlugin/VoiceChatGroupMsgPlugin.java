package me.lukasabbe.voiceChatGroupMsgPlugin;

import de.maxhenkel.voicechat.api.Group;
import de.maxhenkel.voicechat.api.VoicechatPlugin;
import de.maxhenkel.voicechat.api.VoicechatServerApi;
import de.maxhenkel.voicechat.api.events.EventRegistration;
import de.maxhenkel.voicechat.api.events.VoicechatServerStartedEvent;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;
import java.util.UUID;

public final class VoiceChatGroupMsgPlugin extends JavaPlugin implements VoicechatPlugin {
    private static VoicechatServerApi API = null;

    @Override
    public void onEnable() {
        this.getCommand("msgvc").setExecutor(new MsgVCCommand());
    }

    @Override
    public String getPluginId() {
        return "voice_chat_group_msg_plugin";
    }
    @Override
    public void registerEvents(EventRegistration registration) {
        registration.registerEvent(VoicechatServerStartedEvent.class, this::onServerStarted);
    }

    public void onServerStarted(VoicechatServerStartedEvent event) {
        API = event.getVoicechat();
    }

    public static boolean isPlayerInGroup(Player player){
        try{
            return API.getConnectionOf(player.getUniqueId()).isInGroup();
        }catch (NullPointerException ignore){
            return false;
        }
    }
    public static boolean isPlayerInGroup(Player player, UUID groupID){
        try{
            if(!API.getConnectionOf(player.getUniqueId()).isInGroup()) return false;
            return API.getConnectionOf(player.getUniqueId()).getGroup().getId().equals(groupID);

        }catch (NullPointerException ignore){
            return false;
        }
    }
    public static Group getPlayerGroup(Player player){
        return API.getConnectionOf(player.getUniqueId()).getGroup();
    }
    public static List<Player> GroupPlayers(UUID groupUUID, World serverWorld){
        return serverWorld.getPlayers().stream().filter(p -> isPlayerInGroup(p, groupUUID)).toList();
    }
}
