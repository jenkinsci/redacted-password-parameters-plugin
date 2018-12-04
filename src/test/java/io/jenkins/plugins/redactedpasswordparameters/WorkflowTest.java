package io.jenkins.plugins.redactedpasswordparameters;

import hudson.model.ParametersDefinitionProperty;
import hudson.model.PasswordParameterDefinition;
import hudson.model.StringParameterDefinition;
import org.jenkinsci.plugins.workflow.job.WorkflowJob;
import org.jenkinsci.plugins.workflow.job.WorkflowRun;
import org.jenkinsci.plugins.workflow.cps.CpsFlowDefinition;
import org.jvnet.hudson.test.JenkinsRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.Ignore;

public class WorkflowTest {
  @Rule
  public JenkinsRule j = new JenkinsRule();

  @Test
  public void worksInWorkflowJob() throws Exception {
    WorkflowJob project = j.createProject(WorkflowJob.class);

    project.setDefinition(new CpsFlowDefinition(
      "println \"Hello ${params.YOUR_USERNAME}\"\n" +
      "println \"Your password is ${params.YOUR_PASSWORD}\"" +
      "println \"secret\"",
      true
    ));

    project.addProperty(new ParametersDefinitionProperty(
      new StringParameterDefinition("YOUR_USERNAME", "Mr Jenkins", ""),
      new PasswordParameterDefinition("YOUR_PASSWORD", "secret", "")
    ));

    WorkflowRun build = j.buildAndAssertSuccess(project);

    //j.assertLogContains("Hello Mr Jenkins", build);
    //j.assertLogContains("Your password is ****", build);
    //j.assertLogNotContains("secret", build);
  }
}
