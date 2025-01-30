package me.blvckbytes.bluemap_markers.command;

import me.blvckbytes.bluemap_markers.command.sub.images.ImagesCommand;
import me.blvckbytes.bluemap_markers.command.sub.markers.MarkersCommand;
import me.blvckbytes.bluemap_markers.command.sub.sets.SetsCommand;
import me.blvckbytes.bluemap_markers.config.MainSection;
import me.blvckbytes.bluemap_markers.stores.ImageStore;
import me.blvckbytes.bukkitevaluable.ConfigKeeper;
import me.blvckbytes.syllables_matcher.NormalizedConstant;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class BlueMapMarkersCommand implements CommandExecutor, TabCompleter {

  private final ConfigKeeper<MainSection> config;
  private final Map<NormalizedConstant<?>, SubCommand> subCommands;

  public BlueMapMarkersCommand(
    Plugin plugin,
    ImageStore imageStore,
    ConfigKeeper<MainSection> config
  ) {
    this.config = config;
    this.subCommands = new LinkedHashMap<>();

    registerSubCommand(new ImagesCommand(plugin, imageStore));
    registerSubCommand(new SetsCommand());
    registerSubCommand(new MarkersCommand());
  }

  private void registerSubCommand(SubCommand command) {
    this.subCommands.put(command.getCorrespondingAction(), command);
  }

  @Override
  public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
    if (!CommandPermission.COMMAND_BLUE_MAP_MARKERS.hasPermission(sender)) {
      config.rootSection.playerMessages.missingCommandPermission.sendMessage(sender, config.rootSection.builtBaseEnvironment);
      return true;
    }

    var actions = new ArrayDeque<NormalizedConstant<?>>();
    var result = SubCommand.tryRelayCommand(SubCommandAction.matcher, subCommands, sender, args, actions);

    if (result == CommandFailure.INVALID_USAGE || result == CommandFailure.UNREGISTERED) {
      printHelp(actions, sender, label);
      return true;
    }

    if (result == CommandFailure.PLAYER_ONLY) {
      config.rootSection.playerMessages.playerOnlyCommand.sendMessage(sender, config.rootSection.builtBaseEnvironment);
      return true;
    }

    if (result == CommandFailure.MISSING_PERMISSION) {
      config.rootSection.playerMessages.missingCommandPermission.sendMessage(sender, config.rootSection.builtBaseEnvironment);
      return true;
    }

    return true;
  }

  private List<String> decidePartialUsages(@Nullable Queue<NormalizedConstant<?>> actions, CommandSender sender) {
    var partialUsages = new ArrayList<String>();

    NormalizedConstant<?> action;
    SubCommand actionTarget;

    if (actions == null || (action = actions.poll()) == null || (actionTarget = subCommands.get(action)) == null) {
      for (var subCommand : subCommands.values())
        partialUsages.addAll(subCommand.getPartialUsages(null, sender));

      return partialUsages;
    }

    return actionTarget.getPartialUsages(actions, sender);
  }

  private void printHelp(@Nullable Queue<NormalizedConstant<?>> actions, CommandSender sender, String label) {
    var usages = decidePartialUsages(actions, sender)
      .stream()
      .map(it -> "/" + label + " " + it)
      .toList();

    if (usages.size() == 1) {
      config.rootSection.playerMessages.singleUsageMessage.sendMessage(
        sender,
        config.rootSection.getBaseEnvironment()
          .withStaticVariable("usage", usages.get(0))
          .build()
      );
      return;
    }

    config.rootSection.playerMessages.multiUsageScreen.sendMessage(
      sender,
      config.rootSection.getBaseEnvironment()
        .withStaticVariable("usages", usages)
        .build()
    );
  }

  @Override
  public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
    if (!CommandPermission.COMMAND_BLUE_MAP_MARKERS.hasPermission(sender))
      return List.of();

    return SubCommand.tryRelayTabComplete(SubCommandAction.matcher, subCommands, sender, args);
  }
}
