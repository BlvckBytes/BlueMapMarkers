package me.blvckbytes.bluemap_markers.command;

import me.blvckbytes.syllables_matcher.EnumMatcher;
import me.blvckbytes.syllables_matcher.NormalizedConstant;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

public abstract class SubCommand {

  public abstract CommandFailure onCommand(CommandSender sender, String[] args, Queue<NormalizedConstant<?>> queue);

  public abstract List<String> onTabComplete(CommandSender sender, String[] args);

  public abstract List<String> getPartialUsages(@Nullable Queue<NormalizedConstant<?>> actions, CommandSender sender);

  protected List<String> prependOwnAction(SubCommand subCommand, List<String> subUsages) {
    return subUsages
      .stream()
      .map(it -> subCommand.getCorrespondingAction().normalizedName + " " + it)
      .toList();
  }

  protected List<String> collectSubUsages(@Nullable Queue<NormalizedConstant<?>> actions, CommandSender sender, Map<NormalizedConstant<?>, SubCommand> subCommandMap) {
    var action = actions == null ? null : actions.poll();

    if (action != null) {
      var subCommand = subCommandMap.get(action);

      if (subCommand != null)
        return subCommand.getPartialUsages(actions, sender);
    }

    var usages = new ArrayList<String>();

    for (var subCommand : subCommandMap.values())
      usages.addAll(subCommand.getPartialUsages(actions, sender));

    return usages;
  }

  public abstract NormalizedConstant<?> getCorrespondingAction();

  public static CommandFailure tryRelayCommand(
    EnumMatcher<? extends Enum<?>> matcher,
    Map<NormalizedConstant<?>, SubCommand> subCommandMap,
    CommandSender sender, String[] args, Queue<NormalizedConstant<?>> actions
  ) {
    var subCommand = tryFindSubCommand(matcher, subCommandMap, args);

    if (subCommand == null)
      return CommandFailure.UNREGISTERED;

    var subArgs = new String[args.length - 1];
    System.arraycopy(args, 1, subArgs, 0, subArgs.length);

    actions.add(subCommand.getCorrespondingAction());

    return subCommand.onCommand(sender, subArgs, actions);
  }

  public static List<String> tryRelayTabComplete(
    EnumMatcher<? extends Enum<?>> matcher,
    Map<NormalizedConstant<?>, SubCommand> subCommandMap,
    CommandSender sender, String[] args
  ) {
    if (args.length == 1)
      return matcher.createCompletions(args[0]);

    var subCommand = tryFindSubCommand(matcher, subCommandMap, args);

    if (subCommand == null)
      return List.of();

    var subArgs = new String[args.length - 1];
    System.arraycopy(args, 1, subArgs, 0, subArgs.length);

    return subCommand.onTabComplete(sender, subArgs);
  }

  public static @Nullable SubCommand tryFindSubCommand(
    EnumMatcher<? extends Enum<?>> matcher,
    Map<NormalizedConstant<?>, SubCommand> subCommandMap,
    String[] args
  ) {
    if (args.length == 0)
      return null;

    var matchedAction = matcher.matchFirst(args[0]);

    if (matchedAction == null)
      return null;

    return subCommandMap.get(matchedAction);
  }
}
