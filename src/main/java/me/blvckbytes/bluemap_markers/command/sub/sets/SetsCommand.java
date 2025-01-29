package me.blvckbytes.bluemap_markers.command.sub.sets;

import me.blvckbytes.bluemap_markers.command.SubCommandAction;
import me.blvckbytes.bluemap_markers.command.SubCommandWithSubCommands;
import me.blvckbytes.bluemap_markers.command.sub.sets.sub.*;
import me.blvckbytes.syllables_matcher.NormalizedConstant;

public class SetsCommand extends SubCommandWithSubCommands {

  public SetsCommand() {
    super(SetsAction.matcher);

    registerSubCommand(new SetsCreateCommand());
    registerSubCommand(new SetsDeleteCommand());
    registerSubCommand(new SetsListCommand());
    registerSubCommand(new SetsRenameCommand());
    registerSubCommand(new SetsToggleDefaultHiddenCommand());
    registerSubCommand(new SetsMoveToCommand());
    registerSubCommand(new SetsSetLabelCommand());
    registerSubCommand(new SetsToggleToggleableCommand());
    registerSubCommand(new SetsSetSortingCommand());
  }

  @Override
  public NormalizedConstant<?> getCorrespondingAction() {
    return SubCommandAction.matcher.getNormalizedConstant(SubCommandAction.SETS);
  }
}
