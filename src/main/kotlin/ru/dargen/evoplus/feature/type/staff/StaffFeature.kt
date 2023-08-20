package ru.dargen.evoplus.feature.type.staff

import net.minecraft.item.Items
import ru.dargen.evoplus.feature.Feature
import ru.dargen.evoplus.util.minecraft.customItem

object StaffFeature : Feature("staff", "Посохи", customItem(Items.WOODEN_HOE, 4)) {

    var EnabledTimer by settings.boolean("enabled-timer", "Отображение таймера", true)
    var ReadyNotify by settings.boolean("ready-notify", "Уведомление при окончании задержки", true)
    var ReadyMessage by settings.boolean("ready-message", "Сообщение при окончании задержки", true)

}