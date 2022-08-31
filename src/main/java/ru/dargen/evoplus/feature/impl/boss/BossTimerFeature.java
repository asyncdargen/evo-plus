package ru.dargen.evoplus.feature.impl.boss;

import lombok.Getter;
import lombok.val;
import lombok.var;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.text.Text;
import org.apache.commons.lang3.tuple.Pair;
import ru.dargen.evoplus.EvoPlus;
import ru.dargen.evoplus.event.render.HudRenderEvent;
import ru.dargen.evoplus.feature.Feature;
import ru.dargen.evoplus.feature.setting.BooleanSetting;
import ru.dargen.evoplus.feature.setting.RangeSetting;
import ru.dargen.evoplus.notify.Notification;
import ru.dargen.evoplus.util.Util;
import ru.dargen.evoplus.util.common.CollectionUtil;
import ru.dargen.evoplus.util.diamondworld.DiamondWorldUtil;
import ru.dargen.evoplus.util.formatter.TimeFormatter;
import ru.dargen.evoplus.util.minecraft.ItemUtil;
import ru.dargen.evoplus.util.minecraft.Render;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Getter
public class BossTimerFeature extends Feature {

    protected Map<BossType, Long> infoMap = new ConcurrentHashMap<>();

    protected BooleanSetting render = BooleanSetting.builder()
            .name("Отображение")
            .id("render")
            .build();

    protected RangeSetting<Integer> scale = RangeSetting.<Integer>builder()
            .name("Размер (%)")
            .id("render-scale")
            .index(99)
            .elements(CollectionUtil.intRange(1, 200, 2))
            .build();

    protected BooleanSetting messageOnReady = BooleanSetting.builder()
            .name("Сообщение при спавне")
            .id("message")
            .value(false)
            .build();

    protected BooleanSetting notifyOnReady = BooleanSetting.builder()
            .name("Уведомление при спавне")
            .id("notify")
            .build();

    protected BooleanSetting clanChatOnReady = BooleanSetting.builder()
            .name("Уведомление в клановый чат при спавне")
            .id("clan-chat")
            .value(false)
            .build();

    protected BooleanSetting menu = BooleanSetting.builder()
            .name("Отображение времени в меню")
            .id("menu")
            .build();

    protected RangeSetting<BossRenderOrder> renderOrder = RangeSetting.<BossRenderOrder>builder()
            .name("Сортировка при отображении")
            .id("sorting")
            .elements(Arrays.asList(BossRenderOrder.values()))
            .build();

    protected RangeSetting<Integer> minLevel = RangeSetting.<Integer>builder()
            .name("Минимальный уровень босса")
            .id("min-level")
            .elements(Arrays.stream(BossType.values()).map(BossType::getLevel).collect(Collectors.toList()))
            .build();

    protected RangeSetting<Integer> maxLevel = RangeSetting.<Integer>builder()
            .name("Максимальный уровень босса")
            .id("max-level")
            .elements(Arrays.stream(BossType.values()).map(BossType::getLevel).collect(Collectors.toList()))
            .index(BossType.values().length - 1)
            .build();

    public BossTimerFeature() {
        super("Таймер боссов", "boss-timer");
        register();
    }

    @Override
    public void onRegister(EvoPlus mod) {
        mod.getTaskBus().runAsync(20, 20, task -> {
            if (!DiamondWorldUtil.isOnPrisonEvo()) return;

            for (BossType type : BossType.values()) {
                if (!hasCooldown(type) && infoMap.remove(type) != null && DiamondWorldUtil.isOnPrisonEvo() && inLevelBounds(type)) {
                    if (notifyOnReady.getValue())
                        mod.getNotifyManager().notify(Notification.Type.CONFIRM, "§aБосс возрожден", 3, "§6" + type.getName());
                    if (messageOnReady.getValue())
                        Util.printMessage(EvoPlus.PREFIX + "§aБосс возрожден: §6" + type.getName());
                    if (clanChatOnReady.getValue())
                        Util.sendMessage("@&aБосс возрожден: &6" + type.getName());
                }
            }

            val info = getNearbyBossInfo();
            if (info != null) infoMap.put(info.getKey(), fixTime(info.getValue()));
        });
        mod.getEventBus().register(HudRenderEvent.class, event -> {
            if (!render.getValue() || !DiamondWorldUtil.isOnPrisonEvo()) return;
            val matrixStack = event.getMatrixStack();
            val index = new int[]{0};
            val scale = this.scale.getValue() / 100f;
            Render.scaledRunner(scale, (__, ___) -> {
                infoMap.entrySet()
                        .stream()
                        .filter(info -> hasCooldown(info.getKey()) && inLevelBounds(info.getKey()))
                        .sorted(renderOrder.getValue().getComparator())
                        .forEach(info -> {
                            val right = info.getValue() + info.getKey().getTime() - System.currentTimeMillis();
                            Render.drawString(
                                    matrixStack,
                                    "§6" + info.getKey().getName()
                                            + "§7 [§c" + info.getKey().getLevel() + "§7] - §a"
                                            + TimeFormatter.formatText(right),
                                    (int) (5 / scale), (int) (((5 / scale) + Render.getStringHeight() * index[0]++) / scale), -1
                            );
                        });
            });
        });
        mod.getTaskBus().runAsync(10, 10, task -> {
            if (DiamondWorldUtil.isOnPrisonEvo() && menu.getValue()) {
                val screen = Util.getCurrentScreen();
                if (screen instanceof GenericContainerScreen) {
                    val containerScreen = ((GenericContainerScreen) screen);
                    val container = containerScreen.getScreenHandler();
                    container.getStacks().forEach(item -> {
                        val type = BossType.getByName(ItemUtil.getDisplayName(item)
                                .replace("Всадник", "Мёртвый всадник"));
                        if (type == null || !inLevelBounds(type)) return;
                        val info = infoMap.get(type);
                        if (info != null) {
                            val right = type.getTime() + info - System.currentTimeMillis();
                            val text = Text.of("§fРеспавн через: §e" + TimeFormatter.formatText(right));
                            var lore = ItemUtil.getTextLore(item);
                            if (lore.get(3).getString().contains("Респавн через"))
                                lore.set(3, text);
                            else {
                                val newLore = new ArrayList<Text>();
                                for (int i = 0; i < lore.size(); i++) {
                                    if (i == 3) newLore.add(text);
                                    newLore.add(lore.get(i));
                                }
                                lore = newLore;
                            }
                            ItemUtil.setTextLore(item, lore);
                        }
                    });
                }
            }
        });
    }

    public int getMinLevel() {
        return Math.min(minLevel.getValue(), maxLevel.getValue());
    }

    public int getMaxLevel() {
        return Math.max(minLevel.getValue(), maxLevel.getValue());
    }

    public boolean inLevelBounds(BossType type) {
        return type.getLevel() <= getMaxLevel() && type.getLevel() >= getMinLevel();
    }

    public boolean hasCooldown(BossType type) {
        val info = infoMap.get(type);
        return info != null && info + type.getTime() >= System.currentTimeMillis();
    }

    public void putCooldown(BossType type) {
        infoMap.put(type, fixTime(System.currentTimeMillis()));
    }

    public Pair<BossType, Long> getNearbyBossInfo() {
        if (Util.getWorld() == null) return null;
        val holograms = StreamSupport.stream(
                        Spliterators.spliteratorUnknownSize(((ClientWorld) Util.getWorld()).getEntities().iterator(), 0),
                        false
                ).filter(entity -> entity instanceof ArmorStandEntity)
                .map(Entity::getDisplayName)
                .map(Text::getString)
                .map(Util::stripColor)
                .collect(Collectors.toList());
        val bossTypeLine = holograms.stream().filter(line -> line.startsWith("Босс")).findFirst();
        val respawnLine = holograms.stream().filter(line -> line.startsWith("Респавн")).findFirst();
        val rightTimeLine = holograms.stream().filter(line -> line.contains("ч.") || line.contains("мин.") || line.contains("сек.")).findFirst();
        if (!bossTypeLine.isPresent() || !respawnLine.isPresent() || !rightTimeLine.isPresent()) return null;

        val type = BossType.getByName(bossTypeLine.get().substring(5)
                .replace("Всадник", "Мёртвый всадник"));
        if (type == null) return null;

        val rightTime = TimeFormatter.parseText(rightTimeLine.get());
        if (rightTime == 0) return null;

        val killTime = System.currentTimeMillis() - (type.getTime() - rightTime);

        return Pair.of(type, killTime);
    }

    private static long fixTime(long time) {
        return (time / 1000) * 1000;
    }

}
