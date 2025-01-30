package me.blvckbytes.bluemap_markers.command.sub.images;

import me.blvckbytes.bluemap_markers.command.SubCommandAction;
import me.blvckbytes.bluemap_markers.command.SubCommandWithSubCommands;
import me.blvckbytes.bluemap_markers.command.sub.images.sub.*;
import me.blvckbytes.bluemap_markers.stores.ImageStore;
import me.blvckbytes.syllables_matcher.NormalizedConstant;
import org.bukkit.plugin.Plugin;

public class ImagesCommand extends SubCommandWithSubCommands {

  public ImagesCommand(Plugin plugin, ImageStore imageStore) {
    super(ImagesAction.matcher);

    registerSubCommand(new ImagesDownloadCommand(imageStore));
    registerSubCommand(new ImagesDeleteCommand(imageStore));
    registerSubCommand(new ImagesDeleteRecursiveCommand(plugin, imageStore));
    registerSubCommand(new ImagesListCommand(imageStore));
    registerSubCommand(new ImagesRenameCommand(imageStore));
  }

  @Override
  public NormalizedConstant<?> getCorrespondingAction() {
    return SubCommandAction.matcher.getNormalizedConstant(SubCommandAction.IMAGES);
  }
}
