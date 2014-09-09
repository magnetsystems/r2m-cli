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
import groovy.util.logging.Slf4j

import java.util.regex.Pattern

import static com.magnet.tools.utils.StringHelper.b
import static com.magnet.tools.cli.messages.BaseMessages.*

/**
 * Alias command allows defining aliases
 * ex: alias quickbuild project-build -Dmaven.test.skip=true -T 1.5C
 */
@Slf4j
class AliasCommand extends AbstractCommand {

  private static final Pattern ALIAS_REGEX_PATTERN = ~/[a-zA-Z_]+[a-zA-Z0-9_]*/
  /**
   * Constructor: called dynamically by the Shell and injected with name, aliases, and hidden flag defined in
   * magnet_configuration.groovy definition file
   * @param name name of the command
   * @param aliases optional list of aliases, can be null
   * @param hidden whether it is a hidden command
   */

  AliasCommand(String name, List<String> aliases, boolean hidden) {
    super(name, aliases, hidden)
  }

  @Override
  def execute(List<String> args) {
    def session = shell.getSettings()
    def aliases = session.getUserAliases()
    if (args.size() == 0) {
      for (alias in aliases) {
        info(getDescription(alias.getKey(), alias.getValue()))
      }
      return CoreConstants.COMMAND_OK_CODE
    }
    String name = args[0]
    if (args.size() == 1) {
      def list = aliases.get(name)
      if (list) {
        info(getDescription(name, list))
        return CoreConstants.COMMAND_OK_CODE
      } else {
        throw new CommandException(CoreConstants.COMMAND_UNKNOWN_ERROR_CODE, getMessage(NO_ALIAS_FOR, name))
      }
    }

    validateAlias(name)

    // assign alias

    if (name in (shell.getCommandNames(true) + shell.getCommandAliases(true))) {
      throw new CommandException(
          CoreConstants.COMMAND_PARSING_ERROR_CODE,
          getMessage(CONFLICTING_ALIAS,args[0]))
    }
    List target = args[1..-1]
    // first validate the alias parameter
    try {
      session.addUserAlias(name, target)
    } catch (IllegalArgumentException e) {
      throw new CommandException(CoreConstants.COMMAND_PARSING_ERROR_CODE, getMessage(INVALID_ALIAS_ARGUMENTS, args))
    }
    if (verbose) {
      info(getMessage(ASSOCIATING_USER_ALIAS, name, target.join(' ')))
    }
    return CoreConstants.COMMAND_OK_CODE
  }

  private static String getDescription(String name, List<String> args) {
    return "${CoreConstants.ALIAS_COMMAND} ${b(name)} ${args.join(' ')}"
  }

  private static void validateAlias(String alias) {
    if (!(alias.toLowerCase() ==~ ALIAS_REGEX_PATTERN)) {
      throw new CommandException(CoreConstants.COMMAND_PARSING_ERROR_CODE, getMessage(INVALID_ALIAS_NAME, alias))
    }
  }

}

