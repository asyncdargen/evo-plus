package ru.dargen.evoplus.feature;

import lombok.Data;
import lombok.val;
import ru.dargen.evoplus.feature.impl.MiscFeature;
import ru.dargen.evoplus.feature.impl.RenderFeature;
import ru.dargen.evoplus.feature.impl.TeamWarFeature;
import ru.dargen.evoplus.feature.impl.boss.BossTimerFeature;
import ru.dargen.evoplus.feature.impl.staff.StaffTimerFeature;
import ru.dargen.evoplus.feature.impl.stats.StatsFeature;
import ru.dargen.evoplus.EvoPlus;
import ru.dargen.evoplus.feature.setting.Setting;
import ru.dargen.evoplus.gui.GuiElement;

import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Data
public abstract class Feature {

    public static StatsFeature STATS_FEATURE;
    public static BossTimerFeature BOSS_TIMER_FEATURE;
    public static StaffTimerFeature STAFF_TIMER_FEATURE;
    public static TeamWarFeature TEAM_WAR_FEATURE;
//    public static ClanFeature CLAN_FEATURE;
    public static RenderFeature RENDER_FEATURE;
    public static MiscFeature MISC_FEATURE;

    protected final String name;
    protected final String id;
    protected final List<Setting<?>> settings = new LinkedList<>();

    public Feature(String name, String id) {
        this.name = name;
        this.id = id;
    }

    protected final void register() {
        Arrays.stream(getClass().getDeclaredFields())
                .map(field -> {
                    try {
                        field.setAccessible(true);
                        val value = field.get(Modifier.isStatic(field.getModifiers()) ? null : this);
                        return value instanceof Setting ? ((Setting<?>) value) : null;
                    } catch (Throwable t) {
                        EvoPlus.instance().getLogger().error("Error while get setting field value", t);
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .forEach(settings::add);

        EvoPlus.instance().getFeatureManager().getFeatures().add(this);
        EvoPlus.instance().getFeatureManager().loadFeatureSettings(this);
        onRegister(EvoPlus.instance());
    }

    public List<GuiElement> getGuiElements() {
        return settings.stream().map(Setting::getElement).collect(Collectors.toList());
    }

    public void onRegister(EvoPlus mod) {

    }


}
