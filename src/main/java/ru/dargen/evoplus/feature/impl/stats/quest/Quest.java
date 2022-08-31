package ru.dargen.evoplus.feature.impl.stats.quest;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class Quest {

    private final QuestType type;
    private final String task;
    private final long endTime;

    public boolean isActive() {
        return System.currentTimeMillis() < endTime;
    }

    public boolean isExpired() {
        return !isActive();
    }

}
