package me.blvckbytes.bluemap_markers.command.sub.sets;

import me.blvckbytes.bluemap_markers.command.SubCommandAction;
import me.blvckbytes.bluemap_markers.command.SubCommandWithSubCommands;
import me.blvckbytes.bluemap_markers.command.sub.sets.sub.*;
import me.blvckbytes.bluemap_markers.config.MainSection;
import me.blvckbytes.bluemap_markers.stores.MarkerSetStore;
import me.blvckbytes.bukkitevaluable.ConfigKeeper;
import me.blvckbytes.syllables_matcher.NormalizedConstant;

public class SetsCommand extends SubCommandWithSubCommands {

  public SetsCommand(MarkerSetStore markerSetStore, ConfigKeeper<MainSection> config) {
    super(SetsAction.matcher);

    registerSubCommand(new SetsCreateCommand(markerSetStore));
    registerSubCommand(new SetsDeleteCommand(markerSetStore));
    registerSubCommand(new SetsListCommand(markerSetStore, config));
    registerSubCommand(new SetsRenameCommand(markerSetStore));
    registerSubCommand(new SetsToggleDefaultHiddenCommand(markerSetStore));
    registerSubCommand(new SetsMoveToCommand(markerSetStore));
    registerSubCommand(new SetsSetLabelCommand(markerSetStore));
    registerSubCommand(new SetsToggleToggleableCommand(markerSetStore));
    registerSubCommand(new SetsSetSortingCommand(markerSetStore));
  }

  @Override
  public NormalizedConstant<?> getCorrespondingAction() {
    return SubCommandAction.matcher.getNormalizedConstant(SubCommandAction.SETS);
  }
}
