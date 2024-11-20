package juniper.monotone;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import juniper.monotone.command.TaskQueue;
import juniper.monotone.config.MonotoneConfig;
import juniper.monotone.init.MonotoneCommand;
import juniper.monotone.interaction.RegionMask;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.event.player.AttackBlockCallback;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;

public class Monotone implements ClientModInitializer {
    public static final String MODID = "monotone";
    public static final Logger LOGGER = LoggerFactory.getLogger(MODID);
    public static final MonotoneConfig CONFIG = MonotoneConfig.load();

    @Override
    public void onInitializeClient() {
        LOGGER.info(MODID + " init");

        MonotoneCommand.init();
        ClientTickEvents.START_WORLD_TICK.register(TaskQueue::tick);
        ClientLifecycleEvents.CLIENT_STOPPING.register(MonotoneConfig::save);
        AttackBlockCallback.EVENT.register(RegionMask::checkBreakMask);
        UseBlockCallback.EVENT.register(RegionMask::checkPlaceMask);
    }
}