package me.blvckbytes.bluemap_markers.command.sub.images.sub;

import me.blvckbytes.bluemap_markers.command.CommandFailure;
import me.blvckbytes.bluemap_markers.command.SubCommand;
import me.blvckbytes.bluemap_markers.command.sub.images.ImagesAction;
import me.blvckbytes.bluemap_markers.config.MainSection;
import me.blvckbytes.bluemap_markers.stores.ImageStore;
import me.blvckbytes.bukkitevaluable.ConfigKeeper;
import me.blvckbytes.syllables_matcher.NormalizedConstant;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Queue;
import java.util.StringJoiner;

public class ImagesListCommand extends SubCommand {

  private final ImageStore imageStore;
  private final ConfigKeeper<MainSection> config;

  public ImagesListCommand(ImageStore imageStore, ConfigKeeper<MainSection> config) {
    this.imageStore = imageStore;
    this.config = config;
  }

  @Override
  public @Nullable CommandFailure onCommand(CommandSender sender, String[] args, Queue<NormalizedConstant<?>> actions) {
    var mapImages = imageStore.listImages();

    if (mapImages.isEmpty()) {
      sender.sendMessage("§cThere aren't any existing images yet!");
      return null;
    }

    int pageSize = config.rootSection.images.imagesListCommandPageSize;
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
      var patternJoiner = new StringJoiner(" ");

      for (var i = 1; i < args.length; ++i)
        patternJoiner.add(args[i]);

      stringPattern = patternJoiner.toString();
      var pattern = tryCompileGlobPattern(stringPattern, true);

      if (pattern == null) {
        sender.sendMessage("§cMalformed pattern provided §4" + stringPattern + "§c!");
        return null;
      }

      mapImages = mapImages.stream()
        .filter(it -> pattern.matcher(it.fileName()).matches())
        .toList();
    }

    if (mapImages.isEmpty()) {
      sender.sendMessage("§cYour filter of §4" + stringPattern + " §cdid not yield any results!");
      return null;
    }

    int numberOfEntries = mapImages.size();
    int numberOfPages = (numberOfEntries + (pageSize - 1)) / pageSize;

    if (page > numberOfPages)
      page = numberOfPages;

    if (numberOfEntries > pageSize) {
      var firstIndex = (page - 1) * pageSize;
      var lastIndex = Math.min(numberOfEntries, firstIndex + pageSize);
      mapImages = mapImages.subList(firstIndex, lastIndex);
    }

    sender.sendMessage("§8§m                         §8[§aImages§8]§8§m                         ");
    sender.sendMessage("§7Page " + page + "/" + numberOfPages + "; " + pageSize + " per page; " + numberOfEntries + " total");

    if (stringPattern != null)
      sender.sendMessage("§7(With pattern §a" + stringPattern + "§7)");

    for (var mapImage : mapImages) {
      var fileSize = Math.round(mapImage.fileSizeInBytes() / 1000.0 * 100.0) / 100.0;
      sender.sendMessage("§8> §a" + mapImage.fileName() + " §7(" + fileSize + "KB)");
    }

    sender.sendMessage("§8§m                         §8[§aImages§8]§8§m                         ");

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
    return ImagesAction.matcher.getNormalizedConstant(ImagesAction.LIST);
  }
}
