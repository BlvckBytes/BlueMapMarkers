package me.blvckbytes.bluemap_markers.command.sub.sets.sub;

import me.blvckbytes.bluemap_markers.command.CommandFailure;
import me.blvckbytes.bluemap_markers.command.SubCommand;
import me.blvckbytes.bluemap_markers.command.sub.sets.SetsAction;
import me.blvckbytes.bluemap_markers.config.MainSection;
import me.blvckbytes.bluemap_markers.stores.MarkerSetStore;
import me.blvckbytes.bukkitevaluable.ConfigKeeper;
import me.blvckbytes.syllables_matcher.NormalizedConstant;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Queue;

public class SetsListCommand extends SubCommand {

  private final MarkerSetStore markerSetStore;
  private final ConfigKeeper<MainSection> config;

  public SetsListCommand(MarkerSetStore markerSetStore, ConfigKeeper<MainSection> config) {
    this.markerSetStore = markerSetStore;
    this.config = config;
  }

  @Override
  public @Nullable CommandFailure onCommand(CommandSender sender, String[] args, Queue<NormalizedConstant<?>> actions) {
    var markerSets = markerSetStore.listMarkerSets();

    if (markerSets.isEmpty()) {
      sender.sendMessage("§cThere aren't any existing marker-sets yet!");
      return null;
    }

    int pageSize = config.rootSection.markers.markerSetsListCommandPageSize;
    int page = 1;

    if (args.length >= 1) {
      try {
        page = Integer.parseInt(args[0]);
      } catch (Exception e) {
        sender.sendMessage("§cMalformed page-number " + args[0]);
        return null;
      }
    }

    if (page <= 0)
      page = 1;

    String stringPattern = null;

    if (args.length >= 2) {
      stringPattern = joinArgsStartingAtIndex(args, 1);
      var pattern = tryCompileGlobPattern(stringPattern, true);

      if (pattern == null) {
        sender.sendMessage("§cMalformed pattern provided §4" + stringPattern + "§c!");
        return null;
      }

      markerSets = markerSets.stream()
        .filter(it -> (pattern.matcher(it.name()).matches() || it.label() != null && pattern.matcher(it.label()).matches()))
        .toList();
    }

    if (markerSets.isEmpty()) {
      sender.sendMessage("§cYour filter of §4" + stringPattern + " §cdid not yield any results!");
      return null;
    }

    int numberOfEntries = markerSets.size();
    int numberOfPages = (numberOfEntries + (pageSize - 1)) / pageSize;

    if (page > numberOfPages)
      page = numberOfPages;

    if (numberOfEntries > pageSize) {
      var firstIndex = (page - 1) * pageSize;
      var lastIndex = Math.min(numberOfEntries, firstIndex + pageSize);
      markerSets = markerSets.subList(firstIndex, lastIndex);
    }

    sender.sendMessage("§8§m                         §8[§aMarker-Sets§8]§8§m                         ");
    sender.sendMessage("§7Page " + page + "/" + numberOfPages + "; " + pageSize + " per page; " + numberOfEntries + " total");

    if (stringPattern != null)
      sender.sendMessage("§7(With pattern §a" + stringPattern + "§7)");

    for (var markerSet : markerSets) {
      sender.sendMessage(
        "§8> §a" + markerSet.name() + " @ " + markerSet.world().getName() +
          " label=" + markerSet.label() + "," +
          " sorting=" + markerSet.sorting() + "," +
          " toggleable=" + markerSet.toggleable() + "," +
          " defaultHidden=" + markerSet.defaultHidden()
      );
    }

    sender.sendMessage("§8§m                         §8[§aMarker-Sets§8]§8§m                         ");

    return null;
  }

  @Override
  public List<String> onTabComplete(CommandSender sender, String[] args) {
    return List.of();
  }

  @Override
  public List<String> getPartialUsages(@Nullable Queue<NormalizedConstant<?>> actions, CommandSender sender) {
    return List.of(getCorrespondingAction().getNormalizedName() + " [Page] [Pattern]");
  }

  @Override
  public NormalizedConstant<?> getCorrespondingAction() {
    return SetsAction.matcher.getNormalizedConstant(SetsAction.LIST);
  }
}
