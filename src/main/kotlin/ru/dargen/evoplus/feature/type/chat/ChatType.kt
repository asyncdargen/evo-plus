package ru.dargen.evoplus.feature.type.chat

import ru.dargen.evoplus.util.kotlin.KotlinOpens

@KotlinOpens
enum class ChatType(val displayName: String, val pattern: Regex? = null, val prefix: String = "") {

    ALL("Все", null),
    PM("ЛС", "^ЛС".toRegex()),
    CLAN("Клан", "^\\[Клан]".toRegex(), "@"),
    GLOBAL("Глоб.", "^Ⓖ".toRegex(), "!"),
    LOCAL("Локал.", "^Ⓛ".toRegex())

}