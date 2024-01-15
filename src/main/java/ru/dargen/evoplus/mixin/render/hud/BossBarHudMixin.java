package ru.dargen.evoplus.mixin.render.hud;

import net.minecraft.client.gui.hud.BossBarHud;
import net.minecraft.client.gui.hud.ClientBossBar;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import ru.dargen.evoplus.api.event.EventBus;
import ru.dargen.evoplus.api.event.bossbar.BossBarRenderEvent;

@Mixin(BossBarHud.class)
public class BossBarHudMixin {

    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/ClientBossBar;getName()Lnet/minecraft/text/Text;"))
    public Text onAsFormattedString(ClientBossBar clientBossBar) {
        EventBus.INSTANCE.fire(new BossBarRenderEvent(clientBossBar));
        return clientBossBar.getName();
    }
}
