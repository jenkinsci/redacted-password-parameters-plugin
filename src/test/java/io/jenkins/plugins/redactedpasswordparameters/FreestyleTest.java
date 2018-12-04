package io.jenkins.plugins.redactedpasswordparameters;

import hudson.Functions;
import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.BuildListener;
import hudson.model.FreeStyleBuild;
import hudson.model.FreeStyleProject;
import hudson.model.ParametersDefinitionProperty;
import hudson.model.PasswordParameterDefinition;
import hudson.model.StringParameterDefinition;
import org.jvnet.hudson.test.TestBuilder;
import org.jvnet.hudson.test.JenkinsRule;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.io.IOException;

public class FreestyleTest {
  @Rule
  public JenkinsRule j = new JenkinsRule();

  private FreeStyleProject project;

  @Before
  public void setUp() throws IOException {
    this.project = j.createFreeStyleProject();
    project.addProperty(new ParametersDefinitionProperty(
        new PasswordParameterDefinition("YOUR_PASSWORD", "secret", "")
    ));
    project.addProperty(new ParametersDefinitionProperty(
        new StringParameterDefinition("YOUR_USERNAME", "Mr Jenkins", "")
    ));
  }

  @Test
  public void passwordParameterValuesAreRedacted() throws Exception {
    addLogLine("Your password is secret");

    FreeStyleBuild build = j.buildAndAssertSuccess(project);
    j.assertLogContains("Your password is ****", build);
    j.assertLogNotContains("secret", build);
  }

  @Test
  public void otherParameterValuesAreNotRedacted() throws Exception {
    addLogLine("Hello Mr Jenkins");

    FreeStyleBuild build = j.buildAndAssertSuccess(project);
    String log = j.getLog(build);

    j.assertLogContains("Hello Mr Jenkins", build);
  }

  @Test
  public void mixedOutput() throws Exception {
    addLogLine("foo");
    addLogLine("secret");
    addLogLine("bar");

    FreeStyleBuild build = j.buildAndAssertSuccess(project);
    String log = j.getLog(build);

    j.assertLogContains("foo", build);
    j.assertLogContains("****", build);
    j.assertLogNotContains("secret", build);
    j.assertLogContains("bar", build);
  }

  private void addLogLine(String line) {
    this.project.getBuildersList().add(new TestBuilder() {
        @Override
        public boolean perform(AbstractBuild<?, ?> build, Launcher launcher, BuildListener listener)
            throws InterruptedException, IOException {
            listener.getLogger().println(line);
            return true;
        }
    });
  }
}
