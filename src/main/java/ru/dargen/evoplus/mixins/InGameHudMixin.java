package ru.dargen.evoplus.mixins;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
import lombok.val;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.HungerManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.scoreboard.ScoreboardPlayerScore;
import net.minecraft.scoreboard.Team;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.dargen.evoplus.EvoPlus;
import ru.dargen.evoplus.event.render.HudRenderEvent;
import ru.dargen.evoplus.feature.Feature;
import ru.dargen.evoplus.feature.impl.stats.level.LevelRequire;
import ru.dargen.evoplus.util.diamondworld.DiamondWorldUtil;
import ru.dargen.evoplus.util.formatter.TimeFormatter;
import ru.dargen.evoplus.util.minecraft.Render;

import java.util.*;
import java.util.stream.Collectors;

import static net.minecraft.client.gui.DrawableHelper.fill;

@Mixin(InGameHud.class)
public abstract class InGameHudMixin {

    @Shadow
    public abstract TextRenderer getTextRenderer();

    @Shadow
    private int scaledHeight;

    @Shadow
    private int scaledWidth;

    @Shadow
    @Final
    private MinecraftClient client;

    @Shadow
    @Nullable
    private Text overlayMessage;

    @Shadow
    private int overlayRemaining;

    @Shadow
    private boolean overlayTinted;

    @Shadow
    private long heartJumpEndTick;

    @Shadow
    protected abstract PlayerEntity getCameraPlayer();

    @Shadow
    private int ticks;

    @Shadow
    private int lastHealthValue;

    @Shadow
    private long lastHealthCheckTime;

    @Shadow
    private int renderHealthValue;

    @Shadow
    @Final
    private Random random;

    @Shadow
    protected abstract LivingEntity getRiddenEntity();

    @Shadow
    protected abstract int getHeartCount(LivingEntity entity);

    @Shadow
    protected abstract int getHeartRows(int heartCount);

//    @Inject(at = @At("HEAD"), method = "setOverlayMessage", cancellable = true)
//    private void setOverlayMessage(Text message, boolean tinted, CallbackInfo ci) {
//        ci.cancel();
//        val event = new OverlayEvent(message);
//        if (!EvoPlus.instance().getEventBus().fireEvent(event).isCancelled()) {
//            this.overlayMessage = event.getText();
//            this.overlayRemaining = 60;
//            this.overlayTinted = tinted;
//        }
//    }

    @Inject(method = "render", at = @At(value = "TAIL"), slice = @Slice(from = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/PlayerListHud;render(Lnet/minecraft/client/util/math/MatrixStack;ILnet/minecraft/scoreboard/Scoreboard;Lnet/minecraft/scoreboard/ScoreboardObjective;)V")))
    private void setOverlayMessage(MatrixStack matrices, float tickDelta, CallbackInfo ci) {
        matrices.push();
        EvoPlus.instance().getEventBus().fireEvent(new HudRenderEvent(matrices, tickDelta));
        matrices.pop();
    }


    @Inject(at = @At("HEAD"), method = "renderStatusBars", cancellable = true)
    private void renderStatusBars(MatrixStack matrices, CallbackInfo ci) {
        ci.cancel();
        PlayerEntity playerEntity = this.getCameraPlayer();
        if (playerEntity != null) {
            int i = MathHelper.ceil(playerEntity.getHealth());
            boolean bl = this.heartJumpEndTick > (long) this.ticks && (this.heartJumpEndTick - (long) this.ticks) / 3L % 2L == 1L;
            long l = Util.getMeasuringTimeMs();
            if (i < this.lastHealthValue && playerEntity.timeUntilRegen > 0) {
                this.lastHealthCheckTime = l;
                this.heartJumpEndTick = (long) (this.ticks + 20);
            } else if (i > this.lastHealthValue && playerEntity.timeUntilRegen > 0) {
                this.lastHealthCheckTime = l;
                this.heartJumpEndTick = (long) (this.ticks + 10);
            }

            if (l - this.lastHealthCheckTime > 1000L) {
                this.lastHealthValue = i;
                this.renderHealthValue = i;
                this.lastHealthCheckTime = l;
            }

            this.lastHealthValue = i;
            int j = this.renderHealthValue;
            this.random.setSeed((long) (this.ticks * 312871));
            HungerManager hungerManager = playerEntity.getHungerManager();
            int k = hungerManager.getFoodLevel();
            int m = this.scaledWidth / 2 - 91;
            int n = this.scaledWidth / 2 + 91;
            int o = this.scaledHeight - 39;
            float f = (float) playerEntity.getAttributeValue(EntityAttributes.GENERIC_MAX_HEALTH);
            int p = MathHelper.ceil(playerEntity.getAbsorptionAmount());
            int q = MathHelper.ceil((f + (float) p) / 2.0F / 10.0F);
            int r = Math.max(10 - (q - 2), 3);
            int s = o - (q - 1) * r - 10;
            int t = o - 10;
            int u = p;
            int v = playerEntity.getArmor();
            int w = -1;
            if (playerEntity.hasStatusEffect(StatusEffects.REGENERATION)) {
                w = this.ticks % MathHelper.ceil(f + 5.0F);
            }

            this.client.getProfiler().push("armor");

            int z;
            int aa;
            if (!DiamondWorldUtil.isOnPrisonEvo() || Feature.RENDER_FEATURE.getFoodAirArmor().getValue()) {
                for (z = 0; z < 10; ++z) {
                    if (v > 0) {
                        aa = m + z * 8;
                        if (z * 2 + 1 < v) {
                            Render.drawTexture(matrices, aa, s, 34, 9, 9, 9);
                        }

                        if (z * 2 + 1 == v) {
                            Render.drawTexture(matrices, aa, s, 25, 9, 9, 9);
                        }

                        if (z * 2 + 1 > v) {
                            Render.drawTexture(matrices, aa, s, 16, 9, 9, 9);
                        }
                    }
                }
            }

            this.client.getProfiler().swap("health");

            int ah;
            int ai;
            int ad;
            int ae;
            int af;
            val ch = !Feature.RENDER_FEATURE.getHealthBar().getValue();
            for (z = MathHelper.ceil((f + (float) p) / 2.0F) - 1; z >= 0; --z) {
                aa = 16;
                if (playerEntity.hasStatusEffect(StatusEffects.POISON)) {
                    aa += 36;
                } else if (playerEntity.hasStatusEffect(StatusEffects.WITHER)) {
                    aa += 72;
                }

                ah = 0;
                if (bl) {
                    ah = 1;
                }

                ai = MathHelper.ceil((float) (z + 1) / (ch ? 10f : 23f)) - 1;
                ad = m - (ch ? 0 : 1) + z % (ch ? 10 : 23) * 8;
                ae = o - ai * r;
                if (i <= 4) {
                    ae += this.random.nextInt(2);
                }

                if (u <= 0 && z == w) {
                    ae -= 2;
                }

                af = 0;
                if (playerEntity.world.getLevelProperties().isHardcore()) {
                    af = 5;
                }

                Render.drawTexture(matrices, ad, ae, 16 + ah * 9, 9 * af, 9, 9);
                if (bl) {
                    if (z * 2 + 1 < j) {
                        Render.drawTexture(matrices, ad, ae, aa + 54, 9 * af, 9, 9);
                    }

                    if (z * 2 + 1 == j) {
                        Render.drawTexture(matrices, ad, ae, aa + 63, 9 * af, 9, 9);
                    }
                }

                if (u > 0) {
                    if (u == p && p % 2 == 1) {
                        Render.drawTexture(matrices, ad, ae, aa + 153, 9 * af, 9, 9);
                        --u;
                    } else {
                        Render.drawTexture(matrices, ad, ae, aa + 144, 9 * af, 9, 9);
                        u -= 2;
                    }
                } else {
                    if (z * 2 + 1 < i) {
                        Render.drawTexture(matrices, ad, ae, aa + 36, 9 * af, 9, 9);
                    }

                    if (z * 2 + 1 == i) {
                        Render.drawTexture(matrices, ad, ae, aa + 45, 9 * af, 9, 9);
                    }
                }
            }


            LivingEntity livingEntity = this.getRiddenEntity();
            aa = this.getHeartCount(livingEntity);
            if (!DiamondWorldUtil.isOnPrisonEvo() || Feature.RENDER_FEATURE.getFoodAirArmor().getValue())
                if (aa == 0) {
                    this.client.getProfiler().swap("food");

                    for (ah = 0; ah < 10; ++ah) {
                        ai = o;
                        ad = 16;
                        int ak = 0;
                        if (playerEntity.hasStatusEffect(StatusEffects.HUNGER)) {
                            ad += 36;
                            ak = 13;
                        }

                        if (playerEntity.getHungerManager().getSaturationLevel() <= 0.0F && this.ticks % (k * 3 + 1) == 0) {
                            ai = o + (this.random.nextInt(3) - 1);
                        }

                        af = n - ah * 8 - 9;
                        Render.drawTexture(matrices, af, ai, 16 + ak * 9, 27, 9, 9);
                        if (ah * 2 + 1 < k) {
                            Render.drawTexture(matrices, af, ai, ad + 36, 27, 9, 9);
                        }

                        if (ah * 2 + 1 == k) {
                            Render.drawTexture(matrices, af, ai, ad + 45, 27, 9, 9);
                        }
                    }

                    t -= 10;
                }

            this.client.getProfiler().swap("air");
            ah = playerEntity.getMaxAir();
            ai = Math.min(playerEntity.getAir(), ah);
            if (!DiamondWorldUtil.isOnPrisonEvo() || Feature.RENDER_FEATURE.getFoodAirArmor().getValue())
                if (playerEntity.isSubmergedIn(FluidTags.WATER) || ai < ah) {
                    ad = this.getHeartRows(aa) - 1;
                    t -= ad * 10;
                    ae = MathHelper.ceil((double) (ai - 2) * 10.0 / (double) ah);
                    af = MathHelper.ceil((double) ai * 10.0 / (double) ah) - ae;

                    for (int ar = 0; ar < ae + af; ++ar) {
                        if (ar < ae) {
                            Render.drawTexture(matrices, n - ar * 8 - 9, t, 16, 18, 9, 9);
                        } else {
                            Render.drawTexture(matrices, n - ar * 8 - 9, t, 25, 18, 9, 9);
                        }
                    }
                }

            this.client.getProfiler().pop();
        }

    }


    @Inject(at = @At("HEAD"), method = "renderScoreboardSidebar", cancellable = true)
    private void renderScoreboardSidebar(MatrixStack matrices, ScoreboardObjective objective, CallbackInfo ci) {
        ci.cancel();
        if (!Feature.RENDER_FEATURE.getScoreboard().getValue()) return;
        Scoreboard scoreboard = objective.getScoreboard();
        Collection<ScoreboardPlayerScore> collection = scoreboard.getAllPlayerScores(objective);
        List<ScoreboardPlayerScore> list = collection.stream().filter((scores) -> scores.getPlayerName() != null && !scores.getPlayerName().startsWith("#")).collect(Collectors.toList());
        if (list.size() > 15) collection = Lists.newArrayList(Iterables.skip(list, collection.size() - 15));
        else collection = list;

        List<Pair<ScoreboardPlayerScore, Text>> list2 = Lists.newArrayList();
        Text text = objective.getDisplayName();
        int i = getTextRenderer().getWidth(text);
        int j = i;
        int k = getTextRenderer().getWidth(": ");

        ScoreboardPlayerScore scoreboardPlayerScore;
        MutableText text2;
        for (Iterator var11 = ((Collection) collection).iterator(); var11.hasNext(); j = Math.max(j, this.getTextRenderer().getWidth(text2) + k + this.getTextRenderer().getWidth(Integer.toString(scoreboardPlayerScore.getScore())))) {
            scoreboardPlayerScore = (ScoreboardPlayerScore) var11.next();
            Team team = scoreboard.getPlayerTeam(scoreboardPlayerScore.getPlayerName());
            text2 = Team.decorateName(team, Text.of(scoreboardPlayerScore.getPlayerName()));
            list2.add(Pair.of(scoreboardPlayerScore, text2));
            val info = Feature.STATS_FEATURE.getBoosterInfo();
            if (scoreboardPlayerScore.getScore() == 3 && DiamondWorldUtil.isOnPrisonEvo()) {
                if (Feature.STATS_FEATURE.getBooster().getValue() && info.isActive()) {
                    if (info.isStopBreak())
                        list2.add(newScore("  §cКонец через " + TimeFormatter.formatText(info.getRightTime()), scoreboard, objective));
                    list2.add(newScore("  Блоки: " + (info.isCompleted() ? "§a" : "§c") + info.getBlocks() + (info.isMax() ? "" : ("/" + info.getNextBlocks())), scoreboard, objective));
                    if (info.isMax())
                        list2.add(newScore("  Множитель: §a" + info.getType().getBooster(), scoreboard, objective));
                    else
                        list2.add(newScore("  Множитель: §a" + info.getType().getBooster() + " §6-> §a" + info.getNextType().getBooster(), scoreboard, objective));
                    list2.add(newScore(" §E§LБустер от копания", scoreboard, objective));
                    list2.add(newScore("  ", scoreboard, objective));
                }
                if (Feature.STATS_FEATURE.getLevelRequires().getValue() && !Feature.STATS_FEATURE.getRequires().isEmpty()) {
                    Feature.STATS_FEATURE.getRequires()
                            .stream()
                            .sorted(Comparator.comparing(LevelRequire::getType))
                            .map(LevelRequire::toString)
                            .forEach(line -> list2.add(newScore("  " + line, scoreboard, objective)));
                    list2.add(newScore(" §E§LПрокачка уровня", scoreboard, objective));
                    list2.add(newScore("  ", scoreboard, objective));
                }
            }
        }

        int var10000 = list2.size();
        int l = var10000 * 9;
        int m = this.scaledHeight / 2 + l / 3;
        int o = this.scaledWidth - j - 3;
        int p = 0;
        int q = this.client.options.getTextBackgroundColor(0.3F);
        int r = this.client.options.getTextBackgroundColor(0.4F);

        for (Pair<ScoreboardPlayerScore, Text> scoreboardPlayerScoreTextPair : list2) {
            Pair<ScoreboardPlayerScore, Text> pair = scoreboardPlayerScoreTextPair;
            ++p;
            ScoreboardPlayerScore scoreboardPlayerScore2 = pair.getFirst();
            Text text3 = pair.getSecond();
            String string = Feature.RENDER_FEATURE.getScores().getValue() ? (Formatting.RED + "" + scoreboardPlayerScore2.getScore()) : "";
            this.getTextRenderer().getClass();
            int t = m - p * 9;
            int u = this.scaledWidth - 3 + 2;
            int var10001 = o - 2;
            this.getTextRenderer().getClass();
            fill(matrices, var10001, t, u, t + 9, q);
            this.getTextRenderer().draw(matrices, text3, (float) o, (float) t, -1);
            this.getTextRenderer().draw(matrices, string, (float) (u - this.getTextRenderer().getWidth(string)), (float) t, -1);
            if (p == list2.size()) {
                var10001 = o - 2;
                this.getTextRenderer().getClass();
                fill(matrices, var10001, t - 9 - 1, u, t - 1, r);
                fill(matrices, o - 2, t - 1, u, t, q);
                TextRenderer var31 = this.getTextRenderer();
                float var10003 = (float) (o + j / 2 - i / 2);
                this.getTextRenderer().getClass();
                var31.draw(matrices, text, var10003, (float) (t - 9), -1);
            }
        }

    }

    private static Pair<ScoreboardPlayerScore, Text> newScore(String text, Scoreboard scoreboard, ScoreboardObjective objective) {
        val score = new ScoreboardPlayerScore(scoreboard, objective, "");
        score.setScore(3);
        return Pair.of(score, Text.of(text));
    }

    @Inject(at = @At("HEAD"), method = "renderExperienceBar", cancellable = true)
    private void renderScoreboardSidebar(MatrixStack matrices, int x, CallbackInfo ci) {
        ci.cancel();
        this.client.getProfiler().push("expBar");
        this.client.getTextureManager().bindTexture(DrawableHelper.GUI_ICONS_TEXTURE);
        int i = this.client.player.getNextLevelExperience();
        int m;
        int n;
        if (i > 0) {
            m = (int) (this.client.player.experienceProgress * 183.0F);
            n = this.scaledHeight - 32 + 3;
            Render.drawTexture(matrices, x, n, 0, 64, 182, 5);
            if (m > 0) {
                Render.drawTexture(matrices, x, n, 0, 69, m, 5);
            }
        }

        this.client.getProfiler().pop();
        if (this.client.player.experienceLevel > 0) {
            this.client.getProfiler().push("expLevel");
            String string = "" + this.client.player.experienceLevel;
            m = (this.scaledWidth - this.getTextRenderer().getWidth(string)) / 2;
            n = this.scaledHeight - 30;
            this.getTextRenderer().draw(matrices, string, (float) (m + 1), (float) n, 0);
            this.getTextRenderer().draw(matrices, string, (float) (m - 1), (float) n, 0);
            this.getTextRenderer().draw(matrices, string, (float) m, (float) (n + 1), 0);
            this.getTextRenderer().draw(matrices, string, (float) m, (float) (n - 1), 0);
            this.getTextRenderer().draw(matrices, string, (float) m, (float) n, 8453920);
            this.client.getProfiler().pop();
        }

    }


}
