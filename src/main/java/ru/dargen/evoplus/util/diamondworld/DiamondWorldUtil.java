package ru.dargen.evoplus.util.diamondworld;

import com.google.common.collect.Lists;
import lombok.Getter;
import lombok.experimental.UtilityClass;
import lombok.val;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import ru.dargen.evoplus.EvoPlus;
import ru.dargen.evoplus.event.chat.ChatReceiveEvent;
import ru.dargen.evoplus.event.server.ChangeServerEvent;
import ru.dargen.evoplus.event.server.DisconnectServerEvent;
import ru.dargen.evoplus.mixins.PlayerListHudAccessor;
import ru.dargen.evoplus.util.Util;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@UtilityClass
public class DiamondWorldUtil {

    @Getter
    private boolean onDiamondWorld = false;
    private boolean onPrisonEvo = false;

    static {
        val mod = EvoPlus.instance();
        mod.getEventBus().register(ChangeServerEvent.class, event -> onPrisonEvo = false);
        mod.getEventBus().register(DisconnectServerEvent.class, event -> onPrisonEvo = false);
        mod.getEventBus().register(ChangeServerEvent.class, event -> {
            if (isOnDiamondWorld())
                Util.sendMessage("/modinfo server");
        });
        mod.getEventBus().register(ChatReceiveEvent.class, event -> {
            try {
                val encoded = encodeColoredText(event.getText());
                if (encoded.startsWith("[MODINFO")) {
                    val evoServerInfo = encoded.startsWith("[MODINFO-SERVER] PRISONEVO");
                    onPrisonEvo = isOnDiamondWorld() && evoServerInfo;
                    event.setCancelled(true);
                }
            } catch (Throwable ignored) {
            }
        });
        mod.getTaskBus().runAsync(150, 150, task -> {
            if (onDiamondWorld = isOnDiamondWorld0())
                Util.sendMessage("/modinfo server");
        });

        mod.getTaskBus().runAsync(35, 35, task -> {
            if (isOnPrisonEvo() && Util.getBossBarInfos().size() > 1 && Util.getSidebarObjective() == null) {
                Util.printMessage(EvoPlus.PREFIX + "§cДля работы мода необходим интерфейс со скорбордом. Переключаюсь...");
                Util.sendMessage("/interface");
            }
        });
    }

    private boolean isOnDiamondWorld0() {
        val inGameHud = Util.getClient() == null ? null : Util.getClient().inGameHud;
        val playerListHud = inGameHud == null ? null : inGameHud.getPlayerListHud();
        val footerText = playerListHud == null ? null : ((PlayerListHudAccessor) playerListHud).getFooter();
        val footer = footerText == null ? "" : footerText.getString();
        return footer.contains("diamondworld.pro");
    }
//
//    public boolean isPrisonEvoPet(Entity entity) {
//        if (!(entity instanceof ArmorStandEntity)) return false;
//        val tag = entity.toTag(new CompoundTag()).getList("ArmorItems", 10);
//        val headItemTag = tag != null && tag.size() >= 3 ? tag.getCompound(3).getCompound("tag") : null;
//        return headItemTag != null && headItemTag.get("CustomModelData") != null;
//    }

    private final List<Integer> ENCODED_COLOR_ALTERNATIVES = Arrays.asList(
            0, 170, 43520, 43690, 11141120, 11141290, 16755200, 11184810, 5592405, 5592575, 16733695
    );

    public String encodeColoredText(Text text) {
        TextColor color = text.getStyle().getColor();
        List<String> buffer = Lists.newArrayList(color == null ? "" : String.valueOf(ENCODED_COLOR_ALTERNATIVES.indexOf(color.getRgb())));

        for (Text sibling : text.getSiblings()) {
            color = sibling.getStyle().getColor();
            if (color == null || !ENCODED_COLOR_ALTERNATIVES.contains(color.getRgb())) continue;

            buffer.add(ENCODED_COLOR_ALTERNATIVES.indexOf(color.getRgb()) > 9 ? " " : ENCODED_COLOR_ALTERNATIVES.indexOf(color.getRgb()) + "");
        }

        buffer = String.join("", buffer).isEmpty()
                ? Arrays.asList(text.getString().replaceAll("[ §]", "").replaceAll("d", " "))
                : Arrays.asList(String.join("", buffer).replaceAll("-1", "").split(" "));

        return buffer.stream()
                .filter((num) -> !num.isEmpty())
                .map((number) -> String.valueOf((char) Integer.parseInt(number)))
                .collect(Collectors.joining());
    }

    public boolean isOnPrisonEvo() {
        return onPrisonEvo && isOnDiamondWorld();
    }

}
