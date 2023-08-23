package pro.diamondworld.protocol;

import lombok.Data;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.PacketListenerPlayOut;
import net.minecraft.server.level.EntityPlayer;
import org.bukkit.craftbukkit.v1_19_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import pro.diamondworld.protocol.util.ProtocolSerializable;

@Data
public class ConnectedPlayer {

    private final Player player;
    private final PayloadConnector connector;

    private boolean isHandshaked;

    public EntityPlayer getHandle() {
        return ((CraftPlayer) player).getHandle();
    }

    public net.minecraft.server.network.PlayerConnection getConnection() {
        return getHandle().b;
    }

    public void send(ProtocolSerializable object) {
        connector.send(this, object);
    }

    public void send(String channel, ProtocolSerializable object) {
        connector.send(this, channel, object);
    }

    public void send(Packet<PacketListenerPlayOut> packet) {
        getConnection().a(packet);
    }

}
