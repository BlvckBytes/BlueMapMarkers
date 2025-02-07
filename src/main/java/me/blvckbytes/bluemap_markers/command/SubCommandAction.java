package me.blvckbytes.bluemap_markers.command;

import me.blvckbytes.syllables_matcher.EnumMatcher;
import me.blvckbytes.syllables_matcher.MatchableEnum;

public enum SubCommandAction implements MatchableEnum {
  IMAGES,
  SETS,
  MARKERS,
  RELOAD_CONFIG,
  ;

  public static final EnumMatcher<SubCommandAction> matcher = new EnumMatcher<>(values());
}
