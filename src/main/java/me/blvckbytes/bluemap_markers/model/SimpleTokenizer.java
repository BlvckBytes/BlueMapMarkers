package me.blvckbytes.bluemap_markers.model;

import org.jetbrains.annotations.Nullable;

public class SimpleTokenizer {

  private final String input;
  private int nextCharPosition;

  public SimpleTokenizer(String input) {
    this.input = input;
  }

  /**
   * @return An {@link Integer},
   *          a {@link Double},
   *          a {@link Character} for symbols and
   *          a {@link String} for words;
   *          null when reaching the provided input's end
   */
  public @Nullable Object nextToken() {
    consumeWhitespace();

    if (nextCharPosition >= input.length())
      return null;

    var currentChar = input.charAt(nextCharPosition);

    if (isConsideredASymbol(currentChar)) {
      ++nextCharPosition;
      return currentChar;
    }

    var previousPosition = nextCharPosition;
    var consumedNumber = tryConsumeNumber();

    if (consumedNumber != null)
      return consumedNumber;

    nextCharPosition = previousPosition;

    var isLastWhitespace = false;

    while (nextCharPosition < input.length()) {
      currentChar = input.charAt(nextCharPosition++);

      if (Character.isWhitespace(currentChar)) {
        isLastWhitespace = true;
        break;
      }

      if (isConsideredASymbol(currentChar)) {
        --nextCharPosition;
        break;
      }
    }

    return input.substring(previousPosition, isLastWhitespace ? nextCharPosition - 1 : nextCharPosition);
  }

  private @Nullable Number tryConsumeNumber() {
    int[] numberDigits = new int[input.length() - nextCharPosition];
    int numberDigitsLength = 0;
    int decimalDotIndex = -1;
    var isNegative = false;

    if (input.charAt(nextCharPosition) == '-') {
      isNegative = true;
      ++nextCharPosition;
    }

    // I believe that we shouldn't fail on spaced out minus-signs, since the hyphen is not considered
    // to be a symbol within this little application-specific domain of the tokenizer.
    consumeWhitespace();

    while (nextCharPosition < input.length()) {
      var currentChar = input.charAt(nextCharPosition++);

      if (currentChar == '.') {
        if (decimalDotIndex != -1)
          return null;

        decimalDotIndex = numberDigitsLength;
        continue;
      }

      if (Character.isDigit(currentChar)) {
        numberDigits[numberDigitsLength++] = currentChar - '0';
        continue;
      }

      if (Character.isWhitespace(currentChar) || isConsideredASymbol(currentChar)) {
        --nextCharPosition;
        break;
      }

      return null;
    }

    if (numberDigitsLength == 0)
      return null;

    int intAccumulator = 0;
    double doubleAccumulator = 0;

    for (var numberDigitsIndex = numberDigitsLength - 1; numberDigitsIndex >= 0; --numberDigitsIndex) {
      var numberDigit = numberDigits[numberDigitsIndex];
      var powerOfTen = (numberDigitsLength - 1) - numberDigitsIndex;

      if (decimalDotIndex >= 0)
        powerOfTen = decimalDotIndex - numberDigitsIndex - 1;

      var placeValue = Math.pow(10, powerOfTen);
      var currentValue = placeValue * numberDigit;

      intAccumulator += (int) currentValue;
      doubleAccumulator += currentValue;
    }

    if (isNegative) {
      doubleAccumulator *= -1;
      intAccumulator *= -1;
    }

    if (decimalDotIndex >= 0)
      return doubleAccumulator;

    return intAccumulator;
  }

  private boolean isConsideredASymbol(char c) {
    return (
      c == '(' ||
      c == ')' ||
      c == ',' ||
      c == '#'
    );
  }

  private void consumeWhitespace() {
    while (nextCharPosition < input.length() && Character.isWhitespace(input.charAt(nextCharPosition)))
      ++nextCharPosition;
  }
}
