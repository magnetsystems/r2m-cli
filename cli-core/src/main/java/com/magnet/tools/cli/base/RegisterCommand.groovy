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
import com.magnet.tools.utils.FileHelper
import static com.magnet.tools.cli.messages.BaseMessages.*

/**
 * Command used to install plugins
 */
class RegisterCommand extends AbstractCommand {

  private OptionAccessor options

  static final String INSTALL_OPTION = "install"
  static final String UNINSTALL_OPTION = "uninstall"

  /**
   * Constructor: called dynamically by the Shell and injected with name, aliases, and hidden flag defined in
   * magnet_configuration.groovy definition file
   * @param name name of the command
   * @param aliases optional list of aliases, can be null
   * @param hidden whether it is a hidden command
   */
  RegisterCommand(String name, List<String> aliases, boolean hidden) {
    super(name, aliases, hidden)

    // command options:
    u(longOpt: UNINSTALL_OPTION,
      args: 1,
      argName: 'commandName',
      'unregister a command by name')
    i(longOpt: INSTALL_OPTION,
      args: 1,
      argName: 'sourceFile',
      'register a mab command given a path or URL to the command source')
  }

  @Override
  def execute(List<String> arguments) {

    options = getValidatedOptions(arguments)

    if (options.arguments()) {
      register(options.arguments())
    }

    if (options.u) {
      unregister(options.u)
    }

    if (options.i) {
      registerByURL(options.i)
    }
    return CoreConstants.COMMAND_OK_CODE
  }

  private void unregister(String name) {
    shell.unregisterCommand(name)
  }

  private void register(List<String> args) {
    shell.registerCommand(args)
  }

  private String registerByURL(String source) {
    URL url = FileHelper.getURL(source)
    if (null == url) {
      throw new CommandException(CoreConstants.COMMAND_PARSING_ERROR_CODE, CommonMessages.invalidResource(source))
    }
    if (isVerbose(options.v)) {
      info("Installing command by URL: $url")
    }
    return getShell().registerCommandByURL(url)

  }

  private OptionAccessor getValidatedOptions(List<String> args) {
    def options = parse(args)
    // syntax validation
    if (null == options) {
      throw new CommandException(CoreConstants.COMMAND_PARSING_ERROR_CODE, CommonMessages.incorrectArguments())
    }

    // mandatory arguments
    if (!options.i && !options.u && options.arguments().size() == 0) {
      throw new CommandException(CoreConstants.COMMAND_PARSING_ERROR_CODE, getMessage(MISSING_REGISTER_COMMAND_OPTIONS, INSTALL_OPTION, UNINSTALL_OPTION))
    }

    return options

  }



}
