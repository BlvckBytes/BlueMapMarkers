package me.blvckbytes.bluemap_markers.command;

import me.blvckbytes.syllables_matcher.EnumMatcher;

public enum SubCommandAction {
  IMAGES,
  SETS,
  ;

  public static final EnumMatcher<SubCommandAction> matcher = new EnumMatcher<>(values());
}
