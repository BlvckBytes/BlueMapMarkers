package me.blvckbytes.bluemap_markers.model;

import org.jetbrains.annotations.Nullable;

public record VariableValue<T>(
  TemplateVariableType<T> type,
  @Nullable T value
) {}
