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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.hamcrest.MatcherAssert;
import org.hamcrest.text.StringContains;
import org.junit.Assert;

import cucumber.api.java.en.Then;

/**
 * various step definitions for filesystem operations
 */
public class FileSystemStepDefs {
  @Then("^the directory structure for \"([^\"]*)\" should be:$")
  public static void the_directory_should_be(String directory, List<String> entries) throws Throwable {
    the_directory_should_exist(directory);
    List<AssertionError> errors = new ArrayList<AssertionError>();
      for (String entry : entries) {
        try {
          if (entry.endsWith("/") || entry.endsWith("\\")) {
            ScenarioUtils.the_directory_under_directory_should_exist(directory, entry);
          } else {
            ScenarioUtils.the_file_under_directory_should_exist(directory, entry);
          }
        } catch (AssertionError e) {
          errors.add(e);
        }
      }
    if (!errors.isEmpty()) {
      String dir = ScenarioUtils.expandVariables(directory);
      StringBuilder tree = new StringBuilder();
      for (AssertionError e: errors) {
        tree.append("Entry not found: ").append(e.getMessage()).append("\n");
      }
      tree.append("\n").append("The directory structure for ").append(dir).append(" was:").append("\n");
      for (File f : FileUtils.listFilesAndDirs(new File(dir), TrueFileFilter.INSTANCE, TrueFileFilter.INSTANCE)) {
        tree.append(f).append("\n");
      }
      String errorMsg = tree.append(ExceptionUtils.getStackTrace(errors.get(0))).toString();
      throw new AssertionError(errorMsg);
    }
  }

  @Then("^the directory structure for \"([^\"]*)\" should not be:$")
  public static void the_directory_should_not_be(String directory, List<String> entries) throws Throwable {
    the_directory_should_exist(directory);
    try {
      for (String entry : entries) {
        the_file_does_not_exist(directory + "/" + entry);
      }
    } catch (AssertionError e) {
      StringBuilder tree = new StringBuilder("Entry found: " + e.getMessage() +
          "\nThe directory structure for " + directory + " was:\n");
      for (File f : FileUtils.listFilesAndDirs(new File(directory), TrueFileFilter.INSTANCE, TrueFileFilter.INSTANCE)) {
        tree.append(f).append("\n");
      }
      String errorMsg = tree.append(ExceptionUtils.getStackTrace(e)).toString();
      throw new AssertionError(errorMsg);
    }
  }

  @Then("^the file \\\"([^\\\"]*)\\\" should exist$")
  public static void the_file_should_exist(String filepath) throws Throwable {
    File file = new File(ScenarioUtils.expandVariables(filepath));
    Assert.assertTrue("File " + file.getAbsolutePath() + " should be a file", file.isFile());
    Assert.assertTrue("File " + file.getAbsolutePath() + " should exist", file.exists());
  }

  @Then("^the file \\\"([^\\\"]*)\\\" should contain:")
  public static void the_file_should_contain(String filepath, String expected) throws Throwable {
    File file = new File(ScenarioUtils.expandVariables(filepath));
    the_file_should_exist(filepath);
    MatcherAssert.assertThat(FileUtils.readFileToString(file), StringContains.containsString(ScenarioUtils.expandVariables(expected.trim())));
  }

  @Then("^the file \\\"([^\\\"]*)\\\" should not contain:")
  public static void the_file_should_not_contain(String filepath, String expected) throws Throwable {
    File file = new File(ScenarioUtils.expandVariables(filepath));
    the_file_should_exist(filepath);
    Assert.assertFalse(FileUtils.readFileToString(file).contains(ScenarioUtils.expandVariables(expected.trim())));
  }

  @Then("^the file \\\"([^\\\"]*)\\\" should contain all of the following:")
  public static void the_file_should_contain_all(String filepath, List<String> expectedList) throws Throwable {
    File file = new File(ScenarioUtils.expandVariables(filepath));
    the_file_should_exist(filepath);
    for (String expected: expectedList) {
    MatcherAssert.assertThat(FileUtils.readFileToString(file), StringContains.containsString(ScenarioUtils.expandVariables(expected.trim())));
    }
  }

  @Then("^the directory \\\"([^\\\"]*)\\\" should exist$")
  public static void the_directory_should_exist(String filepath) throws Throwable {
    File file = new File(ScenarioUtils.expandVariables(filepath));
    Assert.assertTrue("File " + file.getAbsolutePath() + " should be a directory", file.isDirectory());
    Assert.assertTrue("Directory " + file.getAbsolutePath() + " should exist", file.exists());
  }

  @Then("^the file \\\"([^\\\"]*)\\\" should not exist$")
  public static void the_file_does_not_exist(String filepath) throws Throwable {
    File file = new File(ScenarioUtils.expandVariables(filepath));
    Assert.assertTrue("File " + file.getAbsolutePath() + " should not exist", !file.exists());
  }

  public static String getFileContentAsString(String filePath) throws IOException {
    File file = new File(ScenarioUtils.expandVariables(filePath));
    Assert.assertTrue("file " + filePath + " should exist", file.exists() && file.isFile());
    return FileUtils.readFileToString(file);
  }
}
