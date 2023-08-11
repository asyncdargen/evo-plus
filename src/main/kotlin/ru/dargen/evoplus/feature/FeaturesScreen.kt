package ru.dargen.evoplus.feature

import net.minecraft.item.ItemStack
import ru.dargen.evoplus.ModLabel
import ru.dargen.evoplus.api.render.Colors
import ru.dargen.evoplus.api.render.Relative
import ru.dargen.evoplus.api.render.animation.Easings
import ru.dargen.evoplus.api.render.animation.animate
import ru.dargen.evoplus.api.render.context.OverlayContext.Resolution
import ru.dargen.evoplus.api.render.context.ScreenContext
import ru.dargen.evoplus.api.render.context.screen
import ru.dargen.evoplus.api.render.node.*
import ru.dargen.evoplus.api.render.node.box.hbox
import ru.dargen.evoplus.api.render.node.box.vbox
import ru.dargen.evoplus.api.render.node.input.button
import ru.dargen.evoplus.api.render.node.scroll.hScrollView
import ru.dargen.evoplus.util.alpha
import ru.dargen.evoplus.util.math.v3
import kotlin.math.sign

object FeaturesScreen {

    val BackgroundColor = Colors.Black.alpha(.3)
    var SelectedFeature = Features.List.first()

    fun open() = screen("features") {
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
                box.color = BackgroundColor
                Features.List.forEach {
                    addElements(hbox {
                        color = Colors.Primary
                        +item(ItemStack(it.icon))
                        +text(it.name)
                        leftClick { _, state ->
                            if (state && isHovered && it != SelectedFeature) {
                                val slide = (Features.List.indexOf(SelectedFeature) - Features.List.indexOf(it)).sign
                                SelectedFeature = it

                                val newSettings = SelectedFeature.settingsSection
                                newSettings.translation = v3(x = Resolution.x * -slide)
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
                addElements(button("Виджеты") {
                    on { screen("features-widgets") { transparent  = true }.open() }
                })
            }

            settingsBox = +delegate {
                +SelectedFeature.settingsSection
            }

            resize {
                selector.size = v3(Resolution.x * .7, 35.0, .0)
                settingsBox.size = Resolution.times(.7, .7, .0).minus(.0, selector.size.y, .0)
            }
        }

        display {
            animate("move", .7, Easings.ElasticOut) {
                label.origin = Relative.RightTop
                selectionLabel.origin = Relative.LeftTop

                box.align = Relative.Center
                box.origin = Relative.Center

                color = BackgroundColor
            }
        }

        destroy {
            Features.saveSettings()
        }
    }.open()

    fun isInWidgetEditor() = ScreenContext.current()?.id == "features-widgets"

}