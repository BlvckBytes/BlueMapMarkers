package me.blvckbytes.bluemap_markers.command.sub.sets;

import me.blvckbytes.syllables_matcher.EnumMatcher;

public enum SetsAction {
  LIST,
  DELETE,
  RENAME,
  CREATE,
  SET_LABEL,
  SET_TOGGLEABLE,
  SET_DEFAULT_HIDDEN,
  SET_SORTING,
  ;

  public static final EnumMatcher<SetsAction> matcher = new EnumMatcher<>(values());
}
