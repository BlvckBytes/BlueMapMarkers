package me.blvckbytes.bluemap_markers.command.sub.markers;

import me.blvckbytes.syllables_matcher.EnumMatcher;
import me.blvckbytes.syllables_matcher.MatchableEnum;

public enum MarkersAction implements MatchableEnum {
  CREATE,
  DELETE,
  RENAME,
  TELEPORT_TO,
  LIST,
  NEAR,
  TOGGLE_LISTED,
  SET_SORTING,
  SET_LABEL,
  SET_TEMPLATE,
  MOVE_HERE,
  MOVE_TO,
  CHANGE_VARIABLE,
  LIST_VARIABLES,
  SET_SET,
  ;

  public static final EnumMatcher<MarkersAction> matcher = new EnumMatcher<>(values());
}
