package me.blvckbytes.bluemap_markers.model;

import org.jetbrains.annotations.Nullable;

import java.time.LocalDateTime;

public record MapImage(
  String fileName,
  String absolutePath,
  long fileSizeInBytes,
  @Nullable LocalDateTime createdAt,
  @Nullable LocalDateTime updatedAt
) {}
