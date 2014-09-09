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
import com.magnet.tools.cli.core.ShellSettings
import com.magnet.tools.cli.helper.CommandLineHelper
import com.magnet.tools.cli.helper.PromptHelper
import com.magnet.tools.cli.messages.CommonMessages
import com.magnet.tools.utils.FileHelper
import com.magnet.tools.utils.StringHelper

import static com.magnet.tools.cli.messages.BaseMessages.*

/**
 * Command used to execute a mab script, similar to groovysh' load command
 */
class RunCommand extends AbstractCommand {

  private OptionAccessor options

  private boolean interactive

  private Map<String, String> commandProperties

  /**
   * location in the classloader for all sample scripts
   */
  static final String SCRIPTS_PARAGRAPH = "scripts"

  /**
   * Options
   */
  static final String[] OPTIONS = ['-q', '--quiet', '-v', '--verbose', LIST_SHORT_OPTION, LIST_OPTION, '-p', '--print'] as String[]
  static final String LIST_OPTION = "--list"
  static final String LIST_SHORT_OPTION = "-l"

  /**
   * Available available built-in scripts: a map of script description keyed by their id
   */
  Map<String, String> availableScripts


  /**
   * Constructor: called dynamically by the Shell and injected with name, aliases, and hidden flag defined in
   * magnet_configuration.groovy definition file
   * @param name name of the command
   * @param aliases optional list of aliases, can be null
   * @param hidden whether it is a hidden command
   */
  RunCommand(String name, List<String> aliases, boolean hidden) {
    super(name, aliases, hidden)

    // command options:
    n(longOpt:'no-substitutions', args: 0, "Quiet mode, exit with error if there are missing variables")
    q(longOpt:'quiet', args: 0, "Quiet mode, exit with error if there are missing variables")
    l(longOpt:'list', args: 0, "list all available built-in scripts")
    p(longOpt:'print', args:1, "print out the content fo the script")
    D(args: 2,
      valueSeparator: '=',
      argName: 'key=value',
      'Pass optional variables as key-value pairs. Each token matching ${key} will be replaced with its appropriate value. ' +
      'If you do not specify these on the command line.')
  }

  @Override
  def execute(List<String> arguments) {
    ensurePropertiesAssignments(arguments)
    // Initialize list of available built-in scripts
    availableScripts = shell.getConfiguration()."${SCRIPTS_PARAGRAPH}"

    options = getValidatedOptions(arguments)

    if (options.l) {
      writer.println(availableScriptsMessage())
      return CoreConstants.COMMAND_OK_CODE
    }

    if (options.p) {
      return printScript(options.p)
    }
    if (!options.arguments()) {
      throw new CommandException(CoreConstants.COMMAND_PARSING_ERROR_CODE,
          "Missing mab script path.\n")
    }

    List<String> args = options.arguments()

    def res = CoreConstants.COMMAND_OK_CODE
    for (source in args) {
      URL url = FileHelper.getURL(CommandLineHelper.expandUserHome(source))
      if (!url) {
        // Look for scripts in the classloader
        url = getScriptsFromClassLoader(source)
        if (!url) {
          throw new CommandException(CoreConstants.COMMAND_PARSING_ERROR_CODE,
              CommonMessages.invalidResource(source))
        }
        boldInfo(getMessage(RUNNING_BUILTIN_SCRIPT, source , availableScripts.get(source)))
      } else {
        ShellSettings session = shell.getSettings()
        session?.cacheInvokedScripts(source)
      }
      res = run(url)
    }

    return res
  }

  /**
   * Perform options validation
   * @param args list of arguments
   * @return options accessor
   * @throws CommandException in case of invalid options
   */
  private OptionAccessor getValidatedOptions(List<String> args) {
    def options = parse(args)
    // syntax validation
    if (null == options) {
      throw new CommandException(CoreConstants.COMMAND_PARSING_ERROR_CODE, CommonMessages.incorrectArguments())
    }

    interactive = !options.q
    setForce(options.q)

    commandProperties = getPropertiesListAsMap(options.Ds)
    return options

  }

  /**
   * Execute a script by its url
   * @param url url to script
   * @return
   */
  def run(URL url) {
    if (verbose) {
      boldInfo(CommonMessages.executing(url))
    }

    def res = CoreConstants.COMMAND_OK_CODE
    // Check for variables to replace
    String content = url.text
    Set<String> variables = options.n ? [] : StringHelper.findVariables(content)
    Map<String, Object> bindings = null
    if (variables) {
      bindings = promptForVariables(variables)
    }
    shell.trace("Invoking $url with variables $bindings")
    String expandedContent = StringHelper.replaceVariables(content, bindings)
    shell.trace("Script with expanded content is:\n$expandedContent")
    List<String> lines = expandedContent.readLines()
    for (String line : lines) {
      if (verbose || (!line.startsWith('println') && !line.isEmpty())) {
        boldGreen(CoreConstants.PROMPT + line)
      }
      res = shell << line
      if (res) {
        return res
      }
    }

    return res
  }

  Map<String, Object> promptForVariables(variables) {
    def questionnaire = [:]
    for (v in variables) {
      questionnaire.put(v, [(PromptHelper.TYPE_KEY): PromptHelper.STRING_TYPE])
    }
    def prompt = new PromptHelper(shell, questionnaire)
    return prompt.complete(interactive, commandProperties)
  }

  private URL getScriptsFromClassLoader(String scriptName) {
    return this.getClass().getClassLoader().getResource(SCRIPTS_PARAGRAPH + "/" + scriptName)
  }

  /**
   * Prompt for script name
   * @return the chose scripts
   */
  private String availableScriptsMessage() {
    StringBuilder builder = new StringBuilder(availableScripts())
    builder.append(StringHelper.LINE_SEP)
    for (script in availableScripts) {
      builder.append(StringHelper.padRight(StringHelper.b(script.getKey()), 36)).
          append(StringHelper.f(StringHelper.BULLET + script.getValue())).append("\n")
    }
    ShellSettings userSession = shell.getSettings()
    Map<String, String> cachedScripts = userSession?.getInvokedScripts()
    if (cachedScripts) {
      builder.append(StringHelper.LINE_SEP)
      builder.append(StringHelper.b(getMessage(PREVIOUSLY_INVOKED_SCRIPTS))).append(StringHelper.LINE_SEP)
      for (cachedScript in cachedScripts) {
        def date = new Date(Long.parseLong(cachedScript.getKey().substring(ShellSettings.KEY_PREFIX.size())))
        builder.append(StringHelper.INDENT).append(StringHelper.BULLET).append(cachedScript.getValue()).
            append(StringHelper.INDENT).append(StringHelper.f("(" + getMessage(LAST_INVOKED_SCRIPT_AT, date.format("yyyy-MM-dd HH:mm:ss") + ")\n")))
      }

    }
    return builder.toString()
  }

  private def printScript(def script) {
    shell.trace("Displaying content of URL: $script")
    URL url = FileHelper.getURL(CommandLineHelper.expandUserHome(script))
    if (!url) {
      url = getScriptsFromClassLoader(script)
      if (!url) {
        throw new CommandException(CoreConstants.COMMAND_PARSING_ERROR_CODE,
            CommonMessages.invalidResource(script))
      }
    }

    writer.println(url.text)
    writer.flush()
    return CoreConstants.COMMAND_OK_CODE
  }

}


