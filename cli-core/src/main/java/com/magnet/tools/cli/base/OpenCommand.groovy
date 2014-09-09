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

import com.magnet.tools.cli.core.CommandException
import com.magnet.tools.cli.core.CoreConstants
import com.magnet.tools.cli.core.ShellSettings
import com.magnet.tools.cli.helper.CommandLineHelper
import com.magnet.tools.cli.helper.EnvironmentHelper
import com.magnet.tools.cli.messages.BaseMessages
import com.magnet.tools.cli.messages.CommonMessages
import groovy.util.logging.Slf4j


/**
 * Open a resource giving a path relative to the current project path
 */
@Slf4j
class OpenCommand extends AbstractRelativePathCommand {

  public static final String DEFAULT_WIN_OPEN_DIR = "start"
  public static final String DEFAULT_WIN_OPEN_FILE = "notepad"
  public static final String DEFAULT_LINUX_OPEN_FILE = "xdg-open"
  public static final String DEFAULT_MAC_OPEN_FILE = "open"
  public static final String SERVER_LOG_OPTION = "server-log"
  public static final String MAB_LOG_OPTION = "log"
  public static final String DEFAULT_MAC_INTELLIJ_PROJECT_EDITOR = "open -b com.jetbrains.intellij.ce pom.xml"
  public static final String NO_PROJECT_EDITOR_SET = "<choose_project_editor_start_command>"

  OpenCommand(String name, List<String> aliases, boolean hidden) {
    super(name, aliases, hidden)
    l(longOpt: MAB_LOG_OPTION, args: 0, required: false, 'open the last shell log file.')
  }

  @Override
  boolean processOptions(OptionAccessor options) {
    if (options.l) {
      // open shell log
      def shellLog = new File(shell.getMagnetDirectory(), CoreConstants.CURRENT_LOG_FILE_NAME).getAbsolutePath()
      initPath = null // do not require a project
      return execute([shellLog])
    }

    return true
  }

  @Override
  List<String> getExecutable(List<String> args) {
    def exec = null
    def arg = args ? args[0] : null
    if (null == arg) {
       exec = getProjectEditor()
    }
    if (exec) {
      return CommandLineHelper.tokenize(exec)
    }
    if (arg == null) {
      arg = "."
    }
    if (projectPath && EnvironmentHelper.isWindowsOS() && new File(projectPath, arg).isDirectory()) {
      return [DEFAULT_WIN_OPEN_DIR, arg]
    }
    return CommandLineHelper.tokenize(getEditor()) + [arg]

  }

  String getEditor() {
    String editor = (String) shell.getSettings()?.get(ShellSettings.KEY_EDITOR) ?: getDefaultEditor()
    if (!EnvironmentHelper.findFile(editor)) {
      throw new CommandException(CoreConstants.COMMAND_UNKNOWN_EXECUTABLE_CODE,
          CommonMessages.cannotFindEditorExecutable(editor))
    }
    return editor
  }

  String getProjectEditor() {
    String p = shell.getSettings()?.get(ShellSettings.KEY_PROJECT_EDITOR)

    if (p == null) {
      p = getDefaultProjectEditor()
      warn(BaseMessages.noProjectEditorSet(p?:''))
    }
    return p
  }

  static String getDefaultProjectEditor() {
    if (EnvironmentHelper.isMac()) {   // At least help the intellij mac user
      return DEFAULT_MAC_INTELLIJ_PROJECT_EDITOR
    }
    return null
  }

  static String getDefaultEditor() {
    EnvironmentHelper.Environment env = EnvironmentHelper.getEnvironment()
    switch (env) {
      case EnvironmentHelper.Environment.WIN:
        return DEFAULT_WIN_OPEN_FILE
      case EnvironmentHelper.Environment.LINUX:
        return DEFAULT_LINUX_OPEN_FILE
      case EnvironmentHelper.Environment.MAC:
        return DEFAULT_MAC_OPEN_FILE
      default:
        throw new IllegalArgumentException("Unexpected environment type: ${EnvironmentHelper.getEnvironment()}")
    }

  }
}

