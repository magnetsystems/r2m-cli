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

package com.magnet.tools.cli.core
/**
 * Provides common support for managing a set of configuration
 * files for one particular class of configuration.
 *
 */
public abstract class AbstractConfigManager {


  Shell shell;


  AbstractConfigManager(Shell context) {
    this.shell = context;
  }


  /**
   * return the file extension for use in
   * pattern matching for the purposes of
   * listing.
   *
   * @return
   */
  abstract String getFileExtension();

  abstract AbstractConfigService getConfiguration(File file)

  /**
   * Return the list of configurations of
   * this type.
   */
  public List<String> getNames() {
    String fileExtension = getFileExtension();

    File[] projects = shell.getMagnetDirectory().listFiles(new FilenameFilter() {
      public boolean accept(File dir, String name) {
        return name.endsWith(fileExtension)
      }
    });

    return projects.collect() { it.name[0 .. it.name.lastIndexOf('.')-1] }
  }

  /**
   * Return the list of configurations of
   * this type.
   */
  def List<AbstractConfigService> getConfigurations() {
    String fileExtension = getFileExtension();

    File[] projects = shell.getMagnetDirectory().listFiles(new FilenameFilter() {
      public boolean accept(File dir, String name) {
        return name.endsWith(fileExtension)
      }
    });

    return projects.collect() {getConfiguration(it)}
  }

  protected File getConfigFile(String name) {
    String fullName = name + "." + getFileExtension();

    return new File(shell.getMagnetDirectory(), fullName)
  }

  boolean exists(String name) {
    return getConfigFile(name).exists();
  }

  public remove(String name) {
    File toRemove = getConfigFile(name);

    // add logic to check existence.
    // add logic to prompt as needed.

    toRemove.delete();
  }


}




