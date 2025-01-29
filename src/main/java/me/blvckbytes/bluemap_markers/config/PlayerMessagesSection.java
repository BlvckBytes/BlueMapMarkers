package me.blvckbytes.bluemap_markers.config;

import me.blvckbytes.bbconfigmapper.sections.AConfigSection;
import me.blvckbytes.bukkitevaluable.BukkitEvaluable;
import me.blvckbytes.gpeee.interpreter.EvaluationEnvironmentBuilder;

public class PlayerMessagesSection extends AConfigSection {

  public BukkitEvaluable missingCommandPermission;
  public BukkitEvaluable playerOnlyCommand;
  public BukkitEvaluable singleUsageMessage;
  public BukkitEvaluable multiUsageScreen;

  public PlayerMessagesSection(EvaluationEnvironmentBuilder baseEnvironment) {
    super(baseEnvironment);
  }
}
