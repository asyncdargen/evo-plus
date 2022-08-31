package ru.dargen.evoplus.feature.impl;

import lombok.Getter;
import lombok.val;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import ru.dargen.evoplus.EvoPlus;
import ru.dargen.evoplus.event.chat.ChatReceiveEvent;
import ru.dargen.evoplus.event.entity.AttackEntityEvent;
import ru.dargen.evoplus.event.render.HudRenderEvent;
import ru.dargen.evoplus.feature.Feature;
import ru.dargen.evoplus.feature.setting.BooleanSetting;
import ru.dargen.evoplus.feature.setting.RangeSetting;
import ru.dargen.evoplus.notify.Notification;
import ru.dargen.evoplus.util.Util;
import ru.dargen.evoplus.util.common.CollectionUtil;
import ru.dargen.evoplus.util.diamondworld.DiamondWorldUtil;
import ru.dargen.evoplus.util.minecraft.Render;

import java.util.concurrent.atomic.AtomicInteger;

@Getter
public class MiscFeature extends Feature {

    protected BooleanSetting shardFoundNotify = BooleanSetting.builder()
            .id("shard-found-notify")
            .name("Сообщение о нахождении шарда")
            .value(false)
            .build();

    protected BooleanSetting caseNotify = BooleanSetting.builder()
            .id("case-found-notify")
            .name("Уведомление о нахождении кейсов")
            .build();

    protected BooleanSetting autoThanks = BooleanSetting.builder()
            .id("auto-thanks")
            .name("Автоматическое благодарность за бустеры")
            .build();

    protected BooleanSetting notifySound = BooleanSetting.builder()
            .name("Звуки уведомлений")
            .id("notify-sound")
            .build();

    protected ItemStack hitRenderIconStack = Items.GOLDEN_SWORD.getDefaultStack();
    protected AtomicInteger hitCount = new AtomicInteger();
    protected volatile long lastHit;

    protected BooleanSetting bossHits = BooleanSetting.builder()
            .name("Кол-во ударов по боссу")
            .id("boss-hits")
            .build();
    protected RangeSetting<Integer> bossHitTimeout = RangeSetting.<Integer>builder()
            .name("Таймаут ударов по боссу (сек.)")
            .id("boss-hits-timeout")
            .elements(CollectionUtil.intRange(1, 60, 1))
            .build();

    public MiscFeature() {
        super("Прочее", "misc");
        register();
    }

    @Override
    public void onRegister(EvoPlus mod) {
        mod.getTaskBus().runAsync(20, 20, task -> {
            if (System.currentTimeMillis() - lastHit > bossHitTimeout.getValue() * 1000) hitCount.set(0);
        });
        mod.getEventBus().register(AttackEntityEvent.class, event -> {
            val target = event.getEntity();
            if (!target.hasCustomName() || target instanceof PlayerEntity) return;

            lastHit = System.currentTimeMillis();
            hitCount.incrementAndGet();
        });
        mod.getEventBus().register(ChatReceiveEvent.class, event -> {
            if (!DiamondWorldUtil.isOnPrisonEvo()) return;
            val text = Util.stripColor(event.getText().getString());
            if (caseNotify.getValue() && text.toLowerCase().startsWith("вы нашли кейс")) {
                mod.getNotifyManager().notify(Notification.Type.CONFIRM, "§6" + text, 3);
                event.setCancelled(true);
            }
            if ((!shardFoundNotify.getValue() && text.contains("Вы нашли шард!")) || text.contains("У вас нет предметов, которые можно продать, но вы можете добыть их в шахте"))
                event.setCancelled(true);

            if (autoThanks.getValue() && text.contains("активировал глобальный бустер"))
                EvoPlus.instance().getTaskBus().runLaterAsync(1, task -> Util.sendMessage("/thanks"));
        });
        mod.getEventBus().register(HudRenderEvent.class, event -> {
            if (!bossHits.getValue() || !DiamondWorldUtil.isOnPrisonEvo()) return;
            val text = "§e" + hitCount.get();
            val x = Util.getWidth() / 2 - 126 - Render.getStringWidth(text);
            val y = Util.getHeight() - Render.getStringHeight() - 5;
            val matrixStack = event.getMatrixStack();
            val iconScale = 0.63f;
            Render.scaledRunner(iconScale, (__, ___) -> {
                Render.drawItem(hitRenderIconStack, (int) ((x - 10) / iconScale), (int) ((y - 2) / iconScale));
            });
            Render.drawStringWithShadow(matrixStack, text, x, y, -1);
        });
    }

}
