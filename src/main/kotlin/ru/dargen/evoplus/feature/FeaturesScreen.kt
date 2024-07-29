package ru.dargen.evoplus.feature

import ru.dargen.evoplus.ModLabel
import ru.dargen.evoplus.api.render.Colors
import ru.dargen.evoplus.api.render.Relative
import ru.dargen.evoplus.api.render.animation.Easings
import ru.dargen.evoplus.api.render.animation.animate
import ru.dargen.evoplus.api.render.context.Overlay.ScaledResolution
import ru.dargen.evoplus.api.render.context.ScreenContext
import ru.dargen.evoplus.api.render.context.screen
import ru.dargen.evoplus.api.render.node.*
import ru.dargen.evoplus.api.render.node.box.hbox
import ru.dargen.evoplus.api.render.node.box.vbox
import ru.dargen.evoplus.api.render.node.input.button
import ru.dargen.evoplus.api.render.node.input.input
import ru.dargen.evoplus.api.render.node.scroll.vScrollView
import ru.dargen.evoplus.api.scheduler.async
import ru.dargen.evoplus.util.math.v3
import kotlin.math.sign

object FeaturesScreen {

    var SelectedFeature = Features.List.first()

    fun open() = screen("features") features@{
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

        val box = +hbox box@{
            align = Relative.CenterTop
            origin = Relative.CenterBottom

            space = 1.0

            var search: Node = DummyNode
            var settingsBox: Node = DummyNode
            val selectorBox = +vbox {
                indent = v3()
                space = 1.0
                search = +input {
                    size = v3(150.0, 20.0)
                    color = Colors.TransparentBlack
                    prompt.text = "Поиск"
                }
                val selector = +vScrollView {
                    box.color = Colors.TransparentBlack
                    size = v3(150.0)

                    Features.List.forEach {
                        addElements(hbox {
                            dependSizeX = false
                            size = v3(130.0, 20.0)
                            +item(it.icon)
                            +text(it.name)
                            preRender { _, _ ->
                                color = if (SelectedFeature != it) Colors.Primary else Colors.Primary.darker()
                            }
                            leftClick { _, state ->
                                if (state && isHovered && it != SelectedFeature) {
                                    val slide =
                                        (Features.List.indexOf(SelectedFeature) - Features.List.indexOf(it)).sign
                                    SelectedFeature = it

                                    val newSettings = SelectedFeature.screenSection
                                    newSettings.translation = v3(y = ScaledResolution.y * -slide)
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

                    resize {
                        size = v3(size.x, (parent?.size?.y ?: .0) - 21)
                    }
                }
            }

            settingsBox = +delegate {
                +SelectedFeature.screenSection
            }

            resize {
                selectorBox.size = v3(
                    selectorBox.size.x,
                    ScaledResolution.y * .7,
                    .0
                ) //.plus(selector.size.x, .0, .0)// v3(ScaledResolution.x * .7, 35.0, .0)
                settingsBox.size = ScaledResolution.times(.6, .7, .0).minus(selectorBox.size.x, .0, .0)
            }
        }

        +button {
            align = Relative.RightBottom
            origin = Relative.RightBottom

            translation = v3(-3.0, -3.0)
            size = v3(70.0, 20.0)

            this@button.label.text = "Виджеты"

            on {
                screen("features-widgets") {
                    transparent = true
                }.open()
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