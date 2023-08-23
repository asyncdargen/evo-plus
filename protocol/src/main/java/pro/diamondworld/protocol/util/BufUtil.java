package pro.diamondworld.protocol.util;

import io.netty.buffer.ByteBuf;
import lombok.experimental.UtilityClass;
import lombok.val;

import java.nio.charset.StandardCharsets;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

@UtilityClass
public class BufUtil {

    public ObjectAllocator ALLOCATOR = ObjectAllocator.UNSAFE;

    public void writeVarInt(ByteBuf buf, int value) {
        while ((value & -128) != 0) {
            buf.writeByte(value & 127 | 128);
            value >>>= 7;
        }

        buf.writeByte(value);
    }

    public int readVarInt(ByteBuf buf) {
        int i = 0;
        int j = 0;

        byte tmp;
        do {
            tmp = buf.readByte();
            i |= (tmp & 127) << j++ * 7;
            if (j > 5) {
                throw new RuntimeException("VarInt too big");
            }
        } while ((tmp & 128) == 128);

        return i;
    }

    public void writeVarLong(ByteBuf buf, long value) {
        byte temp;
        do {
            temp = (byte) (value & 0x7F);
            value >>>= 7;
            if (value != 0)
                temp |= 0x80;
            buf.writeByte(temp);
        } while (value != 0);
    }

    public long readVarLong(ByteBuf buf) {
        long value = 0;
        byte temp;
        for (int i = 0; i < 10; i++) {
            temp = buf.readByte();
            value |= ((long) (temp & 0x7F)) << (i * 7);
            if ((temp & 0x80) != 0x80)
                break;
        }
        return value;
    }

    public void writeBytes(ByteBuf buf, byte[] bytes) {
        writeVarInt(buf, bytes.length);
        buf.writeBytes(bytes);
    }

    public byte[] readBytes(ByteBuf buf) {
        byte[] bytes = new byte[readVarInt(buf)];
        buf.readBytes(bytes);
        return bytes;
    }

    public void writeString(ByteBuf buf, String text) {
        writeBytes(buf, text.getBytes(StandardCharsets.UTF_8));
    }

    public String readString(ByteBuf buf) {
        return new String(readBytes(buf), StandardCharsets.UTF_8);
    }

    public void writeStringNullable(ByteBuf buf, String text) {
        writeNullable(buf, text, BufUtil::writeString);
    }

    public String readStringNullable(ByteBuf buf) {
        return readNullable(buf, BufUtil::readString);
    }

    public void writeUUID(ByteBuf buf, UUID uuid) {
        buf.writeLong(uuid.getMostSignificantBits());
        buf.writeLong(uuid.getLeastSignificantBits());
    }

    public UUID readUUID(ByteBuf buf) {
        return new UUID(buf.readLong(), buf.readLong());
    }

    public void writeObject(ByteBuf buf, ProtocolSerializable object) {
        object.write(buf);
    }

    public <T extends ProtocolSerializable> T readObject(ByteBuf buf, T object) {
        object.read(buf);
        return object;
    }

    public <T extends ProtocolSerializable> T readObject(ByteBuf buf, Class<T> objectClass) {
        return readObject(buf, ALLOCATOR.allocate(objectClass));
    }

    public void writeObjectNullable(ByteBuf buf, ProtocolSerializable object) {
        writeNullable(buf, object, BufUtil::writeObject);
    }

    public <T extends ProtocolSerializable> T readObjectNullable(ByteBuf buf, T object, boolean nullIfNotPresent) {
        if (buf.readBoolean())
            return readObject(buf, object);

        return nullIfNotPresent ? null : object;
    }

    public <T extends ProtocolSerializable> T readObjectNullable(ByteBuf buf, T object) {
        return readObjectNullable(buf, object, false);
    }

    public <T extends ProtocolSerializable> T readObjectNullable(ByteBuf buf, Class<T> objectClass) {
        return readNullable(buf, __ -> readObject(buf, ALLOCATOR.allocate(objectClass)));
    }

    public <T> boolean writeNullable(ByteBuf buf, T object, BiConsumer<ByteBuf, T> writer) {
        val state = object != null;

        buf.writeBoolean(state);
        if (state) {
            writer.accept(buf, object);
        }

        return state;
    }

    public <T> T readNullableOrDefault(ByteBuf buf, Function<ByteBuf, T> reader, T value) {
        return readNullableOrLazy(buf, reader, () -> value);
    }

    public <T> T readNullableOrLazy(ByteBuf buf, Function<ByteBuf, T> reader, Supplier<T> lazyDefault) {
        return buf.readBoolean() ? reader.apply(buf) : lazyDefault.get();
    }

    public <T> T readNullable(ByteBuf buf, Function<ByteBuf, T> reader) {
        return buf.readBoolean() ? reader.apply(buf) : null;
    }

}