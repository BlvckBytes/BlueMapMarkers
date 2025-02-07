package me.blvckbytes.bluemap_markers.command.sub.images.sub;

import me.blvckbytes.bluemap_markers.command.CommandFailure;
import me.blvckbytes.bluemap_markers.command.SubCommand;
import me.blvckbytes.bluemap_markers.command.sub.images.ImagesAction;
import me.blvckbytes.bluemap_markers.stores.ImageStore;
import me.blvckbytes.syllables_matcher.NormalizedConstant;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Queue;

public class ImagesDownloadCommand extends SubCommand {

  private final ImageStore imageStore;

  public ImagesDownloadCommand(ImageStore imageStore) {
    this.imageStore = imageStore;
  }

  @Override
  public @Nullable CommandFailure onCommand(CommandSender sender, String[] args, Queue<NormalizedConstant<?>> actions) {
    var parsedArgs = parseQuotesSupportingArguments(args);

    if (parsedArgs.size() != 2)
      return CommandFailure.INVALID_USAGE;

    var url = parsedArgs.get(0);
    var name = parsedArgs.get(1);

    var result = imageStore.downloadImage(name, url);

    if (result.parameter != null) {
      switch (result.parameter) {
        case NAME_CONTAINS_EXTENSION -> sender.sendMessage("§cThe file-name " + name + " contains an extension; it is automatically determined and has to be left out!");
        case URL_INACCESSIBLE -> sender.sendMessage("§cThe URL " + url + " could not be accessed successfully!");
        case URL_MALFORMED -> sender.sendMessage("§cThe URL " + url + " is malformed!");
        case URL_NO_SUPPORTED_IMAGE -> sender.sendMessage("§cThe URL " + url + " does not point *directly* at a valid/supported image-file!");
      }
      return null;
    }

    switch (result.status) {
      case SUCCESS -> sender.sendMessage("§aThe url " + url + " has been successfully downloaded into " + name);
      case DUPLICATE_IDENTIFIER -> sender.sendMessage("§cThe name " + name + " is already in use!");
      case INTERNAL_ERROR -> sender.sendMessage("§cAn internal error occurred; see console!");
      case IDENTIFIER_IN_ACTIVE_USE -> throw new IllegalStateException();
    }

    return null;
  }

  @Override
  public List<String> onTabComplete(CommandSender sender, String[] args) {
    return List.of();
  }

  @Override
  public List<String> getPartialUsages(@Nullable Queue<NormalizedConstant<?>> actions, CommandSender sender) {
    return List.of(getCorrespondingAction().getNormalizedName() + " <URL> <Name>");
  }

  @Override
  public NormalizedConstant<?> getCorrespondingAction() {
    return ImagesAction.matcher.getNormalizedConstant(ImagesAction.DOWNLOAD);
  }
}
