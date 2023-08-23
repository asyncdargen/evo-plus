package pro.diamondworld.protocol.util;

import io.netty.buffer.ByteBuf;
import lombok.experimental.UtilityClass;
import net.minecraft.network.PacketDataSerializer;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.PacketListenerPlayOut;
import net.minecraft.network.protocol.game.PacketPlayOutCustomPayload;

@UtilityClass
public class PayloadUtil {

    public net.minecraft.resources.MinecraftKey key(String name) {
        return net.minecraft.resources.MinecraftKey.a("dw", name);
    }

    public Packet<PacketListenerPlayOut> create(String channel, ByteBuf buf) {
        return new PacketPlayOutCustomPayload(key(channel), new PacketDataSerializer(buf));
    }

}
