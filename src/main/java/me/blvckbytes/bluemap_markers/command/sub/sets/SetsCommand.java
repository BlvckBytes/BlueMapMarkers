package me.blvckbytes.bluemap_markers.command.sub.sets;

import me.blvckbytes.bluemap_markers.command.SubCommandAction;
import me.blvckbytes.bluemap_markers.command.SubCommandWithSubCommands;
import me.blvckbytes.bluemap_markers.command.sub.sets.sub.*;
import me.blvckbytes.syllables_matcher.NormalizedConstant;

public class SetsCommand extends SubCommandWithSubCommands {

  public SetsCommand() {
    super(SetsAction.matcher);

    registerSubCommand(new SetsListCommand());
    registerSubCommand(new SetsDeleteCommand());
    registerSubCommand(new SetsRenameCommand());
    registerSubCommand(new SetsCreateCommand());
    registerSubCommand(new SetsSetLabelCommand());
    registerSubCommand(new SetsSetToggleableCommand());
    registerSubCommand(new SetsSetSortingCommand());
  }

  @Override
  public NormalizedConstant<?> getCorrespondingAction() {
    return SubCommandAction.matcher.getNormalizedConstant(SubCommandAction.SETS);
  }
}
