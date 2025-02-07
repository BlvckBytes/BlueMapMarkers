package me.blvckbytes.bluemap_markers.config;

import me.blvckbytes.bbconfigmapper.MappingError;
import me.blvckbytes.bbconfigmapper.sections.AConfigSection;
import me.blvckbytes.gpeee.interpreter.EvaluationEnvironmentBuilder;

import java.lang.reflect.Field;
import java.util.*;

public class MarkersSection extends AConfigSection {

  public int markerSetsListCommandPageSize;

  public MarkersSection(EvaluationEnvironmentBuilder baseEnvironment) {
    super(baseEnvironment);

    this.markerSetsListCommandPageSize = 15;
  }

  @Override
  public void afterParsing(List<Field> fields) throws Exception {
    super.afterParsing(fields);

    if (this.markerSetsListCommandPageSize <= 0)
      throw new MappingError("The property \"markerSetsListCommandPageSize\" cannot be less than or equal to zero!");
  }
}
