package juniper.monotone;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import juniper.monotone.init.MonotoneCommand;
import net.fabricmc.api.ClientModInitializer;

public class Monotone implements ClientModInitializer {
    public static final String MODID = "monotone";
    public static final Logger LOGGER = LoggerFactory.getLogger(MODID);

    @Override
    public void onInitializeClient() {
        LOGGER.info(MODID + " init");
        MonotoneCommand.init();
    }
}