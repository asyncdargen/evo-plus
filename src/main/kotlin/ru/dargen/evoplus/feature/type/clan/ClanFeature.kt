package ru.dargen.evoplus.feature.type.clan

import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.text.Text
import ru.dargen.evoplus.api.event.chat.ChatReceiveEvent
import ru.dargen.evoplus.api.event.inventory.InventoryClickEvent
import ru.dargen.evoplus.api.event.inventory.InventoryFillEvent
import ru.dargen.evoplus.api.event.inventory.InventorySlotUpdateEvent
import ru.dargen.evoplus.api.event.on
import ru.dargen.evoplus.feature.Feature
import ru.dargen.evoplus.feature.type.boss.BossFeature
import ru.dargen.evoplus.feature.type.boss.BossType
import ru.dargen.evoplus.util.minecraft.*

object ClanFeature : Feature("clan", "Клан", Items.SHIELD) {

    private val MenuPointsPattern = "^[\\W\\S]+: (\\d+)\$".toRegex()
    private val BossCapturePattern =
        "\\[Клан] Клан (\\S+) начал захват вашего босса ([\\s\\S]+)\\. Защитите его\\.".toRegex()

    val BossCaptureNotify by settings.boolean("Уведомление о захвате вашего босса", true)
    val MenuSorting by settings.boolean("Сортировка участников в меню", true)

    var ClanMembersComparator = ClanMember.Comparator.RANK

    init {
        on<InventoryClickEvent> {
            if (MenuSorting && slot == 22
                && CurrentScreen?.title?.string == "Информация о клане"
            ) ClanMembersComparator = ClanMember.Comparator.entries[(ClanMembersComparator.ordinal + 1)
                .let { if (it >= ClanMember.Comparator.entries.size) 0 else it }]
        }

        on<InventoryFillEvent> {
            if (MenuSorting && openEvent?.title?.string == "Информация о клане") {
                contents.firstOrNull { it.name?.string?.uncolored()?.startsWith("Участники клана") == true }
                    ?.transformMembersListItem()
            }
        }
        on<InventorySlotUpdateEvent> {
            if (MenuSorting && slot == 22 && openEvent?.title?.string == "Информация о клане") {
                stack.transformMembersListItem()
                printMessage("2edit")
            }
        }

        on<ChatReceiveEvent> {
            val text = text.uncolored()

            if (BossCaptureNotify) {
                BossCapturePattern.find(text)?.run {
                    val clan = groupValues[1]
                    val bossName = groupValues[2]
                    val bossType = BossType[bossName]!!

                    BossFeature.notify(
                        bossType,
                        "Клан §6$clan§f пытается захватить",
                        "вашего босса ${bossType.displayName}"
                    )
                }
            }
        }
    }

    private fun ItemStack.transformMembersListItem() {
        if ("Сортировка" in displayName!!.string) return
        val members = lore.map(Text::getString).mapNotNull(ClanMember::fromLine)

        printMessage("$members")

        val onlinePoints = (MenuPointsPattern.find(lore[lore.size - 2].string)?.groupValues?.get(1) ?: 0)
        val points = (MenuPointsPattern.find(lore[lore.size - 3].string)?.groupValues?.get(1) ?: 0)

        displayName = "§eУчастники (${members.size}) | Сортировка: ${ClanMembersComparator.displayName}".asText()

        lore = buildList {
            add(Text.empty())
            members.sortedWith(ClanMembersComparator.comparator)
                .map(ClanMember::toString)
                .map(String::asText)
                .forEach(this::add)
            add(Text.empty())
            add("§7Клановый опыт: §a$onlinePoints§7/§2$points".asText())
            add(Text.empty())
        }
    }

}