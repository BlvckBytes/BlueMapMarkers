package me.blvckbytes.bluemap_markers;

import com.cryptomorin.xseries.XMaterial;
import de.bluecolored.bluemap.api.BlueMapAPI;
import me.blvckbytes.bluemap_markers.command.BlueMapMarkersCommand;
import me.blvckbytes.bluemap_markers.config.BlueMapMarkersCommandSection;
import me.blvckbytes.bluemap_markers.config.MainSection;
import me.blvckbytes.bluemap_markers.listener.CommandSendListener;
import me.blvckbytes.bluemap_markers.stores.ImageStore;
import me.blvckbytes.bukkitevaluable.CommandUpdater;
import me.blvckbytes.bukkitevaluable.ConfigKeeper;
import me.blvckbytes.bukkitevaluable.ConfigManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

public class BlueMapMarkersPlugin extends JavaPlugin {

  // TODO: Have configurable usage-strings
  // TODO: Have hover-able usage-strings (descriptions)

  /*
    A marker-template may not only specify a single- but multiple markers, each with a suffix
    as provided by the template's configuration; from the user's point of view, they are hidden behind
    the single suffix-less identifier and are managed at once; placeholders are the same across all entries.
    This may be useful for multiple versions at different zoom-level-intervals each.
   */

  private static final int API_ENABLE_TIMEOUT_S = 30;

  @Override
  public void onEnable() {
    var logger = getLogger();

    var timeoutTask = Bukkit.getScheduler().runTaskLater(this, () -> {
      logger.severe("Timed out while waiting for over " + API_ENABLE_TIMEOUT_S + "s the BlueMap-API to become available; disabling now!");
      Bukkit.getPluginManager().disablePlugin(this);
    }, 20L * API_ENABLE_TIMEOUT_S);

    logger.info("Initialized a " + API_ENABLE_TIMEOUT_S + "s timeout to wait for the BlueMap-API to become available!");

    // See: https://github.com/BlueMap-Minecraft/BlueMapAPI/wiki
    BlueMapAPI.onEnable(api -> {
      if (!isEnabled())
        return;

      timeoutTask.cancel();

      logger.info("The BlueMap-API just became available; bootstrapping now!");

      bootstrap(logger, api);
    });
  }

  private void bootstrap(Logger logger, BlueMapAPI blueMap) {

    try {
      // First invocation is quite heavy - warm up cache
      XMaterial.matchXMaterial(Material.AIR);

      var configManager = new ConfigManager(this, "config");
      var config = new ConfigKeeper<>(configManager, "config.yml", MainSection.class);
      var commandUpdater = new CommandUpdater(this);

      var imageStore = new ImageStore(logger, config, blueMap.getWebApp().getWebRoot());

      var blueMapMarkersCommand = Objects.requireNonNull(getCommand(BlueMapMarkersCommandSection.INITIAL_NAME));
      var blueMapMarkersCommandHandler = new BlueMapMarkersCommand(this, imageStore, config, logger);

      blueMapMarkersCommand.setExecutor(blueMapMarkersCommandHandler);

      Runnable updateCommands = () -> {
        config.rootSection.commands.blueMapMarkers.apply(blueMapMarkersCommand, commandUpdater);

        commandUpdater.trySyncCommands();
      };

      updateCommands.run();
      config.registerReloadListener(updateCommands);

      var commandSendListener = new CommandSendListener(this, config);

      getServer().getPluginManager().registerEvents(commandSendListener, this);
    } catch (Exception e) {
      logger.log(Level.SEVERE, "An error occurred while trying to enable the plugin!", e);
      Bukkit.getPluginManager().disablePlugin(this);
    }
  }
}