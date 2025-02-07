package me.blvckbytes.bluemap_markers.config;

import me.blvckbytes.bbconfigmapper.MappingError;
import me.blvckbytes.bbconfigmapper.sections.AConfigSection;
import me.blvckbytes.bbconfigmapper.sections.CSIgnore;
import me.blvckbytes.bluemap_markers.model.TemplateVariableType;
import me.blvckbytes.gpeee.interpreter.EvaluationEnvironmentBuilder;
import me.blvckbytes.syllables_matcher.NormalizedConstant;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.util.List;

public class HTMLTemplateVariableDescription extends AConfigSection {

  private String type;

  @CSIgnore
  private TemplateVariableType<?> _type;

  public @Nullable String defaultValue;

  public HTMLTemplateVariableDescription(EvaluationEnvironmentBuilder baseEnvironment) {
    super(baseEnvironment);
  }

  @Override
  public void afterParsing(List<Field> fields) throws Exception {
    super.afterParsing(fields);

    NormalizedConstant<TemplateVariableType<?>> matchedType;

    if (type == null || (matchedType = TemplateVariableType.matcher.matchFirst(type)) == null)
      throw new MappingError("Property \"type\" does not represent one of " + TemplateVariableType.matcher.createCompletions(null));

    _type = matchedType.constant;
  }

  public TemplateVariableType<?> getType() {
    return _type;
  }
}
