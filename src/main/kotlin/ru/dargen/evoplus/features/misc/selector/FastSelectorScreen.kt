package ru.dargen.evoplus.features.misc.selector

import net.minecraft.entity.player.PlayerEntity
import ru.dargen.evoplus.api.keybind.Keybinds
import ru.dargen.evoplus.api.keybind.boundKey
import ru.dargen.evoplus.api.render.Colors
import ru.dargen.evoplus.api.render.Relative
import ru.dargen.evoplus.api.render.context.screen
import ru.dargen.evoplus.api.render.node.*
import ru.dargen.evoplus.api.render.node.box.hbox
import ru.dargen.evoplus.api.render.node.box.vbox
import ru.dargen.evoplus.api.render.node.input.button
import ru.dargen.evoplus.api.render.node.input.symbolButton
import ru.dargen.evoplus.api.scheduler.after
import ru.dargen.evoplus.feature.FeaturesScreen
import ru.dargen.evoplus.util.kotlin.safeCast
import ru.dargen.evoplus.util.math.Vector3
import ru.dargen.evoplus.util.math.scale
import ru.dargen.evoplus.util.math.v3
import ru.dargen.evoplus.util.minecraft.TargetEntity
import ru.dargen.evoplus.util.minecraft.sendCommand
import ru.dargen.evoplus.util.render.alpha

object FastSelectorScreen {

    fun open(setting: Boolean = false) {
        val target = TargetEntity?.safeCast<PlayerEntity>()
            ?.takeIf { '§' !in it.gameProfile.name && it.gameProfile.name.isNotBlank() }
            ?.gameProfile?.name ?: ""

        fun String.interaction() = replace("{player}", target)

        screen {
            if (setting) color = Colors.TransparentBlack

            isPassEvents = !setting

            if (!setting) +button("Настройка") {
                origin = Relative.RightTop
                align = Relative.RightTop

                on { open(true) }
            }

            val label = text("") {
                align = Relative.CenterTop
                origin = Relative.CenterBottom
                translation = v3(y = -10.0)
                scale = scale(2.0, 2.0)
            }
            val items = FastSelectorSetting.value.map { line ->
                line.filter { setting || !it.isInteraction || target.isNotBlank() }.map {
                    it.screenItem.apply {
                        if (!setting) typeKey { key ->
                            if (key == -1 && isHovered) {
                                sendCommand(it.command.interaction())
                                true
                            } else false
                        } else hoverHandlers.clear()

                        hoverIn { _ -> label.text = it.name.interaction() }
                    }
                }
            }

            +vbox {
                +!label

                childrenRelative = .5
                space = .0
                indent = v3()

                align = Relative.Center
                origin = Relative.Center

                items.forEachIndexed { lineIndex, line ->
                    +hbox {
                        space = .0
                        indent = v3()

                        line.forEachIndexed { index, item ->
                            +item
                            if (setting) item.asSetting(lineIndex, index, FastSelectorSetting.value[lineIndex][index])
                        }

                        recompose()
                    }
                }


                destroy {
                    if (setting) after { FeaturesScreen.open() }
                    else items.flatten().firstOrNull(Node::isHovered)?.changeKey(-1, true)
                }

                recompose()
            }

            if (!setting) releaseKey(Keybinds.FastSelector.boundKey.code) { close(); true }
        }.open()
    }

    private fun Node.asSetting(line: Int, index: Int, item: SelectorItem) {
        fun adder(relative: Vector3, lineDiff: Int, indexDiff: Int, moveSymbol: String) {
            +(if (lineDiff == 0) vbox() else hbox()).apply {
                space = .5
                indent = v3()

                origin = relative
                align = relative

                translation = v3(z = 300.0)

                +symbolButton("§a+") {
                    buttonColor = Colors.TransparentBlack

                    on {
                        val item = if (lineDiff == 0) FastSelectorSetting.addItem(line, index + indexDiff)
                        else FastSelectorSetting.addItem(line, line + lineDiff, index)
                        FastSelectorSettingScreen.open(item)
                    }
                }
                +symbolButton("§a$moveSymbol") {
                    buttonColor = Colors.TransparentBlack

                    on {
                        FastSelectorSetting.removeItem(line, index, false)
                        FastSelectorSetting.addItem(line + lineDiff, index + indexDiff, item)
                        FastSelectorSetting.reduceEmptyLines()
                        open(true)
                    }
                }

                enabled = false
                this@asSetting.hover { mouse, state -> this@apply.enabled = state }
            }
        }

        +hbox {
            space = .5
            indent = v3()

            align = Relative.Center
            origin = Relative.Center

            translation = v3(z = 300.0)

            +symbolButton("✐") {
                buttonColor = Colors.TransparentBlack
                on { FastSelectorSettingScreen.open(item) }

                enabled = false
                this@asSetting.hover { mouse, state -> this@symbolButton.enabled = state }
            }
            +symbolButton("§c✖") {
                buttonColor = Colors.TransparentBlack
                var confirmed = false
                on {
                    if (confirmed) {
                        FastSelectorSetting.removeItem(line, index)
                        if (FastSelectorSetting.items == 0) FastSelectorSetting.addItem(0, 0)
                        open(true)
                    } else {
                        confirmed = true
                        buttonColor = Colors.Red.alpha(.4)
                    }
                }

                enabled = false
                this@asSetting.hover { mouse, state -> this@symbolButton.enabled = state }
            }
            adder(Relative.RightCenter, 0, 1, "▶")
            adder(Relative.LeftCenter, 0, -1, "◀")
            adder(Relative.CenterTop, -1, 0, "▲")
            adder(Relative.CenterBottom, 1, 0, "▼")
        }
    }

}