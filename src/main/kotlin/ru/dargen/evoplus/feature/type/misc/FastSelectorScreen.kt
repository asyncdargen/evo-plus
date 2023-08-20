package ru.dargen.evoplus.feature.type.misc

import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.Item
import net.minecraft.item.Items
import ru.dargen.evoplus.api.keybind.Keybinds
import ru.dargen.evoplus.api.keybind.on
import ru.dargen.evoplus.api.render.Relative
import ru.dargen.evoplus.api.render.animation.animate
import ru.dargen.evoplus.api.render.context.screen
import ru.dargen.evoplus.api.render.node.*
import ru.dargen.evoplus.api.render.node.box.box
import ru.dargen.evoplus.util.kotlin.safeCast
import ru.dargen.evoplus.util.math.v3
import ru.dargen.evoplus.util.minecraft.*

object FastSelectorScreen {

    init {
        Keybinds.FastSelector.on {
            if (Client.currentScreen != null || !MiscFeature.FastSelector) return@on

            screen {
                isPassEvents = true

                val label = +text {
                    align = Relative.Center
                    origin = Relative.CenterBottom
                    translation = v3(y = -90.0)
                    scale = v3(2.0, 2.0, 2.0)
                }

                +box {

                    fun commandItem(
                        type: Item, data: Int = 0,
                        command: String, title: String
                    ) = item(customItem(type, data)) {
                        origin = Relative.Center
                        align = Relative.Center

                        hover { _, state ->
                            animate("hover", .07) {
                                scale = if (state) v3(3.1, 3.1, 3.1) else v3(2.7, 2.7, 2.7)
                            }
                            if (state) label.text = title
                        }
                        typeKey { key -> if (key == -1 && isHovered) sendCommand(command) }
                    }

                    fun interactItem(
                        type: Item, data: Int = 0,
                        command: String, title: String
                    ) = TargetEntity.takeIf { MiscFeature.FastInteraction }
                        ?.safeCast<PlayerEntity>()
                        ?.takeIf { '§' !in it.gameProfile.name && it.gameProfile.name.isNotBlank()}
                        ?.run {
                            val name = gameProfile.name
                            commandItem(type, data, command.format(name), title.format(name)).apply {
                                +item(itemStack(Items.PLAYER_HEAD)) {
                                    translation = v3(z = 1000.0)
                                    scale = v3(.5, .5, .5)
                                    align = Relative.RightBottom
                                    origin = Relative.RightBottom
                                }
                            }
                        } ?: DummyNode

                    val items = listOf(
                        +commandItem(Items.EMERALD, 0, "shop", "Магазин"),
                        +commandItem(Items.ANVIL, 0, "upgrades", "Прокачки"),
                        +commandItem(Items.COMMAND_BLOCK, 54, "runesbag", "Руны"),
                        +commandItem(Items.BONE, 0, "collections", "Коллекции"),
                        +commandItem(Items.SPIDER_SPAWN_EGG, 0, "pets", "Питомцы"),

                        +interactItem(Items.BOOK, 0, "statistics %s", "Статистика %s"),
                        +commandItem(Items.COMPASS, 0, "spawn", "Спавн"),
                        +commandItem(Items.DIAMOND_PICKAXE, 0, "mine", "Шахты"),
                        +commandItem(Items.NETHER_STAR, 0, "menu", "Меню"),
                        +commandItem(Items.ZOMBIE_HEAD, 0, "bosses", "Боссы"),
                        +commandItem(Items.CLOCK, 0, "hide", "Скрыть игроков"),
                        +interactItem(Items.GOLD_INGOT, 0, "trade %s", "Трейд с %s"),

                        +commandItem(Items.FISHING_ROD, 0, "warp fish", "Рыбалка"),
                        +commandItem(Items.DIAMOND, 0, "diamondpass", "DiamondPass"),
                        +commandItem(Items.COMMAND_BLOCK, 1, "backpack", "Рюкзак"),
                        +commandItem(Items.EXPERIENCE_BOTTLE, 0, "achievements", "Достижения"),
                        +commandItem(Items.COMMAND_BLOCK, 77, "warp mine", "Шахтеры"),
                    )

                    align = Relative.Center
                    origin = Relative.Center

                    animate("stage", .15) {
                        var index = 0
                        for (y in -1..1) {
                            for (x in if (y == 0) -3..3 else -2..2) {
                                (items.getOrNull(index++) ?: continue).apply {
                                    translation = v3(x * 48.0, y * 56.0)
                                    scale = v3(2.7, 2.7, 2.7)
                                }
                            }
                        }
                    }

                    destroy { items.firstOrNull(Node::isHovered)?.changeKey(-1, true) }
                }

                releaseKey(Keybinds.FastSelector.defaultKey.code) { close() }
            }.open()
        }
    }
}