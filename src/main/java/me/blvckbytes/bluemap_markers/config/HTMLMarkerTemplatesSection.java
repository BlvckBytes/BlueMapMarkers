package me.blvckbytes.bluemap_markers.config;

import me.blvckbytes.bbconfigmapper.sections.AConfigSection;
import me.blvckbytes.bukkitevaluable.BukkitEvaluable;
import me.blvckbytes.gpeee.interpreter.EvaluationEnvironmentBuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HTMLMarkerTemplatesSection extends AConfigSection {

  public Map<String, HTMLTemplateVariableDescription> variableDescriptions;
  public List<BukkitEvaluable> htmlMarkers;

  public HTMLMarkerTemplatesSection(EvaluationEnvironmentBuilder baseEnvironment) {
    super(baseEnvironment);

    this.variableDescriptions = new HashMap<>();
    this.htmlMarkers = new ArrayList<>();
  }
}
