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
import groovy.util.logging.Slf4j
import static com.magnet.tools.cli.messages.BaseMessages.*

/**
 * Remove an alias
 */
@Slf4j
class UnaliasCommand extends AbstractCommand {

  /**
   * Constructor: called dynamically by the Shell and injected with name, aliases, and hidden flag defined in
   * magnet_configuration.groovy definition file
   * @param name name of the command
   * @param aliases optional list of aliases, can be null
   * @param hidden whether it is a hidden command
   */
  UnaliasCommand(String name, List<String> aliases, boolean hidden) {
    super(name, aliases, hidden)
  }

  @Override
  def execute(List<String> args) {
    def session = shell.getSettings()
    if (!args) {
      throw new CommandException(CoreConstants.COMMAND_PARSING_ERROR_CODE, CommonMessages.missingArgument())
    }

    for (arg in args) {
      if (!session.removeUserAlias(arg)) {
        throw new CommandException(CoreConstants.COMMAND_PARSING_ERROR_CODE, getMessage(ALIAS_NOT_FOUND, arg))
      }
      if (isVerbose()) {
        info(getMessage(ALIAS_REMOVED, arg))
      }
    }
  }
}

