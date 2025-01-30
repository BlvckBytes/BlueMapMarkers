package me.blvckbytes.bluemap_markers.command.sub.images.sub;

import me.blvckbytes.bluemap_markers.command.CommandFailure;
import me.blvckbytes.bluemap_markers.command.SubCommand;
import me.blvckbytes.bluemap_markers.command.sub.images.ImagesAction;
import me.blvckbytes.bluemap_markers.model.MapImage;
import me.blvckbytes.bluemap_markers.stores.ImageStore;
import me.blvckbytes.syllables_matcher.NormalizedConstant;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Queue;

public class ImagesListCommand extends SubCommand {

  private final ImageStore imageStore;

  public ImagesListCommand(ImageStore imageStore) {
    this.imageStore = imageStore;
  }

  @Override
  public @Nullable CommandFailure onCommand(CommandSender sender, String[] args, Queue<NormalizedConstant<?>> actions) {
    var mapImages = imageStore.listImages();

    if (mapImages.isEmpty()) {
      sender.sendMessage("§cThere aren't any existing images yet!");
      return null;
    }

    String pattern = null;

    if (args.length > 0) {
      pattern = String.join(" ", args);
      mapImages = applyPatternFilter(mapImages, MapImage::fileName, pattern);
    }

    if (mapImages.isEmpty()) {
      sender.sendMessage("§cYour filter of §4" + pattern + " §cdid not yield any results!");
      return null;
    }

    sender.sendMessage("§8§m                         §8[§aImages§8]§8§m                         ");

    if (pattern != null)
      sender.sendMessage("§7(With filter §a" + pattern + "§7)");

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
    return List.of(getCorrespondingAction().normalizedName + " [Pattern]");
  }

  @Override
  public NormalizedConstant<?> getCorrespondingAction() {
    return ImagesAction.matcher.getNormalizedConstant(ImagesAction.LIST);
  }
}
