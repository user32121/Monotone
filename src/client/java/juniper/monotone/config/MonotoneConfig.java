package juniper.monotone.config;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import juniper.monotone.Monotone;
import juniper.monotone.interaction.InteractionType;
import juniper.monotone.interaction.RegionMask;
import net.minecraft.client.MinecraftClient;

public class MonotoneConfig {
    public boolean runningTasks = false;

    public int pathfindNotifyIntervalSeconds = 5;
    public float pathfindSearchRadiusChunks = 2;
    public float pathfindSearchAngleDegrees = 60;
    public boolean pathfindShowPath = false;

    public Map<InteractionType, Boolean> interactionMaskEnabled = new HashMap<>();
    //TODO InstanceCreator or TypeAdapter
    public Map<InteractionType, List<RegionMask>> interactionMask = new HashMap<>();

    public static MonotoneConfig load() {
        try (FileReader fr = new FileReader("config/monotone_config.json")) {
            MonotoneConfig mc = new Gson().fromJson(fr, MonotoneConfig.class);
            return mc == null ? new MonotoneConfig() : mc;
        } catch (IOException e) {
            Monotone.LOGGER.warn("unable to load config:", e);
            return new MonotoneConfig();
        }
    }

    public static void save(MinecraftClient client) {
        try (FileWriter fw = new FileWriter("config/monotone_config.json")) {
            new GsonBuilder().setPrettyPrinting().create().toJson(Monotone.CONFIG, fw);
        } catch (IOException e) {
            Monotone.LOGGER.warn("unable to save config:", e);
        }
    }
}
