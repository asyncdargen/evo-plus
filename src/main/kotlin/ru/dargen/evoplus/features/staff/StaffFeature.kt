package ru.dargen.evoplus.features.staff

import net.minecraft.item.Items
import pro.diamondworld.protocol.packet.staff.StaffTimers
import ru.dargen.evoplus.api.schduler.scheduleEvery
import ru.dargen.evoplus.feature.Feature
import ru.dargen.evoplus.features.misc.Notifies
import ru.dargen.evoplus.protocol.listen
import ru.dargen.evoplus.protocol.registry.StaffType
import ru.dargen.evoplus.util.collection.concurrentHashMapOf
import ru.dargen.evoplus.util.currentMillis
import ru.dargen.evoplus.util.minecraft.customItem
import ru.dargen.evoplus.util.minecraft.printMessage

object StaffFeature : Feature("staff", "Посохи", customItem(Items.WOODEN_HOE, 4)) {

    val Staffs = concurrentHashMapOf<Int, Long>()

    val TimerWidget by widgets.widget("Таймер посохов", "staff-timer", widget = StaffTimerWidget)

    var ReadyNotify by settings.boolean("Уведомление при окончании задержки", true)
    var ReadyMessage by settings.boolean("Сообщение при окончании задержки", true)

    init {
        listen<StaffTimers> {
            it.timers
                .filterValues { it > 2000 }
                .forEach { (id, timestamp) -> Staffs[id] = currentMillis + timestamp }
        }
        scheduleEvery(period = 2) {
            updateStaffs()

            StaffTimerWidget.update()
        }
    }

    private fun updateStaffs() {
        Staffs.forEach { (id, timestamp) ->
            val type = StaffType.byOrdinal(id) ?: return@forEach
            val remainTime = timestamp - currentMillis

            if (remainTime in 0..1000) {
                if (ReadyNotify) Notifies.showText("${type.displayName} §aготов")
                if (ReadyMessage) printMessage("${type.displayName} §aготов")
                Staffs.remove(id)
            }

            if (remainTime < 0) Staffs.remove(id)
        }
    }

}