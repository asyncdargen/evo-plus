package ru.dargen.evoplus.feature.impl.stats.quest;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum QuestType {

    DAILY("Ежедневные"),
    WEEKLY("Еженедельные");

    private final String name;

}
