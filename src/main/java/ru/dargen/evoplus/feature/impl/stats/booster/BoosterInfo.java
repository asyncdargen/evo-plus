package ru.dargen.evoplus.feature.impl.stats.booster;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@RequiredArgsConstructor
public class BoosterInfo {

    protected BoosterType type = BoosterType.NONE;
    protected volatile long lastBreak = 0;
    protected volatile int blocks = 0;

    public void handleBreak() {
        if (!isActive()) {
            blocks = 0;
            type = BoosterType.NONE;
        }

        blocks++;
        update();
    }

    public void cancelBreak() {
        if (--blocks < 0) blocks = 0;
    }

    public void update() {
        lastBreak = System.currentTimeMillis();
    }

    public boolean isStopBreak() {
        return System.currentTimeMillis() - lastBreak >= 1_000;
    }

    public BoosterType getNextType() {
        return isMax() ? null : BoosterType.values()[type.ordinal() + 1];
    }

    public int getNextBlocks() {
        return isMax() ? 0 : getNextType().getBlocks();
    }

    public boolean isCompleted() {
        return getNextBlocks() <= blocks;
    }

    public long getRightTime() {
        return lastBreak + getTimeOut() - System.currentTimeMillis();
    }

    public boolean isMax() {
        return type.ordinal() == BoosterType.values().length - 1;
    }

    public boolean isActive() {
        return System.currentTimeMillis() - lastBreak < getTimeOut();
    }

//    public long getTimeOut() {
//        return 15 /*tmp*/;
//    }

    //Is incorrect?
    public long getTimeOut() {
        return Math.min(blocks / 200 + 4, 40) * 1000L;
    }
}
