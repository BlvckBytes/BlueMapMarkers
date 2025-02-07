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

public class SetsToggleToggleableCommand extends SubCommand {

  private final MarkerSetStore markerSetStore;

  public SetsToggleToggleableCommand(MarkerSetStore markerSetStore) {
    this.markerSetStore = markerSetStore;
  }

  @Override
  public @Nullable CommandFailure onCommand(CommandSender sender, String[] args, Queue<NormalizedConstant<?>> actions) {
    if (args.length != 1)
      return CommandFailure.INVALID_USAGE;

    var name = args[0];
    var markerSet = markerSetStore.getMarkerSet(name);

    if (markerSet == null) {
      sender.sendMessage("§cThere is no marker-set with name " + name);
      return null;
    }

    var newValue = !markerSet.toggleable();
    var result = markerSetStore.setMarkerSetToggleable(name, newValue);

    switch (result.status) {
      case SUCCESS -> sender.sendMessage("§aThe toggleable-value of the marker-set " + name + " has been successfully set to " + newValue);
      case IDENTIFIER_NOT_FOUND -> sender.sendMessage("§cCould not locate a marker-set with name " + name + "!");
      case INTERNAL_ERROR -> sender.sendMessage("§cAn internal error occurred; see console!");
      case DUPLICATE_IDENTIFIER, IDENTIFIER_IN_ACTIVE_USE -> throw new IllegalStateException();
    }

    return null;
  }

  @Override
  public List<String> onTabComplete(CommandSender sender, String[] args) {
    return List.of();
  }

  @Override
  public List<String> getPartialUsages(@Nullable Queue<NormalizedConstant<?>> actions, CommandSender sender) {
    return List.of(getCorrespondingAction().getNormalizedName() + " <Name>");
  }

  @Override
  public NormalizedConstant<?> getCorrespondingAction() {
    return SetsAction.matcher.getNormalizedConstant(SetsAction.TOGGLE_TOGGLEABLE);
  }
}
