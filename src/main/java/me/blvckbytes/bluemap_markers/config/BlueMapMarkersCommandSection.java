package me.blvckbytes.bluemap_markers.config;

import me.blvckbytes.bukkitevaluable.section.ACommandSection;
import me.blvckbytes.gpeee.interpreter.EvaluationEnvironmentBuilder;

public class BlueMapMarkersCommandSection extends ACommandSection {

  public static final String INITIAL_NAME = "bluemapmarkers";

  public BlueMapMarkersCommandSection(EvaluationEnvironmentBuilder baseEnvironment) {
    super(INITIAL_NAME, baseEnvironment);
  }
}
