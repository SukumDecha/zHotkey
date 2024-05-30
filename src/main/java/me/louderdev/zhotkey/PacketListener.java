package me.louderdev.zhotkey;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import lombok.Data;
import me.louderdev.zhotkey.utils.CC;
import me.louderdev.zhotkey.utils.ConfigFile;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

public class PacketListener {

    private ZHotkey main;

    private LinkedList<UUID> playersUuid;
    private Props props;

    public PacketListener(ZHotkey main) {
        this.main = main;
        this.playersUuid = new LinkedList<>();


        loadConfig(main.getConfigFile());
    }

    private void loadConfig(ConfigFile c) {
        props = new Props();

        props.setSoundEnabled(c.getBoolean("sound.enabled"));
        props.setCommand(c.getString("command"));
        props.setMessage(c.getStringList("message"));

        try  {
            Sound sound = Sound.valueOf(c.getString("sound.sound"));

            SoundProps soundProps = new SoundProps();
            soundProps.setType(sound);
            soundProps.setVolume((float) c.getDouble("sound.volume"));
            soundProps.setPitch((float) c.getDouble("sound.pitch"));

            props.setSound(soundProps);
        } catch (Exception e) {
            Bukkit.getConsoleSender().sendMessage("Could not parse sound: " + c.getString("sound"));
        }
    }

    public PacketAdapter getAdapter() {
        return new PacketAdapter(
                main,
                ListenerPriority.NORMAL,
                PacketType.Play.Client.ADVANCEMENTS
        ) {
            @Override
            public void onPacketReceiving(PacketEvent event) {
                Player player = event.getPlayer();

                event.setCancelled(true);

//                PacketContainer fakeOpenInventoryPacket = protocolManager.createPacket(PacketType.Play.Server.OPEN_WINDOW);
//                fakeOpenInventoryPacket.getIntegers().write(0, 0); // Window ID. Use any non-existing ID.
//                fakeOpenInventoryPacket.getChatComponents().write(0, WrappedChatComponent.fromText("")); // Empty title.
//                try {
//                    protocolManager.sendServerPacket(player, fakeOpenInventoryPacket);
//                } catch (Exception e) {
//                    e.printStackTrace(); // Handle any potential exceptions
//                }

                if (!playersUuid.contains(player.getUniqueId())) {
                    playersUuid.add(player.getUniqueId());

                    Bukkit.getScheduler().runTaskLater(main, () -> {
                        player.chat(props.getCommand());

                        if(props.isSoundEnabled()) {
                            player.playSound(player.getLocation(), props.getSound().getType(), props.getSound().getVolume(), props.getSound().getPitch());
                        }

                        for(String msg : props.getMessage()) {
                            player.sendMessage(CC.translate(msg));
                        }
                    }, 1L);
                } else {
                    playersUuid.remove(player.getUniqueId());
                }
            }
        };
    }

    @Data
    class Props {
        boolean soundEnabled;
        String command;
        SoundProps sound;
        List<String> message;
    }

    @Data
    class SoundProps {
        Sound type;
        private float volume;
        private float pitch;
    }
}
