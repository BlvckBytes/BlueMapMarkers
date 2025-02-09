package me.blvckbytes.bluemap_markers.command.sub.markers.sub;

import me.blvckbytes.bluemap_markers.command.CommandFailure;
import me.blvckbytes.bluemap_markers.command.SubCommand;
import me.blvckbytes.bluemap_markers.command.sub.markers.MarkersAction;
import me.blvckbytes.syllables_matcher.NormalizedConstant;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class MarkersChangeVariableCommand extends SubCommand {

  private final Map<VariableAction, String> partialUsageByVariableAction;

  public MarkersChangeVariableCommand() {
    var baseUsage = getCorrespondingAction().getNormalizedName() + " <Marker-Name> <Variable-Name> ";

    partialUsageByVariableAction = new LinkedHashMap<>();

    partialUsageByVariableAction.put(VariableAction.SET, baseUsage + getVariableActionName(VariableAction.SET) + " <Value>");
    partialUsageByVariableAction.put(VariableAction.UNSET, baseUsage + getVariableActionName(VariableAction.UNSET));
    partialUsageByVariableAction.put(VariableAction.ADD, baseUsage + getVariableActionName(VariableAction.ADD) + " <Value>");
    partialUsageByVariableAction.put(VariableAction.INSERT, baseUsage + getVariableActionName(VariableAction.INSERT) + " <Position> <Value>");
    partialUsageByVariableAction.put(VariableAction.REMOVE, baseUsage + getVariableActionName(VariableAction.REMOVE) + " <Position>");
  }

  @Override
  public @Nullable CommandFailure onCommand(CommandSender sender, String[] args, Queue<NormalizedConstant<?>> actions) {
    if (args.length < 3)
      return CommandFailure.INVALID_USAGE;

    var markerName = args[0];
    var variableName = args[1];
    var variableActionName = args[2];

    var normalizedVariableAction = VariableAction.matcher.matchFirst(variableActionName);

    if (normalizedVariableAction == null)
      return CommandFailure.INVALID_USAGE;

    actions.add(normalizedVariableAction);

    switch (normalizedVariableAction.constant) {
      case SET, ADD -> {
        if (args.length != 4)
          return CommandFailure.INVALID_USAGE;

        var value = args[3];

        if (normalizedVariableAction.constant == VariableAction.ADD) {
          sender.sendMessage("§aAdding value " + value + " to variable " + variableName + " for marker " + markerName);
          return null;
        }

        sender.sendMessage("§aSetting variable " + variableName + " to value " + value + " for marker " + markerName);
        return null;
      }

      case UNSET -> {
        if (args.length != 3)
          return CommandFailure.INVALID_USAGE;

        sender.sendMessage("§aUnsetting " + variableName + " for marker " + markerName);
        return null;
      }

      case INSERT, REMOVE -> {
        if (args.length < 4)
          return CommandFailure.INVALID_USAGE;

        int position;

        try {
          position = Integer.parseInt(args[3]);
        } catch (Exception e) {
          sender.sendMessage("§cThe input " + args[3] + " is not a valid integer!");
          return null;
        }

        if (position <= 0) {
          sender.sendMessage("§cPositions start at one!");
          return null;
        }

        if (normalizedVariableAction.constant == VariableAction.INSERT) {
          if (args.length != 5)
            return CommandFailure.INVALID_USAGE;

          var value = args[4];

          sender.sendMessage("§aInserting " + value + " at " + position + " for marker " + markerName);
          return null;
        }

        if (args.length != 4)
          return CommandFailure.INVALID_USAGE;

        sender.sendMessage("§cRemoving at " + position + " for marker " + markerName);
        return null;
      }
    }

    return null;
  }

  @Override
  public List<String> onTabComplete(CommandSender sender, String[] args) {
    return List.of();
  }

  @Override
  public List<String> getPartialUsages(@Nullable Queue<NormalizedConstant<?>> actions, CommandSender sender) {
    var action = actions == null ? null : actions.poll();

    if (action != null)
      return List.of(partialUsageByVariableAction.get((VariableAction) action.constant));

    return new ArrayList<>(partialUsageByVariableAction.values());
  }

  private String getVariableActionName(VariableAction action) {
    return VariableAction.matcher.getNormalizedConstant(action).getNormalizedName();
  }

  @Override
  public NormalizedConstant<?> getCorrespondingAction() {
    return MarkersAction.matcher.getNormalizedConstant(MarkersAction.CHANGE_VARIABLE);
  }
}
