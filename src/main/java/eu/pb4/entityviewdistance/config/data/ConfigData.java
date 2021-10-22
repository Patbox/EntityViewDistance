package eu.pb4.entityviewdistance.config.data;

import eu.pb4.entityviewdistance.config.ConfigManager;
import eu.pb4.entityviewdistance.config.EvdOverrideSide;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class ConfigData {
    public int CONFIG_VERSION_DONT_TOUCH_THIS = ConfigManager.VERSION;
    public String _comment = "Before changing anything, see https://github.com/Patbox/EntityViewDistance#configuration";

    public String mode = EvdOverrideSide.BOTH.name().toLowerCase(Locale.ROOT);

    public Map<String, Integer> entityViewDistance = new HashMap<>();
}
