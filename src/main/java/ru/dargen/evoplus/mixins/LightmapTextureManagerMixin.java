package ru.dargen.evoplus.mixins;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.world.World;
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
    private float field_21528;

    @Shadow
    protected abstract float getBrightness(World world, int i);

    @Shadow
    @Final
    private GameRenderer renderer;

    @Shadow
    protected abstract float method_23795(float f);

    @Shadow
    @Final
    private NativeImage image;

    @Shadow
    @Final
    private NativeImageBackedTexture texture;

    @Inject(at = @At("HEAD"), method = "update")
    public void update(float delta, CallbackInfo ci) {
        if (dirty) {
            dirty = false;
            client.getProfiler().push("lightTex");
            ClientWorld clientWorld = this.client.world;
            if (clientWorld != null) {
                float f = clientWorld.method_23783(1.0F);
                float h;
                if (clientWorld.getLightningTicksLeft() > 0) {
                    h = 1.0F;
                } else {
                    h = f * 0.95F + 0.05F;
                }

                float i = this.client.player.getUnderwaterVisibility();
                float l;
                if (this.client.player.hasStatusEffect(StatusEffects.NIGHT_VISION)) {
                    l = GameRenderer.getNightVisionStrength(this.client.player, delta);
                } else if (i > 0.0F && this.client.player.hasStatusEffect(StatusEffects.CONDUIT_POWER)) {
                    l = i;
                } else {
                    l = 0.0F;
                }

                Vector3f vector3f = new Vector3f(f, f, 1.0F);
                vector3f.lerp(new Vector3f(1.0F, 1.0F, 1.0F), 0.35F);
                float m = field_21528 + 1.5F;
                Vector3f vector3f2 = new Vector3f();

                for (int n = 0; n < 16; ++n) {
                    for (int o = 0; o < 16; ++o) {
                        float p = getBrightness(clientWorld, n) * h;
                        float q = getBrightness(clientWorld, o) * m;
                        float s = q * ((q * 0.6F + 0.4F) * 0.6F + 0.4F);
                        float t = q * (q * q * 0.6F + 0.4F);
                        vector3f2.set(q, s, t);
                        float w;
                        Vector3f vector3f5;
                        if (clientWorld.getSkyProperties().shouldBrightenLighting()) {
                            vector3f2.lerp(new Vector3f(0.99F, 1.12F, 1.0F), 0.25F);
                        } else {
                            Vector3f vector3f3 = vector3f.copy();
                            vector3f3.scale(p);
                            vector3f2.add(vector3f3);
                            vector3f2.lerp(new Vector3f(0.75F, 0.75F, 0.75F), 0.04F);
                            if (renderer.getSkyDarkness(delta) > 0.0F) {
                                w = renderer.getSkyDarkness(delta);
                                vector3f5 = vector3f2.copy();
                                vector3f5.multiplyComponentwise(0.7F, 0.6F, 0.6F);
                                vector3f2.lerp(vector3f5, w);
                            }
                        }

                        vector3f2.clamp(0.0F, 1.0F);
                        float v;
                        if (l > 0.0F) {
                            v = Math.max(vector3f2.getX(), Math.max(vector3f2.getY(), vector3f2.getZ()));
                            if (v < 1.0F) {
                                w = 1.0F / v;
                                vector3f5 = vector3f2.copy();
                                vector3f5.scale(w);
                                vector3f2.lerp(vector3f5, l);
                            }
                        }

                        v = Feature.RENDER_FEATURE.getFullBright().getValue() ?
                                100f : (float) this.client.options.gamma;
                        Vector3f vector3f6 = vector3f2.copy();
                        vector3f6.modify(this::method_23795);
                        vector3f2.lerp(vector3f6, v);
                        vector3f2.lerp(new Vector3f(0.75F, 0.75F, 0.75F), 0.04F);
                        vector3f2.clamp(0.0F, 1.0F);
                        vector3f2.scale(255.0F);
                        int z = (int) vector3f2.getX();
                        int aa = (int) vector3f2.getY();
                        int ab = (int) vector3f2.getZ();
                        image.setPixelColor(o, n, -16777216 | ab << 16 | aa << 8 | z);
                    }
                }

                texture.upload();
                this.client.getProfiler().pop();
            }
        }
    }
}
