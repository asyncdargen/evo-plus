package pro.diamondworld.protocol;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import pro.diamondworld.protocol.packet.LevelInfo;
import pro.diamondworld.protocol.packet.ServerInfo;

public class TemplatePlugin extends JavaPlugin {

    private PayloadConnector payloadConnector;

    @Override
    public void onEnable() {
        payloadConnector = PayloadConnector.create(this, new ServerInfo("PRISONEVO", 10));

        //for example
        Bukkit.getScheduler().runTaskTimer(this, () -> {
            payloadConnector.getPlayers().values().forEach(player -> {
                if (player.isHandshaked()) {
                    player.send(new LevelInfo(10, 100.0, 50.0, 226, 341));
                }
            });
        }, 40, 40);
    }

}
