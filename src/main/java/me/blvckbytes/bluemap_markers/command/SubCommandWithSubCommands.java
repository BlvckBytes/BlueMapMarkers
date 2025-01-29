package me.blvckbytes.bluemap_markers.command;

import me.blvckbytes.syllables_matcher.EnumMatcher;
import me.blvckbytes.syllables_matcher.NormalizedConstant;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public abstract class SubCommandWithSubCommands extends SubCommand {

  private final EnumMatcher<?> actionMatcher;
  private final Map<NormalizedConstant<?>, SubCommand> subCommands;

  protected SubCommandWithSubCommands(EnumMatcher<?> actionMatcher) {
    this.actionMatcher = actionMatcher;
    this.subCommands = new LinkedHashMap<>();
  }

  protected void registerSubCommand(SubCommand subCommand) {
    this.subCommands.put(subCommand.getCorrespondingAction(), subCommand);
  }

  @Override
  public CommandFailure onCommand(CommandSender sender, String[] args, Queue<NormalizedConstant<?>> actions) {
    return tryRelayCommand(actionMatcher, subCommands, sender, args, actions);
  }

  @Override
  public List<String> onTabComplete(CommandSender sender, String[] args) {
    return tryRelayTabComplete(actionMatcher, subCommands, sender, args);
  }

  @Override
  public List<String> getPartialUsages(@Nullable Queue<NormalizedConstant<?>> actions, CommandSender sender) {
    return prependOwnAction(this, collectSubUsages(actions, sender, subCommands));
  }
}
