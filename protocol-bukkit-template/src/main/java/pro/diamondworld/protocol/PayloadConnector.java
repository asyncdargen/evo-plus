package pro.diamondworld.protocol;

import io.netty.buffer.ByteBuf;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import pro.diamondworld.protocol.packet.ServerInfo;
import pro.diamondworld.protocol.util.ProtocolSerializable;

import java.util.Collection;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public interface PayloadConnector {

    static PayloadConnector create(JavaPlugin plugin, ServerInfo serverInfo) {
        return new DefaultPayloadConnector(plugin, serverInfo);
    }

    ProtocolRegistry getRegistry();

    Map<String, ConnectedPlayer> getPlayers();

    ConnectedPlayer getPlayer(String playerName);

    ConnectedPlayer getPlayer(Player player);

    boolean isConnected(String playerName);

    boolean isConnected(Player player);

    void register(String channel, BiConsumer<ConnectedPlayer, ByteBuf> handler);

    void registerDummy(String channel, Consumer<ConnectedPlayer> handler);

    <T extends ProtocolSerializable> void register(String channel, Class<T> type, BiConsumer<ConnectedPlayer, T> handler);

    <T extends ProtocolSerializable> void register(Class<T> type, BiConsumer<ConnectedPlayer, T> handler);

    void send(Collection<ConnectedPlayer> players, String channel, ByteBuf buf);

    void sendToPlayers(Collection<Player> players, String channel, ByteBuf buf);

    void sendToPlayers(Collection<Player> players, String channel, ProtocolSerializable packet);

    void send(Collection<ConnectedPlayer> players, String channel, ProtocolSerializable packet);

    void sendToPlayers(Collection<Player> players, ProtocolSerializable packet);

    void send(Collection<ConnectedPlayer> players, ProtocolSerializable packet);

    void sendToPlayer(Player player, String channel, ProtocolSerializable packet);

    void sendToPlayer(Player player, ProtocolSerializable packet);

    void send(ConnectedPlayer player, String channel, ProtocolSerializable packet);

    void send(ConnectedPlayer player, ProtocolSerializable packet);

    void broadcast(String channel, ProtocolSerializable packet);

    void broadcast(ProtocolSerializable packet);

}
