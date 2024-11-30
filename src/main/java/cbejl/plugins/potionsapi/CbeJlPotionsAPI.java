package cbejl.plugins.potionsapi;

import cbejl.plugins.potionsapi.events.EntitiesListener;
import cbejl.plugins.potionsapi.events.PotionConsumeListener;
import org.bukkit.plugin.java.JavaPlugin;

public final class CbeJlPotionsAPI extends JavaPlugin {
    public static CbeJlPotionsAPI instance;

    @Override
    public void onEnable() {
        instance = this;
        getServer().getPluginManager().registerEvents(new PotionConsumeListener(), this);
        getServer().getPluginManager().registerEvents(new EntitiesListener(), this);
    }

    @Override
    public void onDisable() {
        instance = null;
    }

    public static CbeJlPotionsAPI getInstance() {
        return instance;
    }
}
