package me.blvckbytes.bluemap_markers.model;

import org.bukkit.World;

import java.util.List;

public record MapMarkerSet(
  World world,
  String name,
  String label,
  int sorting,
  boolean toggleable,
  boolean defaultHidden,
  List<MapMarker> members
) {}
