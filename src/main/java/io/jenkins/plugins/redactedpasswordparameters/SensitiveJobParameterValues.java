package io.jenkins.plugins.redactedpasswordparameters;

import hudson.model.ParameterValue;
import hudson.model.ParametersAction;
import hudson.model.Run;
import hudson.util.Secret;

import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

public final class SensitiveJobParameterValues {
  private static final Logger LOG = Logger.getLogger(SensitiveJobParameterValues.class.getName());

  private SensitiveJobParameterValues() {
    // com.puppycrawl.tools.checkstyle.checks.design.HideUtilityClassConstructorCheck
  }

  public static Set<String> get(final Run build) {
    HashSet<String> set = new HashSet<String>();
    ParametersAction parameters = build.getAction(ParametersAction.class);

    if (parameters == null) {
      return set;
    }

    for (ParameterValue value : parameters) {
      if (value.isSensitive()) {
        Object rawValue = value.getValue();
        if (rawValue instanceof Secret && value.toString() != null) {
          set.add(rawValue.toString());
        }

        if (rawValue instanceof String) {
          set.add((String) rawValue);
        }
      }
    }

    return set;
  }
}
