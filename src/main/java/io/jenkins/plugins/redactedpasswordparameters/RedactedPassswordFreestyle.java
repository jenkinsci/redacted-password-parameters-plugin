package io.jenkins.plugins.redactedpasswordparameters;

import hudson.Extension;
import hudson.console.ConsoleLogFilter;
import hudson.model.Run;

import java.io.IOException;
import java.io.OutputStream;
import java.util.logging.Logger;

/**
 * Redacts the contents of password parameters from the console log.
 */
@Extension
public class RedactedPassswordFreestyle extends ConsoleLogFilter {
  private static final Logger LOG = Logger.getLogger(RedactedPassswordFreestyle.class.getName());

  @Override
  public final OutputStream decorateLogger(final Run build, final OutputStream logger)
    throws IOException, InterruptedException {
    RedactedOutputStream out = new RedactedOutputStream(logger, build.getCharset());

    for (String value : SensitiveJobParameterValues.get(build)) {
      out.addStringToRedact(value);
    }

    return out;
  }
}
