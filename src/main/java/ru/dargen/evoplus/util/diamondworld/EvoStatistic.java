package ru.dargen.evoplus.util.diamondworld;

import lombok.Getter;
import lombok.val;
import ru.dargen.evoplus.EvoPlus;
import ru.dargen.evoplus.util.Util;
import ru.dargen.evoplus.util.formatter.DoubleFormatter;

import java.util.concurrent.atomic.AtomicBoolean;

@Getter
public class EvoStatistic {

    private static final AtomicBoolean UPDATE_STATE = new AtomicBoolean(false);
    protected boolean captured;
    protected int kills;
    protected int level;
    protected int shards;
    protected double money;
    protected double blocks;

    public EvoStatistic(EvoPlus mod) {
        mod.getTaskBus().runAsync(10, 10, task -> {
            val scores = Util.getSidebarScores();

            UPDATE_STATE.set(true);

            try {
                if (!DiamondWorldUtil.isOnDiamondWorld() || scores.isEmpty())
                    UPDATE_STATE.set(false);
                else {
                    kills = (int) parse(scores.get(4));
                    blocks = parse(scores.get(5));
                    level = (int) parse(scores.get(6));
                    shards = (int) parse(scores.get(7));
                    money = parse(scores.get(8));
                }
            } catch (Throwable t) {
                UPDATE_STATE.set(false);
            }

            captured = UPDATE_STATE.get();

            if (!captured) {
                kills = 0;
                blocks = 0;
                level = 0;
                shards = 0;
                money = 0;
            }
        });
    }

    private double parse(String score) {
        score = Util.stripColor(score);
        if (score == null) {
            UPDATE_STATE.set(false);
            return 0;
        }

        val args = score.split(": ");
        if (args.length != 2) {
            UPDATE_STATE.set(false);
            return 0;
        }
        return DoubleFormatter.parse(args[1]
                .replace("$", "") //for money
                .replace("I", "") //for level
                .replace("V", "")
        );
    }
}
