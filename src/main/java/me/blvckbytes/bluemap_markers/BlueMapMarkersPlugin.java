package me.blvckbytes.bluemap_markers;

import com.cryptomorin.xseries.XMaterial;
import me.blvckbytes.bluemap_markers.command.BlueMapMarkersCommand;
import me.blvckbytes.bluemap_markers.config.BlueMapMarkersCommandSection;
import me.blvckbytes.bluemap_markers.config.MainSection;
import me.blvckbytes.bluemap_markers.listener.CommandSendListener;
import me.blvckbytes.bukkitevaluable.CommandUpdater;
import me.blvckbytes.bukkitevaluable.ConfigKeeper;
import me.blvckbytes.bukkitevaluable.ConfigManager;
import org.bukkit.Material;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;
import java.util.logging.Level;

public class BlueMapMarkersPlugin extends JavaPlugin {

  /*
    - Marker-sets group markers into named and toggleable containers
      - id: String (internal to this plugin)
      - toggleable: Boolean
      - label: String
      - default-hidden: Boolean
      - sorting: Integer
      - markers: Marker[]

    - Each world can have arbitrarily many marker-sets, each with a unique ID

    - Markers will always be rendered as HTML, based on configurable templates containing placeholders;
      - id: String (internal to this plugin)
      A HTML-Marker has the following properties:
      - classes: String[]
      - anchor: Vector2
      - html: String

    - HTML-templates are stored in the configuration-file and can only be edited/managed there; they have
      unique names also, which essentially are their YAML keys under the containing parent.

    - Marker-placeholders are typed and do not just accept any values, as to ensure
      proper behavior when rendering. Types that come to mind are:
      - Web-Color RGB, RGBA, HEX, HEX-A
      - Text
      - Image

    - Images are stored in /bluemap/web/assets and can have the following file-extensions:
      - png
      - jpg
      - jpeg
      - svg
      - gif

    - Images are automatically detected and can be downloaded into said directory via
      a command, while their full name (file-name plus extension) acts as a unique identifier.

    - Each world can have markers at arbitrary positions
      - Create new at- or move existing markers to
        - The player's location
        - Specified x/y/z coordinates

    - A marker-template may not only specify a single- but multiple markers, each with a suffix
      as provided by the template's configuration; from the user's point of view, they are hidden behind
      the single suffix-less identifier and are managed at once; placeholders are the same across all entries.
      This may be useful for multiple versions at different zoom-level-intervals each.

   */

  @Override
  public void onEnable() {
    var logger = getLogger();

    try {
      // First invocation is quite heavy - warm up cache
      XMaterial.matchXMaterial(Material.AIR);

      var configManager = new ConfigManager(this, "config");
      var config = new ConfigKeeper<>(configManager, "config.yml", MainSection.class);
      var commandUpdater = new CommandUpdater(this);

      var blueMapMarkersCommand = Objects.requireNonNull(getCommand(BlueMapMarkersCommandSection.INITIAL_NAME));
      var blueMapMarkersCommandHandler = new BlueMapMarkersCommand(config);

      blueMapMarkersCommand.setExecutor(blueMapMarkersCommandHandler);

      Runnable updateCommands = () -> {
        config.rootSection.commands.blueMapMarkers.apply(blueMapMarkersCommand, commandUpdater);

        commandUpdater.trySyncCommands();
      };

      updateCommands.run();
      config.registerReloadListener(updateCommands);

      var commandSendListener = new CommandSendListener(this, config);

      getServer().getPluginManager().registerEvents(commandSendListener, this);

      // See: https://github.com/BlueMap-Minecraft/BlueMapAPI/wiki
//      var mapApi = BlueMapAPI.getInstance().orElseThrow(() -> new IllegalStateException("Could not access the BlueMap-API"));
    } catch (Exception e) {
      logger.log(Level.SEVERE, "An error occurred while trying to enable the plugin!", e);
    }
  }
}