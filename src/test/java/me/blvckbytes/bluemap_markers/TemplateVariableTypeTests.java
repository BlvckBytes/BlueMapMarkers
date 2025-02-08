package me.blvckbytes.bluemap_markers;

import me.blvckbytes.bluemap_markers.model.ColorFormat;
import me.blvckbytes.bluemap_markers.model.RgbaColor;
import me.blvckbytes.bluemap_markers.model.TemplateVariableType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class TemplateVariableTypeTests {

  @Test
  public void shouldDeserializeRgbColors() {
    assertEquals(new RgbaColor(255, 128, 21, 255, ColorFormat.HEX_RGB), TemplateVariableType.COLOR_RGB.tryRevive("#FF8015"));
    assertNull(TemplateVariableType.COLOR_RGB.tryRevive("#FF8015aa"));
    assertEquals(new RgbaColor(122, 128, 21, 255, ColorFormat.HEX_RGB), TemplateVariableType.COLOR_RGB.tryRevive("#7a8015"));
    assertEquals(new RgbaColor(225, 124, 23, 255, ColorFormat.WEB_RGB), TemplateVariableType.COLOR_RGB.tryRevive("rgb (   225,124,   23)"));
    assertNull(TemplateVariableType.COLOR_RGB.tryRevive("rgb(255, 128, 21"));
    assertNull(TemplateVariableType.COLOR_RGB.tryRevive("rgb(255, 128, 2000)"));
    assertNull(TemplateVariableType.COLOR_RGB.tryRevive("rgb(255, 128, 21)asd"));
    assertNull(TemplateVariableType.COLOR_RGB.tryRevive("rgba(225, 124, 23, 122)"));
  }

  @Test
  public void shouldSerializeRgbColors() {
    assertEquals("#FF8015", TemplateVariableType.COLOR_RGB.serialize(new RgbaColor(255, 128, 21, 255, ColorFormat.HEX_RGB)));
    assertEquals("rgb(255, 128, 21)", TemplateVariableType.COLOR_RGB.serialize(new RgbaColor(255, 128, 21, 255, ColorFormat.WEB_RGB)));
  }

  @Test
  public void shouldDeserializeRgbaColors() {
    assertEquals(new RgbaColor(255, 128, 21, 170, ColorFormat.HEX_RGBA), TemplateVariableType.COLOR_RGBA.tryRevive("#FF8015aa"));
    assertNull(TemplateVariableType.COLOR_RGBA.tryRevive("#FF8015aab"));
    assertNull(TemplateVariableType.COLOR_RGBA.tryRevive("#FF8015aabsad"));
    assertEquals(new RgbaColor(255, 128, 21, 255, ColorFormat.HEX_RGBA), TemplateVariableType.COLOR_RGBA.tryRevive("#FF8015"));
    assertEquals(new RgbaColor(225, 124, 23, 122, ColorFormat.WEB_RGBA), TemplateVariableType.COLOR_RGBA.tryRevive("rgba(225, 124, 23, .478)"));
    assertEquals(new RgbaColor(225, 124, 23, 128, ColorFormat.WEB_RGBA), TemplateVariableType.COLOR_RGBA.tryRevive("rgba(225, 124, 23, .5)"));
    assertEquals(new RgbaColor(225, 124, 23, 255, ColorFormat.WEB_RGBA), TemplateVariableType.COLOR_RGBA.tryRevive("rgb(225, 124, 23)"));
    assertNull(TemplateVariableType.COLOR_RGBA.tryRevive("rgb(225, 124, 256"));
    assertNull(TemplateVariableType.COLOR_RGBA.tryRevive("rgba(225, 124, 251, 400"));
  }

  @Test
  public void shouldSerializeRgbaColors() {
    assertEquals("#FF8015FF", TemplateVariableType.COLOR_RGBA.serialize(new RgbaColor(255, 128, 21, 255, ColorFormat.HEX_RGBA)));
    assertEquals("rgba(255, 128, 21, 1.0)", TemplateVariableType.COLOR_RGBA.serialize(new RgbaColor(255, 128, 21, 255, ColorFormat.WEB_RGBA)));
  }
}
