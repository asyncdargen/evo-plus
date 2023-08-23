package pro.diamondworld.protocol;

import pro.diamondworld.protocol.util.Channel;
import pro.diamondworld.protocol.util.ProtocolSerializable;

import java.util.HashMap;
import java.util.Map;

public class ProtocolRegistry {

    protected final Map<String, Class<? extends ProtocolSerializable>> channel2packetType = new HashMap<>();
    protected final Map<Class<? extends ProtocolSerializable>, String> packetType2channel = new HashMap<>();

    public String registerChannel(Class<? extends ProtocolSerializable> type, String channel) {
        if (channel == null && type.isAnnotationPresent(Channel.class)) {
            channel = type.getDeclaredAnnotation(Channel.class).value();
        }

        if (channel == null) {
            return null;
        }

        channel2packetType.put(channel, type);
        packetType2channel.put(type, channel);

        return channel;
    }

    public String registerChannel(Class<? extends ProtocolSerializable> type) {
        return registerChannel(type, null);
    }

    public String lookupOrRegisterChannel(Class<? extends ProtocolSerializable> type) {
        String channel = lookupChannel(type);

        if (channel == null) {
            registerChannel(type);
            channel = lookupChannel(type);
        }

        return channel;
    }

    public String lookupOrRegisterChannel(ProtocolSerializable object) {
        return lookupOrRegisterChannel(object.getClass());
    }

    public String lookupChannel(Class<? extends ProtocolSerializable> type) {
        return packetType2channel.get(type);
    }

    public String lookupChannel(ProtocolSerializable object) {
        return lookupChannel(object.getClass());
    }

    public Class<? extends ProtocolSerializable> lookupType(String channel) {
        return channel2packetType.get(channel);
    }

}
