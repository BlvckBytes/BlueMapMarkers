package me.blvckbytes.bluemap_markers.config;

import me.blvckbytes.bbconfigmapper.sections.AConfigSection;
import me.blvckbytes.bbconfigmapper.sections.CSAlways;
import me.blvckbytes.gpeee.interpreter.EvaluationEnvironmentBuilder;

import java.util.ArrayList;
import java.util.List;

@CSAlways
public class MainSection extends AConfigSection {

  public CommandsSection commands;
  public PlayerMessagesSection playerMessages;
  public List<HTMLMarkerTemplatesSection> htmlMarkerTemplates;
  public ImagesSection images;
  public MarkersSection markers;

  public MainSection(EvaluationEnvironmentBuilder baseEnvironment) {
    super(baseEnvironment);

    this.htmlMarkerTemplates = new ArrayList<>();
  }
}
