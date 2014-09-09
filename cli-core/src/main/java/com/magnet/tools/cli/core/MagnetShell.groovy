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
import com.magnet.tools.cli.helper.IncompleteQuestionnaireException
import com.magnet.tools.cli.messages.CommonMessages
import com.magnet.tools.cli.validation.Diagnostic
import com.magnet.tools.cli.validation.Validator
import com.magnet.tools.utils.AnsiHelper
import com.magnet.tools.utils.StringHelper
import groovy.transform.Synchronized
import groovy.util.logging.Slf4j
import jline.Completor
import jline.ConsoleReader
import jline.History
import jline.JlineInterruptException
import jline.Terminal
import org.codehaus.groovy.runtime.StackTraceUtils
import org.codehaus.groovy.tools.shell.AnsiDetector
import org.codehaus.groovy.tools.shell.Interpreter
import org.codehaus.groovy.tools.shell.Parser
import org.fusesource.jansi.Ansi
import org.fusesource.jansi.AnsiConsole

import java.lang.reflect.Constructor

/**
 * Base implementation for {@link Shell}
 */
@Slf4j
class MagnetShell implements Shell {
  private static final String ALIASES_KEY = "aliases";
  private static final String HIDDEN_KEY = "hidden";
  private static final String URL_KEY = "url";
  private static final String FILE_KEY = "file";
  private static final String CLASS_KEY = "class";

  /**
   * Message printed upon exit
   */

  private static final String CURRENT_PATH;

  private static final File RUNTIME_DIRECTORY;

  static {
    // Install the system adapters
    AnsiConsole.systemInstall()

    // Register jline ansi detector
    Ansi.setDetector(new AnsiDetector())

    File tmpRunTimeDir = new File(".")
    String path = tmpRunTimeDir.getAbsolutePath()
    if (path.endsWith(".")) {
      path = path.substring(0, path.length() - 1)
    }
    CURRENT_PATH = path
    RUNTIME_DIRECTORY = new File(CURRENT_PATH)
  }

  /**
   * Command line History
   * Extend original JLINE1 {@link History} implementation to support windows and expanded events among other.
   */
  private final MagnetHistory history

  /**
   * Groovysh parser: to parse one-liner groovy expression
   */
  private Parser parser

  /**
   * Groovysh interpreter: to interpret one-liner groovy expression
   */
  private Interpreter interpreter

  private ShellSettings session

  private Map<String, Object> commandsMap

  private Map<String, Object> aliasesMap

  private final Map<String, ShellExtension> extensionsMap

  /**
   * The tool configuration
   */
  final ConfigObject configuration

  /**
   * Reader from which we get the user input
   */
  ConsoleReader reader

  /**
   * Console writer
   * note: default this during startup to System.out
   */
  PrintWriter writer = new PrintWriter(System.out)

  Terminal terminal
  /**
   * whether the tool should exit
   */
  private boolean exited = false

  /**
   * Whether the shell is started in console mode or not
   */
  private final boolean isConsoleMode

  /**
   * Constructor
   * @param configuration config object which is the combination of the
   * @param isConsoleMode whether to start the shell in console mode or not  // added for WON-7125
   */
  MagnetShell(ConfigObject configuration, boolean isConsoleMode = false) {
    this.isConsoleMode = isConsoleMode
    this.configuration = configuration
    this.extensionsMap = [:]

    // WON-7354
    // this has the side effect of triggering the directory if it does not exist
    getMagnetDirectory();

    // initialize history if enabled
    File historyFile = configuration.historyFile
    if (historyFile) {
      if (!historyFile.exists()) {
        historyFile.createNewFile()
      }
      history = new MagnetHistory(historyFile)
    } else {
      history = null
    }

    commandsMap = configuration.commands
    this.aliasesMap = [:]
    commandsMap.values().each { v ->
      if (v.containsKey(ALIASES_KEY)) {
        for (alias in v.get(ALIASES_KEY)) this.aliasesMap.put(alias, v)
      }
    }

    // for groovy interpreter
    parser = new Parser()
    interpreter = new Interpreter(getClass().getClassLoader(), new Binding())
    if (isConsoleMode) {
      terminal = Terminal.setupTerminal()

      log.debug("Terminal: $terminal . Supported: $terminal.supported, " +
          "Width: ${terminal.getTerminalWidth()}, " +
          "Height: ${terminal.getTerminalHeight()}, " +
          "ANSI: ${terminal.isANSISupported()}")
    }

  }

  @Override
  Integer leftShift(String line) {
    if (!line) {
      trace("Line is empty.")
      return CoreConstants.COMMAND_OK_CODE
    }

    // interpret as groovy if first token is not a command
    def l = line.trim()
    def tokens = l.split(/\s/)
    if (!getCommand(tokens[0]) && !tokens[0].startsWith('!')) {
      return interpret([l])
    }

    // interpret command
    String expandedLine = expandEvents(l)
    def lineArguments = CommandLineHelper.tokenize(expandedLine)
    return this << lineArguments
  }

  /**
   * One-liner interpreter
   * @param lines lines to interpret as groovy
   * @return status code , not zero if an error occurred.
   */
  private Integer interpret(List<String> lines) {
    // try to parse it
    try {
      trace("Interpreting line as Groovy")
      def result = interpreter.evaluate(lines)
      if (result) {
        writer.println(AnsiHelper.bold("Result => ") + result)
        writer.flush()
      }

    } catch (Exception e) {
      unknownCommand(lines.join(StringHelper.LINE_SEP))
      return CoreConstants.COMMAND_PARSING_ERROR_CODE
    }
    return CoreConstants.COMMAND_OK_CODE
  }


  @Override
  Integer leftShift(List<String> lineArguments) {
    trace("Executing command line $lineArguments")
    def name = lineArguments.get(0)
    Command command = getCommand(name)

    if (null == command) {
      return CoreConstants.COMMAND_UNKNOWN_COMMAND
    }

    def commandArgs = lineArguments.drop(1)
    log.debug("Executing command {} with arguments {}", name, commandArgs)

    def ret
    try {
      ret = command.execute(commandArgs)
      getWriter().flush()
    } catch (ce) {
      switch (ce) {
        case CommandException:
          CommandException ex = (CommandException) ce
          if (ex.getErrorCode() == CoreConstants.COMMAND_ABORT_CODE) {
            info(ex.getMessage()) // aborted commands are voluntary, so don't display them as error.
          } else {
            error(ex.getMessage())
          }
          ret = [errorCode: ex.getErrorCode(), errorMessage: ex.getMessage()]
          break
        case IncompleteQuestionnaireException:
          error(ce.getMessage())
          ret = [errorCode: CoreConstants.COMMAND_PARSING_ERROR_CODE, errorMessage: ce.getMessage()]
          break
        case JlineInterruptException:
          trace("Questionnaire aborted with " + JlineInterruptException.getSimpleName())
          throw ce
        default:
          def logFile = new File(getMagnetDirectory(), CoreConstants.CURRENT_LOG_FILE_NAME).getAbsolutePath()
          error(CommonMessages.unexpectedException(ce.getMessage(), logFile))
          log.error("Unexpected exception", StackTraceUtils.sanitize(ce))
          ret = [errorCode: CoreConstants.COMMAND_UNKNOWN_ERROR_CODE, errorMessage: ce.getMessage()]
      }
    }

    log.debug("Execution of command {} with arguments {} returned {}", name, commandArgs, ret)

    switch (ret) {
      case null:
      case false:
        ret = CoreConstants.COMMAND_OK_CODE
        break
      case Map:
        if (ret["errorCode"] && ret["errorCode"] instanceof Integer) {
          ret = ret["errorCode"] as Integer
        } else {
          ret = CoreConstants.COMMAND_UNKNOWN_ERROR_CODE
        }
        break
      case Integer:
        ret = ret as Integer
        break
      default:
        ret = CoreConstants.COMMAND_OK_CODE
    }

    return ret
  }


  @Override
  boolean isConsole() {
    return isConsoleMode || reader != null
  }

  @Override
  boolean hasExited() {
    return exited
  }

  @Override
  File getMagnetDirectory() {
    if (!configuration.magnetDirectory.exists()) {
      configuration.magnetDirectory.mkdirs()
    }
    return configuration.magnetDirectory
  }

  @Override
  File getInstallationDirectory() {
    return (File) configuration.installationDirectory
  }

  @Override
  History getHistory() {
    return history
  }

  @Override
  File getCurrentPath() {
    return RUNTIME_DIRECTORY
  }

  File getWorkspaceDir() {
    return getSettings().getWorkspace()
  }

  @Override
  File getProjectPath() {
    String projectName = getSettings().getCurrentProject();

    if (!projectName) {
      return null;
    }

    // TODO: design smell: dependence on project extension
    ProjectManager projectManager = (ProjectManager) getExtension(CoreConstants.PROJECT_MANAGER_EXTENSION)
    return projectManager?.getProjectPath(projectName) ? new File(projectManager.getProjectPath(projectName)) : null;
  }

  @Override
  File getProjectDeploymentDir() {

    String projectName = getSettings().getCurrentProject();

    if (!projectName) {
      return null;
    }
    // TODO: design smell: dependence on project extension
    String projectDeploymentDir = getExtension(CoreConstants.PROJECT_MANAGER_EXTENSION).getProjectDeployment(projectName);
    if (projectDeploymentDir) {
      return new File(projectDeploymentDir);
    } else {
      return null;
    }

  }

  @Override
  void setProjectDeploymentDir(String deployDir) {

    String projectName = getSettings().getCurrentProject();

    if (!projectName) {
      return;
    }

    // TODO: design smell: dependence on project extension
    getExtension(CoreConstants.PROJECT_MANAGER_EXTENSION).setProjectDeployment(projectName, deployDir);
  }


  @Override
  File getLoginFile() {
    return new File(magnetDirectory.getAbsolutePath() + "/login")
  }

  @Override
  URL getFactoryUrl() {
    return session?.getFactoryUrl() ?: configuration.developerCenterURL
  }

  @Override
  URL getMavenRepository() {
    return session?.getMavenRepository() ?: configuration.magnetMavenRepositoryURL
  }

  @Override
  void exit() {
    getWriter().println(CommonMessages.bye())
    this.exited = true
    this.history.close()
  }

  @Override
  String registerCommandByURL(URL url) {
    Command command
    try {
      command = (Command) getClassByURL(url).newInstance()
    } catch (Exception e) {
      log.error("Invalid command url $url", StackTraceUtils.sanitize(e))
      throw new CommandException(CoreConstants.COMMAND_UNKNOWN_ERROR_CODE,
          CommonMessages.getMessage(CommonMessages.getMessage(CommonMessages.INVALID_COMMAND_URL, url)))
    }

    if (commandsMap.containsKey(command.name)) {
      throw new CommandException(CoreConstants.COMMAND_UNKNOWN_ERROR_CODE,
          CommonMessages.getMessage(CommonMessages.COMMAND_ALREADY_REGISTERED, command.name))
    }
    commandsMap.put(command.name, [(URL_KEY): url.toExternalForm()])
    return command.name
  }

  @Override
  void registerCommand(List<String> args) {
    switch (args?.size()) {
      case null:
      case 0:
        throw new UnsupportedOperationException(CommonMessages.getMessage(CommonMessages.MISSING_ARGUMENT_FOR_REGISTERING_COMMAND))
      case 1:  // we only have the command
        String className = args[0]
        registerCommandByClass(className)
        break
      default: // command with at least one name
        String className = args[0]
        String name = args[1]
        def spec = [:]
        spec.put(CLASS_KEY, className)
        if (args.size() > 2) {
          List<String> aliases = args.drop(2)
          spec.put(ALIASES_KEY, aliases)
          for (alias in aliases) {
            aliasesMap.put(alias, spec)
          }
        }
        commandsMap.put(name, spec)
        break
    }
  }

  private void registerCommandByClass(String className) {
    Class<?> type
    try {
      type = loadClass(className)
    } catch (Exception e) {
      log.error("Invalid command name $className", StackTraceUtils.sanitize(e))
      throw new CommandException(CoreConstants.COMMAND_UNKNOWN_ERROR_CODE,
          CommonMessages.getMessage(CommonMessages.INVALID_COMMAND_NAME, className))
    }

    Command command = type.newInstance() as Command
    if (commandsMap.containsKey(command.name)) {
      throw new CommandException(CoreConstants.COMMAND_UNKNOWN_ERROR_CODE,
          CommonMessages.getMessage(CommonMessages.COMMAND_ALREADY_REGISTERED, command.name))
    }
    def spec = [className, command.name]
    command.getAliases()?.each { spec.add(it) }
    registerCommand(spec)
  }

  @Override
  void unregisterCommand(String name) {
    Object spec = commandsMap.remove(name)
    if (!spec) {
      throw new CommandException(CoreConstants.COMMAND_UNKNOWN_ERROR_CODE,
          CommonMessages.getMessage(CommonMessages.NO_REGISTERED_COMMAND_FOR_NAME, name))
    } else {
      writer.println(CommonMessages.getMessage(CommonMessages.COMMAND_UNREGISTED, name))
      if (spec.containsKey(ALIASES_KEY)) {
        String alias = spec.get(ALIASES_KEY)
        if (aliasesMap.remove(alias)) {
          info(CommonMessages.getMessage(CommonMessages.ALIAS_UNREGISTERED))
        }
      }
    }
  }

  @Override
  List<Diagnostic> validate(Object target, List<String> filter) {
    if (null == target) {
      throw new IllegalArgumentException(CommonMessages.getMessage(CommonMessages.CANNOT_VALIDATE_NULL_OBJECT))
    }

    String targetClazz = target.getClass().getName()
    def map = this.configuration.validators
    def filteredMap = map.findAll { k, v -> v.contains(targetClazz) }
    List<Validator> validators = filteredMap?.collect { k, v ->
      Validator validator
      Class<Validator> clazz = loadClass(k)
      if (clazz in ShellAware) {
        validator = clazz.newInstance(this)
      } else {
        validator = clazz.newInstance()
      }
      if (filter && !filter.contains(validator.getType())) {
        return null
      }
      return validator
    }


    List<Diagnostic> diags = []
    validators.each {
      if (it) {
        diags.addAll(it.validate(target))
      }
    }
    return diags
  }

  @Override
  Command getCommand(String name) {
    Map value = commandsMap.get(name)

    boolean isCommandMainName = true
    if (!value) {
      value = aliasesMap.get(name)
      isCommandMainName = false
    }

    Command command = null

    if (null == value) { // try user alias
      command = getSettings().getUserAliasCommand(name)
      if (null == command) {  // possibly a groovy invocation
        return null
      }
    }


    if (!(command instanceof MagnetSettings.UserAlias)) {
      log.debug("Instantiating command for $name")
      if (value[CLASS_KEY]) {
        if (isCommandMainName) {
          command = constructCommand(name, value)
        } else {
          // find the main name first (this is important for displaying the help
          String mainName = commandsMap.find { k, v -> v.get(ALIASES_KEY)?.contains(name) }?.getKey()
          if (!mainName) {
            throw new IllegalStateException(CommonMessages.getMessage(CommonMessages.CANNOT_FIND_MAIN_FOR_ALIAS, name))
          }
          command = constructCommand(mainName, value)
        }

      } else if (value[FILE_KEY]) {
        command = getClassByFile(new File(value[FILE_KEY] as String)).newInstance() as Command
      } else if (value[URL_KEY]) {
        command = getClassByURL(new URL(value[URL_KEY] as String)).newInstance() as Command
      }
    }

    command?.setShell(this)

    log.debug("Returning command $command")

    return command
  }

  private Command constructCommand(String name, Map commandSpecification) {
    Class<?> clazz = loadClass(commandSpecification[CLASS_KEY] as String)
    Constructor<?>[] constructors = clazz.declaredConstructors
    Constructor<?> noArgConstructor = constructors.find { !it.parameterTypes }

    if (noArgConstructor) { // for backward compatibility, we choose the no arg constructor first, if it exists
      return noArgConstructor.newInstance()
    }

    List<String> aliases = commandSpecification.get(ALIASES_KEY)
    Boolean hidden = commandSpecification.get(HIDDEN_KEY) ?: false
    try {
      return clazz.newInstance(name, aliases, hidden)
    } catch (Exception e) {
      throw new IllegalArgumentException(
          CommonMessages.getMessage(CommonMessages.CANNOT_INSTANTIATE_COMMAND, name, aliases, hidden), e)
    }


  }

  private Class<?> getClassByFile(File resource) {
    return getClassByURL(resource.toURI().toURL())
  }

  private Class<?> getClassByURL(URL resource) {
    def content = resource.getText()
    return getGroovyClassLoader().parseClass(content);
  }

  @Override
  GroovyClassLoader getGroovyClassLoader() {
    return interpreter.classLoader
  }


  @Override
  Class<?> loadClass(String name) {
    File file = getSourceFile(name)
    return file ? getClassByFile(file) : getGroovyClassLoader().loadClass(name)
  }

  @Override
  Set<String> getCommandNames(boolean includeHidden = true) {
    if (includeHidden) {
      return commandsMap.keySet()
    }
    return commandsMap.findAll { !(it.getValue().containsKey(HIDDEN_KEY) && it.getValue().get(HIDDEN_KEY)) }?.keySet()
  }

  @Override
  Set<String> getCommandAliases(boolean includeHidden = true) {
    if (includeHidden) {
      return aliasesMap.keySet()
    }
    return aliasesMap.findAll { !(it.getValue().containsKey(HIDDEN_KEY) && it.getValue().get(HIDDEN_KEY)) }?.keySet()

  }


  @Override
  List<String> getAliases(String commandName) {
    return commandsMap.get(commandName)?.get(ALIASES_KEY)
  }

  @Override
  @Synchronized
  ShellSettings getSettings() {
    if (!session) {
      session = new MagnetSettings(this)
    }

    return session
  }

  @Override
  Completor getCompleter(String name) {
    log.debug("Instantiating completer for command $name")
    Command command = getCommand(name)
    if (null == command || command.isHidden()) {
      return null
    }
    return command.getCompleter()
  }

  @Override
  void error(String s) {
    writer?.println(AnsiHelper.renderError(CommonMessages.errorMessage(s)))
    writer?.flush()
  }

  @Override
  void error(String s, Throwable t) {
    if (writer) {
      writer.println(AnsiHelper.renderError(CommonMessages.errorMessage(s)))
      writer.println(AnsiHelper.renderError(t.toString()))
      StackTraceUtils.printSanitizedStackTrace(t, writer)
      writer.flush()
    }
  }

  @Override
  void warn(String s) {
    writer?.println(AnsiHelper.bold(CommonMessages.warningMessage(s)))
    writer?.flush()
  }

  @Override
  void info(String s) {
    writer?.println(s)
    writer?.flush()
  }

  @Override
  void boldInfo(String s) {
    info(AnsiHelper.bold(s))
  }

  @Override
  void boldGreen(String s) {
    info(AnsiHelper.boldGreen(s))
  }

  @Override
  void trace(String s) {
    if (getSettings()?.isTracing()) {
      writer?.println(AnsiHelper.boldGreen("[TRACE] ") + s)
      writer?.flush()
    }
  }

  private static File getSourceFile(String className) {
    String srcDirs = System.getenv('MAB_COMMANDS_SRC') ?: System.getProperty('MAB_COMMANDS_SRC')
    if (!srcDirs) {
      return null
    }

    for (srcDir in srcDirs.split(':')) {
      File file = new File(new File(srcDirs), className.replaceAll("\\.", File.separator) + ".groovy")
      if (file.exists()) {
        return file
      }
    }
    return null
  }

  private void unknownCommand(String commandLine) {
    if (commandLine) {
      info(CommonMessages.getMessage(CommonMessages.INVALID_COMMAND_LINE, commandLine))
    }
  }

  /**
   * Expand event designator such as !!, !#, !3, etc...
   * See http://www.gnu.org/software/bash/manual/html_node/Event-Designators.html
   * Directly inspired from JLINE2.
   */
  private String expandEvents(String str) throws IOException {
    if (!history) {
      return str
    }
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < str.length(); i++) {
      char c = str.charAt(i);
      switch (c) {
        case '\\':
          // any '\!' should be considered an expansion escape, so skip expansion and strip the escape character
          // a leading '\^' should be considered an expansion escape, so skip expansion and strip the escape character
          // otherwise, add the escape
          if (i + 1 < str.length()) {
            char nextChar = str.charAt(i + 1);
            if (nextChar == '!' || (nextChar == '^' && i == 0)) {
              c = nextChar;
              i++;
            }
          }
          sb.append(c);
          break;
        case MagnetHistory.EXPANDED_EVENT_CHAR:
          if (i + 1 < str.length()) {
            c = str.charAt(++i);
            boolean neg = false;
            String rep = null;
            int i1, idx;
            switch (c) {
              case '!':
                if (history.size() == 0) {
                  throw new IllegalArgumentException(CommonMessages.getMessage.eventNotFound("!!"))
                }
                rep = history.getHistory(history.getCurrentIndex() - 1);
                break;
              case '#':
                sb.append(sb.toString());
                break;
              case '?':
                i1 = str.indexOf('?', i + 1);
                if (i1 < 0) {
                  i1 = str.length();
                }
                String sc = str.substring(i + 1, i1);
                i = i1;
                idx = history.searchBackwards(sc, history.getCurrentIndex() - 1);
                if (idx < 0) {
                  throw new IllegalArgumentException(CommonMessages.eventNotFound("!?" + sc))
                } else {
                  rep = history.getHistory(idx);
                }
                break;
              case ' ':
              case '\t':
                sb.append('!');
                sb.append(c);
                break;
              case '-':
                neg = true;
                i++;
            // fall through
              case '0':
              case '1':
              case '2':
              case '3':
              case '4':
              case '5':
              case '6':
              case '7':
              case '8':
              case '9':
                i1 = i;
                for (; i < str.length(); i++) {
                  c = str.charAt(i);
                  if (c < '0' || c > '9') {
                    break;
                  }
                }
                try {
                  idx = Integer.parseInt(str.substring(i1, i));
                } catch (NumberFormatException e) {
                  throw new IllegalArgumentException(CommonMessages.eventNotFound(neg ? "!-" : "!") + str.substring(i1, i))
                }
                if (neg) {
                  if (idx > 0 && idx <= history.size()) {
                    rep = (history.getHistory(history.getCurrentIndex() - idx)).toString();
                  } else {
                    throw new IllegalArgumentException(CommonMessages.eventNotFound(neg ? "!-" : "!") + str.substring(i1, i))
                  }
                } else {
                  if (idx > history.getCurrentIndex() - history.size() && idx <= history.getCurrentIndex()) {
                    rep = (history.getHistory(idx - 1)).toString();
                  } else {
                    throw new IllegalArgumentException(CommonMessages.eventNotFound(neg ? "!-" : "!") + str.substring(i1, i) + ": event not found")
                  }
                }
                break;
              default:
                String ss = str.substring(i);
                i = str.length();
                idx = history.searchBackwards(ss, history.getCurrentIndex() - 1);
                if (idx < 0) {
                  throw new IllegalArgumentException(CommonMessages.eventNotFound("!" + ss))
                } else {
                  rep = history.getHistory(idx);
                }
                break;
            }
            if (rep != null) {
              sb.append(rep);
            }
          } else {
            sb.append(c);
          }
          break;
        case MagnetHistory.REPLACEMENT_EVENT_CHAR:
          if (i == 0) {
            int i1 = str.indexOf("${MagnetHistory.REPLACEMENT_EVENT_CHAR}", i + 1)
            int i2 = str.indexOf("${MagnetHistory.REPLACEMENT_EVENT_CHAR}", i1 + 1)
            if (i2 < 0) {
              i2 = str.length()
            }
            if (i1 > 0 && i2 > 0) {
              String s1 = str.substring(i + 1, i1)
              String s2 = str.substring(i1 + 1, i2)
              String s = history.getHistory(history.getCurrentIndex() - 1).toString().replace(s1, s2)
              sb.append(s)
              i = i2 + 1
              break
            }
          }
          sb.append(c)
          break
        default:
          sb.append(c)
          break
      }
    }

    String result = sb.toString()
    // Print expanded command
    if (!str.equals(result)) {
      history.addToHistory(result)
      writer.println(result);
      writer.println();
      writer.flush();
    }
    return result

  }

  @Override
  ShellExtension getExtension(String key) {
    def extension = extensionsMap.get(key)
    if (extension) {
      return extension
    }

    if (!configuration.extensions) {
      trace(CommonMessages.missingExtension(key))
      return null
    }

    String className = configuration.extensions.get(key)

    Class<ShellExtension> clazz
    try {
      clazz = (Class<ShellExtension>) Class.forName(className)
    } catch (Exception e) {
      trace(CommonMessages.missingExtension(key))
      return null
    }

    extension = clazz.newInstance(this) // assuming a shell extension with this constructor
    extensionsMap.put(key, extension)

    return extension
  }
}
