package me.blvckbytes.bluemap_markers.model;

import java.util.Map;

public record MapMarker(
  String name,
  Map<String, VariableValue> variables,
  int x,
  int y,
  int z,
  int sorting,
  boolean listed
) {}
