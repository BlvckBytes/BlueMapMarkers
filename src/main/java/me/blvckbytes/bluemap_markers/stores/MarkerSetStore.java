package me.blvckbytes.bluemap_markers.stores;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import me.blvckbytes.bluemap_markers.model.MapMarkerSet;
import me.blvckbytes.bluemap_markers.model.OperationResult;
import me.blvckbytes.bluemap_markers.model.OperationStatus;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.logging.Logger;

public class MarkerSetStore extends PersistingStore<Map<String, MapMarkerSet>> {

  // TODO: Call events when modifying data as to push latest state to BlueMap

  private static final DateTimeFormatter PERSISTENCE_DATE_TIME_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

  public MarkerSetStore(Plugin plugin, Logger logger) {
    super(plugin, logger, "marker-sets");
  }

  public @Nullable MapMarkerSet getMarkerSet(String name) {
    return data.get(normalizeIdentifier(name));
  }

  public List<MapMarkerSet> listMarkerSets() {
    return new ArrayList<>(data.values());
  }

  public OperationResult<?> createMarkerSet(String name, World world) {
    var normalizedName = normalizeIdentifier(name);

    if (data.containsKey(normalizedName))
      return OperationStatus.DUPLICATE_IDENTIFIER.asResult();

    if (world == null) {
      logWarning("Tried to create a marker-set with a null-valued world!");
      return OperationStatus.INTERNAL_ERROR.asResult();
    }

    var newMarkerSet = new MapMarkerSet(
      world, name, null,
      MapMarkerSet.DEFAULT_SORTING,
      MapMarkerSet.DEFAULT_TOGGLEABLE,
      MapMarkerSet.DEFAULT_DEFAULT_HIDDEN,
      LocalDateTime.now(),
      null
    );

    data.put(normalizedName, newMarkerSet);
    markDirty();

    return OperationStatus.SUCCESS.asResult();
  }

  public OperationResult<?> deleteMarkerSet(String name, boolean force) {
    var normalizedName = normalizeIdentifier(name);
    var markerSet = data.get(normalizedName);

    if (markerSet == null)
      return OperationStatus.IDENTIFIER_NOT_FOUND.asResult();

    if (!force) {
      // TODO: Check if this there are any markers attached to this set
    }

    data.remove(normalizedName);
    markDirty();

    return OperationStatus.SUCCESS.asResult();
  }

  public OperationResult<?> moveMarkerSet(String name, World world) {
    return tryModifyByName(name, markerSet -> new MapMarkerSet(
      world,
      markerSet.name(),
      markerSet.label(),
      markerSet.sorting(),
      markerSet.toggleable(),
      markerSet.defaultHidden(),
      markerSet.createdAt(),
      LocalDateTime.now()
    ));
  }

  public OperationResult<?> renameMarkerSet(String oldName, String newName) {
    return tryModifyByName(oldName, markerSet -> new MapMarkerSet(
      markerSet.world(),
      newName,
      markerSet.label(),
      markerSet.sorting(),
      markerSet.toggleable(),
      markerSet.defaultHidden(),
      markerSet.createdAt(),
      LocalDateTime.now()
    ));
  }

  public OperationResult<?> setMarkerSetLabel(String name, String label) {
    return tryModifyByName(name, markerSet -> new MapMarkerSet(
      markerSet.world(),
      markerSet.name(),
      label,
      markerSet.sorting(),
      markerSet.toggleable(),
      markerSet.defaultHidden(),
      markerSet.createdAt(),
      LocalDateTime.now()
    ));
  }

  public OperationResult<?> setMarkerSetSorting(String name, int sorting) {
    return tryModifyByName(name, markerSet -> new MapMarkerSet(
      markerSet.world(),
      markerSet.name(),
      markerSet.label(),
      sorting,
      markerSet.toggleable(),
      markerSet.defaultHidden(),
      markerSet.createdAt(),
      LocalDateTime.now()
    ));
  }

  public OperationResult<?> setMarkerSetToggleable(String name, boolean toggleable) {
    return tryModifyByName(name, markerSet -> new MapMarkerSet(
      markerSet.world(),
      markerSet.name(),
      markerSet.label(),
      markerSet.sorting(),
      toggleable,
      markerSet.defaultHidden(),
      markerSet.createdAt(),
      LocalDateTime.now()
    ));
  }

  public OperationResult<?> setMarkerSetDefaultHidden(String name, boolean defaultHidden) {
    return tryModifyByName(name, markerSet -> new MapMarkerSet(
      markerSet.world(),
      markerSet.name(),
      markerSet.label(),
      markerSet.sorting(),
      markerSet.toggleable(),
      defaultHidden,
      markerSet.createdAt(),
      LocalDateTime.now()
    ));
  }

  private OperationResult<?> tryModifyByName(String name, Function<MapMarkerSet, MapMarkerSet> modifier) {
    var normalizedName = normalizeIdentifier(name);
    var markerSet = data.get(normalizedName);

    if (markerSet == null)
      return OperationStatus.IDENTIFIER_NOT_FOUND.asResult();

    var modifiedMarkerSet = modifier.apply(markerSet);
    var normalizedNewName = normalizeIdentifier(modifiedMarkerSet.name());

    if (!normalizedNewName.equals(normalizedName)) {
      if (data.containsKey(normalizedNewName))
        return OperationStatus.DUPLICATE_IDENTIFIER.asResult();

      data.remove(normalizedName);
    }

    data.put(normalizeIdentifier(modifiedMarkerSet.name()), modifiedMarkerSet);

    // TODO: Don't forget to notify this set's members as well!
    markDirty();

    return OperationStatus.SUCCESS.asResult();
  }

  // ================================================================================
  // Persistence
  // ================================================================================

  @Override
  protected JsonObject jsonify(@NotNull Map<String, MapMarkerSet> value) {
    var result = new JsonObject();
    var markerSets = new JsonArray();

    for (var markerSet : value.values()) {
      var markerSetObject = new JsonObject();

      markerSetObject.addProperty("world", markerSet.world().getName());
      markerSetObject.addProperty("name", markerSet.name());
      markerSetObject.addProperty("label", markerSet.label());
      markerSetObject.addProperty("sorting", markerSet.sorting());
      markerSetObject.addProperty("toggleable", markerSet.toggleable());
      markerSetObject.addProperty("defaultHidden", markerSet.defaultHidden());
      markerSetObject.addProperty("createdAt", PERSISTENCE_DATE_TIME_FORMATTER.format(markerSet.createdAt()));

      if (markerSet.updatedAt() != null)
        markerSetObject.addProperty("updatedAt", PERSISTENCE_DATE_TIME_FORMATTER.format(markerSet.updatedAt()));

      markerSets.add(markerSetObject);
    }

    result.add("marker-sets", markerSets);
    return result;
  }

  @Override
  protected @NotNull Map<String, MapMarkerSet> revive(@Nullable JsonObject json) {
    if (json == null)
      return new HashMap<>();

    if (!(json.get("marker-sets") instanceof JsonArray markerArray)) {
      logWarning("Expected key marker-sets to be an array");
      return new HashMap<>();
    }

    var result = new HashMap<String, MapMarkerSet>();

    for (var markerIndex = 0; markerIndex < markerArray.size(); ++markerIndex) {
      var markerElement = markerArray.get(markerIndex);

      if (!(markerElement instanceof JsonObject markerObject)) {
        logWarning("Expected item marker-sets.[" + markerIndex + "] to be an object");
        continue;
      }

      if (!(markerObject.get("world") instanceof JsonPrimitive worldPrimitive)) {
        logWarning("Expected item marker-sets.[" + markerIndex + "].world to be a primitive");
        continue;
      }

      var worldName = worldPrimitive.getAsString();
      var world = Bukkit.getWorld(worldName);

      if (world == null) {
        logWarning("Could not locate world \"" + worldName + "\" of marker-sets.[" + markerIndex + "]; skipping item");
        continue;
      }

      if (!(markerObject.get("name") instanceof JsonPrimitive namePrimitive)) {
        logWarning("Expected item marker-sets.[" + markerIndex + "].name to be a primitive");
        continue;
      }

      var name = namePrimitive.getAsString();

      var normalizedName = normalizeIdentifier(name);

      if (result.containsKey(normalizedName)) {
        logWarning("Encountered duplicate entry with normalized-name of \"" + normalizedName + "\"; skipping");
        continue;
      }

      String label = null;

      if (markerObject.get("label") instanceof JsonPrimitive labelPrimitive)
        label = labelPrimitive.getAsString();

      var sorting = MapMarkerSet.DEFAULT_SORTING;

      if (markerObject.get("sorting") instanceof JsonPrimitive sortingPrimitive && sortingPrimitive.isNumber())
        sorting = sortingPrimitive.getAsInt();

      var toggleable = MapMarkerSet.DEFAULT_TOGGLEABLE;

      if (markerObject.get("toggleable") instanceof JsonPrimitive toggleablePrimitive && toggleablePrimitive.isBoolean())
        toggleable = toggleablePrimitive.getAsBoolean();

      var defaultHidden = MapMarkerSet.DEFAULT_DEFAULT_HIDDEN;

      if (markerObject.get("defaultHidden") instanceof JsonPrimitive defaultHiddenPrimitive && defaultHiddenPrimitive.isBoolean())
        defaultHidden = defaultHiddenPrimitive.getAsBoolean();

      LocalDateTime createdAt = null;

      if (markerObject.get("createdAt") instanceof JsonPrimitive createdAtPrimitive)
        createdAt = tryParseDateTime(createdAtPrimitive.getAsString());

      if (createdAt == null)
        createdAt = LocalDateTime.now();

      LocalDateTime updatedAt = null;

      if (markerObject.get("updatedAt") instanceof JsonPrimitive updatedAtPrimitive)
        updatedAt = tryParseDateTime(updatedAtPrimitive.getAsString());

      result.put(
        normalizedName,
        new MapMarkerSet(world, name, label, sorting, toggleable, defaultHidden, createdAt, updatedAt)
      );
    }

    return result;
  }

  private @Nullable LocalDateTime tryParseDateTime(String input) {
    try {
      return LocalDateTime.from(PERSISTENCE_DATE_TIME_FORMATTER.parse(input));
    } catch (Exception e) {
      return null;
    }
  }

  @Override
  protected Class<?> getDataType() {
    return JsonObject.class;
  }
}
