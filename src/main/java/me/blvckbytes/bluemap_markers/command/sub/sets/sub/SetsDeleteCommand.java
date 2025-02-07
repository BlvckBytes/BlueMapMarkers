package me.blvckbytes.bluemap_markers.command.sub.sets.sub;

import me.blvckbytes.bluemap_markers.command.CommandFailure;
import me.blvckbytes.bluemap_markers.command.SubCommand;
import me.blvckbytes.bluemap_markers.command.sub.sets.SetsAction;
import me.blvckbytes.bluemap_markers.stores.MarkerSetStore;
import me.blvckbytes.syllables_matcher.NormalizedConstant;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Queue;

public class SetsDeleteCommand extends SubCommand {

  private final MarkerSetStore markerSetStore;

  public SetsDeleteCommand(MarkerSetStore markerSetStore) {
    this.markerSetStore = markerSetStore;
  }

  @Override
  public @Nullable CommandFailure onCommand(CommandSender sender, String[] args, Queue<NormalizedConstant<?>> actions) {
    if (!(args.length == 1 || args.length == 2))
      return CommandFailure.INVALID_USAGE;

    var name = args[0];
    var force = false;

    if (args.length == 2) {
      if (!args[1].equalsIgnoreCase("force"))
        return CommandFailure.INVALID_USAGE;

      force = true;
    }

    var result = markerSetStore.deleteMarkerSet(name, force);

    switch (result.status) {
      case SUCCESS -> sender.sendMessage("§aThe marker-set " + name + " has been successfully deleted!");
      case IDENTIFIER_NOT_FOUND -> sender.sendMessage("§cThere is no marker-set called " + name + "!");
      case IDENTIFIER_IN_ACTIVE_USE -> sender.sendMessage("§cThe marker-set " + name + " is currently in use and cannot be deleted without using the force-flag!");
      case INTERNAL_ERROR -> sender.sendMessage("§cAn internal error occurred; see console!");
      case DUPLICATE_IDENTIFIER -> throw new IllegalStateException();
    }

    return null;
  }

  @Override
  public List<String> onTabComplete(CommandSender sender, String[] args) {
    return List.of();
  }

  @Override
  public List<String> getPartialUsages(@Nullable Queue<NormalizedConstant<?>> actions, CommandSender sender) {
    return List.of(getCorrespondingAction().getNormalizedName() + " <Name> [force]");
  }

  @Override
  public NormalizedConstant<?> getCorrespondingAction() {
    return SetsAction.matcher.getNormalizedConstant(SetsAction.DELETE);
  }
}
