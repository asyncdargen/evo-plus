package ru.dargen.evoplus.feature.impl;

import io.netty.util.internal.ConcurrentSet;
import lombok.Getter;
import lombok.val;
import ru.dargen.evoplus.EvoPlus;
import ru.dargen.evoplus.event.inventory.InventorySlotUpdateEvent;
import ru.dargen.evoplus.feature.Feature;
import ru.dargen.evoplus.feature.setting.BooleanSetting;
import ru.dargen.evoplus.feature.setting.SetConfig;
import ru.dargen.evoplus.util.Util;
import ru.dargen.evoplus.util.diamondworld.DiamondWorldUtil;
import ru.dargen.evoplus.util.minecraft.ItemUtil;

import java.util.Set;
import java.util.stream.Collectors;

@Getter
public class TeamWarFeature extends Feature {

    protected Set<String> clanList = new ConcurrentSet<>();

    protected SetConfig<String> warList = SetConfig.<String>builder()
            .id("war-list")
            .build();

    protected SetConfig<String> teamList = SetConfig.<String>builder()
            .id("team-list")
            .build();

    protected BooleanSetting teamWarTagGlow = BooleanSetting.builder()
            .id("team-war-tag-glow")
            .name("Подсветка тега союзников/врагов")
            .build();

    protected BooleanSetting teamWarTabGlow = BooleanSetting.builder()
            .id("team-war-tab-glow")
            .name("Подсветка союзников/врагов в табе")
            .build();

    protected BooleanSetting glowClan = BooleanSetting.builder()
            .id("glow-clan")
            .name("Подсвечивать соклановцев")
            .build();

    public TeamWarFeature() {
        super("Союзники/Враги", "team-war");
        register();
    }

    @Override
    public void onRegister(EvoPlus mod) {
//        mod.getEventBus().register(AttackEntityEvent.class, event -> {
//            if (blockTeamHit.getValue() && DiamondWorldUtil.isOnPrisonEvo() && teamList.getValue().contains(event.getEntity().getEntityName()))
//                event.setCancelled(true);
//        });
        mod.getEventBus().register(InventorySlotUpdateEvent.class, event -> {
            val openEvent = event.getOpenEvent();
            if (openEvent != null
                    && DiamondWorldUtil.isOnPrisonEvo()
                    && event.getSlot() == 22
                    && Util.stripColor(openEvent.getNameString()).equalsIgnoreCase("Информация о клане")) {
                val members = ItemUtil.getStringLore(event.getStack())
                        .stream()
                        .filter(line -> line.contains(")") && !line.contains(Util.getName()))
                        .map(line -> Util.stripColor(line).split(" ")[1])
                        .map(String::toLowerCase)
                        .collect(Collectors.toSet());
                if (clanList.equals(members)) return;
                clanList.clear();
                clanList.addAll(members);
                Util.printMessage(EvoPlus.PREFIX + "§aСписок клана обновлен. Найдено " + members.size() + " игроков.");
            }
        });
    }

    public String getColorForPlayer(String playerName) {
        return teamList.getValue().contains(playerName.toLowerCase()) || (glowClan.getValue() && clanList.contains(playerName.toLowerCase()))
                ? "§2" : warList.getValue().contains(playerName.toLowerCase())
                ? "§c" : null;
    }

    public String getTabColorForPlayer(String playerName) {
        return teamWarTabGlow.getValue() ? getColorForPlayer(playerName) : null;
    }

    public String getTagColorForPlayer(String playerName) {
        return teamWarTagGlow.getValue() ? getColorForPlayer(playerName) : null;
    }

}
