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

import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.Flushable;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.ExecuteResultHandler;
import org.apache.commons.exec.ExecuteStreamHandler;
import org.apache.commons.exec.ExecuteWatchdog;
import org.apache.commons.exec.PumpStreamHandler;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.output.TeeOutputStream;
import org.apache.commons.lang.StringUtils;
import org.hamcrest.MatcherAssert;
import org.hamcrest.text.StringContains;
import org.junit.Assert;

/**
 * Executor for commands within cucumber test
 */
class CukeCommandExecutor extends DefaultExecutor implements Closeable, Flushable {

  private final String command;
  private final boolean async;
  private final TeeOutputStream teeOutputStream;
  private final File logFile;
  private final Map<String, String> environment;
  private final CommandLine commandLine;
  private static final long TIMEOUT = 20 * 60 * 1000;
  private static final String H = "ensureExecute : ";
  private static final String LINE_SEP = System.getProperty("line.separator");
  private final Map<String, String> substitutionMap;
  private final Map<String, String> testEnvironment;
  private final Map<String, Object> variables;

  /**
   * Ctor
   *
   * @param workingDirectory working directory for execution
   * @param async            whether to wait for command to return
   * @param subsMap          variables substitution
   * @param vars             variables where to save optional expression evaluation
   * @param testEnv          test environment
   * @param command          command line to execute
   * @throws java.io.IOException if an exception occurred
   */
  public CukeCommandExecutor(String command,
                             String workingDirectory,
                             boolean async,
                             Map<String, String> subsMap,
                             Map<String, Object> vars,
                             Map<String, String> testEnv) throws IOException {

    this.substitutionMap = subsMap;
    this.testEnvironment = testEnv;
    this.variables = vars;
    // Must get the existing environment so it inherits but overrides
    Map<String, String> env = new HashMap<String, String>(System.getenv());
    if (null != testEnvironment) {
      env.putAll(testEnvironment);
    }
    env.put("user.home", System.getProperty("user.home"));

    ExecuteWatchdog watchdog = new ExecuteWatchdog(TIMEOUT);
    setWatchdog(watchdog);
    String[] args = command.split("\\s+");
    commandLine = new CommandLine(args[0]);
    if (args.length > 0) {
      for (int i = 1; i < args.length; i++) {
        commandLine.addArgument(args[i]);
      }
    }
    commandLine.setSubstitutionMap(substitutionMap);

    this.environment = env;
    this.command = command;
    this.async = async;
    setWorkingDirectory(new File(workingDirectory));
    String logFileName = command.replaceAll("[:\\.\\-\\s\\/\\\\]", "_");
    if (logFileName.length() > 128) {
      logFileName = logFileName.substring(0, 100);
    }
    this.logFile = new File(getWorkingDirectory(), "cuke_logs" + File.separator + +System.currentTimeMillis() + "-" + logFileName + ".log");

    FileUtils.forceMkdir(logFile.getParentFile());

    OutputStream outputStream = new FileOutputStream(logFile);
    this.teeOutputStream = new TeeOutputStream(outputStream, System.out); // also redirected to stdout
    ExecuteStreamHandler streamHandler = new PumpStreamHandler(outputStream);
    this.setStreamHandler(streamHandler);
  }

  public void close() throws IOException {
    teeOutputStream.close();
  }

  public void flush() throws IOException {
    teeOutputStream.flush();
  }

  public void assertExecute(int expectedStatus,
                            String expectedContent,
                            String unexpectedContent,
                            String expression,
                            String variable,
                            boolean expectedFailure) throws IOException {
    ScenarioUtils.log(prepare(expectedStatus, expectedContent, unexpectedContent, false));
    if (async) {
      if (expectedStatus != 0 || null != expectedContent || null != unexpectedContent) {
        ScenarioUtils.log("WARNING: CANNOT ASSERT EXECUTION RESULT FOR ASYNCHRONOUS INVOCATION!");
      }
      ExecuteResultHandler resultHandler = new PrintResultHandler(getWatchdog());
      try {
        execute(commandLine, environment, resultHandler);
      } catch (Exception e) {
        if (!expectedFailure) {
          e.printStackTrace();
          String msg = String.format("Command failed: %s\n, Output of command is : \n%s",
              commandLine, FileUtils.readFileToString(logFile));
          Assert.fail(msg);
        }
      }
    } else {
      int exitValue = 0;
      long start = System.currentTimeMillis();
      try {
        exitValue = execute(commandLine, environment);
      } catch (Exception e) {
        if (!expectedFailure) {
          e.printStackTrace();
          String msg = String.format("Command failed: %s"
                  + LINE_SEP + "Specification is : "
                  + LINE_SEP + " %s"
                  + LINE_SEP + "Output of command is : "
                  + LINE_SEP + "%s"
                  + LINE_SEP + "Exception message: %s",
              commandLine, prepare(expectedStatus, expectedContent, unexpectedContent, true), FileUtils.readFileToString(logFile), e.getMessage());
          Assert.fail(msg);
        }
      }
      ScenarioUtils.log(H + "====> RETURN VALUE: " + exitValue
          + " (" + ((System.currentTimeMillis() - start) / 1000) + " seconds)");
      Assert.assertEquals("Command returned unexpected status: " + exitValue, expectedStatus, exitValue);
      String content = FileUtils.readFileToString(logFile);
      Object expressionResult = null;
      if (null != expectedContent) {
        MatcherAssert.assertThat(String.format(
                "%s should contain %s"
                    + LINE_SEP + "Specification is : "
                    + LINE_SEP + "%s"
                    + LINE_SEP + "output of command %s is"
                    + LINE_SEP + "%s",
                logFile, expectedContent, prepare(expectedStatus, expectedContent, unexpectedContent, true), commandLine, content),
            content, StringContains.containsString(ScenarioUtils.expandVariables(expectedContent.trim())));
      }
      if (null != unexpectedContent && StringUtils.isNotEmpty(unexpectedContent)) {
        Assert.assertFalse(String.format(
                "%s should not contain %s"
                    + LINE_SEP + "Specification is : "
                    + LINE_SEP + "%s"
                    + LINE_SEP + "output of command %s is"
                    + LINE_SEP + "%s",
                logFile, unexpectedContent, prepare(expectedStatus, expectedContent, unexpectedContent, true), commandLine, content),
            content.contains(ScenarioUtils.expandVariables(unexpectedContent.trim())));
      }

      if (null != expression && !expression.isEmpty()) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("content", content);
        map.put("logFile", logFile);
        ScenarioUtils.log("Evaluating expression: " + expression);
        expressionResult = GroovyExpression.evaluate(map, expression);
        ScenarioUtils.log("Expression result: " + expressionResult);
      }
      if (null != variable && !variable.isEmpty()) {
        ScenarioUtils.log("Saving variable '" + variable + "' with value: '" + expressionResult + "'");
        variables.put(variable, expressionResult);
      }
    }

  }

  /**
   * @param key environment variable name
   * @return value for environment variable, test environment takes precendence
   * over system environment
   */
  private static String getEnvironmentVariable(String key) {
    String value;
    value = ScenarioUtils.getTestEnvironment() != null ? ScenarioUtils.getTestEnvironment().get(key) : null;
    value = value == null ? System.getenv(key) : value;
    return value;
  }

  private String prepare(Integer expectedStatus,
                         String expectedOutput,
                         String unexpectedOutput,
                         boolean verbose) {
    StringBuilder sb = new StringBuilder(LINE_SEP);
    sb.append(LINE_SEP);
    sb.append(H + "====> EXECUTING " + (this.async ? "(ASYNCHRONOUSLY)" : "") + ": " + this.command);
    sb.append(LINE_SEP);
    sb.append(H + "Working Directory: " + this.getWorkingDirectory());
    sb.append(LINE_SEP);
    if (verbose) {
      if (!this.async) {
        sb.append(H + "Expected status: " + expectedStatus);
        sb.append(LINE_SEP);
      }
      if (expectedOutput != null && !expectedOutput.isEmpty() && !this.async) {
        sb.append(H + "Expected output: " + expectedOutput);
        sb.append(LINE_SEP);
      }
      if (unexpectedOutput != null && !unexpectedOutput.isEmpty() && !this.async) {
        sb.append(H + "Unexpected output: " + unexpectedOutput);
        sb.append(LINE_SEP);
      }
    }
    sb.append(H + "Output redirected to: " + this.logFile.getAbsolutePath());
    String mabHome = getEnvironmentVariable("MAB_HOME");
    if (null == mabHome || mabHome.isEmpty()) {
      mabHome = new File(System.getProperty("user.home"), ".magnet.com").getAbsolutePath();
    }
    sb.append(LINE_SEP);
    this.environment.put("MAB_HOME", mabHome);
    sb.append(H + "MAB_HOME=" + mabHome);

    String mavenHome = getEnvironmentVariable("M2_HOME");
    if (null != mavenHome && !mavenHome.isEmpty()) {
      sb.append(LINE_SEP);
      sb.append(H + "M2_HOME=" + mavenHome);
      this.environment.put("M2_HOME", mavenHome);
    }

    String mysqlHome = getEnvironmentVariable("MYSQL_HOME");
    if (null != mysqlHome && !mysqlHome.isEmpty()) {
      sb.append(LINE_SEP);
      sb.append(H + "MYSQL_HOME=" + mysqlHome);
      this.environment.put("MYSQL_HOME", mysqlHome);
    }

    String mavenSettings = getEnvironmentVariable("MAGNET_MAVEN_SETTINGS");
    if (null != mavenSettings && !mavenSettings.isEmpty()) {
      sb.append(LINE_SEP);
      sb.append(H + "MAGNET_MAVEN_SETTINGS=" + mavenSettings);
      this.environment.put("MAGNET_MAVEN_SETTINGS", mavenSettings);
    }
    String javaHome = getEnvironmentVariable("JAVA_HOME");
    if (verbose) {
      sb.append(LINE_SEP);
      sb.append(H + "JAVA_HOME=" + javaHome);
      sb.append(LINE_SEP);
      sb.append(H + "PATH=" + System.getenv("PATH"));

    }
    this.environment.put("JAVA_HOME", javaHome);
    this.environment.put("PATH", System.getenv("PATH"));
    sb.append(LINE_SEP);
    sb.append("...");
    return sb.toString();

  }

}
