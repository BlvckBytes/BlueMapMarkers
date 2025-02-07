package me.blvckbytes.bluemap_markers.command.sub.images.sub;

import me.blvckbytes.bluemap_markers.command.CommandFailure;
import me.blvckbytes.bluemap_markers.command.SubCommand;
import me.blvckbytes.bluemap_markers.command.sub.images.ImagesAction;
import me.blvckbytes.bluemap_markers.model.MapImage;
import me.blvckbytes.bluemap_markers.stores.ImageStore;
import me.blvckbytes.syllables_matcher.NormalizedConstant;
import org.apache.commons.lang.StringUtils;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Queue;

public class ImagesRenameCommand extends SubCommand {

  private final ImageStore imageStore;

  public ImagesRenameCommand(ImageStore imageStore) {
    this.imageStore = imageStore;
  }

  @Override
  public @Nullable CommandFailure onCommand(CommandSender sender, String[] args, Queue<NormalizedConstant<?>> actions) {
    var parsedArgs = parseQuotesSupportingArguments(args);

    if (parsedArgs.size() != 2)
      return CommandFailure.INVALID_USAGE;

    var oldName = parsedArgs.get(0);
    var newName = parsedArgs.get(1);

    switch (imageStore.renameImage(oldName, newName).status) {
      case SUCCESS -> sender.sendMessage("§aSuccessfully renamed " + oldName + " to " + newName + ".");
      case IDENTIFIER_NOT_FOUND -> sender.sendMessage("§cCould not locate an image with name " + oldName + "!");
      case DUPLICATE_IDENTIFIER -> sender.sendMessage("§cThe new name " + newName + " is already in use!");
      case INTERNAL_ERROR -> sender.sendMessage("§cAn internal error occurred; see console!");
      case IDENTIFIER_IN_ACTIVE_USE -> throw new IllegalStateException();
    }

    return null;
  }

  @Override
  public List<String> onTabComplete(CommandSender sender, String[] args) {
    if (args.length == 1) {
      return imageStore.listImages().stream()
        .map(MapImage::fileName)
        .filter(fileName -> StringUtils.containsIgnoreCase(fileName, args[0]))
        .toList();
    }

    return List.of();
  }

  @Override
  public List<String> getPartialUsages(@Nullable Queue<NormalizedConstant<?>> actions, CommandSender sender) {
    return List.of(getCorrespondingAction().getNormalizedName() + " <Old-Name> <New-Name>");
  }

  @Override
  public NormalizedConstant<?> getCorrespondingAction() {
    return ImagesAction.matcher.getNormalizedConstant(ImagesAction.RENAME);
  }
}
