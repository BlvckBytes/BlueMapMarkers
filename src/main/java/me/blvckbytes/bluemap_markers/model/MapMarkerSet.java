package me.blvckbytes.bluemap_markers.model;

import org.bukkit.World;
import org.jetbrains.annotations.Nullable;

public record MapMarkerSet(
  World world,
  String name,
  @Nullable String label,
  int sorting,
  boolean toggleable,
  boolean defaultHidden
) {
  public static final int DEFAULT_SORTING = 0;
  public static final boolean DEFAULT_TOGGLEABLE = false;
  public static final boolean DEFAULT_DEFAULT_HIDDEN = false;
}
