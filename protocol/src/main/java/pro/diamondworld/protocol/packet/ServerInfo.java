package pro.diamondworld.protocol.packet;

import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import pro.diamondworld.protocol.util.BufUtil;
import pro.diamondworld.protocol.util.Channel;
import pro.diamondworld.protocol.util.ProtocolSerializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Channel("serverinfo")
public class ServerInfo implements ProtocolSerializable {

    public static final ServerInfo EMPTY = new ServerInfo("UNKNOWN", 0);

    private String serverName;
    private int serverId;

    @Override
    public void read(ByteBuf buf) {
        serverName = BufUtil.readString(buf);
        serverId = buf.readInt();
    }

    @Override
    public void write(ByteBuf buf) {
        BufUtil.writeString(buf, serverName);
        buf.writeInt(serverId);
    }

    @Override
    public String toString() {
        return "%s-%s".formatted(serverName, serverId);
    }

}
