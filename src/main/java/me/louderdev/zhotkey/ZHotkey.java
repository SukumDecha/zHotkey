package me.louderdev.zhotkey;

import com.comphenix.protocol.ProtocolManager;
import lombok.Getter;
import lombok.SneakyThrows;
import me.louderdev.zhotkey.utils.ConfigFile;
import org.bukkit.plugin.java.JavaPlugin;
import com.comphenix.protocol.ProtocolLibrary;


public final class ZHotkey extends JavaPlugin {

    @Getter static ZHotkey instance;


    @Getter private ConfigFile configFile;
    @Getter private ProtocolManager protocolManager;

    private PacketListener packetListener;
    @Override
    public void onEnable() {
        instance = this;

        loadConfig();
        loadPacket();
    }


    @SneakyThrows
    private void loadConfig()  {
        configFile = new ConfigFile(this, "config.yml" );
    }
    private void loadPacket() {
        protocolManager = ProtocolLibrary.getProtocolManager();

        packetListener = new PacketListener(this);
        protocolManager.addPacketListener(packetListener.getAdapter());
    }

}
