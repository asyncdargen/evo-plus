package ru.dargen.evoplus;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.Getter;
import lombok.experimental.Accessors;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.FabricLoader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.dargen.evoplus.command.CommandManager;
import ru.dargen.evoplus.command.commands.HelpCommand;
import ru.dargen.evoplus.command.commands.TeamCommand;
import ru.dargen.evoplus.command.commands.WarCommand;
import ru.dargen.evoplus.event.EventBus;
import ru.dargen.evoplus.feature.Feature;
import ru.dargen.evoplus.feature.FeatureManager;
import ru.dargen.evoplus.feature.impl.MiscFeature;
import ru.dargen.evoplus.feature.impl.RenderFeature;
import ru.dargen.evoplus.feature.impl.TeamWarFeature;
import ru.dargen.evoplus.feature.impl.boss.BossTimerFeature;
import ru.dargen.evoplus.feature.impl.staff.StaffTimerFeature;
import ru.dargen.evoplus.feature.impl.stats.StatsFeature;
import ru.dargen.evoplus.notify.NotificationsManager;
import ru.dargen.evoplus.task.TaskBus;
import ru.dargen.evoplus.util.diamondworld.EvoStatistic;

import java.io.File;

@Getter
public class EvoPlus implements ModInitializer {

    public final static String NAME = "§eEvo§6Plus";
    public final static String PREFIX = "§7[" + NAME + "§7] §r";

    @Getter
    @Accessors(fluent = true)
    protected static EvoPlus instance;

    @Getter
    @Accessors(fluent = true)
    public final static File folder = new File(System.getProperty("user.dir"), "evo-plus");
    @Getter
    @Accessors(fluent = true)
    private static final String version = FabricLoader.INSTANCE
            .getModContainer("evo-plus")
            .get()
            .getMetadata()
            .getVersion()
            .getFriendlyString();
    private final Gson prettyGson = new GsonBuilder().setPrettyPrinting().create();
    private final Gson gson = new GsonBuilder().create();
    private final Logger logger = LogManager.getLogger("EvoPlus");

    private TaskBus taskBus;
    private EventBus eventBus;
    private EvoStatistic stats;
    private FeatureManager featureManager;
    private CommandManager commandManager;
    private NotificationsManager notifyManager;

    @Override
    public void onInitialize() {
        instance = this;
        taskBus = new TaskBus(this);
        eventBus = new EventBus(this);
        stats = new EvoStatistic(this);
        featureManager = new FeatureManager(this);
        commandManager = new CommandManager(this);
        notifyManager = new NotificationsManager(this);

        commandManager.registerCommands(
                new TeamCommand(), new WarCommand(), new HelpCommand()/*, new ClanWebHookCommand()*//*, new SellCommand()*/
        );

        Feature.STATS_FEATURE = new StatsFeature();
        Feature.BOSS_TIMER_FEATURE = new BossTimerFeature();
        Feature.STAFF_TIMER_FEATURE = new StaffTimerFeature();
        Feature.TEAM_WAR_FEATURE = new TeamWarFeature();
//        Feature.CLAN_FEATURE = new ClanFeature();
        Feature.RENDER_FEATURE = new RenderFeature();
        Feature.MISC_FEATURE = new MiscFeature();
    }

}
