package ru.dargen.evoplus.feature.type.clan

import ru.dargen.evoplus.util.minecraft.uncolored

data class ClanMember(val name: String, val rank: Rank, val level: Int, val points: Int, val online: Boolean) {

    override fun toString() = "$rank ${if (online) "§a" else "§c"}$name §8[§6$level§8]: §e$points к.о"

    companion object {

        private val MenuPattern =
            "^(?:\\[(Гл\\.|М\\.)] |)(\\w+) \\[Ур\\. (\\d+)] \\(([-+])\\) - (\\d+) клановых очков\$".toRegex()

        fun fromLine(line: String) = MenuPattern.find(line.uncolored().trim())?.run {
            ClanMember(
                groupValues[2], Rank[groupValues[1]], groupValues[3].toInt(),
                groupValues[5].toInt(), groupValues[4] == "+"
            )
        }

    }

    enum class Rank(val prefix: String = "", val color: String = "") {

        MEMBER, MODER("М.", "§a"), OWNER("Гл.", "§6");

        override fun toString() = if (prefix.isNotBlank()) "§7[$color$prefix§7]" else ""

        companion object {

            operator fun get(prefix: String?) = entries.first { it.prefix == prefix }

        }

    }

    enum class Comparator(val displayName: String, val comparator: kotlin.Comparator<ClanMember>) {

        RANK("Ранг", compareBy(ClanMember::rank).reversed()),
        LEVEL("Уровень", compareBy(ClanMember::level).reversed()),
        ONLINE("Онлайн", compareBy(ClanMember::online).reversed()),
        POINTS("К.О", compareBy(ClanMember::points).reversed()),

    }

}