package me.blvckbytes.bluemap_markers.command.sub.images.sub;

import me.blvckbytes.bluemap_markers.command.CommandResult;
import me.blvckbytes.bluemap_markers.command.SubCommand;
import me.blvckbytes.bluemap_markers.command.sub.images.ImagesAction;
import me.blvckbytes.syllables_matcher.NormalizedConstant;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Queue;

public class ImagesDownloadCommand extends SubCommand {

  @Override
  public CommandResult onCommand(CommandSender sender, String[] args, Queue<NormalizedConstant<?>> actions) {
    return CommandResult.INVALID_USAGE;
  }

  @Override
  public List<String> onTabComplete(CommandSender sender, String[] args) {
    return List.of();
  }

  @Override
  public List<String> getPartialUsages(@Nullable Queue<NormalizedConstant<?>> actions, CommandSender sender) {
    return List.of(getCorrespondingAction().normalizedName + " <URL> <Name>");
  }

  @Override
  public NormalizedConstant<?> getCorrespondingAction() {
    return ImagesAction.matcher.getNormalizedConstant(ImagesAction.DOWNLOAD);
  }
}
