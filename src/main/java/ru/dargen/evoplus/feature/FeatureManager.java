package ru.dargen.evoplus.feature;

import com.google.gson.JsonObject;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.val;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.options.KeyBinding;
import ru.dargen.evoplus.EvoPlus;
import ru.dargen.evoplus.util.Util;
import ru.dargen.evoplus.util.common.FileUtil;

import java.util.HashSet;
import java.util.Set;

@Getter
public class FeatureManager {

    private final Set<Feature> features = new HashSet<>();
    private final KeyBinding keyBinding = new KeyBinding("Открыть меню", 344, "EvoPlus");
    private final JsonObject config;
    private final EvoPlus mod;

    public FeatureManager(EvoPlus mod) {
        this.mod = mod;

        KeyBindingHelper.registerKeyBinding(keyBinding);

        mod.getTaskBus().runAsync(1, 1, task -> {
            if (keyBinding.isPressed() && !Util.isOpenedScreen())
                new FeaturesGuiScreen().display();
        });

        val configContent = FileUtil.getStringContent("features.cfg");
        val json = mod.getPrettyGson().fromJson(configContent, JsonObject.class);
        config = json == null ? new JsonObject() : json;

        Runtime.getRuntime().addShutdownHook(new Thread(this::saveSettings));
    }

    protected void loadFeatureSettings(Feature feature) {
        val section = config.getAsJsonObject(feature.id);
        if (section != null && !section.isJsonNull()) feature.getSettings().forEach(setting -> {
            val settingValue = section.get(setting.getId());
            if (settingValue != null && !section.isJsonNull()) setting.load(settingValue);
        });
    }

    @SneakyThrows
    public void saveSettings() {
        features.forEach(feature -> {
            val section = new JsonObject();
            feature.getSettings().forEach(setting -> section.add(setting.getId(), setting.unload()));
            config.add(feature.getId(), section);
        });
        val configContent = mod.getPrettyGson().toJson(config);
        FileUtil.setStringContent("features.cfg", configContent);
    }

}
