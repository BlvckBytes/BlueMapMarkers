package me.blvckbytes.bluemap_markers.command.sub.images;

import me.blvckbytes.syllables_matcher.EnumMatcher;
import me.blvckbytes.syllables_matcher.MatchableEnum;

public enum ImagesAction implements MatchableEnum {
  LIST,
  DELETE,
  DELETE_RECURSIVE,
  DOWNLOAD,
  RENAME
  ;

  public static final EnumMatcher<ImagesAction> matcher = new EnumMatcher<>(values());
}
