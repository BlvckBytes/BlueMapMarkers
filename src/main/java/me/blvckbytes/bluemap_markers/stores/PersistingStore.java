package me.blvckbytes.bluemap_markers.stores;

import com.google.common.base.Charsets;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonWriter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class PersistingStore<T> {

  private static final Gson SHARED_GSON_INSTANCE = new GsonBuilder().setPrettyPrinting().create();

  private final File dataFile;
  private final Logger logger;
  private final BukkitTask task;

  protected T data;
  private boolean isDirty;

  protected PersistingStore(Plugin plugin, Logger logger, String fileNameWithoutExtension) {
    this.dataFile = new File(plugin.getDataFolder(), fileNameWithoutExtension + ".json");
    this.logger = logger;

    load();

    this.task = Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, this::store, 0L, 20L * 30);
  }

  public void shutdown() {
    this.task.cancel();
    this.store();
  }

  /**
   * Transforms the specified data-type into json that can be persisted in a file
   */
  protected abstract JsonObject jsonify(@NotNull T value);

  /**
   * Revive json-data into the specified data-type
   * @param json Null for initial state, namely the absence of a file
   */
  protected abstract @NotNull T revive(@Nullable JsonObject json);

  /**
   * Provider of the class representing the specified data-type
   */
  protected abstract Class<?> getDataType();

  protected void markDirty() {
    this.isDirty = true;
  }

  protected void logWarning(String message) {
    logger.warning("[Persistence " + dataFile.getName() + "] " + message);
  }

  protected String normalizeIdentifier(String identifier) {
    var result = new StringBuilder(identifier.length());

    for (var charIndex = 0; charIndex < identifier.length(); ++charIndex) {
      var currentChar = identifier.charAt(charIndex);

      if (Character.isWhitespace(currentChar))
        continue;

      result.append(Character.toLowerCase(currentChar));
    }

    return result.toString();
  }

  private void load() {
    if (!dataFile.exists()) {
      this.data = revive(null);
      return;
    }

    if (dataFile.isDirectory())
      throw new IllegalStateException("There's a directory in place of the data-file " + dataFile);

    try (
      var reader = new FileReader(dataFile, Charsets.UTF_8);
    ) {
      this.data = revive(SHARED_GSON_INSTANCE.fromJson(reader, JsonObject.class));
    } catch (Exception e) {
      throw new IllegalStateException("An error occurred while trying to load from " + dataFile, e);
    }
  }

  private void store() {
    if (!isDirty)
      return;

    try (
      var fileWriter = new FileWriter(dataFile, Charsets.UTF_8);
      var jsonWriter = new JsonWriter(fileWriter)
    ) {
      SHARED_GSON_INSTANCE.toJson(jsonify(data), getDataType(), jsonWriter);
      isDirty = false;
    } catch (Exception e) {
      logger.log(Level.SEVERE, "An error occurred while trying to store to " + dataFile, e);
    }
  }
}
