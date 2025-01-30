package me.blvckbytes.bluemap_markers.model;

public enum OperationStatus {
  SUCCESS,
  DUPLICATE_IDENTIFIER,
  IDENTIFIER_NOT_FOUND,
  IDENTIFIER_IN_ACTIVE_USE,
  INTERNAL_ERROR
  ;

  public OperationResult<?> asResult() {
    return new OperationResult<>(this, null);
  }

  public <T> OperationResult<T> asResult(T parameter) {
    return new OperationResult<>(this, parameter);
  }
}
