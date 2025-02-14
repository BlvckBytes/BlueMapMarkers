package me.blvckbytes.bluemap_markers.command.sub.markers.sub;

import me.blvckbytes.bluemap_markers.command.CommandFailure;
import me.blvckbytes.bluemap_markers.command.SubCommand;
import me.blvckbytes.bluemap_markers.command.sub.markers.MarkersAction;
import me.blvckbytes.syllables_matcher.NormalizedConstant;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Queue;

public class MarkersSetTemplateCommand extends SubCommand {

  @Override
  public @Nullable CommandFailure onCommand(CommandSender sender, String[] args, Queue<NormalizedConstant<?>> actions) {
    return CommandFailure.INVALID_USAGE;
  }

  @Override
  public List<String> onTabComplete(CommandSender sender, String[] args) {
    return List.of();
  }

  @Override
  public List<String> getPartialUsages(@Nullable Queue<NormalizedConstant<?>> actions, CommandSender sender) {
    return List.of(getCorrespondingAction().getNormalizedName() + " <Name> <Template>");
  }

  @Override
  public NormalizedConstant<?> getCorrespondingAction() {
    return MarkersAction.matcher.getNormalizedConstant(MarkersAction.SET_TEMPLATE);
  }
}
