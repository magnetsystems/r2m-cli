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

package com.magnet.tools.config

import com.magnet.tools.utils.FileHelper

/**
 * Common methods for introspecting a project file structure use by various commands.
 * We will gradually move code that introspects the project directory to this class.
 *
 */
class ProjectStructureHelper {

  public static final String SERVER_DIR =
      "server"
  public static final String TARGET_DIR =
      "target"
  public static final String DEPLOY_ZIP_PATTERN =
      "*-deploy.zip"
  public static final String CONFIG_ZIP_PATTERN =
      "*-config.zip"
  public static final String DEPLOY_FILE_PATTERN =
      "*-deploy"
  public static final String SERVER_ENVIRONMENT_GROOVY =
      "groovy/ServerEnvironment.groovy"
  public static final String APP_LIBS =
      "app-libs"

  static File getServerProjectDirectory(File projectDirectory) {
    return new File(projectDirectory.absolutePath, SERVER_DIR);
  }


  static File getServerTargetDirectory(File projectDirectory) {
    File serverDir = getServerProjectDirectory(projectDirectory);
    if (!serverDir.exists()) {
      return null;
    }

    File targetDirectory = new File(serverDir, TARGET_DIR);
    if (!targetDirectory.exists()) {
      return null;
    }
    return targetDirectory;
  }

  static File getLocalDeployDirectory(File projectDirectory) {
    File serverTargetDir = getServerTargetDirectory(projectDirectory);
    if (serverTargetDir) {
      File targetDir = FileHelper.findDirectory(serverTargetDir, DEPLOY_FILE_PATTERN)
      if (targetDir && targetDir.isDirectory() && targetDir.exists()) {
        return targetDir
      }
    }
    return null
  }

  static File getMobileApisDirectory(File projectDirectory) {
    return new File(projectDirectory, ConfigLexicon.MOBILE_APIS_DIR_RELATIVE_PATH)
  }

  static File getMobileAppsDirectory(File projectDirectory) {
    return new File(projectDirectory, ConfigLexicon.MOBILE_APPS_DIR_RELATIVE_PATH)
  }

  static File getAppLibsDirectory(File projectDirectory) {
    return new File(getLocalDeployDirectory(projectDirectory), APP_LIBS)
  }
}
