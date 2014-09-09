/*
 * Copyright (c) 2014 Magnet Systems, Inc.
 * All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you
 * may not use this file except in compliance with the License. You
 * may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */
package com.magnet.tools.tests;

import static com.magnet.tools.tests.ScenarioUtils.*;

import java.io.File;
import java.io.FileInputStream;
import java.util.List;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.Executor;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.junit.Assert;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;

/**
 * WebLogic-specific step definitions
 */
public class WebLogicStepDefs {

  @Given("^the WLS server located at \"([^\"]*)\" is started$")
  public static void wls_server_running_at(String location) throws Throwable {
    ensureStartServer(location, "http://localhost:7001/bea_wls_internal/index.html");
  }


  @Given("^these applications are deployed on the WLS server running at \"([^\"]*)\":$")
  public static void applications_deployed_on_wls_server(String url, List<ApplicationEntry> entries)
      throws Throwable {
    for (ApplicationEntry e : entries) {
      ensureDeployApp(url, e.name, expandVariables(e.location));
    }
  }

  @Then("^I stop all WLS servers$")
  public static void I_stop_all_WLS_servers() throws Throwable {
    // Express the Regexp above with the code you wish you had
    killWlsServers();
  }

  @Given("^the WLS server located at \"([^\"]*)\" is started and available at \"([^\"]*)\"$")
  public static void the_WLS_server_located_at_is_started_and_available_at(String location, String httpPingUrl) throws Throwable {
    ensureStartServer(location, httpPingUrl);
  }

  public static void ensureDeployApp(String url, String name, String location) throws Exception {
    Executor exec = new DefaultExecutor();
    CommandLine cl = new CommandLine(getScriptsDir() + File.separator + REDEPLOY_WLS_APP_SCRIPT);
    if (getArchetypeSettings() != null && getArchetypeSettings().length() != 0) {
      cl.addArgument(getArchetypeSettings());
    }
    cl.addArgument(url);
    cl.addArgument(name);
    cl.addArgument(location);
    cl.setSubstitutionMap(getSubstitutionMap());
    int exitValue = exec.execute(cl);
    String msg = String.format("Server running at %s failed to deploy app %s at %s", url, name, expandVariables(location));
    ensureBuildSuccessful("redeploy.log");
    Assert.assertEquals(msg, 0, exitValue);
  }

  // TODO: does not work on windows (but not called by current tests)
  public static void killWlsServers() throws Exception {
    ensureExecute("${basedir}", getScriptsDir() + File.separator + KILL_WLS_SCRIPT, false, 0, null, null, null, null, false);
  }

  public static void ensureStartServer(String location, String httpPingUrl) throws Exception {
    Executor exec = new DefaultExecutor();
    CommandLine cl = new CommandLine(getScriptsDir() + File.separator + START_WLS_SERVER_SCRIPT);
    if (getArchetypeSettings() != null && getArchetypeSettings().length() != 0) {
      cl.addArgument("--s");
      cl.addArgument(getArchetypeSettings());
    }
    cl.addArgument(String.format("-DdomainHome=%s", location));
    cl.addArgument(String.format("-DhttpPingUrl=%s", httpPingUrl));
    cl.setSubstitutionMap(getSubstitutionMap());
    int exitValue = exec.execute(cl);
    ensureBuildSuccessful("start-server.log");
    String msg = String.format("server failed to start at %s", expandVariables(location));
    Assert.assertEquals(msg, 0, exitValue);
  }

  public static void ensureBuildSuccessful(String filePath) {
    File logFile = new File(filePath);
    Assert.assertTrue(logFile + " should exist", logFile.exists() && logFile.isFile());
    try {
      FileInputStream fis = new FileInputStream(logFile);
      String content = IOUtils.toString(fis);
      File savedFile = new File(filePath + System.currentTimeMillis() + ".log");
      FileUtils.copyFile(logFile, savedFile);
      Assert.assertTrue("build from log: " + savedFile.getAbsolutePath() + " should be successful", content.contains("BUILD SUCCESS"));
      Assert.assertFalse("build from log: " + savedFile.getAbsolutePath() + " should not fail", content.contains("BUILD FAILURE"));
      if (!savedFile.delete()) {
        ScenarioUtils.log("Couldn't delete " + savedFile);
      }
//      logFile.delete();
    } catch (Exception e) {
      Assert.fail("An exception occurred: " + e);
    }

  }

  public class ApplicationEntry {
    String name;
    String location;
  }


}
