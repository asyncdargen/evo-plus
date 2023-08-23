package pro.diamondworld.protocol.packet;

import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import pro.diamondworld.protocol.util.Channel;
import pro.diamondworld.protocol.util.ProtocolSerializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Channel("levelinfo")
public class LevelInfo implements ProtocolSerializable {

    private int level;
    private double money, requiredMoney;
    private int blocks, requiredBlocks;

    @Override
    public void read(ByteBuf buf) {
        level = buf.readInt();

        money = buf.readDouble();
        requiredMoney = buf.readDouble();

        blocks = buf.readInt();
        requiredMoney = buf.readInt();
    }

    @Override
    public void write(ByteBuf buf) {
        buf.writeInt(level);

        buf.writeDouble(money);
        buf.writeDouble(requiredMoney);

        buf.writeInt(blocks);
        buf.writeInt(requiredBlocks);
    }

}
