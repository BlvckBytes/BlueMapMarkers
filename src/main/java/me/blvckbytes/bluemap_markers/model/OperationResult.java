package me.blvckbytes.bluemap_markers.model;

import org.jetbrains.annotations.Nullable;

public class OperationResult<T> {

  public final OperationStatus status;
  public final @Nullable T parameter;

  public OperationResult(OperationStatus status, @Nullable T parameter) {
    this.status = status;
    this.parameter = parameter;
  }
}
