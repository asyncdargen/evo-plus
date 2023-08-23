package pro.diamondworld.protocol.util;

import io.netty.buffer.ByteBuf;

public interface ProtocolSerializable {

    void read(ByteBuf buf);

    void write(ByteBuf buf);

}
