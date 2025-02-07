package me.blvckbytes.bluemap_markers.command;

import me.blvckbytes.syllables_matcher.EnumMatcher;
import me.blvckbytes.syllables_matcher.NormalizedConstant;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.regex.Pattern;

public abstract class SubCommand {

  public abstract @Nullable CommandFailure onCommand(CommandSender sender, String[] args, Queue<NormalizedConstant<?>> queue);

  public abstract List<String> onTabComplete(CommandSender sender, String[] args);

  public abstract List<String> getPartialUsages(@Nullable Queue<NormalizedConstant<?>> actions, CommandSender sender);

  protected List<String> prependOwnAction(SubCommand subCommand, List<String> subUsages) {
    return subUsages
      .stream()
      .map(it -> subCommand.getCorrespondingAction().getNormalizedName() + " " + it)
      .toList();
  }

  protected String joinArgsStartingAtIndex(String[] args, int index) {
    var argsJoiner = new StringJoiner(" ");

    for (var i = index; i < args.length; ++i)
      argsJoiner.add(args[i]);

    return argsJoiner.toString();
  }

  protected List<String> collectSubUsages(@Nullable Queue<NormalizedConstant<?>> actions, CommandSender sender, Map<NormalizedConstant<?>, SubCommand> subCommandMap) {
    var action = actions == null ? null : actions.poll();

    if (action != null) {
      var subCommand = subCommandMap.get(action);

      if (subCommand != null)
        return subCommand.getPartialUsages(actions, sender);
    }

    var usages = new ArrayList<String>();

    for (var subCommand : subCommandMap.values())
      usages.addAll(subCommand.getPartialUsages(actions, sender));

    return usages;
  }

  public abstract NormalizedConstant<?> getCorrespondingAction();

  public static @Nullable CommandFailure tryRelayCommand(
    EnumMatcher<? extends Enum<?>> matcher,
    Map<NormalizedConstant<?>, SubCommand> subCommandMap,
    CommandSender sender, String[] args, Queue<NormalizedConstant<?>> actions
  ) {
    var subCommand = tryFindSubCommand(matcher, subCommandMap, args);

    if (subCommand == null)
      return CommandFailure.UNREGISTERED;

    var subArgs = new String[args.length - 1];
    System.arraycopy(args, 1, subArgs, 0, subArgs.length);

    actions.add(subCommand.getCorrespondingAction());

    return subCommand.onCommand(sender, subArgs, actions);
  }

  public static List<String> tryRelayTabComplete(
    EnumMatcher<? extends Enum<?>> matcher,
    Map<NormalizedConstant<?>, SubCommand> subCommandMap,
    CommandSender sender, String[] args
  ) {
    if (args.length == 1)
      return matcher.createCompletions(args[0]);

    var subCommand = tryFindSubCommand(matcher, subCommandMap, args);

    if (subCommand == null)
      return List.of();

    var subArgs = new String[args.length - 1];
    System.arraycopy(args, 1, subArgs, 0, subArgs.length);

    return subCommand.onTabComplete(sender, subArgs);
  }

  public static @Nullable SubCommand tryFindSubCommand(
    EnumMatcher<? extends Enum<?>> matcher,
    Map<NormalizedConstant<?>, SubCommand> subCommandMap,
    String[] args
  ) {
    if (args.length == 0)
      return null;

    var matchedAction = matcher.matchFirst(args[0]);

    if (matchedAction == null)
      return null;

    return subCommandMap.get(matchedAction);
  }

  // ================================================================================
  // Utilities
  // ================================================================================

  protected static List<String> parseQuotesSupportingArguments(String[] args) {
    // TODO: Implement
    return Arrays.asList(args);
  }

  protected static @Nullable Pattern tryCompileGlobPattern(String pattern, boolean caseInsensitive) {
    try {
      return Pattern.compile(convertGlobToRegex(pattern), caseInsensitive ? Pattern.CASE_INSENSITIVE : 0);
    } catch (Exception e) {
      return null;
    }
  }

  /**
   * Converts a standard POSIX Shell globbing pattern into a regular expression
   * pattern. The result can be used with the standard {@link java.util.regex} API to
   * recognize strings which match the glob pattern.
   * <p/>
   * See also, the POSIX Shell language:
   * <a href="http://pubs.opengroup.org/onlinepubs/009695399/utilities/xcu_chap02.html#tag_02_13_01">Specification</a>
   * Credits to the author:
   * <a href="https://stackoverflow.com/a/17369948">Neil Traft</a>
   *
   * @param pattern A glob pattern.
   * @return A regex pattern to recognize the given glob pattern.
   */
  private static String convertGlobToRegex(String pattern) {
    StringBuilder sb = new StringBuilder(pattern.length());

    int inGroup = 0;
    int inClass = 0;
    int firstIndexInClass = -1;

    char[] arr = pattern.toCharArray();
    for (int i = 0; i < arr.length; i++) {
      char ch = arr[i];
      switch (ch) {
        case '\\':
          if (++i >= arr.length) {
            sb.append('\\');
          } else {
            char next = arr[i];
            switch (next) {
              case ',':
                // escape not needed
                break;
              case 'Q':
              case 'E':
                // extra escape needed
                sb.append('\\');
              default:
                sb.append('\\');
            }
            sb.append(next);
          }
          break;
        case '*':
          if (inClass == 0)
            sb.append(".*");
          else
            sb.append('*');
          break;
        case '?':
          if (inClass == 0)
            sb.append('.');
          else
            sb.append('?');
          break;
        case '[':
          inClass++;
          firstIndexInClass = i+1;
          sb.append('[');
          break;
        case ']':
          inClass--;
          sb.append(']');
          break;
        case '.':
        case '(':
        case ')':
        case '+':
        case '|':
        case '^':
        case '$':
        case '@':
        case '%':
          if (inClass == 0 || (firstIndexInClass == i && ch == '^'))
            sb.append('\\');
          sb.append(ch);
          break;
        case '!':
          if (firstIndexInClass == i)
            sb.append('^');
          else
            sb.append('!');
          break;
        case '{':
          inGroup++;
          sb.append('(');
          break;
        case '}':
          inGroup--;
          sb.append(')');
          break;
        case ',':
          if (inGroup > 0)
            sb.append('|');
          else
            sb.append(',');
          break;
        default:
          sb.append(ch);
      }
    }
    return sb.toString();
  }
}
