package me.blvckbytes.bluemap_markers.command;

import me.blvckbytes.syllables_matcher.NormalizedConstant;

import java.util.Queue;

public record ResultAndActions(
  CommandFailure result,
  Queue<NormalizedConstant<?>> actions
) {}
