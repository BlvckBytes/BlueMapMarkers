package me.blvckbytes.bluemap_markers.command;

import me.blvckbytes.syllables_matcher.EnumMatcher;

public enum SubCommandAction {
  IMAGES,
  SETS,
  MARKERS,
  RELOAD_CONFIG,
  ;

  public static final EnumMatcher<SubCommandAction> matcher = new EnumMatcher<>(values());
}
