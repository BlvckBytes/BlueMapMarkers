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

public class SetsRenameCommand extends SubCommand {

  private final MarkerSetStore markerSetStore;

  public SetsRenameCommand(MarkerSetStore markerSetStore) {
    this.markerSetStore = markerSetStore;
  }

  @Override
  public @Nullable CommandFailure onCommand(CommandSender sender, String[] args, Queue<NormalizedConstant<?>> actions) {
    if (args.length != 2)
      return CommandFailure.INVALID_USAGE;

    var oldName = args[0];
    var newName = args[1];

    var result = markerSetStore.renameMarkerSet(oldName, newName);

    switch (result.status) {
      case SUCCESS -> sender.sendMessage("§aThe marker-set " + oldName + " has been successfully renamed to " + newName);
      case IDENTIFIER_NOT_FOUND -> sender.sendMessage("§cCould not locate a marker-set with name " + oldName + "!");
      case DUPLICATE_IDENTIFIER -> sender.sendMessage("§cThe new name " + newName + " is already in use!");
      case INTERNAL_ERROR -> sender.sendMessage("§cAn internal error occurred; see console!");
      case IDENTIFIER_IN_ACTIVE_USE -> throw new IllegalStateException();
    }

    return null;
  }

  @Override
  public List<String> onTabComplete(CommandSender sender, String[] args) {
    return List.of();
  }

  @Override
  public List<String> getPartialUsages(@Nullable Queue<NormalizedConstant<?>> actions, CommandSender sender) {
    return List.of(getCorrespondingAction().getNormalizedName() + " <Old-Name> <New-Name>");
  }

  @Override
  public NormalizedConstant<?> getCorrespondingAction() {
    return SetsAction.matcher.getNormalizedConstant(SetsAction.RENAME);
  }
}
