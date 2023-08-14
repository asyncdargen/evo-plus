package ru.dargen.evoplus.feature.type.misc

import net.minecraft.item.Item
import net.minecraft.item.Items
import ru.dargen.evoplus.api.keybind.Keybinds
import ru.dargen.evoplus.api.keybind.on
import ru.dargen.evoplus.api.render.Colors
import ru.dargen.evoplus.api.render.Relative
import ru.dargen.evoplus.api.render.animation.animate
import ru.dargen.evoplus.api.render.context.screen
import ru.dargen.evoplus.api.render.node.*
import ru.dargen.evoplus.api.render.node.box.box
import ru.dargen.evoplus.util.alpha
import ru.dargen.evoplus.util.math.v3
import ru.dargen.evoplus.util.minecraft.Client
import ru.dargen.evoplus.util.minecraft.customItem
import ru.dargen.evoplus.util.minecraft.sendCommand

object FastSelectorScreen {

    init {
        Keybinds.FastSelector.on {
            if (Client.currentScreen != null || !MiscFeature.FastSelector) return@on

            screen {
                isPassEvents = true

                +box {
                    val label = +!text {
                        origin = Relative.CenterBottom
                        align = Relative.CenterTop

                        scale = v3(2.5, 2.5, 2.5)
                    }
                    fun commandItem(
                        type: Item, data: Int = 0,
                        command: String, title: String
                    ) = item(customItem(type, data)) {
                        origin = Relative.Center
                        align = Relative.Center

                        postRender { matrices, _ ->
                            matrices.translate(0f, 0f, 150f) //bc item render translates z to 100f
                            if (isHovered) drawTip(matrices, title, color = Colors.Black.alpha(0))
                        }
                        hover { _, state ->
                            animate("hover", .07) {
                                scale = if (state) v3(3.1, 3.1, 3.1) else v3(2.7, 2.7, 2.7)
                            }
                            label.text = if (state) title else ""
                        }
                        typeKey { key -> if (key == -1 && isHovered) sendCommand(command) }
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

                    color = Colors.TransparentBlack
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

                    destroy { items.firstOrNull(Node::isHovered)?.changeKey(-1, true) }
                }

                releaseKey(Keybinds.FastSelector.defaultKey.code) { close() }
            }.open()
        }
    }

}