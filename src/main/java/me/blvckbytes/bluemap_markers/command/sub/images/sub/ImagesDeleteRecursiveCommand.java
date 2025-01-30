package me.blvckbytes.bluemap_markers.command.sub.images.sub;

import me.blvckbytes.bluemap_markers.command.CommandFailure;
import me.blvckbytes.bluemap_markers.command.SubCommand;
import me.blvckbytes.bluemap_markers.command.sub.images.ImagesAction;
import me.blvckbytes.bluemap_markers.stores.ImageStore;
import me.blvckbytes.syllables_matcher.NormalizedConstant;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;

public class ImagesDeleteRecursiveCommand extends SubCommand {

  private static final int CONFIRMATION_DURATION_S = 5;
  private final Map<CommandSender, Map<String, BukkitTask>> confirmationExpirationTaskByPathLowerBySender;

  private final ImageStore imageStore;
  private final Plugin plugin;

  public ImagesDeleteRecursiveCommand(Plugin plugin, ImageStore imageStore) {
    this.imageStore = imageStore;
    this.plugin = plugin;
    this.confirmationExpirationTaskByPathLowerBySender = new HashMap<>();
  }

  @Override
  public @Nullable CommandFailure onCommand(CommandSender sender, String[] args, Queue<NormalizedConstant<?>> actions) {
    var parsedArgs = parseQuotesSupportingArguments(args);

    if (parsedArgs.size() != 1)
      return CommandFailure.INVALID_USAGE;

    var path = parsedArgs.get(0);

    var senderBucket = this.confirmationExpirationTaskByPathLowerBySender.computeIfAbsent(sender, k -> new HashMap<>());
    var pathLower = path.toLowerCase();

    if (!senderBucket.containsKey(pathLower)) {
      var expirationTask = Bukkit.getScheduler().runTaskLater(
        plugin,
        () -> clearConfirmationEntry(sender, path, true),
        20L * CONFIRMATION_DURATION_S
      );

      senderBucket.put(pathLower, expirationTask);

      sender.sendMessage("§aPlease execute the same command again within " + CONFIRMATION_DURATION_S + "s in order to confirm and dispatch your action.");
      return null;
    }

    clearConfirmationEntry(sender, path, false);

    switch (imageStore.deleteRecursive(path).status) {
      case SUCCESS -> sender.sendMessage("§aThe path " + path + " has been deleted successfully!");
      case IDENTIFIER_NOT_FOUND -> sender.sendMessage("§aThere is no path named " + path + "!");
      case INTERNAL_ERROR -> sender.sendMessage("§cAn internal error occurred; see console!");
      case IDENTIFIER_IN_ACTIVE_USE, DUPLICATE_IDENTIFIER -> throw new IllegalStateException();
    }

    return null;
  }

  @Override
  public List<String> onTabComplete(CommandSender sender, String[] args) {
    return List.of();
  }

  @Override
  public List<String> getPartialUsages(@Nullable Queue<NormalizedConstant<?>> actions, CommandSender sender) {
    return List.of(getCorrespondingAction().normalizedName + " <Path>");
  }

  @Override
  public NormalizedConstant<?> getCorrespondingAction() {
    return ImagesAction.matcher.getNormalizedConstant(ImagesAction.DELETE_RECURSIVE);
  }

  private void clearConfirmationEntry(CommandSender sender, String path, boolean isExpiry) {
    var senderBucket = confirmationExpirationTaskByPathLowerBySender.get(sender);

    if (senderBucket == null)
      return;

    var pendingTask = senderBucket.remove(path.toLowerCase());

    if (pendingTask != null) {
      pendingTask.cancel();

      if (isExpiry && !(sender instanceof Player player && !player.isOnline()))
        sender.sendMessage("§cYour confirmation for the recursive deletion of path " + path + " has expired");
    }

    if (senderBucket.isEmpty())
      confirmationExpirationTaskByPathLowerBySender.remove(sender);
  }
}
