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

public class ImagesDeleteCommand extends SubCommand {

  private final ImageStore imageStore;

  public ImagesDeleteCommand(ImageStore imageStore) {
    this.imageStore = imageStore;
  }

  @Override
  public @Nullable CommandFailure onCommand(CommandSender sender, String[] args, Queue<NormalizedConstant<?>> actions) {
    var parsedArgs = parseQuotesSupportingArguments(args);

    if (parsedArgs.size() != 1)
      return CommandFailure.INVALID_USAGE;

    var name = parsedArgs.get(0);

    switch (imageStore.deleteImage(name).status) {
      case SUCCESS -> sender.sendMessage("§aThe image " + name + " has been deleted successfully!");
      case IDENTIFIER_NOT_FOUND -> sender.sendMessage("§aThere is no image named " + name + "!");
      case IDENTIFIER_IN_ACTIVE_USE -> sender.sendMessage("§aThe image " + name + " is currently in use and cannot be deleted!");
      case INTERNAL_ERROR -> sender.sendMessage("§cAn internal error occurred; see console!");
      case DUPLICATE_IDENTIFIER -> throw new IllegalStateException();
    }

    return null;
  }

  @Override
  public List<String> onTabComplete(CommandSender sender, String[] args) {
    return List.of();
  }

  @Override
  public List<String> getPartialUsages(@Nullable Queue<NormalizedConstant<?>> actions, CommandSender sender) {
    return List.of(getCorrespondingAction().normalizedName + " <Name>");
  }

  @Override
  public NormalizedConstant<?> getCorrespondingAction() {
    return ImagesAction.matcher.getNormalizedConstant(ImagesAction.DELETE);
  }
}
