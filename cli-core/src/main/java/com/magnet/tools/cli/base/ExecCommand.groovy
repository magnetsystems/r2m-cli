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
import com.magnet.tools.cli.core.CommandException
import com.magnet.tools.cli.core.CoreConstants
import com.magnet.tools.cli.helper.CommandLineHelper
import com.magnet.tools.cli.helper.EnvironmentHelper
import com.magnet.tools.cli.helper.ProcessHelper
import com.magnet.tools.cli.helper.ProcessOutput
import groovy.util.logging.Slf4j
import static com.magnet.tools.cli.messages.BaseMessages.*

/**
 * Allows running sh/cmd commands from tool
 */
@Slf4j
class ExecCommand extends AbstractCommand {

  File workingDirectory = null

  /**
   * Constructor: called dynamically by the Shell and injected with name, aliases, and hidden flag defined in
   * magnet_configuration.groovy definition file
   * @param name name of the command
   * @param aliases optional list of aliases, can be null
   * @param hidden whether it is a hidden command
   */
  ExecCommand(String name, List<String> aliases, boolean hidden) {
    super(name, aliases, hidden)
  }

  @Override
  def execute(List<String> args) {
    if (!args) {
      throw new CommandException(CoreConstants.COMMAND_PARSING_ERROR_CODE, getMessage(NO_SHELL_COMMAND_TO_RUN))
    }
    
    String command
    if(EnvironmentHelper.isWindowsOS()) {
      command = "cmd"
      args = new ArrayList<String>(args)
      args.add(0, "/c")
    } else {
      command = args.get(0)
      args = args.subList(1, args.size())
    }

    // Expand user home
    for(int i = 0; i < args.size(); i++) {
      args.set(i, CommandLineHelper.expandUserHome(args.get(i)))
    }

    try {
      shell.trace("Executing $command with args $args ${workingDirectory? "in " + workingDirectory : ''}")
      ProcessOutput output = ProcessHelper.runReturnOutput(getShell(), command, args, null, workingDirectory, false)
      getWriter().write(output.getOutput())
      return output.getExitCode() ?: CoreConstants.COMMAND_OK_CODE
    } catch (e) {
      log.error(getMessage(PROCESS_FAILED_WITH_MESSAGE, e.getMessage()), e)
      throw new CommandException(CoreConstants.COMMAND_PARSING_ERROR_CODE, getMessage(PROCESS_FAILED_WITH_MESSAGE, e.getMessage()))
    } finally {
      getWriter().flush()
    }
  }



}

