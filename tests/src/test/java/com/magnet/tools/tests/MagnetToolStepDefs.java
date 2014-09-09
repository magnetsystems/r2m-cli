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

import static com.magnet.tools.tests.FileSystemStepDefs.the_file_should_exist;
import static com.magnet.tools.tests.ScenarioUtils.ensureExecute;
import static com.magnet.tools.tests.ScenarioUtils.expandVariables;
import static com.magnet.tools.tests.ScenarioUtils.getTestEnvironment;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.commons.io.FileUtils;
import org.junit.Assert;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import expectj.ExpectJ;
import expectj.Spawn;
import expectj.TimeoutException;

/**
 * Magnet Tool specific steps
 */
public class MagnetToolStepDefs {

  /**
   * Environment variable pointing to last installation directory
   */
  public static final String MAGNET_TOOL_HOME = "MAGNET_TOOL_HOME";

  private static final String LINE_SEP = System.getProperty("line.separator");

  private static final Integer DEFAULT_COMMAND_TIMEOUT = 30;

  private static final String MAB_INSTALLER_FILE_PATH = "${basedir}/target/magnet-tools-cli-installer-" + ScenarioUtils.getMABVersion() + ".zip";

  private static final String MOB_INSTALLER_FILE_PATH = "${basedir}/target/r2m-installer-" + ScenarioUtils.getMABVersion() + ".zip";

  @When("^I install the magnet installer at \"([^\"]*)\" to \"([^\"]*)\"$")
  public static void install_the_magnet_tool_installer(String filePath, String directory) throws Throwable {
    installMagnetZip(filePath, directory, true);
  }

  public static void installMagnetZip(String filePath, String directory, boolean cleanFirst) throws Throwable {
    String file = expandVariables(filePath).trim();
    Assert.assertTrue("installer zip should exist:" + file, new File(file).exists());
    Assert.assertTrue("installer zip should be a file:" + file, new File(file).isFile());
    Assert.assertTrue("installer zip path must be absolute:" + file, new File(file).isAbsolute());
    File dir = new File(expandVariables(directory).trim());
    if (cleanFirst) {
      if (dir.exists()) {
        FileUtils.deleteDirectory(dir);
      }
      if (!dir.mkdirs()) {
        ScenarioUtils.log("Directory already exists: " + dir);
      }
    } else {
      // delete only the installation dir, not the parent
      String[] dirs = { "lib", "bin", "config"};
      for (String d: dirs) {
        File dirFile = new File(dir, d);
        if (dirFile.exists()) {
          FileUtils.deleteDirectory(dirFile);
        }
      }
    }
    ScenarioUtils.unzip(new File(file), dir);
    ScenarioUtils.setEnvironmentVariable(MAGNET_TOOL_HOME, dir.getCanonicalPath());

  }

  public static void install_mab_to(String directory, boolean cleanFirst) throws Throwable {
    installMagnetZip(MAB_INSTALLER_FILE_PATH, directory, cleanFirst);
  }

  public static void install_mob_to(String directory, boolean cleanFirst) throws Throwable {
    installMagnetZip(MOB_INSTALLER_FILE_PATH, directory, cleanFirst);
  }

  @Then("^the package \"([^\"]*)\" should contain the following entries:$")
  public static void package_should_contain(String packagePath, List<String> entries) throws Throwable {
    File file = new File(expandVariables(packagePath));
    Assert.assertTrue("File " + file + " should exist", file.exists());

    ZipInputStream zip = null;
    Set<String> actualSet;
    try {
      FileInputStream fis = new FileInputStream(file);
      zip = new ZipInputStream(fis);
      ZipEntry ze;
      actualSet = new HashSet<String>();
      while ((ze = zip.getNextEntry()) != null) {
        actualSet.add(ze.getName());
      }
    } finally {
      if (null != zip) {
        zip.close();
      }
    }

    for (String e : entries) {
      String expected = expandVariables(e);
      Assert.assertTrue("File " + file + " should contain entry " + expected
          + ", actual set of entries are " + actualSet, actualSet.contains(e));
    }
  }


  @Then("^the jar \"([^\"]*)\" should not contain any matches for:$")
  public static void and_the_jar_should_not_contain_any_matches_for(String file, List<String> patterns) throws Throwable {
    the_file_should_exist(file);
    JarFile jarFile = null;
    try {
      jarFile = new JarFile(file);
      Enumeration<JarEntry> jarEntryEnumeration = jarFile.entries();
      StringBuilder matchList = new StringBuilder();
      JarEntry currentEntry;
      while (jarEntryEnumeration.hasMoreElements()) {
        currentEntry = jarEntryEnumeration.nextElement();
        for (String pattern : patterns) {
          if (currentEntry.getName().matches(pattern)) {
            matchList.append(currentEntry.getName());
            matchList.append("\n");
          }
        }
      }
      String matchedStrings = matchList.toString();
      Assert.assertTrue("The jar " + file + "contained\n" + matchedStrings, matchedStrings.isEmpty());

    } finally {
      if (null != jarFile) {
        try { jarFile.close(); } catch (Exception e) { /* do nothing */ }
      }
    }
  }


  @Then("^I run the mab tool at \"([^\"]*)\" in console mode:$")
  public static void I_run_the_mab_tool_at_in_console_mode(String path, List<ExpectedInteraction> entries) throws Throwable {
    File mabTool = new File(expandVariables(path));

    Assert.assertTrue("File " + mabTool + " should exist", mabTool.exists());
    ExpectJ expectinator = new ExpectJ(300); // 5 min
    Spawn shell = expectinator.spawn(new CukeExpectJExecutor(getTestEnvironment(), mabTool.getAbsolutePath()));

    ExpectedInteraction entry = null;
    try {
      for (ExpectedInteraction e : entries) {
        entry = e;
        shell.send(entry.input == null ? LINE_SEP : expandVariables(entry.input) + LINE_SEP);
        shell.expect(entry.expect, entry.timeout != null ? entry.timeout : DEFAULT_COMMAND_TIMEOUT);
      }
    } catch (TimeoutException te) {
      te.printStackTrace();
      Assert.fail("expected interaction timed out: " + entry);
    } catch (IOException ioe) {
      ioe.printStackTrace();
      Assert.fail("expected interaction " + entry + " failed with " + ioe);

    } finally {
      shell.send("exit" + LINE_SEP);
      shell.expectClose();
      shell.stop();
    }
  }

  @Then("The server at \"([^\"]*)\" is down$")
  public static void _the_server_at_is_down(String endpointUrl) {
    try {
      HttpURLConnection connection = (HttpURLConnection) new URL(endpointUrl).openConnection();
      connection.setConnectTimeout(5000);
      connection.setReadTimeout(5000);
      connection.setRequestMethod("GET");
      connection.getResponseCode();
      Assert.fail("the endpoint " + endpointUrl + " should not have been available");
    } catch (MalformedURLException e) {
      throw new AssertionError(e);
    } catch (IOException e) {
      // this is what we are hoping for..
    }

  }


  static public class CommandLineEntry {
    String workingDirectory;
    String command;
    boolean async;   // default is false
    int expectedStatus;
    String expectedContent;
    String unexpectedContent;
    String expression;
    String variable;
    boolean expectedFailure; // default is false
    String description; // usually unused by client code, used for describing scenario use cases in cucumber report

  }

  static public class ExpectedInteraction {
    String input;
    String expect;
    Integer timeout;

    @Override
    public String toString() {
      return "Expected interaction: " +
          "input: " + input + ", " +
          "expected snippet : " + expect + ", " +
          "timeout: " + timeout;
    }
  }

  @When("I run the mab script \"([^\"]*)\" with content:")
  public static void I_run_mab_script(String filePath, String content) throws Throwable {
    runScript(filePath, content, ScenarioUtils.InstallerType.MAB);
  }

  @When("I run the r2m script \"([^\"]*)\" with content:")
  public static void I_run_r2m_script(String filePath, String content) throws Throwable {
    runScript(filePath, content, ScenarioUtils.InstallerType.R2M);
  }

  @When("I run the r2m-debug-suspend script \"([^\"]*)\" with content:")
  public static void I_run_mob_suspend_script(String filePath, String content) throws Throwable {
    runScript(filePath, content, ScenarioUtils.InstallerType.R2M_DEBUG_SUSPEND);
  }

  public static void runScript(String filePath, String content, ScenarioUtils.InstallerType type) throws Throwable {
    File f = ScenarioUtils.createFile(filePath, content);
    CommandLineEntry entry = new CommandLineEntry();
    String magnetToolHome = getTestEnvironment().get(MAGNET_TOOL_HOME);
    Assert.assertNotNull("MAGNET_TOOL_HOME not set, verify you installed the MAB", magnetToolHome);
    ScenarioUtils.log("\n====   SCRIPT   ====\n" + content + "\n==== END SCRIPT ====");
    String s;
    switch (type) {
      case MAB:
        s = "mab";
        break;
      case R2M:
        s = "r2m";
        break;
      case MAB_DEBUG_SUSPEND:
        s = "mab-debug-suspend";
        break;
      case R2M_DEBUG_SUSPEND:
        s = "r2m-debug-suspend";
        break;

      default:
        throw new IllegalArgumentException("Unknown executable type " + type);
    }
    entry.command = "bash bin/" + s + " run -v " + f.getCanonicalPath();
    I_run_under_working_directory_the_commands(magnetToolHome, Collections.singletonList(entry));
  }
  @When("I run under \"([^\"]*)\" the commands:")
  public static void I_run_under_working_directory_the_commands(String workingDirectory, List<CommandLineEntry> entries) throws Throwable {
    for (CommandLineEntry e : entries) {
      ensureExecute(workingDirectory.trim(),
          e.command,
          e.async,
          e.expectedStatus,
          e.expectedContent,
          e.unexpectedContent,
          e.expression,
          e.variable,
          e.expectedFailure);
    }
  }

  @When("I run under \"([^\"]*)\" the MAB commands:")
  public static void I_run_under_working_directory_the_mab_commands(String workingDirectory, List<CommandLineEntry> entries) throws Throwable {
    for (CommandLineEntry e : entries) {
      ensureExecute(workingDirectory.trim(),
          "bash bin/mab " + e.command,
          e.async,
          e.expectedStatus,
          e.expectedContent,
          e.unexpectedContent,
          e.expression,
          e.variable,
          e.expectedFailure);
    }
  }

  @When("^I run the commands:$")
  public static void I_run_the_commands(List<CommandLineEntry> entries) throws Throwable {
    for (CommandLineEntry e : entries) {
      ensureExecute(e.workingDirectory.trim(),
          e.command,
          e.async,
          e.expectedStatus,
          e.expectedContent,
          e.unexpectedContent,
          e.expression,
          e.variable,
          e.expectedFailure);
    }
  }

  @When("^I install the magnet server at \"([^\"]*)\" to \"([^\"]*)\"$")
  public static void I_install_server(String filePath, String directory) throws Throwable {
    String file = expandVariables(filePath).trim();
    Assert.assertTrue("server zip should exist:" + file, new File(file).exists());
    Assert.assertTrue("server zip should be a file:" + file, new File(file).isFile());
    File dir = new File(expandVariables(directory).trim());
    if (dir.exists()) {
      FileUtils.deleteDirectory(dir);
    }
    if (!dir.mkdir()) {
      ScenarioUtils.log("Directory already exists: " + dir);
    }
    ScenarioUtils.unzip(new File(dir, file), dir);
  }

  @When("^I wait for (\\d+) seconds$")
  public static void I_wait_for_seconds(int seconds) throws Throwable {
    Thread.sleep(seconds * 1000);
  }

  @Given("^I wait for the magnet server at \"([^\"]*)\" to be up$")
  public static void pingMagnetServer(String serverUrl) throws Throwable {
    String actual = expandVariables(serverUrl + "/rest/controllers.json");
    ScenarioUtils.log("====> Waiting for server at " + serverUrl);
    Assert.assertTrue(String.format("server at %s is not up after %d secs", actual, 90), ping(actual, 90));
  }

  @Then("^the server at \"([^\"]*)\" is up$")
  public static void serverShouldBeUp(String serverUrl) throws Throwable {
    String actual = expandVariables(serverUrl);
    Assert.assertTrue(ping(actual, 20));
  }

  @Given("^I wait for (\\d+) seconds for the magnet server at \"([^\"]*)\" to be up$")
  public static void pingMagnetServeFor(int seconds, String serverUrl) throws Throwable {
    String actual = expandVariables(serverUrl + "/rest/controllers.json");
    ScenarioUtils.log("====> Waiting for server at " + serverUrl);
    Assert.assertTrue(String.format("server at %s is not up after %d secs", actual, seconds), ping(actual, seconds));
  }


  public static boolean ping(String url, int maxSecs) {

    long startMs = System.currentTimeMillis();
    try {
      while (maxSecs >= ((System.currentTimeMillis() - startMs) / 1000)) {
        try {
          HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
          connection.setConnectTimeout(5000);
          connection.setReadTimeout(5000);
          connection.setRequestMethod("GET");
          int responseCode = connection.getResponseCode();
          if (200 <= responseCode && responseCode <= 399) {
            return true;
          } else {
            Thread.sleep(2000);
          }
        } catch (IOException exception) {
          Thread.sleep(2000);
        }

      }
    } catch (InterruptedException ie) {
      // IGNORE
    }
    return false;
  }


}
