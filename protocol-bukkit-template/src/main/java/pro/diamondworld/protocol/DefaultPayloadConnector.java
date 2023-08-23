package pro.diamondworld.protocol;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.ConnectionSide;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import lombok.Getter;
import lombok.val;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import pro.diamondworld.protocol.packet.ServerInfo;
import pro.diamondworld.protocol.util.BufUtil;
import pro.diamondworld.protocol.util.PayloadUtil;
import pro.diamondworld.protocol.util.ProtocolSerializable;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Getter
public class DefaultPayloadConnector implements PayloadConnector, Listener {

    protected final Map<String, ConnectedPlayer> players = new ConcurrentHashMap<>();
    protected final Map<String, BiConsumer<ConnectedPlayer, ByteBuf>> handlers = new HashMap<>();
    protected final ProtocolRegistry registry = new ProtocolRegistry();

    protected final Logger logger = Logger.getLogger("Protocol");
    protected final ServerInfo serverInfo;
    protected final JavaPlugin plugin;

    public DefaultPayloadConnector(JavaPlugin plugin, ServerInfo info) {
        this.plugin = plugin;
        this.serverInfo = info;

        Bukkit.getPluginManager().registerEvents(this, plugin);
        registerPacketListener();

        registerDummy("handshake", player -> {
            if (!player.isHandshaked()) {
                logger.info("Protocol player " + player.getPlayer().getName() + " successful handshake.");
                player.setHandshaked(true);
            }
            player.send(serverInfo);
        });
    }

    private void registerPacketListener() {
        val parameters = new PacketAdapter.AdapterParameteters()
                .connectionSide(ConnectionSide.CLIENT_SIDE)
                .plugin(plugin)
                .types(PacketType.Play.Client.CUSTOM_PAYLOAD)
                .optionAsync();
        ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(parameters) {
            @Override
            public void onPacketReceiving(PacketEvent event) {
                try {
                    val player = getPlayer(event.getPlayer());
                    if (player == null) {
                        return;
                    }

                    val channel = event.getPacket().getMinecraftKeys().read(0).getKey();
                    val buf = (ByteBuf) event.getPacket().getModifier().read(1);

                    val handler = handlers.get(channel);
                    if (handler != null) {
                        handler.accept(player, buf);
                    }
                } catch (Throwable t) {
                    logger.log(Level.SEVERE, "Error while reading packet", t);
                }
            }
        });
    }

    @EventHandler(priority = EventPriority.LOWEST)
    private void onPlayerJoin(PlayerJoinEvent event) {
        players.put(event.getPlayer().getName().toLowerCase(), new ConnectedPlayer(event.getPlayer(), this));
    }

    @EventHandler(priority = EventPriority.LOWEST)
    private void onPlayerQuit(PlayerQuitEvent event) {
        players.remove(event.getPlayer().getName().toLowerCase());
    }

    @Override
    public ConnectedPlayer getPlayer(String playerName) {
        return players.get(playerName.toLowerCase());
    }

    @Override
    public ConnectedPlayer getPlayer(Player player) {
        return getPlayer(player.getName());
    }

    @Override
    public boolean isConnected(String playerName) {
        val player = getPlayer(playerName);
        return player != null && player.isHandshaked();
    }

    @Override
    public boolean isConnected(Player player) {
        return isConnected(player.getName());
    }

    @Override
    public void register(String channel, BiConsumer<ConnectedPlayer, ByteBuf> handler) {
        handlers.put(channel, handler);
    }

    @Override
    public void registerDummy(String channel, Consumer<ConnectedPlayer> handler) {
        register(channel, (player, __) -> handler.accept(player));
    }

    @Override
    public <T extends ProtocolSerializable> void register(String channel, Class<T> type, BiConsumer<ConnectedPlayer, T> handler) {
        register(channel, (player, buf) -> handler.accept(player, BufUtil.readObject(buf, type)));
    }

    @Override
    public <T extends ProtocolSerializable> void register(Class<T> type, BiConsumer<ConnectedPlayer, T> handler) {
        register(registry.lookupOrRegisterChannel(type), type, handler);
    }

    @Override
    public void sendToPlayers(Collection<Player> players, String channel, ProtocolSerializable packet) {
        send(players.stream()
                .map(this::getPlayer)
                .filter(Objects::nonNull)
                .collect(Collectors.toList()), channel, packet);
    }

    @Override
    public void send(Collection<ConnectedPlayer> players, String channel, ByteBuf buf) {
        val payload = PayloadUtil.create(channel, buf);

        players.forEach(player -> player.send(payload));
    }

    @Override
    public void sendToPlayers(Collection<Player> players, String channel, ByteBuf buf) {
        send(players.stream()
                .map(this::getPlayer)
                .filter(Objects::nonNull)
                .collect(Collectors.toList()), channel, buf);
    }

    @Override
    public void send(Collection<ConnectedPlayer> players, String channel, ProtocolSerializable packet) {
        val buf = Unpooled.buffer();
        BufUtil.writeObject(buf, packet);
        send(players, channel, buf);
    }

    @Override
    public void sendToPlayers(Collection<Player> players, ProtocolSerializable packet) {
        sendToPlayers(players, registry.lookupOrRegisterChannel(packet), packet);
    }

    @Override
    public void send(Collection<ConnectedPlayer> players, ProtocolSerializable packet) {
        send(players, registry.lookupOrRegisterChannel(packet), packet);
    }

    @Override
    public void sendToPlayer(Player player, String channel, ProtocolSerializable packet) {
        val protocolPlayer = getPlayer(player);

        if (protocolPlayer != null) {
            send(protocolPlayer, channel, packet);
        }
    }

    @Override
    public void sendToPlayer(Player player, ProtocolSerializable packet) {
        sendToPlayer(player, registry.lookupOrRegisterChannel(packet), packet);
    }

    @Override
    public void broadcast(String channel, ProtocolSerializable packet) {
        send(players.values(), channel, packet);
    }

    @Override
    public void broadcast(ProtocolSerializable packet) {
        broadcast(registry.lookupOrRegisterChannel(packet), packet);
    }

    @Override
    public void send(ConnectedPlayer player, String channel, ProtocolSerializable packet) {
        send(Collections.singleton(player), channel, packet);
    }

    @Override
    public void send(ConnectedPlayer player, ProtocolSerializable packet) {
        send(player, registry.lookupOrRegisterChannel(packet), packet);
    }

}
