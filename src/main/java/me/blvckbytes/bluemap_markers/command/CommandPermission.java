package me.blvckbytes.bluemap_markers.command;

import org.bukkit.permissions.Permissible;

public enum CommandPermission {
  COMMAND_BLUE_MAP_MARKERS("bluemapmarkers"),
  ;

  private static final String nodePrefix = "bluemapmarkers.command.";

  private final String node;

  CommandPermission(String nodeSuffix) {
    this.node = nodePrefix + nodeSuffix;
  }

  public boolean hasPermission(Permissible permissible) {
    return permissible.hasPermission(node);
  }
}
