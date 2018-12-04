package io.jenkins.plugins.redactedpasswordparameters;

import hudson.Extension;
import hudson.model.Queue;
import hudson.model.Run;
import org.jenkinsci.plugins.workflow.flow.FlowExecutionOwner;
import org.jenkinsci.plugins.workflow.log.TaskListenerDecorator;

import java.io.IOException;
import java.io.OutputStream;
import java.util.logging.Logger;

/**
 * Redacts the contents of password parameters from the console log.
 *
 * <p>For example, imagine the following Pipeline code;
 *
 * <pre>
 *     pipeline {
 *       parameters(
 *         string(name: 'YOUR_USERNAME', defaultValue: 'Mr Jenkins'),
 *         password(name: 'YOUR_PASSWORD', defaultValue 'SECRET')
 *       )
 *
 *       stages {
 *         stage('Example') {
 *           steps {
 *             echo "Hello ${params.YOUR_USERNAME}"
 *             echo "Your password is ${params.YOUR_PASSWORD}"
 *           }
 *         }
 *       }
 *     }
 * </pre>
 *
 * <p>The following would get printed to the console.
 *
 * <pre>
 *     [Pipeline] echo
 *     Hello Mr Jenkins
 *     [Pipeline] echo
 *     Your password is ****
 * </pre>
 */
@Extension
public class RedactedPasswordWorkflow implements TaskListenerDecorator.Factory {
  private static final Logger LOG = Logger.getLogger(RedactedPasswordWorkflow.class.getName());

  @Override
  public TaskListenerDecorator of(final FlowExecutionOwner owner) {
    RedactedPasswordTaskListenerDecorator decorator = new RedactedPasswordTaskListenerDecorator();

    try {
      // Copied from org.jenkinsci.plugins.workflow.flow.FlowCopier
      Queue.Executable exec = owner.getExecutable();
      if (exec instanceof Run) {
        decorator.setRun((Run) exec);
      }
    } catch (IOException exception) {
      LOG.warning("Caught IOException: " + exception.getMessage());
    }

    return decorator;
  }

  private static class RedactedPasswordTaskListenerDecorator extends TaskListenerDecorator {
    private Run run;

    public void setRun(final Run run) {
      this.run = run;
    }

    public OutputStream decorate(final OutputStream logger)
      throws IOException, InterruptedException {

      if (run == null) {
        return logger;
      }

      RedactedOutputStream out = new RedactedOutputStream(logger, run.getCharset());

      for (String value : SensitiveJobParameterValues.get(run)) {
        out.addStringToRedact(value);
      }

      return out;
    }
  }
}
