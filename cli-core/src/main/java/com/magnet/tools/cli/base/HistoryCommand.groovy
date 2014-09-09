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
import com.magnet.tools.cli.messages.CommonMessages
import com.magnet.tools.utils.AnsiHelper
import groovy.util.logging.Slf4j
import jline.History

/**
 * History command
 * Display history or execute command from history
 *
 */
@Slf4j
class HistoryCommand extends AbstractCommand {

  /**
   * Constructor: called dynamically by the Shell and injected with name, aliases, and hidden flag defined in
   * magnet_configuration.groovy definition file
   * @param name name of the command
   * @param aliases optional list of aliases, can be null
   * @param hidden whether it is a hidden command
   */
  HistoryCommand(String name, List<String> aliases, boolean hidden) {
    super(name, aliases, hidden)
  }

  @Override
  def execute(List<String> args) {
    def options = parse(args)
    validateOptionsOrThrow(options)

    History history = shell.getHistory()
    if (!history) {
      throw new CommandException(CoreConstants.COMMAND_UNKNOWN_ERROR_CODE, "History is not enabled.")
    }

    // display all history
    if (options.arguments() && options.arguments().size() > 1) {
      // invalid
      throw new CommandException(CoreConstants.COMMAND_PARSING_ERROR_CODE,
          CommonMessages.tooManyArguments(getName()))
    }
    if (options.arguments()) {
      String s = options.arguments()[0]
      switch (s) {
        case "clear":
          history.clear()
          return CoreConstants.COMMAND_OK_CODE
        default:
          throw new CommandException(CoreConstants.COMMAND_PARSING_ERROR_CODE, CommonMessages.incorrectArguments())


      }
    }
    List entries = history.getHistoryList()
    if (!entries) {
      return
    }
    int i = 0
    for (e in entries) {
      i++
      writer.println(String.format('%s %s', AnsiHelper.bold(i + ":"), e))
    }
    writer.flush()

  }


}
