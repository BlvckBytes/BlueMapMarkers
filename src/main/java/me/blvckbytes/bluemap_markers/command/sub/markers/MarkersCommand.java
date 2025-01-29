package me.blvckbytes.bluemap_markers.command.sub.markers;

import me.blvckbytes.bluemap_markers.command.SubCommandAction;
import me.blvckbytes.bluemap_markers.command.SubCommandWithSubCommands;
import me.blvckbytes.bluemap_markers.command.sub.markers.sub.*;
import me.blvckbytes.syllables_matcher.NormalizedConstant;

public class MarkersCommand extends SubCommandWithSubCommands {

  public MarkersCommand() {
    super(MarkersAction.matcher);

    registerSubCommand(new MarkersCreateCommand());
    registerSubCommand(new MarkersDeleteCommand());
    registerSubCommand(new MarkersRenameCommand());
    registerSubCommand(new MarkersListCommand());
    registerSubCommand(new MarkersNearCommand());
    registerSubCommand(new MarkersToggleListedCommand());
    registerSubCommand(new MarkersSetSortingCommand());
    registerSubCommand(new MarkersMoveHereCommand());
    registerSubCommand(new MarkersMoveToCommand());
    registerSubCommand(new MarkersSetVariableCommand());
    registerSubCommand(new MarkersUnsetVariableCommand());
    registerSubCommand(new MarkersListVariablesCommand());
    registerSubCommand(new MarkersSetTemplateCommand());
    registerSubCommand(new MarkersSetSetCommand());
  }

  @Override
  public NormalizedConstant<?> getCorrespondingAction() {
    return SubCommandAction.matcher.getNormalizedConstant(SubCommandAction.MARKERS);
  }
}
