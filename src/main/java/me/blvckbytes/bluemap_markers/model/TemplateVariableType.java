package me.blvckbytes.bluemap_markers.model;

import me.blvckbytes.syllables_matcher.EnumMatcher;
import me.blvckbytes.syllables_matcher.MatchableEnum;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.function.Function;

public class TemplateVariableType<T> implements MatchableEnum {

  public static final TemplateVariableType<RgbaColor> COLOR_RGBA = new TemplateVariableType<>(
    RgbaColor.class,
    serialized -> tryDeserializeColor(serialized, true),
    TemplateVariableType::serializeColor
  );

  public static final TemplateVariableType<RgbaColor> COLOR_RGB = new TemplateVariableType<>(
    RgbaColor.class,
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

  private static @Nullable RgbaColor tryDeserializeColor(String input, boolean allowAlphaChannel) {
    var tokenizer = new SimpleTokenizer(input);
    var currentToken = tokenizer.nextToken();

    if (currentToken == null)
      return null;

    if (currentToken.equals('#')) {
      if (!(tokenizer.nextToken() instanceof String hexString))
        return null;

      if (tokenizer.nextToken() != null)
        return null;

      long hexValue;

      try {
        hexValue = Long.parseLong(hexString, 16);
      } catch (Exception e) {
        return null;
      }

      if (hexValue > 0xFFFFFFFFL)
        return null;

      var hasAlpha = hexValue > 0xFFFFFF;
      var shiftOffset = 0;

      if (hasAlpha) {
        if (!allowAlphaChannel)
          return null;

        shiftOffset = 8;
      }

      int redValue = (int) ((hexValue >> (16 + shiftOffset)) & 0xFF);
      int greenValue = (int) ((hexValue >> (8 + shiftOffset)) & 0xFF);
      int blueValue = (int) ((hexValue >> shiftOffset) & 0xFF);
      int alphaValue = (int) (shiftOffset == 0 ? 0xFF : (hexValue & 0xFF));

      if (!(isValidByte(redValue) && isValidByte(greenValue) && isValidByte(blueValue) && isValidByte(alphaValue)))
        return null;

      return new RgbaColor(
        redValue, greenValue, blueValue, alphaValue,
        allowAlphaChannel ? ColorFormat.HEX_RGBA : ColorFormat.HEX_RGB
      );
    }

    var isRgba = false;

    if (currentToken.equals("rgb") || (isRgba = currentToken.equals("rgba"))) {
      if (isRgba && !allowAlphaChannel)
        return null;

      currentToken = tokenizer.nextToken();

      if (currentToken == null || !currentToken.equals('('))
        return null;

      currentToken = tokenizer.nextToken();

      if (!(currentToken instanceof Integer redValue))
        return null;

      currentToken = tokenizer.nextToken();

      if (currentToken == null || !currentToken.equals(','))
        return null;

      currentToken = tokenizer.nextToken();

      if (!(currentToken instanceof Integer greenValue))
        return null;

      currentToken = tokenizer.nextToken();

      if (currentToken == null || !currentToken.equals(','))
        return null;

      currentToken = tokenizer.nextToken();

      if (!(currentToken instanceof Integer blueValue))
        return null;

      int alphaValue = 0xFF;

      if (isRgba) {
        currentToken = tokenizer.nextToken();

        if (currentToken == null || !currentToken.equals(','))
          return null;

        currentToken = tokenizer.nextToken();

        if (currentToken instanceof Double doubleValue)
          alphaValue = (int) Math.round(doubleValue * 255);
        else
          return null;
      }

      currentToken = tokenizer.nextToken();

      if (currentToken == null || !currentToken.equals(')'))
        return null;

      if (tokenizer.nextToken() != null)
        return null;

      if (!(isValidByte(redValue) && isValidByte(greenValue) && isValidByte(blueValue) && isValidByte(alphaValue)))
        return null;

      return new RgbaColor(
        redValue, greenValue, blueValue, alphaValue,
        allowAlphaChannel ? ColorFormat.WEB_RGBA : ColorFormat.WEB_RGB
      );
    }

    return null;
  }

  private static boolean isValidByte(int value) {
    return value >= 0 && value <= 255;
  }

  private static String serializeColor(RgbaColor input) {
    var result = new StringBuilder();

    switch (input.format()) {
      case HEX_RGB, HEX_RGBA -> {
        result.append('#');

        result.append(String.format("%02X", input.red()));
        result.append(String.format("%02X", input.green()));
        result.append(String.format("%02X", input.blue()));

        if (input.format() == ColorFormat.HEX_RGBA)
          result.append(String.format("%02X", input.alpha()));

      }

      case WEB_RGB, WEB_RGBA -> {
        result.append("rgb");

        if (input.format() == ColorFormat.WEB_RGBA)
          result.append('a');

        result.append('(');
        result.append(input.red());
        result.append(", ");
        result.append(input.green());
        result.append(", ");
        result.append(input.blue());

        if (input.format() == ColorFormat.WEB_RGBA) {
          result.append(", ");
          result.append(Math.round(input.alpha() / 255.0 * 100.0) / 100.0);
        }

        result.append(')');
      }
    }

    return result.toString();
  }

  // TODO: Implement these once they're actually used

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