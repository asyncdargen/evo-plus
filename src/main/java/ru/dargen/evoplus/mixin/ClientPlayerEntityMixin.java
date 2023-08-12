package ru.dargen.evoplus.mixin;

import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.dargen.evoplus.api.event.EventBus;
import ru.dargen.evoplus.api.event.chat.ChatReceiveEvent;
import ru.dargen.evoplus.api.event.chat.OverlayEvent;

@Mixin(ClientPlayerEntity.class)
public abstract class ClientPlayerEntityMixin {


}
