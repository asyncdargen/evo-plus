package ru.dargen.evoplus.mixins;

import lombok.val;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.ScreenHandler;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.dargen.evoplus.EvoPlus;
import ru.dargen.evoplus.feature.Feature;
import ru.dargen.evoplus.feature.impl.stats.RuneStat;
import ru.dargen.evoplus.util.Util;
import ru.dargen.evoplus.util.minecraft.ItemUtil;
import ru.dargen.evoplus.util.minecraft.Render;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Mixin(GenericContainerScreen.class)
public class GenericContainerScreenMixin {

    @Shadow
    @Final
    private int rows;
    private static final int[] runesSlots = {11, 13, 15, 18, 26};
    private static final Map<String, RuneStat> runesStats = new HashMap<>();

    @Inject(at = @At("TAIL"), method = "render")
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        val screenHandler = ((GenericContainerScreenHandler) Util.getPlayer().currentScreenHandler);
        val screen = ((GenericContainerScreen) Util.getCurrentScreen());

        if (!Feature.STATS_FEATURE.getRunesStats().getValue() || screenHandler == null ||
                screen == null || !screen.getTitle().getString().toLowerCase().contains("\uE962")) // this is /runesbag
            return;

        runesStats.clear();

        for (int slot : runesSlots) {
            getRuneStats(getItemStackIfPaper(screenHandler, slot))
                    .stream()
                    .filter(line -> line.contains(":"))
                    .forEach(raw -> {
                        try {
                            val args = Util.stripColor(raw).split(": ");
                            val name = args[0].trim();
                            val amountRaw = args[1].replace("%", "");
                            val number = amountRaw.equals("+") ? 0 : Double.parseDouble(amountRaw.replace("x", ""));
                            runesStats.computeIfAbsent(name, __ -> new RuneStat(0, args[1].equals("+"), args[1].contains("%"))).amount += number;
                        } catch (Throwable t) {
                            EvoPlus.instance().getLogger().error("Error while parse rune data " + raw, t);
                        }
                    });
        }

        final int[] index = {0};
        int x = Util.getWidth() / 2 + 94;
        int y = Util.getHeight() / 2 - (114 + rows * 18) / 2;
        runesStats.forEach((name, stat) -> {
            Render.drawString(
                    matrices, name + ": " + (stat.amount < 0 ? "§c" : "§a")
                            + (stat.flag ? "+" : String.format("%.2f", stat.amount))
                            + (stat.percentage ? "%" : ""),
                    x, y + index[0]++ * Render.getStringHeight(), -1
            );
        });
    }

    private static ItemStack getItemStackIfPaper(ScreenHandler screen, int slot) {
        val item = screen.getSlot(slot).getStack();
        return item == null || item.getItem() != Items.PAPER ? null : item;
    }

    private static List<String> getRuneStats(ItemStack itemStack) {
        if (itemStack == null)
            return Collections.emptyList();

        val lore = ItemUtil.getStringLore(itemStack);

        if (lore == null || lore.isEmpty())
            return Collections.emptyList();

        lore.remove(0);
        boolean handledStatsBreak;
        for (int i = lore.size() - 1; i >= 0; i--) {
            handledStatsBreak = lore.get(i).contains("---");
            lore.remove(i);
            if (handledStatsBreak) break;
        }
        return lore;
    }
}
