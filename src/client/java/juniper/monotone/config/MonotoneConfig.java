package juniper.monotone.config;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

import juniper.monotone.Monotone;
import juniper.monotone.interaction.InteractionType;
import juniper.monotone.interaction.MaskDisplayType;
import juniper.monotone.interaction.RegionMask;
import juniper.monotone.interaction.RegionMaskAdapter;
import net.minecraft.client.MinecraftClient;

public class MonotoneConfig {
    public boolean runningTasks = false;

    public int pathfindNotifyIntervalSeconds = 5;
    public float pathfindSearchRadiusChunks = 2;
    public float pathfindSearchAngleDegrees = 60;
    public boolean pathfindShowPath = false;

    public Map<InteractionType, List<RegionMask>> interactionMask = new HashMap<>();
    public Map<InteractionType, Boolean> interactionMaskEnabled = new HashMap<>();
    public Map<InteractionType, MaskDisplayType> interactionMaskDisplay = new HashMap<>();

    public static Gson gson = new GsonBuilder().registerTypeAdapter(RegionMask.class, new RegionMaskAdapter()).setPrettyPrinting().create();

    public static MonotoneConfig load() {
        Path filename = Paths.get("config", "monotone_config.json");
        try (FileReader fr = new FileReader(filename.toFile())) {
            MonotoneConfig mc = gson.fromJson(fr, MonotoneConfig.class);
            return mc == null ? new MonotoneConfig() : mc;
        } catch (IOException | JsonSyntaxException e) {
            Monotone.LOGGER.warn("unable to load config, backing up config:", e);
            try {
                Files.copy(filename, Paths.get("config", "monotone_config.json.bak"), StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e2) {
                Monotone.LOGGER.warn("unable to backup config:", e2);
                e2.printStackTrace();
            }
            return new MonotoneConfig();
        }
    }

    public static void save(MinecraftClient client) {
        Path filename = Paths.get("config", "monotone_config.json");
        try (FileWriter fw = new FileWriter(filename.toFile())) {
            gson.toJson(Monotone.CONFIG, fw);
        } catch (IOException e) {
            Monotone.LOGGER.warn("unable to save config:", e);
        }
    }
}
