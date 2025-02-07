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

public class SetsMoveToCommand extends SubCommand {

  private final MarkerSetStore markerSetStore;

  public SetsMoveToCommand(MarkerSetStore markerSetStore) {
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

    var result = markerSetStore.moveMarkerSet(name, world);

    switch (result.status) {
      case SUCCESS -> sender.sendMessage("§aThe marker-set " + name + " has been successfully moved to world " + world.getName() + "!");
      case IDENTIFIER_NOT_FOUND -> sender.sendMessage("§cThere is no marker-set called " + name + "!");
      case INTERNAL_ERROR -> sender.sendMessage("§cAn internal error occurred; see console!");
      case IDENTIFIER_IN_ACTIVE_USE, DUPLICATE_IDENTIFIER -> throw new IllegalStateException();
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
    return SetsAction.matcher.getNormalizedConstant(SetsAction.MOVE_TO);
  }
}
