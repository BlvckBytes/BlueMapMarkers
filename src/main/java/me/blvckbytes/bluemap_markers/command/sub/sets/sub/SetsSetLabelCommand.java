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

public class SetsSetLabelCommand extends SubCommand {

  private final MarkerSetStore markerSetStore;

  public SetsSetLabelCommand(MarkerSetStore markerSetStore) {
    this.markerSetStore = markerSetStore;
  }

  @Override
  public @Nullable CommandFailure onCommand(CommandSender sender, String[] args, Queue<NormalizedConstant<?>> actions) {
    var parsedArgs = parseQuotesSupportingArguments(args);

    if (parsedArgs.size() != 2)
      return CommandFailure.INVALID_USAGE;

    var name = parsedArgs.get(0);
    var label = parsedArgs.get(1);

    var result = markerSetStore.setMarkerSetLabel(name, label);

    switch (result.status) {
      case SUCCESS -> sender.sendMessage("§aThe label of the marker-set " + name + " has been successfully set to " + label);
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
    return List.of(getCorrespondingAction().getNormalizedName() + " <Name> <Label>");
  }

  @Override
  public NormalizedConstant<?> getCorrespondingAction() {
    return SetsAction.matcher.getNormalizedConstant(SetsAction.SET_LABEL);
  }
}
