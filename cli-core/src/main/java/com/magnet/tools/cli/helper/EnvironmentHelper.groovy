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
package com.magnet.tools.cli.helper

import com.magnet.tools.cli.core.CoreConstants
import groovy.util.logging.Slf4j

import static com.magnet.tools.cli.messages.HelperMessages.*

/**
 * A helper class to look up executables on the environment
 */
@Slf4j
class EnvironmentHelper {

  static final Map<String, File> paths = new HashMap<String, File>()


  static enum Environment {
    WIN,
    LINUX,
    MAC
  }

  /**
   * Get the environment type
   * @return an {@link Environment} enum
   */
  static Environment getEnvironment() {
    if (isWindowsOS()) {
      return Environment.WIN
    }
    if (isLinux()) {
      return Environment.LINUX
    }
    if (isMac()) {
      return Environment.MAC
    }
    throw new IllegalStateException("Unknown environment ${System.getProperty("os.name")}")
  }

  /**
   * Check whether current OS is Windows
   * @return true if windows platform
   */
  static boolean isWindowsOS() {
    return System.getProperty("os.name").startsWith("Win")
  }

  /**
   * Check whether current OS is Mac
   * @return true if mac platform
   */
  static boolean isMac() {
    return System.getProperty("os.name").startsWith("Mac")
  }

  /**
   * Check whether current OS is Linux
   * @return true if linux platform
   */
  static boolean isLinux() {
    return System.getProperty("os.name").startsWith("Linux")
  }

  private static String getPathFromEvn(String evnName) {
    String value = System.getenv(evnName)
    if(value) {
      if(!value.endsWith(File.separator)) {
        value = value + File.separator
      }
    }

    return value
  }

  static File findFile(String fileName) {
    String cmd
    if(isWindowsOS()) {
      cmd = "cmd /c where " + fileName
    } else {
      cmd = "which " + fileName
    }

    // Execute the command
    Process p = cmd.execute()
    p.waitFor()

    int exitValue = p.exitValue()
    if(!exitValue) {
      String result = p.text?.trim()  // remove trailing spaces (in particular carrier returns!)
      if(result) {
        return new File(result)
      }
    }
    return null
  }

  public static String getPath(String command) {

    File result = paths.get(command);
    if(result == null) {
      String fileName;
      switch(command) {
      case CoreConstants.CMD_MVN:
        fileName = "mvn"
        if(isWindowsOS()) {
          fileName += ".bat"
        }
        // USE M2_HOME, if it is set, otherwise, try the path
        String home = getPathFromEvn(CoreConstants.M2_HOME)
        if(home) {
          result = new File(home + 'bin' + File.separator + fileName)
        } else {
          result = findFile(fileName)
        }
        break;
      case CoreConstants.CMD_JAVA:
        fileName = "java"
        if(isWindowsOS()) {
          fileName += ".exe"
        }
        // Use JAVA_HOME, if it is set, otherwise, try the path
        String home = getPathFromEvn(CoreConstants.JAVA_HOME)
        if(home) {
          result = new File(home + 'bin' + File.separator + fileName)
        } else {
          result = findFile(fileName)
        }
        break;
      case CoreConstants.CMD_JPS:
        fileName = "jps"
        if(isWindowsOS()) {
          fileName += ".exe"
        }
        // Use JAVA_HOME, if it is set, otherwise, try the path
        String home = getPathFromEvn(CoreConstants.JAVA_HOME)
        if(home) {
          result = new File(home + 'bin' + File.separator + fileName)
        } else {
          result = findFile(fileName)
        }
        break;
      case CoreConstants.CMD_MYSQL:
        fileName = "mysql"
        if(isWindowsOS()) {
          fileName += ".exe"
        }
        String home = getPathFromEvn(CoreConstants.MYSQL_HOME)
        if(home) {
          result = new File(home + 'bin' + File.separator + fileName)
        } else {
          result = findFile(fileName)
        }
        break;
      case CoreConstants.CMD_POWERSHELL:
        if(!isWindowsOS()) {
          throw new IllegalArgumentException(getMessage(COMMAND_NOT_SUPPORTED, command))
        }
        fileName = "powershell.exe"

        // Try the path first, then try the typical absolute path
        result = findFile(fileName)
        if(result == null) {
          result = new File(getPathFromEvn("WINDIR") + 'System32\\WindowsPowerShell\\v1.0\\' + fileName);
        }
        break;
      default:
        // Uknown command - assume it is a shell command
        return command;
      }

      // Check to make sure the result exists
      if(result == null || !result.isFile()) {
        throw new IllegalArgumentException(getMessage(COMMAND_NOT_FOUND, command))
      }
      // Cache it - it can't change in this session
      paths.put(command, result);
    } else {
      if(!result.isFile()) {
        throw new IllegalStateException(getMessage(COMMAND_NO_LONGER_EXISTS, command, result.getAbsolutePath()))
      }
    }

    return result.getAbsolutePath();
  }

}

