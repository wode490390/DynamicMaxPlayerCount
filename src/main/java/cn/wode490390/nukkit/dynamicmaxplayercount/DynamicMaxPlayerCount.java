package cn.wode490390.nukkit.dynamicmaxplayercount;

import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerKickEvent;
import cn.nukkit.event.server.QueryRegenerateEvent;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.utils.Config;

public class DynamicMaxPlayerCount extends PluginBase implements Listener {

    private static final String PERMISSION_FULLJOIN = "dynamicmaxplayercount.fulljoin";

    private static final String CONFIG_UNLIMIT = "unlimit";

    private boolean unlimit;

    @Override
    public void onEnable() {
        this.saveDefaultConfig();
        Config config = this.getConfig();
        String node = CONFIG_UNLIMIT;
        try {
            this.unlimit = config.getBoolean(node);
        } catch (Exception e) {
            this.unlimit = false;
            this.logLoadException(node);
        }
        this.getServer().getPluginManager().registerEvents(this, this);
        new MetricsLite(this);
    }

    @EventHandler
    public void onQueryRegenerate(QueryRegenerateEvent event) {
        int count;
        if ((count = event.getPlayerCount() + 1) < event.getMaxPlayerCount() || this.unlimit) {
            event.setMaxPlayerCount(count);
        }
    }

    @EventHandler
    public void onPlayerKick(PlayerKickEvent event) {
        if (event.getReasonEnum() == PlayerKickEvent.Reason.SERVER_FULL && this.unlimit && event.getPlayer().hasPermission(PERMISSION_FULLJOIN)) {
            event.setCancelled(true);
        }
    }

    private void logLoadException(String text) {
        this.getLogger().alert("An error occurred while reading the configuration '" + text + "'. Use the default value.");
    }
}
