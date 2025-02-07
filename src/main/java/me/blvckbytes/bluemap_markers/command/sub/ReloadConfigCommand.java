package me.blvckbytes.bluemap_markers.command.sub;

import me.blvckbytes.bluemap_markers.command.CommandFailure;
import me.blvckbytes.bluemap_markers.command.SubCommand;
import me.blvckbytes.bluemap_markers.command.SubCommandAction;
import me.blvckbytes.bluemap_markers.config.MainSection;
import me.blvckbytes.bukkitevaluable.ConfigKeeper;
import me.blvckbytes.syllables_matcher.NormalizedConstant;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Queue;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ReloadConfigCommand extends SubCommand {

  private final ConfigKeeper<MainSection> config;
  private final Logger logger;

  public ReloadConfigCommand(ConfigKeeper<MainSection> config, Logger logger) {
    this.config = config;
    this.logger = logger;
  }

  @Override
  public @Nullable CommandFailure onCommand(CommandSender sender, String[] args, Queue<NormalizedConstant<?>> queue) {
    if (args.length != 0)
      return CommandFailure.INVALID_USAGE;

    try {
      this.config.reload();
      sender.sendMessage("§aThe configuration has been reloaded successfully!");
    } catch (Exception e) {
      logger.log(Level.SEVERE, "An error occurred while trying to reload the config", e);
      sender.sendMessage("§cAn error occurred while trying to reload the configuration; check the console!");
    }

    return null;
  }

  @Override
  public List<String> onTabComplete(CommandSender sender, String[] args) {
    return List.of();
  }

  @Override
  public List<String> getPartialUsages(@Nullable Queue<NormalizedConstant<?>> actions, CommandSender sender) {
    return List.of(getCorrespondingAction().getNormalizedName());
  }

  @Override
  public NormalizedConstant<?> getCorrespondingAction() {
    return SubCommandAction.matcher.getNormalizedConstant(SubCommandAction.RELOAD_CONFIG);
  }
}
