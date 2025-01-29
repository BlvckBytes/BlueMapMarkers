package me.blvckbytes.bluemap_markers.command;

import me.blvckbytes.syllables_matcher.NormalizedConstant;

import java.util.Queue;

public record ResultAndActions(
  CommandResult result,
  Queue<NormalizedConstant<?>> actions
) {}
