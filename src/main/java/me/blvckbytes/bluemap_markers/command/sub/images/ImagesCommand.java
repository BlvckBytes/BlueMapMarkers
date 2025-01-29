package me.blvckbytes.bluemap_markers.command.sub.images;

import me.blvckbytes.bluemap_markers.command.SubCommandAction;
import me.blvckbytes.bluemap_markers.command.SubCommandWithSubCommands;
import me.blvckbytes.bluemap_markers.command.sub.images.sub.*;
import me.blvckbytes.syllables_matcher.NormalizedConstant;

public class ImagesCommand extends SubCommandWithSubCommands {

  public ImagesCommand() {
    super(ImagesAction.matcher);

    registerSubCommand(new ImagesDeleteCommand());
    registerSubCommand(new ImagesDownloadCommand());
    registerSubCommand(new ImagesListCommand());
    registerSubCommand(new ImagesRenameCommand());
  }

  @Override
  public NormalizedConstant<?> getCorrespondingAction() {
    return SubCommandAction.matcher.getNormalizedConstant(SubCommandAction.IMAGES);
  }
}
