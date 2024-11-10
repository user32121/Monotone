package juniper.monotone;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import juniper.monotone.command.TaskQueue;
import juniper.monotone.init.MonotoneCommand;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;

public class Monotone implements ClientModInitializer {
    public static final String MODID = "monotone";
    public static final Logger LOGGER = LoggerFactory.getLogger(MODID);

    @Override
    public void onInitializeClient() {
        LOGGER.info(MODID + " init");

        MonotoneCommand.init();
        ClientTickEvents.END_CLIENT_TICK.register(TaskQueue::tick);
    }
}