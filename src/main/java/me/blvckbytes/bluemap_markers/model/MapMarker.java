package me.blvckbytes.bluemap_markers.model;

import org.jetbrains.annotations.Nullable;

import java.util.Map;

public record MapMarker(
  String name,
  String label,
  @Nullable MarkerTemplate template,
  Map<String, String> rawVariables,
  Map<String, VariableValue<?>> revivedVariables,
  int x,
  int y,
  int z,
  int sorting,
  boolean listed
) {}
