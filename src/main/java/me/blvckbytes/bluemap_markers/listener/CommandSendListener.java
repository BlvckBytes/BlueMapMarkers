package me.blvckbytes.bluemap_markers.listener;

import me.blvckbytes.bluemap_markers.command.CommandPermission;
import me.blvckbytes.bluemap_markers.config.MainSection;
import me.blvckbytes.bukkitevaluable.ConfigKeeper;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandSendEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class CommandSendListener implements Listener {

  private final Map<Command, Function<CommandSender, Boolean>> pluginCommands;
  private final String lowerPluginName;

  public CommandSendListener(JavaPlugin plugin, ConfigKeeper<MainSection> config) {
    this.pluginCommands = new HashMap<>();
    this.lowerPluginName = plugin.getName().toLowerCase();

    pluginCommands.put(
      plugin.getCommand(config.rootSection.commands.blueMapMarkers.evaluatedName),
      CommandPermission.COMMAND_BLUE_MAP_MARKERS::hasPermission
    );
  }

  @EventHandler
  public void onCommandSend(PlayerCommandSendEvent event) {
    var player = event.getPlayer();

    for (var pluginCommandEntry : pluginCommands.entrySet()) {
      if (pluginCommandEntry.getValue().apply(player))
        continue;

      var pluginCommand = pluginCommandEntry.getKey();

      event.getCommands().remove(pluginCommand.getName());
      event.getCommands().remove(lowerPluginName + ":" + pluginCommand.getName());

      for (var alias : pluginCommand.getAliases()) {
        event.getCommands().remove(alias);
        event.getCommands().remove(lowerPluginName + ":" + alias);
      }
    }
  }
}
