package me.blvckbytes.bluemap_markers.command.sub.markers;

import me.blvckbytes.syllables_matcher.EnumMatcher;

public enum MarkersAction {
  CREATE,
  DELETE,
  RENAME,
  TELEPORT_TO,
  LIST,
  NEAR,
  TOGGLE_LISTED,
  SET_SORTING,
  SET_TEMPLATE,
  MOVE_HERE,
  MOVE_TO,
  SET_VARIABLE,
  UNSET_VARIABLE,
  LIST_VARIABLES,
  SET_SET,
  ;

  public static final EnumMatcher<MarkersAction> matcher = new EnumMatcher<>(values());
}
