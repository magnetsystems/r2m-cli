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

package com.magnet.tools.cli.base

import com.magnet.tools.cli.core.AbstractCommand
import com.magnet.tools.cli.core.CoreConstants

import static com.magnet.tools.cli.messages.BaseMessages.*

/**
 * Creates a zip file of data that may be useful in diagnosing mab issues.
 * The zip is {@link DiagnosticsCommand#ZIP_NAME} and is placed in the Magnet directory
 * (see {@link com.magnet.tools.cli.core.Shell#getMagnetDirectory()}).
 * The data in the zip include:
 * <ul>
 *     <li>The maven settings file: {@link CoreConstants#MAGNET_MAVEN_SETTINGS_XML}</li>
 *     <li>The result of running {@link CoreConstants#VALIDATE_COMMAND} with the verbose flag.
 *     This includes details of OS java, maven and other tools</li>
 *     <li>A subset of the tool directory including logs and project setup.
 *     These are located in the {@link com.magnet.tools.cli.core.CoreConstants#TOOL_NAME} sub directory</li>
 * </ul>
 */
class DiagnosticsCommand extends AbstractCommand {
  static final String ZIP_NAME = "diagnostics.zip"
  static final String VALIDATE_NAME = "validate"

  /**
   * Constructor: called dynamically by the Shell and injected with name, aliases, and hidden flag defined in
   * magnet_configuration.groovy definition file
   * @param name name of the command
   * @param aliases optional list of aliases, can be null
   * @param hidden whether it is a hidden command
   */
  DiagnosticsCommand(String name, List<String> aliases, boolean hidden) {
    super(name, aliases, hidden)
  }

  @Override
  def execute(List<String> args) {
    File dir = File.createTempDir("mab-", "-diagnostics")
    try {
      copyMabDir(dir)
      executeValidate(new File(dir, VALIDATE_NAME))
      copyMavenSettings(new File(dir, CoreConstants.MAGNET_MAVEN_SETTINGS_XML))
      //TODO: write all sorts of other info

      createZip(dir)
    } finally {
      dir.deleteDir();
    }
    info(getMessage(DIAGNOSTICS_SAVED_IN_FILE, getZipFile().getAbsoluteFile()))
  }

  private void copyMavenSettings(File toFile) {
    String settingsFile = shell.getConfiguration().mavenSettings
    if (!settingsFile) {
      def cmd = shell.getCommand(CoreConstants.VALIDATE_COMMAND)
      settingsFile = cmd.getDefaultMavenSettings() // cannot cast to ValidateCommand : get Exception.
    }
    if (settingsFile) {
      new AntBuilder().copy(file: settingsFile, toFile: toFile.getAbsoluteFile())
    } else {
      warn(getMessage(MAVEN_SETTINGS_NOT_FOUND))
    }
  }

  private void copyMabDir(File targetDir) {
    new AntBuilder().copy(todir: new File(targetDir, CoreConstants.TOOL_NAME).absolutePath) {
      fileset(dir: shell.getMagnetDirectory().absolutePath, includes: "*.project, *.cloud, magnet.*")
    }
  }

  private void executeValidate(File toFile) {
    PrintWriter writer = new PrintWriter(new PrintStream(toFile))
    try {
      ValidateCommand validateCommand =  (ValidateCommand) shell.getCommand(CoreConstants.VALIDATE_COMMAND)
      validateCommand.setWriter(writer)
      validateCommand.execute(["-v"])
      writer.flush()
    } finally {
      writer.close()
    }
  }

  private void createZip(File srcDir) {
    new AntBuilder().zip(destfile: getZipFile().absolutePath, basedir: srcDir.absolutePath)
  }

  private File getZipFile() {
    return new File(shell.getMagnetDirectory(), ZIP_NAME)
  }
}
