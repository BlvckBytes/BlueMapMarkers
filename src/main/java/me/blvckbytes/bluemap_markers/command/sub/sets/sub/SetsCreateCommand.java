package me.blvckbytes.bluemap_markers.command.sub.sets.sub;

import me.blvckbytes.bluemap_markers.command.CommandFailure;
import me.blvckbytes.bluemap_markers.command.SubCommand;
import me.blvckbytes.bluemap_markers.command.sub.sets.SetsAction;
import me.blvckbytes.bluemap_markers.stores.MarkerSetStore;
import me.blvckbytes.syllables_matcher.NormalizedConstant;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Queue;

public class SetsCreateCommand extends SubCommand {

  private final MarkerSetStore markerSetStore;

  public SetsCreateCommand(MarkerSetStore markerSetStore) {
    this.markerSetStore = markerSetStore;
  }

  @Override
  public @Nullable CommandFailure onCommand(CommandSender sender, String[] args, Queue<NormalizedConstant<?>> actions) {
    if (args.length != 2)
      return CommandFailure.INVALID_USAGE;

    var name = args[0];
    var worldName = args[1];
    var world = Bukkit.getWorld(worldName);

    if (world == null) {
      sender.sendMessage("§cThe world " + worldName + " could not be located!");
      return null;
    }

    var result = markerSetStore.createMarkerSet(name, world);

    switch (result.status) {
      case DUPLICATE_IDENTIFIER -> sender.sendMessage("§cA marker-set called " + name + " already exists!");
      case INTERNAL_ERROR -> sender.sendMessage("§cAn internal error occurred; see console!");
      case SUCCESS -> sender.sendMessage("§aThe marker-set " + name + " has successfully been created for the world " + world.getName() + "!");
      case IDENTIFIER_NOT_FOUND, IDENTIFIER_IN_ACTIVE_USE -> throw new IllegalStateException();
    }

    return null;
  }

  @Override
  public List<String> onTabComplete(CommandSender sender, String[] args) {
    return List.of();
  }

  @Override
  public List<String> getPartialUsages(@Nullable Queue<NormalizedConstant<?>> actions, CommandSender sender) {
    return List.of(getCorrespondingAction().getNormalizedName() + " <Name> <World>");
  }

  @Override
  public NormalizedConstant<?> getCorrespondingAction() {
    return SetsAction.matcher.getNormalizedConstant(SetsAction.CREATE);
  }
}
