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
import com.magnet.tools.cli.core.Command
import com.magnet.tools.cli.core.CommandException
import com.magnet.tools.cli.core.CoreConstants
import com.magnet.tools.cli.messages.CommonMessages

/**
 * Base classes for all commands that apply to a resource relative to the project path
 */
abstract class AbstractRelativePathCommand extends AbstractCommand {

  /**
   * Path to current project
   */
  File projectPath = null

  /**
   * Constructor: called dynamically by the Shell and injected with name, aliases, and hidden flag defined in
   * magnet_configuration.groovy definition file
   * @param name name of the command
   * @param aliases optional list of aliases, can be null
   * @param hidden whether it is a hidden command
   */
  AbstractRelativePathCommand(String name, List<String> aliases, boolean hidden) {
    super(name, aliases, hidden)
  }

  /**
   * Get the command line to execute for a particular argument
   * @param file first argument
   * @return list of arguments to execute
   */
  abstract List<String> getExecutable(List<String> args)

  @Override
  def execute(List<String> args) {
    // Get and validate options
    def options = super.parse(args)
    if (null == options) {
      throw new CommandException(CoreConstants.COMMAND_PARSING_ERROR_CODE, CommonMessages.incorrectArguments())
    }
    if (!processOptions(options)) {
      return CoreConstants.COMMAND_OK_CODE
    }
    initPath?.call()

    def exec = getExecutable(args)
    trace("Internally calling: ${exec.join(' ')} in directory $projectPath")
    Command execCommand = shell.getCommand(CoreConstants.EXEC_COMMAND)
    execCommand.workingDirectory = projectPath
    return execCommand.execute(exec)
  }

  /**
   * To support the case where the shell does not support projects, we assume that the current path will be the project
   * path
   */
  Closure initPath = {
    projectPath = getShell().getCurrentPath()
  }

  /**
   * Override this method to perform option process
   * @param options
   * @param projectPath
   * @return true if processing should continue after processing, false, if command should return
   */
  boolean processOptions(OptionAccessor options) {
    return true
  }

}
