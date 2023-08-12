package ru.dargen.evoplus.feature.type

import net.minecraft.item.Item
import net.minecraft.item.Items
import ru.dargen.evoplus.api.keybind.Keybinds
import ru.dargen.evoplus.api.keybind.on
import ru.dargen.evoplus.api.render.Relative
import ru.dargen.evoplus.api.render.animation.animate
import ru.dargen.evoplus.api.render.context.screen
import ru.dargen.evoplus.api.render.node.*
import ru.dargen.evoplus.api.render.node.box.box
import ru.dargen.evoplus.feature.Feature
import ru.dargen.evoplus.feature.FeaturesScreen
import ru.dargen.evoplus.util.customItem
import ru.dargen.evoplus.util.math.v3
import ru.dargen.evoplus.util.sendCommand

object MiscFeature : Feature("misc", "Прочее", Items.REPEATER) {

    var fastSelector by settings.boolean("fast-selector", "Быстрый доступ", true)

    init {
        Keybinds.FastSelector.on {
            if (!fastSelector) return@on

            screen {
                val itemTitle = +text {
                    translation = v3(y = 20.0)
                    scale = v3(2.5, 2.5, 2.5)
                    align = Relative.CenterTop
                    origin = Relative.CenterTop
                }

                fun commandItem(
                    type: Item, data: Int = 0,
                    command: String, title: String
                ) = item(customItem(type, data)) {
                    hover { _, state ->
                        animate("hover", .07) {
                            scale = if (state) v3(3.1, 3.1, 3.1) else v3(2.7, 2.7, 2.7)
                        }
                        if (state) {
                            itemTitle.text = title
                        }
                    }
                    typeKey { key -> if (key == -1 && isHovered) sendCommand(command) }
                    align = Relative.Center
                    origin = Relative.Center
                }

                val items = listOf(
                    +commandItem(Items.EMERALD, 0, "shop", "Магазин"),
                    +commandItem(Items.ANVIL, 0, "upgrades", "Прокачки"),
                    +commandItem(Items.COMMAND_BLOCK, 54, "runesbag", "Руны"),
                    +commandItem(Items.BONE, 0, "collections", "Коллекции"),
                    +commandItem(Items.SPIDER_SPAWN_EGG, 0, "pets", "Питомцы"),

                    +commandItem(Items.BEACON, 0, "spawn", "Спавн"),
                    +commandItem(Items.DIAMOND_PICKAXE, 0, "mine", "Шахты"),
                    +commandItem(Items.NETHER_STAR, 0, "menu", "Меню"),
                    +commandItem(Items.ZOMBIE_HEAD, 0, "bosses", "Боссы"),
                    +commandItem(Items.CLOCK, 0, "hide", "Скрыть игроков"),

                    +commandItem(Items.FISHING_ROD, 0, "warp fish", "Рыбалка"),
                    +commandItem(Items.DIAMOND, 0, "diamondpass", "DiamondPass"),
                    +commandItem(Items.COMMAND_BLOCK, 1, "backpack", "Рюкзак"),
                    +commandItem(Items.EXPERIENCE_BOTTLE, 0, "achievements", "Достижения"),
                    +commandItem(Items.COMMAND_BLOCK, 77, "warp mine", "Шахтеры"),
                )
                +box {
                    color = FeaturesScreen.BackgroundColor
                    align = Relative.Center
                    origin = Relative.Center

                    animate("stage", .15) {
                        var index = 0
                        for (y in -1..1) {
                            for (x in -2..2) {
                                items[index++].apply {
                                    translation = v3(x * 48.0, y * 48.0)
                                    scale = v3(2.7, 2.7, 2.7)
                                }
                            }
                        }
                    }
                }

                releaseKey(Keybinds.FastSelector.defaultKey.code) { close() }
                destroy { items.firstOrNull(Node::isHovered)?.changeKey(-1, true) }
            }.open()
        }
    }

}