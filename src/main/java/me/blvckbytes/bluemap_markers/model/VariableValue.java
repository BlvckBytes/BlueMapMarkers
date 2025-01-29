package me.blvckbytes.bluemap_markers.model;

import me.blvckbytes.bluemap_markers.config.TemplateVariableType;
import org.jetbrains.annotations.Nullable;

public record VariableValue(
  TemplateVariableType type,
  @Nullable String value
) {}
