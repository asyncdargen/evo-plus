package ru.dargen.evoplus.feature.impl.staff;

import lombok.Getter;
import lombok.val;
import net.minecraft.util.ChatUtil;
import ru.dargen.evoplus.EvoPlus;
import ru.dargen.evoplus.event.interact.InteractItemEvent;
import ru.dargen.evoplus.event.inventory.InventorySlotUpdateEvent;
import ru.dargen.evoplus.event.render.HudRenderEvent;
import ru.dargen.evoplus.feature.Feature;
import ru.dargen.evoplus.feature.setting.BooleanSetting;
import ru.dargen.evoplus.feature.setting.RangeSetting;
import ru.dargen.evoplus.notify.Notification;
import ru.dargen.evoplus.util.Util;
import ru.dargen.evoplus.util.common.CollectionUtil;
import ru.dargen.evoplus.util.diamondworld.DiamondWorldUtil;
import ru.dargen.evoplus.util.minecraft.ItemUtil;
import ru.dargen.evoplus.util.minecraft.Render;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Getter
public class StaffTimerFeature extends Feature {

    protected Map<StaffType, StaffInfo> infoMap = new ConcurrentHashMap<>();

    protected BooleanSetting render = BooleanSetting.builder()
            .name("Отображение")
            .id("render")
            .build();

    protected BooleanSetting messageOnReady = BooleanSetting.builder()
            .name("Сообщение при окончании задержки")
            .id("message")
            .value(false)
            .build();

    protected BooleanSetting notifyOnReady = BooleanSetting.builder()
            .name("Уведомление при окончании задержки")
            .id("notify")
            .build();

    protected RangeSetting<Integer> perkLevel = RangeSetting.<Integer>builder()
            .name("Уровень перка \"Чродей\"")
            .id("magic-level")
            .elements(CollectionUtil.intRange(0, 2, 1))
            .build();

    public StaffTimerFeature() {
        super("Таймер посохов", "staff-timer");
        register();
    }

    @Override
    public void onRegister(EvoPlus mod) {
        mod.getTaskBus().runAsync(10, 10, task -> {
            for (StaffType type : StaffType.values()) {
                if (!hasCooldown(type) && infoMap.remove(type) != null && DiamondWorldUtil.isOnPrisonEvo()) {
                    if (notifyOnReady.getValue())
                        mod.getNotifyManager().notify(Notification.Type.CONFIRM, "§aПосох снова доступен", 3, "§e" + type.getName());
                    if (messageOnReady.getValue())
                        Util.printMessage(EvoPlus.PREFIX + "§aПосох снова доступен: §e" + type.getName());
                }
            }
        });
        mod.getEventBus().register(InventorySlotUpdateEvent.class, event -> {
            val itemStack = event.getStack();
            val displayName = itemStack == null ? null : ChatUtil.stripTextFormat(ItemUtil.getDisplayName(itemStack));
            if (displayName == null || !displayName.contains("Чародей")) return;
            val level = ItemUtil.getStringLore(itemStack)
                    .stream()
                    .anyMatch(line -> line.contains("Максимальная прокачка")) ? 2 : displayName.contains("2") ? 1 : 0;
            perkLevel.setIndex(level);
        });
        mod.getEventBus().register(InteractItemEvent.class, event -> {
            val itemStack = event.getItemStack();
            val type = itemStack == null ? null : StaffType.getByStack(itemStack);
            if (itemStack == null || type == null || hasCooldown(type)) return;
            val durationLine = ItemUtil.getStringLore(itemStack)
                    .stream()
                    .map(Util::stripColor)
                    .filter(line -> line.contains("Перезарядка"))
                    .findFirst();
            if (!durationLine.isPresent()) return;
            val durationInfo = durationLine.get().split(": ")[1];
            val rawDuration = Float.parseFloat(durationInfo.substring(0, durationInfo.length() - 1));
            val duration = Math.round((1 - perkLevel.getValue() * .05f) * rawDuration);
            putCooldown(type, duration);
        });
        mod.getEventBus().register(HudRenderEvent.class, event -> {
            if (!render.getValue() || !DiamondWorldUtil.isOnPrisonEvo()) return;
            val startX = Util.getWidth() / 2 + 95;
            val height = Util.getHeight();
            val matrixStack = event.getMatrixStack();
            for (StaffType type : StaffType.values()) {
                val info = infoMap.get(type);
                val state = hasCooldown(type) ? "§6" + (info.getUsedTime() + info.getDelay() - System.currentTimeMillis()) / 1000 : "§2§l✓";
                Render.drawCenteredString(matrixStack, state, startX + 20 * type.ordinal() + 10, height - 24 - Render.getStringHeight() / 2, -1);
                Render.drawItem(type.getRenderItem(), startX + 20 * type.ordinal(), height - 20);
                if (!hasCooldown(type) && infoMap.remove(type) != null) {
                    if (notifyOnReady.getValue())
                        mod.getNotifyManager().notify(Notification.Type.CONFIRM, "§aПосох снова доступен", 3, "§e" + type.getName());
                    if (messageOnReady.getValue())
                        Util.printMessage(EvoPlus.PREFIX + "§aПосох снова доступен: §e" + type.getName());
                }
            }
        });
    }

    public boolean hasCooldown(StaffType type) {
        val info = infoMap.get(type);
        return info != null && info.getUsedTime() + info.getDelay() >= System.currentTimeMillis();
    }

    public void putCooldown(StaffType type, int duration) {
        infoMap.put(type, new StaffInfo(System.currentTimeMillis(), duration * 1000L));
    }

}
