package me.blvckbytes.bluemap_markers.model;

public record MapImage(
  String fileName,
  String absolutePath,
  int fileSizeInBytes
) {}
