package me.blvckbytes.bluemap_markers.command.sub.markers.sub;

import me.blvckbytes.syllables_matcher.EnumMatcher;
import me.blvckbytes.syllables_matcher.MatchableEnum;

public enum VariableAction implements MatchableEnum {
  SET,
  UNSET,
  ADD,
  INSERT,
  REMOVE
  ;

  public static final EnumMatcher<VariableAction> matcher = new EnumMatcher<>(values());
}
