package me.blvckbytes.bluemap_markers.config;

import me.blvckbytes.bbconfigmapper.sections.AConfigSection;
import me.blvckbytes.gpeee.interpreter.EvaluationEnvironmentBuilder;
import org.jetbrains.annotations.Nullable;

public class TemplateVariableDescription extends AConfigSection {

  public TemplateVariableType type;
  public @Nullable String defaultValue;

  public TemplateVariableDescription(EvaluationEnvironmentBuilder baseEnvironment) {
    super(baseEnvironment);

    this.type = TemplateVariableType.STRING;
  }
}
