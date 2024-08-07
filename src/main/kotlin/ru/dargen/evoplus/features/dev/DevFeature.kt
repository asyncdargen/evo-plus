package ru.dargen.evoplus.features.dev

import net.minecraft.item.Items
import ru.dargen.evoplus.api.render.context.Overlay
import ru.dargen.evoplus.api.render.context.RenderContext
import ru.dargen.evoplus.api.render.context.ScreenContext
import ru.dargen.evoplus.api.render.context.WorldContext
import ru.dargen.evoplus.api.render.node.Node
import ru.dargen.evoplus.api.render.node.text
import ru.dargen.evoplus.api.render.node.tick
import ru.dargen.evoplus.feature.Feature
import ru.dargen.evoplus.util.selector.toSelector

object DevFeature : Feature("dev-env", "DevEnv", Items.COMMAND_BLOCK) {

    val NodeDebugMode by widgets.switcher(
        "Тип вывода компонентов",
        NodeDebugModeType.entries.toSelector(),
        nameMapper = { it?.displayName ?: "null" })
    val NodeDebugWidget by widgets.widget("Вывод компонентов", "node-debug") {
        +text {
            tick {
                text =
                    mapOf(
                        "Overlay" to Overlay,
                        "World" to WorldContext,
                        "Screen" to ScreenContext.current()
                    ).map { (name, ctx) ->
                        "$name:\n ${ctx?.let(NodeDebugMode.totalizer)?.entries?.joinToString("\n ") { "${it.key.simpleName}: ${it.value}" } ?: ""}"
                    }.joinToString("\n")

            }
        }
    }

    init {

    }


    enum class NodeDebugModeType(val displayName: String, val totalizer: RenderContext.() -> Map<Class<*>, Int>) {

        TOTAL("Древо компонентов", {
            val summary = hashMapOf<Class<*>, Int>()
            fun Node.sumNodes() {
                summary.merge(javaClass, 1, Int::plus)
                children.forEach { it.sumNodes() }
            }

            children.forEach { it.sumNodes() }

            summary
        }),
        CONTEXTS("Компоненты контекстов", { children.groupBy { it.javaClass }.mapValues { it.value.size } })

    }

}