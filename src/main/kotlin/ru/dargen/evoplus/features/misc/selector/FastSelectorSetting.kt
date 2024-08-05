package ru.dargen.evoplus.features.misc.selector

import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import net.minecraft.item.Item
import net.minecraft.item.Items
import ru.dargen.evoplus.api.render.node.input.button
import ru.dargen.evoplus.feature.screen.FeatureBaseElement
import ru.dargen.evoplus.feature.settings.Setting
import ru.dargen.evoplus.util.collection.insertAt

object FastSelectorSetting :
    Setting<MutableList<MutableList<SelectorItem>>>("fast-selector-buttons", "Настройка fast-селектора") {

    val items get() = value.sumOf { it.size }
    
    override val settingElement = FeatureBaseElement(name) {
        button("Открыть") {
            on { FastSelectorScreen.open(true) }
        }
    }

    override var value = mutableListOf(
        mutableListOf(
            SelectorItem(Items.EMERALD, 0, "shop", "Магазин"),
            SelectorItem(Items.ANVIL, 0, "upgrades", "Прокачки"),
            SelectorItem(Items.COMMAND_BLOCK, 54, "runesbag", "Руны"),
            SelectorItem(Items.BONE, 0, "collections", "Коллекции"),
            SelectorItem(Items.SPIDER_SPAWN_EGG, 0, "pets", "Питомцы")
        ),
        mutableListOf(
            SelectorItem(Items.BOOK, 0, "statistics {player}", "Статистика {player}"),
            SelectorItem(Items.COMPASS, 0, "spawn", "Спавн"),
            SelectorItem(Items.DIAMOND_PICKAXE, 0, "mine", "Шахты"),
            SelectorItem(Items.NETHER_STAR, 0, "menu", "Меню"),
            SelectorItem(Items.ZOMBIE_HEAD, 0, "bosses", "Боссы"),
            SelectorItem(Items.CLOCK, 0, "hide", "Скрыть игроков"),
            SelectorItem(Items.GOLD_INGOT, 0, "trade {player}", "Трейд с {player}"),
        ),
        mutableListOf(
            SelectorItem(Items.FISHING_ROD, 0, "warp fish", "Рыбалка"),
            SelectorItem(Items.DIAMOND, 0, "diamondpass", "DiamondPass"),
            SelectorItem(Items.COMMAND_BLOCK, 1, "backpack", "Рюкзак"),
            SelectorItem(Items.EXPERIENCE_BOTTLE, 0, "achievements", "Достижения"),
            SelectorItem(Items.COMMAND_BLOCK, 77, "warp mine", "Шахтеры"),
        )
    )

    fun removeItem(line: Int, index: Int, reduceEmptyLine: Boolean = true) = value.getOrNull(line)?.apply {
        removeAt(index)
        if (reduceEmptyLine && isEmpty()) value.removeAt(line)
    }

    fun reduceEmptyLines() = value.removeIf(List<*>::isEmpty)

    fun addItem(
        currentLine: Int, line: Int, index: Int,
        item: SelectorItem = SelectorItem(Items.PAPER, 0, "command", "Название")
    ) = addItem(
        line, if (line !in value.indices) 0
        else (value[line].size * (index / value[currentLine].size.toDouble())).toInt(),
        item
    )

    fun addItem(
        line: Int, index: Int,
        item: SelectorItem = SelectorItem(Items.PAPER, 0, "Название", "command")
    ): SelectorItem {
        if (line !in value.indices) value = value.insertAt(line, mutableListOf()).toMutableList()
        val line = line.coerceAtLeast(0)
        value[line] = value[line].insertAt(index, item).toMutableList()

        return item
    }

    override fun load(element: JsonElement) {
        value = mutableListOf<MutableList<SelectorItem>>().apply {
            element.asJsonArray.forEach { lines ->
                add(mutableListOf<SelectorItem>().apply {
                    lines.asJsonArray.forEach {
                        val item = it.asJsonObject
                        add(
                            SelectorItem(
                                Item.byRawId(item["item"].asInt), item["model-id"].asInt,
                                item["command"].asString, item["name"].asString
                            )
                        )
                    }
                })
            }
        }
    }

    override fun store() = JsonArray().apply {
        value.forEach { line ->
            add(JsonArray().apply {
                line.forEach { item ->
                    add(JsonObject().apply {
                        addProperty("item", Item.getRawId(item.item))
                        addProperty("model-id", item.customModel)
                        addProperty("name", item.name)
                        addProperty("command", item.command)
                    })
                }
            })
        }
    }

}