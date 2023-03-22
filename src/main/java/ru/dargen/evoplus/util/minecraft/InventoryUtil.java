package ru.dargen.evoplus.util.minecraft;

import lombok.experimental.UtilityClass;
import net.minecraft.network.packet.c2s.play.CloseHandledScreenC2SPacket;
import ru.dargen.evoplus.util.Util;

@UtilityClass
public class InventoryUtil {

    public void closeInventory(int syncId) {
        Util.sendPacket(new CloseHandledScreenC2SPacket(syncId));
    }
}
