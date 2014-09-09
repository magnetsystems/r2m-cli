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

import com.magnet.tools.cli.base.ValidateCommand
import com.magnet.tools.cli.helper.EnvironmentHelper
import com.magnet.tools.cli.messages.CommonMessages
import com.magnet.tools.utils.AnsiHelper
import com.magnet.tools.utils.LogDecoratedPrintWriter
import com.magnet.tools.utils.MessagesSupport
import groovy.util.logging.Slf4j
import jline.ConsoleReader
import jline.JlineInterruptException
import org.codehaus.groovy.runtime.StackTraceUtils
import org.fusesource.jansi.AnsiConsole

import java.util.logging.LogManager

import static com.magnet.tools.utils.StringHelper.b
import static com.magnet.tools.utils.StringHelper.printStealthy

/**
 * Main class for magnet command
 */
@Slf4j
class Main {

  /**
   * Configuration object . Configuration values are defined in magnet_configuration.groovy
   */
  public static ConfigObject CONFIGURATION

  //
  // Initialize configuration statically from magnet configuration file
  //
  static {
    CONFIGURATION = getMergedConfiguration()
  }

  /**
   * Main method
   * @param args
   */
  static void main(String[] args) {
    Integer ret = CoreConstants.COMMAND_UNKNOWN_ERROR_CODE
    try {

      // Initialize the ANSI console
      AnsiConsole.systemInstall();

      boolean isConsoleMode = (args.size() == 0)

      Shell shell = new MagnetShell(CONFIGURATION, isConsoleMode)

      init(shell)

      ret = isConsoleMode ? runConsole(shell) : runBatchMode(shell, args as List<String>)
    } catch (Throwable t) {
      log.error("Mobile Application Builder error", StackTraceUtils.sanitize(t))
      t.printStackTrace()
    } finally {
      log.info("Exiting Mobile Application Builder...")
    }

    System.exit(ret ?: CoreConstants.COMMAND_OK_CODE)
  }

  /**
   * Start a console
   * @return exit value
   */
  private static Integer runConsole(Shell shell) {

    boolean retry = true
    Integer ret = CoreConstants.COMMAND_UNKNOWN_ERROR_CODE


    log.info("Starting console...")

    def firstTime = true
    while (retry) {
      retry = false
      ret = CoreConstants.COMMAND_UNKNOWN_ERROR_CODE

      // Initialize input, output
      InputStream input = new FileInputStream(FileDescriptor.in)
      Writer output = new OutputStreamWriter(System.out,
          System.getProperty("jline.WindowsTerminal.output.encoding", System.getProperty("file.encoding")))
      PrintWriter writer = null
      writer = new LogDecoratedPrintWriter(output)

      // Initialize Console Reader: attach history, set prompt and completor
      ConsoleReader reader = new ConsoleReader(input, output)
      reader.setBellEnabled(false)  // WON-8216
      shell.getHistory() ? reader.setHistory(shell.getHistory()) : reader.setUseHistory(false)
      reader.setDefaultPrompt(shell.getConfiguration().prompt)
      reader.addCompletor(new MagnetConsoleCompleter(shell))

      // attach reader, writer to shell
      shell.with {
        setReader reader
        setWriter writer
      }

      if (firstTime) {
        validate(shell)
        greetings(shell)
        firstTime = false
      }

      try {

        String line
        while ((line = reader.readLine(getPrompt(shell))) != null) {
          ret = shell << line
          if (shell.hasExited()) {
            break
          }
        }
      } catch (IllegalArgumentException e) {
        log.info("Incorrect entry", StackTraceUtils.sanitize(e))
        writer.println(CommonMessages.getMessage(CommonMessages.INCORRECT_ENTRY, e.getMessage()))
        writer.flush()
        retry = true
      } catch (CompleterException e) {
        writer.println(AnsiHelper.renderError(e.getMessage()))
        writer.flush()
        retry = true
      } catch (JlineInterruptException j) {
        retry = true
      }
      catch (Throwable e) {
        log.error("Unexpected exception:", StackTraceUtils.sanitize(e))
        writer.println(CommonMessages.unexpectedException("${e.getClass()}: ${e.getMessage()}", ""))
        writer.flush()
        retry = true
      } finally {
        if (!retry) {
          writer.flush()
          writer.close()
          return ret
        } else {
          System.setProperty(CoreConstants.SKIP_VALIDATION, 'true')
        }
      }
    } // while
    return ret
  }

  /**
   * Run in batch mode
   * @param args command name and its argument
   * @return exit value
   */
  private static Integer runBatchMode(Shell shell, List<String> lineArguments) {
    log.info("Starting in batch mode...")
    PrintWriter writer = new PrintWriter(System.out)
    shell.with {
      setReader null  // console mode
      setWriter writer
    }
    validate(shell)


    try {
      return shell << lineArguments
    } finally {
      writer.flush()
      writer.close()
    }
  }

  /**
   * Validate mab at startup
   * @return not false, if invalid, false otherwise (as in groovy-truth)
   */
  private static def validate(Shell shell) {
    int ret = CoreConstants.COMMAND_OK_CODE
    if (System.getProperty(CoreConstants.SKIP_VALIDATION)?.equals("true") || shell.configuration.skipValidation?.equals(true)) {
      return ret
    }
    if (!checkSingleProcess(shell)) {
      ret = CoreConstants.COMMAND_ABORT_CODE
    } else {
      ret = shell << "$CoreConstants.VALIDATE_COMMAND $ValidateCommand.SHORT_OPTION"
    }
    if (ret) {
      System.err.println(AnsiHelper.renderError(CommonMessages.validationFailed(ret)))
      if (!(System.getProperty(CoreConstants.ABORT_ON_VALIDATION_FAILURE_FLAG)?.equalsIgnoreCase('false'))) {
        System.exit(ret)
      }
      return ret
    }
  }

  /**
   * Ensure that there is a single instance of mab process
   * This is only called during the startup phase
   * @return false if there already is a mab.lock (suspecting an existing process)
   */
  private static boolean checkSingleProcess(Shell shell) {
    File lockFile = new File(shell.getMagnetDirectory(), CoreConstants.MAB_LOCK_FILE)
    if (lockFile.exists()) {
      String msg = CommonMessages.getMessage(CommonMessages.ANOTHER_MAB_IS_RUNNING, lockFile.getAbsolutePath())
      System.err.println(AnsiHelper.renderError(msg))
      return false;
    }
    //create lock file
    lockFile.createNewFile()

    //add shutdown hook
    Runtime.getRuntime().addShutdownHook(new Thread() {
      public void run() {
        lockFile.delete()
      }
    })

    return true
  }

  /**
   * The greeting message
   * @param shell shell instance
   */
  private static void greetings(Shell shell) {
    shell.info(CommonMessages.introMessage())
  }

  /**
   * Get the prompt for this shell
   * @param shell shell instance
   * @return prompt string
   */
  private static String getPrompt(Shell shell) {

    def extension = (PromptExtension) shell.getExtension(CoreConstants.PROMPT_EXTENSION)
    if (!extension) {
      return shell.getConfiguration().prompt
    }
    return extension.getPrompt()
  }

  /**
   * Merge all {@link CoreConstants#MAGNET_CONFIGURATION_FILE} so that services get meshed with all those that are
   * declared on the classpath.
   * Use the {@link CoreConstants#MAGNET_CONFIGURATION_OVERRIDE_FILE} to override any setting (without merge)
   * @return the merged groovy configuration for the shell
   */
  private static ConfigObject getMergedConfiguration() {

    String message = EnvironmentHelper.isWindowsOS() ? CommonMessages.loadingConfiguration() : b(CommonMessages.loadingConfiguration())
    printStealthy(System.out, message)
    Enumeration<URL> urls = Main.getClassLoader().getResources(CoreConstants.MAGNET_CONFIGURATION_FILE)
    ConfigSlurper slurper = new ConfigSlurper()
    def configs = urls.collect { slurper.parse(it) }
    ConfigObject mainConfig = new ConfigObject()

    for (config in configs) {
      mainConfig.merge(config)
    }
    // add override
    Enumeration<URL> overrideUrls = Main.getClassLoader().getResources(CoreConstants.MAGNET_CONFIGURATION_OVERRIDE_FILE)
    def overrideConfigs = overrideUrls.collect { slurper.parse(it) }
    for (overrideConfig in overrideConfigs) {
      mainConfig.merge(overrideConfig)
    }
    printStealthy(System.out, "                       ")


    return mainConfig
  }

  static void init(Shell shell) {

    // Logging initialization
    def logging = shell.getConfiguration().logging
    if (logging) {
      configureLogging(logging.toProperties())
    }

    // Locale initialization
    def supportedLocales = shell.getConfiguration().supportedLocales
    if (supportedLocales) {
      MessagesSupport.supportedLocales = supportedLocales.keySet().collect {
        MessagesSupport.stringToLocale(it)
      } as List
    }
    if (shell.getSettings()?.getLocale()) {
      MessagesSupport.currentLocale = MessagesSupport.stringToLocale(shell.getSettings().getLocale())
    }

    // Trigger before hooks in order.
    def orderedHookEntries = new TreeMap<Integer, Map>(shell.configuration.beforeHooks)
    for (e in orderedHookEntries) {
      Hook h = (Hook) Class.forName(e.getValue()).newInstance(e.getKey())
      h.run(shell)
    }

    def projectPath = shell.getProjectPath()

    if (!projectPath) {
      return
    }

    if (!projectPath.isDirectory()) {
      shell.error(CommonMessages.projectNotFound(projectPath))
      return
    }

  }

  /**
   * configure logging properties using magnet_configuration logging properties
   * @param properties properties from magnet_configuration file
   */
  private static void configureLogging(Properties properties) {
    File propsFile = File.createTempFile("logging.properties", ".tmp")
    properties.store(propsFile.newWriter(), null)
    LogManager.getLogManager().readConfiguration(propsFile.newDataInputStream())
  }


}
