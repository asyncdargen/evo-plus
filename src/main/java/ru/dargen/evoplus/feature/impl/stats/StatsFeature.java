package ru.dargen.evoplus.feature.impl.stats;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import io.netty.util.internal.ConcurrentSet;
import lombok.Getter;
import lombok.val;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPos;
import ru.dargen.evoplus.EvoPlus;
import ru.dargen.evoplus.event.chat.ChatReceiveEvent;
import ru.dargen.evoplus.event.interact.BlockBreakEvent;
import ru.dargen.evoplus.event.inventory.InventorySlotUpdateEvent;
import ru.dargen.evoplus.feature.Feature;
import ru.dargen.evoplus.feature.impl.stats.booster.BoosterInfo;
import ru.dargen.evoplus.feature.impl.stats.booster.BoosterType;
import ru.dargen.evoplus.feature.impl.stats.level.LevelRequire;
import ru.dargen.evoplus.feature.impl.stats.level.RequireType;
import ru.dargen.evoplus.feature.setting.BooleanSetting;
import ru.dargen.evoplus.util.Util;
import ru.dargen.evoplus.util.diamondworld.DiamondWorldUtil;
import ru.dargen.evoplus.util.formatter.DoubleFormatter;
import ru.dargen.evoplus.util.minecraft.ItemUtil;

import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Getter
public class StatsFeature extends Feature {

    protected final Cache<BlockPos, Long> latestBlocks = CacheBuilder.newBuilder()
            .expireAfterWrite(500, TimeUnit.MILLISECONDS)
            .build();
//    protected final Map<Integer, Quest> quests = new ConcurrentHashMap<>();
    protected final Set<LevelRequire> requires = new ConcurrentSet<>();
    protected final BoosterInfo boosterInfo = new BoosterInfo();
    protected long eliteEndTime = 0;

    protected BooleanSetting levelRequires = BooleanSetting.builder()
            .id("level-requires")
            .name("Отображение требований на новый уровень")
            .build();

//    protected BooleanSetting questTasks = BooleanSetting.builder()
//            .id("quest-tasks")
//            .name("Отображение квестовых заданий")
//            .value(false)
//            .build();

    protected BooleanSetting runesStats = BooleanSetting.builder()
            .id("runes-stats")
            .name("Отображение общей статистики рун")
            .build();

    protected BooleanSetting booster = BooleanSetting.builder()
            .id("booster")
            .name("Отображение информации о бустере от копания")
            .build();

    protected BooleanSetting eliteTimer = BooleanSetting.builder()
            .id("elite")
            .name("Таймер элитной шахты")
            .build();

    public StatsFeature() {
        super("Статистика", "statistic");
        register();
    }

    @Override
    public void onRegister(EvoPlus mod) {
//        mod.getEventBus().register(HudRenderEvent.class, event -> {
//            if (!DiamondWorldUtil.isOnPrisonEvo()) return;
//            val matrixStack = event.getMatrixStack();
//            val fHeight = Render.getStringHeight();
//            if (questTasks.getValue()) {
//                val x = ((int) (Util.getWidth() * 0.7));
//                var y = new int[]{50};
//                for (QuestType type : QuestType.values()) {
//                    val quests = this.quests
//                            .values()
//                            .stream()
//                            .filter(quest -> quest.getType() == type)
//                            .collect(Collectors.toSet());
//                    Render.drawString(matrixStack, "§a§l" + type.getName() + " задания:", x, y[0], -1);
//                    y[0] += fHeight;
//                    if (quests.isEmpty()) {
//                        Render.drawString(matrixStack, "   §cЗадания выполнены или не заполнены.", x, y[0], -1);
//                        y[0] += fHeight;
//                    } else quests.forEach(quest -> {
//                        Render.drawString(matrixStack, " §e- " + quest.getTask(), x, y[0], -1);
//                        y[0] += fHeight;
//                    });
//                    y[0] += 3;
//                }
//            }
//            if (eliteTimer.getValue() && eliteEndTime > System.currentTimeMillis()) {
//                Render.drawCenteredStringWithShadow(
//                        matrixStack,
//                        "§aДоступ к элитной шахте еще " + TimeFormatter.formatText(eliteEndTime - System.currentTimeMillis()),
//                        Util.getWidth() / 2, 16, -1
//                );
//            }
//        });
        mod.getEventBus().register(InventorySlotUpdateEvent.class, event -> {
            val openEvent = event.getOpenEvent();
            val item = event.getStack();
            if (openEvent == null || item == null || item.getItem() == Items.AIR || !DiamondWorldUtil.isOnPrisonEvo())
                return;
            val itemName = Util.stripColor(ItemUtil.getDisplayName(item));
            if (itemName.contains("Поднять уровень")) {
                val requires = ItemUtil.getStringLore(item)
                        .stream()
                        .map(Util::stripColor)
                        .filter(line -> line.contains(":"))
                        .map(line -> {
                            val info = line.substring(2).split(": ");
                            val name = info[0].trim();
                            val requireAmount = info[1].split("/")[1].trim();
                            val type = RequireType.getByName(name);
                            if (type == null) return null;
                            else return new LevelRequire(name, type, DoubleFormatter.parse(requireAmount));
                        })
                        .filter(Objects::nonNull)
                        .collect(Collectors.toSet());
                this.requires.clear();
                this.requires.addAll(requires);
            }

//            if (title.contains("DP") && title.contains("Задания") && item.getItem() == Items.PAPER) {
//                if (itemName.contains("завершено")) return;
//                val type = itemName.contains("Ежедневное") ? QuestType.DAILY : QuestType.WEEKLY;
//                val lore = ItemUtil.getStringLore(item);
//                var name = Util.stripColor(lore.get(1));
//                name = name.substring(1, name.lastIndexOf(":"));
//                val expiresTimeRaw = Util.stripColor(lore.get(lore.size() - 1))
//                        .replace("Осталось: ", "")
//                        .trim()
//                        .replace("ч", " час.")
//                        .replace("мин", " мин.")
//                        .replace("д", " дн.");
//                val expiresTime = TimeFormatter.parseText(expiresTimeRaw);
//                if (expiresTime == 0) return;
//                quests.put(name.hashCode(), new Quest(type, name, System.currentTimeMillis() + expiresTime));
//            }
        });
        mod.getEventBus().register(ChatReceiveEvent.class, event -> {
            val text = Util.stripColor(event.getText().getString());
            if (!DiamondWorldUtil.isOnPrisonEvo()) return;
            if (booster.getValue() && text.startsWith("Вы в режиме комбо. Продолжайте копать, чтобы сохранять бустер денег ")) {
                val multiplier = Double.parseDouble(text.substring(69));
                val type = BoosterType.getByMultiplier(multiplier);
                if (type == null) return;
                boosterInfo.setType(type);
                boosterInfo.update();
            }
            if (text.contains("Вы получили доступ к элитной шахте")) {
                eliteEndTime = Integer.parseInt(text.split(" ")[7]) * 60_000L + System.currentTimeMillis();
            }
        });
        mod.getEventBus().register(BlockBreakEvent.class, event -> {
            if (DiamondWorldUtil.isOnPrisonEvo()) {
                latestBlocks.put(event.getPosition(), getBoosterInfo().getLastBreak());
                getBoosterInfo().handleBreak();
            }
        });
//        mod.getTaskBus().runAsync(20, 20, task -> {
//            quests.values()
//                    .stream()
//                    .filter(Quest::isExpired)
//                    .map(Quest::getTask)
//                    .map(String::hashCode)
//                    .forEach(quests::remove);
//        });
    }
}
