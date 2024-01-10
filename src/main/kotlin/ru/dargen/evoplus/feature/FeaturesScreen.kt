package ru.dargen.evoplus.feature

import net.minecraft.item.Items
import ru.dargen.evoplus.ModLabel
import ru.dargen.evoplus.api.render.Colors
import ru.dargen.evoplus.api.render.Relative
import ru.dargen.evoplus.api.render.Tips
import ru.dargen.evoplus.api.render.animation.Easings
import ru.dargen.evoplus.api.render.animation.animate
import ru.dargen.evoplus.api.render.context.Overlay.ScaledResolution
import ru.dargen.evoplus.api.render.context.ScreenContext
import ru.dargen.evoplus.api.render.context.screen
import ru.dargen.evoplus.api.render.node.*
import ru.dargen.evoplus.api.render.node.box.hbox
import ru.dargen.evoplus.api.render.node.box.vbox
import ru.dargen.evoplus.api.render.node.scroll.hScrollView
import ru.dargen.evoplus.api.schduler.async
import ru.dargen.evoplus.util.math.v3
import ru.dargen.evoplus.util.minecraft.itemStack
import kotlin.math.sign

object FeaturesScreen {

    var SelectedFeature = Features.List.first()

    fun open() = screen("features") features@ {
        val label = +text(ModLabel) {
            scale = v3(1.7, 1.7, 1.7)
            position = v3(-5.0, 5.0)

            origin = Relative.LeftTop
            align = Relative.RightTop
        }
        val selectionLabel = +text(SelectedFeature.name) {
            position = v3(5.0, 5.0)
            scale = v3(1.7, 1.7, 1.7)

            origin = Relative.RightTop
        }

        val box = +vbox box@{
            align = Relative.CenterTop
            origin = Relative.CenterBottom

            space = 1.0

            var settingsBox: Node = DummyNode
            val selector = +hScrollView {
                box.color = Colors.TransparentBlack

                Features.List.forEach {
                    addElements(hbox {
                        dependSizeY = false
                        +item(it.icon)
                        +text(it.name)
//                        postRender { matrices , _ ->
//                            if (isHovered && it.description.isNotEmpty()) {
//                                drawTip(matrices, *it.description)
//                            }
//                        }
                        preRender { _, _ ->
                            color = if (SelectedFeature != it) Colors.Primary else Colors.Primary.darker()
                        }
                        leftClick { _, state ->
                            if (state && isHovered && it != SelectedFeature) {
                                val slide = (Features.List.indexOf(SelectedFeature) - Features.List.indexOf(it)).sign
                                SelectedFeature = it

                                val newSettings = SelectedFeature.screenSection
                                newSettings.translation = v3(x = ScaledResolution.x * -slide)
                                settingsBox + newSettings

                                box.animate("switch", .2, Easings.BackOut) {
                                    val oldSettings = settingsBox.children.first()
                                    oldSettings.translation = !newSettings.translation
                                    newSettings.translation = v3()

                                    selectionLabel.origin = Relative.RightTop

                                    next("show-label", .2, Easings.BackOut) {
                                        selectionLabel.text = it.name
                                        selectionLabel.origin = Relative.LeftTop
                                    }

                                    after(true) { settingsBox - oldSettings }
                                }
                            }
                        }
                    })
                }

                addElements(hbox {
                    dependSizeY = false
                    color = Colors.Primary

                    +item(itemStack(Items.COMMAND_BLOCK))
                    +text("Виджеты")

                    postRender { matrices , _ ->
                        if (isHovered) {
                            Tips.draw(matrices, "В этом разделе мы можете редактировать", "позиции и размеры виджетов.")
                        }
                    }

                    leftClick { _, state ->
                        if (isHovered && state) {
                            screen("features-widgets") {
                                transparent = true
                            }.open()
                        }
                    }
                })
            }

            settingsBox = +delegate {
                +SelectedFeature.screenSection
            }

            resize {
                selector.size = v3(ScaledResolution.x * .7, 35.0, .0)
                settingsBox.size = ScaledResolution.times(.7, .7, .0).minus(.0, selector.size.y, .0)
            }
        }

        display {
            animate("move", .7, Easings.ElasticOut) {
                label.origin = Relative.RightTop
                selectionLabel.origin = Relative.LeftTop

                box.align = Relative.Center
                box.origin = Relative.Center

                color = Colors.TransparentBlack
            }
        }

        destroy { async(Features::saveSettings) }
    }.openIfNoScreen()

}

val isWidgetEditor get() = ScreenContext.current()?.id == "features-widgets"