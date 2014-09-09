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
import com.magnet.tools.cli.core.MagnetSettings
import com.magnet.tools.cli.messages.CommonMessages
import com.magnet.tools.utils.AnsiHelper
import static com.magnet.tools.cli.messages.BaseMessages.*

/**
 * Help command
 * Display help for one or all command
 */
class HelpCommand extends AbstractCommand {

  /**
   * Constructor: called dynamically by the Shell and injected with name, aliases, and hidden flag defined in
   * magnet_configuration.groovy definition file
   * @param name name of the command
   * @param aliases optional list of aliases, can be null
   * @param hidden whether it is a hidden command
   */
  HelpCommand(String name, List<String> aliases, boolean hidden) {
    super(name, aliases, hidden)
  }

  @Override
  def execute(List<String> args) {


    OptionAccessor options = parse(args)
    if (!options) {
      throw new CommandException(CoreConstants.COMMAND_PARSING_ERROR_CODE, CommonMessages.incorrectArguments())
    }

    if (options.arguments().size() > 1) {
      info(CommonMessages.tooManyArguments(getName()))
      execute(null)
      return CoreConstants.COMMAND_PARSING_ERROR_CODE
    }

    if (!options.arguments()) {
      info(getMessage(LIST_AVAILABLE_COMMANDS))
      for (it in shell.getCommandNames(false).sort()) {
        Command command = shell.getCommand(it)
        String s = String.format(
            '%1$-24s - %2$2s',
            AnsiHelper.bold(it + (shell.getAliases(it) ? " (${shell.getAliases(it).join(',')})" : '')),
            command.getHeader())
        writer.println(s)
        writer.flush()
      }
      return CoreConstants.COMMAND_OK_CODE
    }

    def arg = options.arguments()[0]
    Command command = getShell().getCommand(arg)
    if (null == command) {
      throw new CommandException(CoreConstants.COMMAND_PARSING_ERROR_CODE,
          getMessage(UNKNOWN_COMMAND_TRY_HELP, arg, CoreConstants.HELP_COMMAND))
    }

    command.usage(isVerbose())
    if (!isVerbose() && !(command instanceof MagnetSettings.UserAlias)/* WON-7454 */) {
      info(getMessage(USE_COMMAND_HELP_FOR_MORE, arg, CoreConstants.HELP_COMMAND))
    }

    return CoreConstants.COMMAND_OK_CODE

  }


}
