package ru.dargen.evoplus.mixins;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.util.math.MathHelper;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.dargen.evoplus.feature.Feature;

@Mixin(LightmapTextureManager.class)
public abstract class LightmapTextureManagerMixin {

    @Shadow
    private boolean dirty;

    @Shadow
    @Final
    private MinecraftClient client;

    @Shadow
    private float flickerIntensity;

    @Shadow
    @Final
    private GameRenderer renderer;

    @Shadow
    @Final
    private NativeImage image;

    @Shadow
    @Final
    private NativeImageBackedTexture texture;

    @Shadow protected abstract float getDarknessFactor(float delta);

    @Shadow protected abstract float getDarkness(LivingEntity entity, float factor, float delta);

    @Shadow protected abstract float easeOutQuart(float x);

    @Inject(at = @At("HEAD"), method = "update")
    public void update(float delta, CallbackInfo ci) {
        if (this.dirty) {
            this.dirty = false;
            this.client.getProfiler().push("lightTex");
            ClientWorld clientWorld = this.client.world;
            if (clientWorld != null) {
                float f = clientWorld.getSkyBrightness(1.0F);
                float g;
                if (clientWorld.getLightningTicksLeft() > 0) {
                    g = 1.0F;
                } else {
                    g = f * 0.95F + 0.05F;
                }

                float h = this.client.options.getDarknessEffectScale().getValue().floatValue();
                float i = getDarknessFactor(delta) * h;
                float j = getDarkness(this.client.player, i, delta) * h;
                float k = this.client.player.getUnderwaterVisibility();
                float l;
                if (this.client.player.hasStatusEffect(StatusEffects.NIGHT_VISION)) {
                    l = GameRenderer.getNightVisionStrength(this.client.player, delta);
                } else if (k > 0.0F && this.client.player.hasStatusEffect(StatusEffects.CONDUIT_POWER)) {
                    l = k;
                } else {
                    l = 0.0F;
                }

                Vector3f vector3f = (new Vector3f(f, f, 1.0F)).lerp(new Vector3f(1.0F, 1.0F, 1.0F), 0.35F);
                float m = this.flickerIntensity + 1.5F;
                Vector3f vector3f2 = new Vector3f();

                for(int n = 0; n < 16; ++n) {
                    for(int o = 0; o < 16; ++o) {
                        float p = LightmapTextureManager.getBrightness(clientWorld.getDimension(), n) * g;
                        float q = LightmapTextureManager.getBrightness(clientWorld.getDimension(), o) * m;
                        float s = q * ((q * 0.6F + 0.4F) * 0.6F + 0.4F);
                        float t = q * (q * q * 0.6F + 0.4F);
                        vector3f2.set(q, s, t);
                        boolean bl = clientWorld.getDimensionEffects().shouldBrightenLighting();
                        float u;
                        Vector3f vector3f4;
                        if (bl) {
                            vector3f2.lerp(new Vector3f(0.99F, 1.12F, 1.0F), 0.25F);
                            clamp(vector3f2);
                        } else {
                            Vector3f vector3f3 = (new Vector3f(vector3f)).mul(p);
                            vector3f2.add(vector3f3);
                            vector3f2.lerp(new Vector3f(0.75F, 0.75F, 0.75F), 0.04F);
                            if (this.renderer.getSkyDarkness(delta) > 0.0F) {
                                u = this.renderer.getSkyDarkness(delta);
                                vector3f4 = (new Vector3f(vector3f2)).mul(0.7F, 0.6F, 0.6F);
                                vector3f2.lerp(vector3f4, u);
                            }
                        }

                        float v;
                        if (l > 0.0F) {
                            v = Math.max(vector3f2.x(), Math.max(vector3f2.y(), vector3f2.z()));
                            if (v < 1.0F) {
                                u = 1.0F / v;
                                vector3f4 = (new Vector3f(vector3f2)).mul(u);
                                vector3f2.lerp(vector3f4, l);
                            }
                        }

                        if (!bl) {
                            if (j > 0.0F) {
                                vector3f2.add(-j, -j, -j);
                            }

                            clamp(vector3f2);
                        }

                        v = Feature.RENDER_FEATURE.getFullBright().getValue() ?
                                100f : this.client.options.getGamma().getValue().floatValue();
                        Vector3f vector3f5 = new Vector3f(this.easeOutQuart(vector3f2.x), this.easeOutQuart(vector3f2.y), this.easeOutQuart(vector3f2.z));
                        vector3f2.lerp(vector3f5, Math.max(0.0F, v - i));
                        vector3f2.lerp(new Vector3f(0.75F, 0.75F, 0.75F), 0.04F);
                        clamp(vector3f2);
                        vector3f2.mul(255.0F);
                        int x = (int)vector3f2.x();
                        int y = (int)vector3f2.y();
                        int z = (int)vector3f2.z();
                        this.image.setColor(o, n, -16777216 | z << 16 | y << 8 | x);
                    }
                }

                this.texture.upload();
                this.client.getProfiler().pop();
            }
        }
    }

    private static void clamp(Vector3f vec) {
        vec.set(MathHelper.clamp(vec.x, 0.0F, 1.0F), MathHelper.clamp(vec.y, 0.0F, 1.0F), MathHelper.clamp(vec.z, 0.0F, 1.0F));
    }
}
