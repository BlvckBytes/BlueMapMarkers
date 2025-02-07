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

public class SetsSetSortingCommand extends SubCommand {

  private final MarkerSetStore markerSetStore;

  public SetsSetSortingCommand(MarkerSetStore markerSetStore) {
    this.markerSetStore = markerSetStore;
  }

  @Override
  public @Nullable CommandFailure onCommand(CommandSender sender, String[] args, Queue<NormalizedConstant<?>> actions) {
    var parsedArgs = parseQuotesSupportingArguments(args);

    if (parsedArgs.size() != 2)
      return CommandFailure.INVALID_USAGE;

    var name = parsedArgs.get(0);
    var numberString = parsedArgs.get(1);
    int sorting;

    try {
      sorting = Integer.parseInt(numberString);
    } catch (Exception e) {
      sender.sendMessage("§cThe input " + numberString + " is not an integer!");
      return null;
    }

    var result = markerSetStore.setMarkerSetSorting(name, sorting);

    switch (result.status) {
      case SUCCESS -> sender.sendMessage("§aThe sorting-value of the marker-set " + name + " has been successfully set to " + sorting);
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
    return List.of(getCorrespondingAction().getNormalizedName() + " <Name> <Index>");
  }

  @Override
  public NormalizedConstant<?> getCorrespondingAction() {
    return SetsAction.matcher.getNormalizedConstant(SetsAction.SET_SORTING);
  }
}
