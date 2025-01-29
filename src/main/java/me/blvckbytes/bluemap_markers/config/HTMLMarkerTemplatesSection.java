package me.blvckbytes.bluemap_markers.config;

import me.blvckbytes.bbconfigmapper.sections.AConfigSection;
import me.blvckbytes.gpeee.interpreter.EvaluationEnvironmentBuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HTMLMarkerTemplatesSection extends AConfigSection {

  public Map<String, TemplateVariableDescription> variableDescriptions;
  public List<String> htmlMarkers;

  public HTMLMarkerTemplatesSection(EvaluationEnvironmentBuilder baseEnvironment) {
    super(baseEnvironment);

    this.variableDescriptions = new HashMap<>();
    this.htmlMarkers = new ArrayList<>();
  }
}
