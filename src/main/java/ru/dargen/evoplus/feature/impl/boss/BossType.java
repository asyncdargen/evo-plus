package ru.dargen.evoplus.feature.impl.boss;

import lombok.Getter;
import ru.dargen.evoplus.util.Util;

@Getter
public enum BossType {

    KRIEGER("Кригер", 15, 1200),
    SLIME("Слизень", 20, 1800),
    STEEL_GUARD("Стальной Страж", 25, 2100),
    NIGHTMARE("Кошмар", 30, 2700),
    TWINS("Близнецы", 35, 7200),
    FIRE_LORD("Повелитель Огня", 40, 3600),
    SPIDER("Паучиха", 45, 10800),
    DROWNED("Утопленник", 50, 5400),
    MAGICIAN("Колдун", 55, 14400),
    DIE("Смерть", 60, 7200),
    RIDER("Наездник", 65, 14400),
    OUTLAW("Разбойник", 70, 9000),
    MAGMA_SLIME("Лавовый куб", 75, 21600),
    WARDEN("Варден", 80, 25200),
    GHOST_HUNTER("Призрачный охотник", 90, 10800),
    BLACK_DRAGON("Чёрный дракон", 95, 23400),
    GIANT("Гигант", 100, 12600),
    CURSED_LEGION("Проклятый легион", 105, 25200),
    MONSTER("Монстр", 110, 14400),
    NECROMANCER("Некромант", 115, 25200),
    DARKNESS_DEVOURER("Пожиратель тьмы", 120, 16200),
    MONSTER_2("Чудовище", 125, 18000),
    OCTOPUS("Октопус", 130, 25200),
    SMITH("Кузнец", 140, 18000),
    SHULKER("Могущественный шалкер", 150, 19800),
    CASTER("Заклинатель", 160, 19800),
    DIE_RIDER("Всадник", 170, 21600),
    KOBOLD("Кобольд", 180, 25200),
    SAMURAI("Самурай", 190, 23400),
    DIE_LORD("Повелитель мёртвых", 200, 19800),
    SHADOW_LORD("Теневой лорд", 210, 25200),
    BIG_TURTLE("Гигантская черепаха", 220, 25200),
    GOLIATH("Голиаф", 230, 25200),
    DESTROYER("Разрушитель", 240, 19800),
    SNOW_MONSTER("Снежный монстр", 250, 3600),
    SCREAM("Крик", 260, 25200),
    SPECTRAL_CUBE("Спектральный куб", 270, 25200),
    SHADOW("Тень", 280, 25200),
    CYNTHIA("Синтия", 290, 25200),
    MAGNUS("Магнус", 300, 25200),
    HELL_HERALD("Вестник ада", 310, 25200),

    HELL_HOUND("Цербер", 320, 28800),
    IFRIT("Ифрит", 330, 25200),
    BAPHOMET("Бафомет", 340, 0),
    PIGLIN("Пиглин", 350, 25200),
    QUEEN_PIGLIN("Королева пиглинов", 360, 25200),
    HOGLIN("Хоглин", 370, 25200),
    ZOMBIE_PIGLIN("Зомби пиглин", 380, 25200),
    BRUTAL_PIGLIN("Брутальный пиглин", 390, 25200),
    MAGMA("Магма", 400, 25200),
    ZOGLIN("Зоглин", 410, 25200),
    HELL_KNIGHT("Адский рыцарь", 410, 25200),
    ;

    private final String name;
    private final int level;
    private final long time;

    BossType(String name, int level, int time) {
        this.name = name;
        this.level = level;
        this.time = time * 1000L;
    }

    public static BossType getByName(String name) {
        name = Util.stripColor(name);
        for (BossType type : values())
            if (name.contains(type.name))
                return type;
        return null;
    }
}
