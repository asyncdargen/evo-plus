package ru.dargen.evoplus.mixin.render.screen;

import com.mojang.authlib.GameProfile;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.PlayerSkinDrawer;
import net.minecraft.client.gui.hud.PlayerListHud;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.PlayerModelPart;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardCriterion;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.scoreboard.ScoreboardPlayerScore;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.world.GameMode;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.dargen.evoplus.feature.type.misc.MiscFeature;

import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static net.minecraft.client.gui.DrawableHelper.fill;

@Mixin(PlayerListHud.class)
public abstract class PlayerListHudMixin {

    @Shadow
    @Final
    private MinecraftClient client;

    @Shadow
    protected abstract void renderLatencyIcon(MatrixStack matrices, int width, int x, int y, PlayerListEntry entry);

    @Shadow
    protected abstract void renderScoreboardObjective(ScoreboardObjective objective, int y, String player, int left, int right, UUID uuid, MatrixStack matrices);

    @Shadow
    public abstract Text getPlayerName(PlayerListEntry entry);

    @Shadow
    @Nullable
    private Text footer;

    @Shadow
    @Nullable
    private Text header;

    @Shadow
    protected abstract List<PlayerListEntry> collectPlayerEntries();

    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    private void render(MatrixStack matrices, int scaledWindowWidth, Scoreboard scoreboard, ScoreboardObjective objective, CallbackInfo ci) {
        ci.cancel();
        List<PlayerListEntry> list = this.collectPlayerEntries();
        int i = 0;
        int j = 0;
        Iterator var8 = list.iterator();

        int k;
        while (var8.hasNext()) {
            PlayerListEntry playerListEntry = (PlayerListEntry) var8.next();
            k = this.client.textRenderer.getWidth(this.getPlayerName(playerListEntry));
            i = Math.max(i, k);
            if (objective != null && objective.getRenderType() != ScoreboardCriterion.RenderType.HEARTS) {
                TextRenderer var10000 = this.client.textRenderer;
                ScoreboardPlayerScore var10001 = scoreboard.getPlayerScore(playerListEntry.getProfile().getName(), objective);
                k = var10000.getWidth(" " + var10001.getScore());
                j = Math.max(j, k);
            }
        }

        int l = list.size();
        int m = l;

        for (k = 1; m > 20; m = (l + k - 1) / k) {
            ++k;
        }

        boolean bl = this.client.isInSingleplayer() || this.client.getNetworkHandler().getConnection().isEncrypted();
        int n;
        if (objective != null) {
            if (objective.getRenderType() == ScoreboardCriterion.RenderType.HEARTS) {
                n = 90;
            } else {
                n = j;
            }
        } else {
            n = 0;
        }

        int o = Math.min(k * ((bl ? 9 : 0) + i + n + 13), scaledWindowWidth - 50) / k;
        int p = scaledWindowWidth / 2 - (o * k + (k - 1) * 5) / 2;
        int q = 10;
        int r = o * k + (k - 1) * 5;
        java.util.List<OrderedText> list2 = null;
        if (this.header != null) {
            list2 = this.client.textRenderer.wrapLines(this.header, scaledWindowWidth - 50);

            OrderedText orderedText;
            for (Iterator var18 = list2.iterator(); var18.hasNext(); r = Math.max(r, this.client.textRenderer.getWidth(orderedText))) {
                orderedText = (OrderedText) var18.next();
            }
        }

        List<OrderedText> list3 = null;
        OrderedText orderedText2;

        Iterator var35;
        if (this.footer != null) {
            var currentFooter = MiscFeature.INSTANCE.getShowServerInTab()
                    ? Text.of(this.footer.getString() + "\n%s".formatted("§fВы на: §e" + MiscFeature.INSTANCE.getCurrentServer()))
                    : this.footer;
            list3 = this.client.textRenderer.wrapLines(currentFooter, scaledWindowWidth - 50);

            for (var35 = list3.iterator(); var35.hasNext(); r = Math.max(r, this.client.textRenderer.getWidth(orderedText2))) {
                orderedText2 = (OrderedText) var35.next();
            }
        }

        int var10002;
        int var10003;
        int var10005;
        int s;
        int var33;
        if (list2 != null) {
            var33 = scaledWindowWidth / 2 - r / 2 - 1;
            var10002 = q - 1;
            var10003 = scaledWindowWidth / 2 + r / 2 + 1;
            var10005 = list2.size();
            Objects.requireNonNull(this.client.textRenderer);
            fill(matrices, var33, var10002, var10003, q + var10005 * 9, Integer.MIN_VALUE);

            for (var35 = list2.iterator(); var35.hasNext(); q += 9) {
                orderedText2 = (OrderedText) var35.next();
                s = this.client.textRenderer.getWidth(orderedText2);
                this.client.textRenderer.drawWithShadow(matrices, orderedText2, (float) (scaledWindowWidth / 2 - s / 2), (float) q, -1);
                Objects.requireNonNull(this.client.textRenderer);
            }

            ++q;
        }

        fill(matrices, scaledWindowWidth / 2 - r / 2 - 1, q - 1, scaledWindowWidth / 2 + r / 2 + 1, q + m * 9, Integer.MIN_VALUE);
        int t = this.client.options.getTextBackgroundColor(553648127);

        int v;
        for (int u = 0; u < l; ++u) {
            s = u / m;
            v = u % m;
            int w = p + s * o + s * 5;
            int x = q + v * 9;
            fill(matrices, w, x, w + o, x + 8, t);
            RenderSystem.enableBlend();
            if (u < list.size()) {
                PlayerListEntry playerListEntry2 = list.get(u);
                GameProfile gameProfile = playerListEntry2.getProfile();
                if (bl) {
                    PlayerEntity playerEntity = this.client.world.getPlayerByUuid(gameProfile.getId());
                    boolean bl2 = playerEntity != null && LivingEntityRenderer.shouldFlipUpsideDown(playerEntity);
                    boolean bl3 = playerEntity != null && playerEntity.isPartVisible(PlayerModelPart.HAT);
                    RenderSystem.setShaderTexture(0, playerListEntry2.getSkinTexture());
                    PlayerSkinDrawer.draw(matrices, w, x, 8, bl3, bl2);
                    w += 9;
                }

                this.client.textRenderer.drawWithShadow(matrices, this.getPlayerName(playerListEntry2), (float) w, (float) x, playerListEntry2.getGameMode() == GameMode.SPECTATOR ? -1862270977 : -1);
                if (objective != null && playerListEntry2.getGameMode() != GameMode.SPECTATOR) {
                    int y = w + i + 1;
                    int z = y + n;
                    if (z - y > 5) {
                        this.renderScoreboardObjective(objective, x, gameProfile.getName(), y, z, gameProfile.getId(), matrices);
                    }
                }

                this.renderLatencyIcon(matrices, o, w - (bl ? 9 : 0), x, playerListEntry2);
            }
        }

        if (list3 != null) {
            q += m * 9 + 1;
            var33 = scaledWindowWidth / 2 - r / 2 - 1;
            var10002 = q - 1;
            var10003 = scaledWindowWidth / 2 + r / 2 + 1;
            var10005 = list3.size();
            Objects.requireNonNull(this.client.textRenderer);
            fill(matrices, var33, var10002, var10003, q + var10005 * 9, Integer.MIN_VALUE);

            for (Iterator<OrderedText> var38 = list3.iterator(); var38.hasNext(); q += 9) {
                OrderedText orderedText3 = var38.next();
                v = this.client.textRenderer.getWidth(orderedText3);
                this.client.textRenderer.drawWithShadow(matrices, orderedText3, (float) (scaledWindowWidth / 2 - v / 2), (float) q, -1);
                Objects.requireNonNull(this.client.textRenderer);
            }
        }

    }
}
