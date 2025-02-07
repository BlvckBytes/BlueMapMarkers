package me.blvckbytes.bluemap_markers;

import me.blvckbytes.bluemap_markers.model.TemplateVariableType;
import org.junit.jupiter.api.Test;

import java.awt.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class TemplateVariableTypeTests {

  @Test
  public void shouldDeserializeRgbColors() {
    assertEquals(new Color(255, 128, 21), TemplateVariableType.COLOR_RGB.tryRevive("#FF8015"));
    assertNull(TemplateVariableType.COLOR_RGB.tryRevive("#FF8015aa"));
    assertEquals(new Color(122, 128, 21), TemplateVariableType.COLOR_RGB.tryRevive("#7a8015"));
    assertEquals(new Color(225, 124, 23), TemplateVariableType.COLOR_RGB.tryRevive("rgb (   225,124,   23)"));
    assertNull(TemplateVariableType.COLOR_RGB.tryRevive("rgb(255, 128, 21"));
    assertNull(TemplateVariableType.COLOR_RGB.tryRevive("rgb(255, 128, 2000)"));
    assertNull(TemplateVariableType.COLOR_RGB.tryRevive("rgb(255, 128, 21)asd"));
    assertNull(TemplateVariableType.COLOR_RGB.tryRevive("rgba(225, 124, 23, 122)"));
  }

  @Test
  public void shouldDeserializeRgbaColors() {
    assertEquals(new Color(255, 128, 21, 170), TemplateVariableType.COLOR_RGBA.tryRevive("#FF8015aa"));
    assertNull(TemplateVariableType.COLOR_RGBA.tryRevive("#FF8015aab"));
    assertNull(TemplateVariableType.COLOR_RGBA.tryRevive("#FF8015aabsad"));
    assertEquals(new Color(255, 128, 21), TemplateVariableType.COLOR_RGBA.tryRevive("#FF8015"));
    assertEquals(new Color(225, 124, 23, 122), TemplateVariableType.COLOR_RGBA.tryRevive("rgba(225, 124, 23, 122)"));
    assertEquals(new Color(225, 124, 23, 128), TemplateVariableType.COLOR_RGBA.tryRevive("rgba(225, 124, 23, .5)"));
    assertEquals(new Color(225, 124, 23, 255), TemplateVariableType.COLOR_RGBA.tryRevive("rgb(225, 124, 23)"));
    assertNull(TemplateVariableType.COLOR_RGBA.tryRevive("rgb(225, 124, 256"));
    assertNull(TemplateVariableType.COLOR_RGBA.tryRevive("rgba(225, 124, 251, 400"));
  }
}
