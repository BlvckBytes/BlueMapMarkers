package me.blvckbytes.bluemap_markers;

import me.blvckbytes.bluemap_markers.model.SimpleTokenizer;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class SimpleTokenizerTests {

  private static final double DOUBLE_COMPARISON_DELTA = .001;

  @Test
  public void shouldParseNumericTokens() {
    makeCase("5.21", 5.21);
    makeCase(".21", .21);
    makeCase("1024", 1024);
    makeCase("-.21", -.21);
    makeCase("-1024", -1024);
    makeCase("- .21", -.21);
    makeCase("- 1024", -1024);
  }

  @Test
  public void shouldParseSymbols() {
    makeCase("(", '(');
    makeCase(")", ')');
    makeCase("#", '#');
    makeCase(",", ',');
  }

  @Test
  public void shouldParseWords() {
    makeCase("rgb", "rgb");
    makeCase("my words", "my", "words");
  }

  @Test
  public void shouldParseComplex() {
    makeCase(
      "rgba(255, 128, 12, .55)",
      "rgba", '(', 255, ',', 128, ',', 12, ',', .55, ')'
    );
  }

  private static void makeCase(String input, Object... expectedTokens) {
    var tokenizer = new SimpleTokenizer(input);

    for (Object expectedToken : expectedTokens) {
      var actualToken = tokenizer.nextToken();

      if (expectedToken instanceof Double || expectedToken instanceof Float) {
        assertDoubleEquals(((Number) expectedToken).doubleValue(), actualToken);
        continue;
      }

      assertEquals(expectedToken, actualToken);
    }

    assertNull(tokenizer.nextToken(), "Expected the tokenizer to have no more tokens in store");
  }

  private static void assertDoubleEquals(double expected, Object actual) {
    if (!(actual instanceof Double doubleValue))
      throw new AssertionError("Expected double but got " + actual.getClass());

    if (Math.abs(expected - doubleValue) > DOUBLE_COMPARISON_DELTA)
      throw new AssertionError("Expected double " + actual + " to be within +-" + DOUBLE_COMPARISON_DELTA + " of " + expected);
  }
}
