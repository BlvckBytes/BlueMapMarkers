package me.blvckbytes.bluemap_markers.config;

import me.blvckbytes.bbconfigmapper.MappingError;
import me.blvckbytes.bbconfigmapper.sections.AConfigSection;
import me.blvckbytes.bbconfigmapper.sections.CSIgnore;
import me.blvckbytes.gpeee.interpreter.EvaluationEnvironmentBuilder;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.util.*;

public class ImagesSection extends AConfigSection {

  public Map<String, String> imageFileExtensionByMimeType;
  public @Nullable List<String> additionalFileExtensions;
  public int httpRequestTimeoutSeconds;

  @CSIgnore
  public Set<String> imageFileExtensions;

  public ImagesSection(EvaluationEnvironmentBuilder baseEnvironment) {
    super(baseEnvironment);

    this.imageFileExtensionByMimeType = new HashMap<>();
    this.httpRequestTimeoutSeconds = 3000;
  }

  @Override
  public void afterParsing(List<Field> fields) throws Exception {
    super.afterParsing(fields);

    if (this.httpRequestTimeoutSeconds <= 0)
      throw new MappingError("The property \"httpRequestTimeoutSeconds\" cannot be less than or equal to zero!");

    this.imageFileExtensions = new HashSet<>(imageFileExtensionByMimeType.values());

    if (additionalFileExtensions != null)
      this.imageFileExtensions.addAll(additionalFileExtensions);

    for (var fileExtension : imageFileExtensions) {
      int dotIndex = fileExtension.indexOf('.');

      if (dotIndex < 0)
        throw new MappingError("File-extension \"" + fileExtension + "\" of property \"imageFileExtensionByMimeType\" or \"additionalFileExtensions\" did not start with a required dot!");

      if (fileExtension.indexOf('.', dotIndex + 1) > 0)
        throw new MappingError("File-extension \"" + fileExtension + "\" of property \"imageFileExtensionByMimeType\" or \"additionalFileExtensions\" contained more than one dot!");
    }
  }
}
