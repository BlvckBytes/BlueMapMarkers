package me.blvckbytes.bluemap_markers.model;

import me.blvckbytes.syllables_matcher.EnumMatcher;
import me.blvckbytes.syllables_matcher.MatchableEnum;
import org.jetbrains.annotations.Nullable;

import java.awt.Color;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.function.Function;

public class TemplateVariableType<T> implements MatchableEnum {

  public static final TemplateVariableType<Color> COLOR_RGBA = new TemplateVariableType<>(
    Color.class,
    serialized -> tryDeserializeColor(serialized, true),
    TemplateVariableType::serializeColor
  );

  public static final TemplateVariableType<Color> COLOR_RGB = new TemplateVariableType<>(
    Color.class,
    serialized -> tryDeserializeColor(serialized, false),
    TemplateVariableType::serializeColor
  );

  public static final TemplateVariableType<String> ANY_TEXT = new TemplateVariableType<>(
    String.class,
    serialized -> serialized,
    value -> value
  );

  public static final TemplateVariableType<Integer> POSITIVE_INTEGER = new TemplateVariableType<>(
    Integer.class,
    serialized -> tryDeserializeInteger(serialized, false),
    String::valueOf
  );

  public static final TemplateVariableType<Integer> ANY_SIGN_INTEGER = new TemplateVariableType<>(
    Integer.class,
    serialized -> tryDeserializeInteger(serialized, true),
    String::valueOf
  );

  public static final TemplateVariableType<Float> POSITIVE_FLOATING_POINT = new TemplateVariableType<>(
    Float.class,
    serialized -> tryDeserializeFloat(serialized, false),
    String::valueOf
  );

  public static final TemplateVariableType<Float> ANY_SIGN_FLOATING_POINT = new TemplateVariableType<>(
    Float.class,
    serialized -> tryDeserializeFloat(serialized, true),
    String::valueOf
  );

  public static final TemplateVariableType<Boolean> BOOLEAN = new TemplateVariableType<>(
    Boolean.class,
    TemplateVariableType::tryDeserializeBoolean,
    String::valueOf
  );

  public static final TemplateVariableType<XZCoordinates[]> XZ_COORDINATE_ARRAY = new TemplateVariableType<>(
    XZCoordinates[].class,
    TemplateVariableType::tryDeserializeXZCoordinateArray,
    TemplateVariableType::serializeXZCoordinateArray
  );

  public static final TemplateVariableType<String> ASSETS_IMAGE_FILE = new TemplateVariableType<>(
    String.class,
    serialized -> serialized,
    value -> value
  );

  private static final TemplateVariableType<?>[] values;
  public static final EnumMatcher<TemplateVariableType<?>> matcher;

  static {
    var valuesBuffer = new ArrayList<TemplateVariableType<?>>();
    var thisClass = TemplateVariableType.class;

    for (var field : thisClass.getDeclaredFields()) {
      if (!(Modifier.isStatic(field.getModifiers()) && Modifier.isPublic(field.getModifiers())))
        continue;

      if (field.getType() != thisClass)
        continue;

      try {
        var fieldValue = (TemplateVariableType<?>) field.get(null);

        fieldValue.fieldName = field.getName();

        valuesBuffer.add(fieldValue);
      } catch (Exception ignored) {}
    }

    values = valuesBuffer.toArray(TemplateVariableType[]::new);
    matcher = new EnumMatcher<>(values);
  }

  private String fieldName;
  public final Class<T> type;
  private final Function<String, T> reviver;
  private final Function<T, String> serializer;

  private TemplateVariableType(
    Class<T> type,
    Function<String, T> reviver,
    Function<T, String> serializer
  ) {
    this.type = type;
    this.reviver = reviver;
    this.serializer = serializer;
  }

  public @Nullable T tryRevive(String input) {
    return reviver.apply(input);
  }

  public String serialize(T input) {
    return serializer.apply(input);
  }

  @Override
  public String name() {
    return fieldName;
  };

  // ================================================================================
  // Value-Transformers
  // ================================================================================

  // TODO: Implement these once they're actually used

  private static @Nullable Color tryDeserializeColor(String input, boolean allowAlphaChannel) {
    throw new UnsupportedOperationException();
  }

  private static String serializeColor(Color input) {
    throw new UnsupportedOperationException();
  }

  private static @Nullable Integer tryDeserializeInteger(String input, boolean allowNegative) {
    throw new UnsupportedOperationException();
  }

  private static @Nullable Float tryDeserializeFloat(String input, boolean allowNegative) {
    throw new UnsupportedOperationException();
  }

  private static @Nullable Boolean tryDeserializeBoolean(String input) {
    throw new UnsupportedOperationException();
  }

  private static @Nullable XZCoordinates[] tryDeserializeXZCoordinateArray(String input) {
    throw new UnsupportedOperationException();
  }

  private static String serializeXZCoordinateArray(XZCoordinates[] input) {
    throw new UnsupportedOperationException();
  }
}