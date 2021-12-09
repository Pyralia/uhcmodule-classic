package fr.ariloxe.classic;

import com.pyralia.uhc.UHCApi;
import fr.ariloxe.classic.module.Classic;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class Main extends JavaPlugin {

    private static Main instance;
    private UHCApi uhcApi;

    @Override
    public void onEnable() {
        instance = this;
        Bukkit.getScheduler().runTaskLater(this, ()->{
            this.uhcApi = UHCApi.getApi();
            final Classic classic = new Classic(UHCApi.getApi());
            UHCApi.getApi().getUhcManager().getModules().add(classic);
            UHCApi.getApi().getGameManager().setGameMode(classic);
        }, 20);

    }

    @Override
    public void onDisable() {
        getLogger().info("ClassicModule disabled.");
    }

    public static Main getInstance() {
        return instance;
    }

    public UHCApi getApi() {
        return uhcApi;
    }
}
