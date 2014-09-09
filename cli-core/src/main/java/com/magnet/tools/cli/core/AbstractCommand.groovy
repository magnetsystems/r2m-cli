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
package com.magnet.tools.cli.core

import com.magnet.tools.cli.helper.CommandLineHelper
import com.magnet.tools.cli.helper.PromptHelper
import com.magnet.tools.cli.messages.CommandUsagesMessages
import com.magnet.tools.cli.messages.CommonMessages
import com.magnet.tools.cli.validation.Diagnostic
import com.magnet.tools.utils.AnsiHelper
import com.magnet.tools.utils.FileHelper
import groovy.util.logging.Slf4j
import jline.Completor
import org.apache.commons.cli.GnuParser
import org.apache.commons.cli.ParseException
import org.apache.commons.cli.PosixParser
import org.codehaus.groovy.cli.GroovyPosixParser

import static com.magnet.tools.utils.StringHelper.*

/**
 * Base implementation for {@link com.magnet.tools.cli.core.Command}
 */
@Slf4j
abstract class AbstractCommand extends CliBuilder implements Command {

  private static final String COMMAND_SUMMARY_VARIABLE = "command.summary"
  private static final String COMMAND_ALIASES_VARIABLE = "command.aliases"
  private static final String COMMAND_OPTIONS_VARIABLE = "command.options"
  private static final String COMMAND_DESCRIPTION_VARIABLE = "command.description"

  /**
   * By convention the completer FQN is the associated command class FQN + "Completer"
   */
  private static final String COMPLETER_CLASS_SUFFIX = "Completer"

  /**
   * Shell within which this command is executing
   * Holds references on console input/output commands etc...
   */
  Shell shell

  /**
   * Name of this command
   */
  String name

  /**
   * If this command was invoked with verbose on.
   */
  boolean verboseOn

  /**
   * whether command is hidden
   */
  boolean hidden

  /**
   * Aliases, can be null if none
   */
  List<String> aliases

  /**
   * If commands should force the default answer.
   */
  private boolean force = false

  AbstractCommand(String name, List<String> aliases, boolean hidden = false) {
    super()

    this.name = name
    this.aliases = aliases
    this.hidden = hidden

    this.verboseOn = false
    stopAtNonOption = false

    // common option(s):
    this.with {
      v(longOpt: 'verbose', args: 0, required: false, 'specify for verbose output')
    }

  }

  /**
   * Constructor. Infer the usage, and summary from a ResourceBundle
   * @param name name of command
   * @param hidden whether command is hidden
   */
  AbstractCommand(String name, boolean hidden = false) {
    this(name, null, hidden)
  }

  /**
   * Constructor
   * @param name command name
   * @param usage command usage
   * @param header command short help (appearing in help [command])
   */
  AbstractCommand(String name, String usage, String header) {
    this(name, usage, header, false)
  }

  /**
   * Constructor
   * @param name command name
   * @param usage command usage
   * @param header command short help (appearing in help [command])
   * @param hidden whether command is hidden
   */
  AbstractCommand(String name, String usage, String header, boolean hidden) {
    super()

    // command option
    v(longOpt: 'verbose', args: 0, argName: 'verbose flag', required: false, 'specify for verbose output')

    // other initializations
    this.usage = usage
    setHeader(header)
    this.name = name
    this.verboseOn = false
    this.hidden = hidden
  }

  /**
   * Print the usage message with writer (default: System.out) and formatter (default: HelpFormatter)
   */
  @Override
  void usage(boolean verbose) {
    if (!CommandUsagesMessages.isDefined(this.class)) {
      super.usage()
      return
    }
    // Summary (mandatory)
    shell.getWriter().println()
    shell.getWriter().println(" " + AnsiHelper.bold(name) + "\t- " + getHeader())
    shell.getWriter().println()

    ResourceBundle bundle = CommandUsagesMessages.getBundle(this.class)
    // Aliases (optional)
    try {
      String aliases = bundle.getString(COMMAND_ALIASES_VARIABLE)
      if (aliases != null && aliases.size() != 0) {
        shell.getWriter().println(" " + AnsiHelper.underline("Aliases"))
        shell.getWriter().println()
        shell.getWriter().println(" " + AnsiHelper.bold(aliases))
        shell.getWriter().println()
      }
    } catch (MissingResourceException e) {
      // eat it and proceed
    }
    // Usage (mandatory)
    shell.getWriter().println(" " + CommonMessages.getMessage(CommonMessages.USAGE))
    shell.getWriter().println()
    shell.getWriter().println(" " + AnsiHelper.bold(name) + " " + AnsiHelper.renderFormatted(bundle.getString(COMMAND_OPTIONS_VARIABLE)))
    shell.getWriter().println()

    if (isVerbose() || verbose) {
      // Description (mandatory, but will not appear if empty)
      shell.getWriter().println(AnsiHelper.renderFormatted(bundle.getString(COMMAND_DESCRIPTION_VARIABLE)))
      shell.getWriter().flush()
    }
  }

  void validateOptionsOrThrow(OptionAccessor options) throws CommandException {
    if (null == options) {
      throw new CommandException(CoreConstants.COMMAND_PARSING_ERROR_CODE,
          CommonMessages.incorrectArguments())
    }
  }

  /**
   * Get the argument at index <code>index</code>, use index 0 by default
   * @param options options accessor
   * @param defaultValue default value if not found
   * @param index index (1 for 2nd arg for instance), if not set use index 0
   * @return the argument or the default value if none is found
   */
  String getSingleArgument(OptionAccessor options, String defaultValue = null) {
    if (options.arguments().size() > 1) {
      throw new CommandException(CoreConstants.COMMAND_PARSING_ERROR_CODE,
          CommonMessages.tooManyArguments(getName()))
    }

    return getArgumentAt(options, 0, defaultValue)
  }

  static List<String> getAllArguments(OptionAccessor options, List<String> defaultArguments = []) {
    return options.arguments().size() ? options.arguments() : defaultArguments;
  }

  static String getArgumentAt(OptionAccessor options, int index, String defaultValue = null) {
    options.arguments()
    String result = defaultValue;
    if (options.arguments().size() >= index + 1) {
      result = options.arguments()[index];
    }
    return result?.trim();

  }

  /**
   * same as super.parse() except that we don't want to display the usage.
   * @param args list of arguments
   * @return null if error, otherwise return options access instance
   */
  private OptionAccessor _parse(List<String> args) {
    if (expandArgumentFiles) {
      args = expandArgumentFiles(args)
    }
    if (!parser) {
      parser = posix == null ? new GroovyPosixParser() : posix ? new PosixParser() : new GnuParser()
    }
    try {
      return new OptionAccessor(parser.parse(options, args as String[], stopAtNonOption))
    } catch (ParseException pe) {
      error(pe.message)
      return null
    }
  }

  OptionAccessor parse(List<String> args) {
    def options = _parse(args)
    // Get verbose flag
    verboseOn = options?.v
    return options

  }

  void checkForce(OptionAccessor options) {
    force = options?.f
  }

  @Override
  void setShell(Shell context) {
    this.shell = context
    this.writer = context.writer
  }

  @Override
  String toString() {
    getName()
  }

  @Override
  void error(String message) {
    shell.error(message)
  }

  @Override
  void error(String message, Throwable t) {
    shell.error(message, t)
  }

  @Override
  void trace(String message) {
    shell.trace(message)
  }

  @Override
  void warn(String message) {
    shell.warn(message)
  }

  @Override
  void info(String message) {
    shell.info(message)
  }

  @Override
  void boldInfo(String message) {
    shell.boldInfo(message)
  }

  @Override
  void boldGreen(String message) {
    shell.boldGreen(message)
  }

  boolean isVerbose(Boolean localVerbose = null) {
    return shell.getSettings()?.getVerbose() || verboseOn || localVerbose
  }

  boolean isDebug(Boolean localDebug = null) {
    return shell.getSettings()?.getDebug() || localDebug
  }

  boolean isForce() {
    return force
  }

  void setForce(boolean force) {
    this.force = force
  }

  boolean promptForYorN(String message, boolean defaultValueIn) {
    if (isForce()) {
      return defaultValueIn
    }
    return PromptHelper.promptYesOrNoRequired(shell, message)
  }

  /**
   * create a map of properties out of the list of properties
   * i.e. ['key1','val1','key2','val2'] -> [key1:'val1', key2:'val2']
   * This is a helper method to support specifying properties in a command where -Dkey1=val1 -Dkey2=val2 options get
   * captured as a list and not a map
   * @param propertiesList list of properties , can be  Boolean.FALSE too.
   * @return an empty map if the list is null or empty, or the associated map for this list
   */
  static Map<String, String> getPropertiesListAsMap(def propertiesList) {
    if (!propertiesList) {
      return [:]
    }

    // create a map of properties out of the list of properties
    Map<String, String> properties = new HashMap((int) (propertiesList.size() / 2));
    int i = 0;
    while (i < propertiesList.size()) {
      properties.put(propertiesList[i++], propertiesList[i++]);
    }
    return properties

  }

  /**
   * Get the file corresponding to the <code>source</code>
   * If <code>source</code> is a file path then return the file instance for it
   * If <code>source</code> is a URL then copy the resource to a local file, and return an instance of it
   * If there is no resource associated with source, then throw a CommandException
   * @param filePath
   * @return file instance
   * @throws CommandException no resource associated with the source
   */
  static File getFileFromURL(String source) throws CommandException {

    File file = new File(source)
    if (file.exists()) {
      return file
    }

    URL url = FileHelper.getURL(source)
    if (!url) {
      return null
    }

    return download(source)

  }

  static File download(String address) {
    String fileName = address.tokenize("/")[-1]
    File temp = File.createTempFile("downloaded-" + System.currentTimeMillis(), fileName)
    log.info("Downloading resource ${address} to ${temp}")
    OutputStream out = null
    try {
      out = new BufferedOutputStream(new FileOutputStream(temp))
      out << new URL(address).openStream()
    } finally {
      out?.close()
    }

    return temp
  }


  @Override
  List<Diagnostic> validate(Object target, List<String> filter) {
    trace("Command validation of $target")
    List<Diagnostic> diags = getShell().validate(target, filter)
    if (!diags) {
      return diags
    }

    def errors = diags.findAll { it.getType() == Diagnostic.Type.ERROR }
    if (errors) {
      def msg = errors*.getDiagnostic().join(LINE_SEP)
      error(msg)
    }

    def warnings = diags.findAll { it.getType() == Diagnostic.Type.WARNING }
    if (warnings) {
      def msg = warnings*.getDiagnostic().join(LINE_SEP)
      warn(msg)
    }

    def infos = diags.findAll { it.getType() == Diagnostic.Type.INFO }
    if (infos) {
      def msg = infos*.getDiagnostic().join(LINE_SEP)
      info(msg)
    }

    if (errors) {
      throw new CommandException(CoreConstants.COMMAND_INVALID_PROJECT_CODE,
          CommonMessages.validationFailed(errors.join(", ")))
    }

    if (warnings && !isForce()) {
      boolean proceed = PromptHelper.promptYesOrNo(getShell(),
          CommonMessages.continueQuestion(), true)
      if (!proceed) {
        throw new CommandException(CoreConstants.COMMAND_ABORT_CODE,
            CommonMessages.commandAborted())
      }
    }

    return diags

  }

  @Override
  String getHeader() {
    return CommandUsagesMessages.isDefined(this.class) ? CommandUsagesMessages.getMessage(this.class, COMMAND_SUMMARY_VARIABLE) : super.getHeader()
  }

  /**
   * pre-validate the arguments to workaround issues with property argument parsing
   * Ensure that all properties are set with -Dkey=value and not -Dkey
   * @param list of arguments
   * @throws CommandException if list is invalid
   */
  static void ensurePropertiesAssignments(List<String> args) {
    List<String> found = []
    args.each {
      if (it ==~ /-D[^=]*/) {
        found << it
      }
    }
    if (found) {
      String msg = CommonMessages.getMessage(CommonMessages.ARGUMENTS_NOT_SUPPORTED_AS_PROPERTIES, found.join(','))
      throw new CommandException(CoreConstants.COMMAND_PARSING_ERROR_CODE, msg)
    }
  }

  @Override
  Completor getCompleter() {
    def commandClassName = this.getClass().getCanonicalName()
    def className = commandClassName + COMPLETER_CLASS_SUFFIX
    def completer = null
    try {
      Class<?> clazz = shell.loadClass(className)
      completer = (clazz in ShellAware) ? clazz.newInstance(shell) : clazz.newInstance()
    } catch (ClassNotFoundException e) {
      // eat it
    }
    log.debug("Returning completer $completer")
    return completer as Completor
  }

  void writePadding(boolean isCurrent, String... messages = null) {
    if (isCurrent) {
      writer.append(SELECTED)
    } else {
      writer.append(LIST_PADDING)
    }
    if (messages) {
      StringBuilder sb = new StringBuilder()
      for (m in messages) {
        if (m) {
          sb.append(m)
        }
      }
      writer.println(sb.toString())
    }
  }

  void writeBullet(int n = 1, String... messages = null) {
    n.times { writer.append(LIST_PADDING) }
    writer.append(BULLET)
    if (messages) {
      StringBuilder sb = new StringBuilder()
      for (m in messages) {
        if (m) {
          sb.append(m)
        }
      }
      writer.println(sb.toString())
    }
  }

  void printStealthy(String b) {
    printStealthy(writer, b)
  }

  String expandPath(String path) {
    if(path) {
      path = path.trim()
      String toolRunTimePath = getShell().getCurrentPath().getCanonicalPath()
      if(!toolRunTimePath.endsWith(File.separator)) {
        toolRunTimePath += File.separator
      }
      if(path.startsWith("..${File.separator}")) {
        return toolRunTimePath + path
      } else if(path.startsWith(".${File.separator}")) {
        return toolRunTimePath + path.substring(2)
      } else if('.'.equals(path)) {
        return toolRunTimePath
      } else {
        return CommandLineHelper.expandUserHome(path)
      }
    }

    return path
  }

}
