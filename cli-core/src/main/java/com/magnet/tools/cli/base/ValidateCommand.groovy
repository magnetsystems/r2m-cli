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
import com.magnet.tools.cli.core.CoreConstants
import com.magnet.tools.cli.helper.EnvironmentHelper
import com.magnet.tools.cli.helper.ProcessHelper
import com.magnet.tools.cli.helper.ProcessOutput
import com.magnet.tools.cli.messages.CommonMessages
import com.magnet.tools.config.ConfigLexicon

import static com.magnet.tools.utils.StringHelper.*
import static com.magnet.tools.cli.messages.BaseMessages.*

/**
 * validate command
 * <p>
 * Currently, it validates
 * <ul>
 *   <li>Java</li>
 *     <ul>
 *       <li>JAVA_HOME is set : WARNING if not set</li>
 *       <li>Java version is 1.7 : ERROR if not match</li>
 *       <li>Run 'java -version' : ERROR if fail</li>
 *    </ul>
 *  <li>Maven</li>
 *    <ul>
 *       <li>M2_HOME is set : INFO message is not set</li>
 *       <li>Run 'mysql --version' : ERROR if fail</li>
 *       <li>MAGNET_MAVEN_SETTINGS : INFO if not set. Try to find <user home>/.m2/settings.xml. ERROR if it's not found</li>
 *    </ul>
 *  <li>MySQL</li>
 *    <ul>
 *       <li>MYSQL_HOME is set : INFO message if not set</li>
 *       <li>Run 'mvn -version' : ERROR if fail</li>
 *    </ul>
 * </ul>
 */
class ValidateCommand extends AbstractCommand {

  public static final String SHORT_OPTION = "-s"

  private StringBuilder sbInfo = new StringBuilder()
  private static final String WARNING_HEADER = LINE_SEP
  private StringBuilder sbWarning = new StringBuilder(WARNING_HEADER)
  private static final String ERROR_HEADER = LINE_SEP
  private StringBuilder sbError = new StringBuilder(ERROR_HEADER)


  /**
   * Constructor: called dynamically by the Shell and injected with name, aliases, and hidden flag defined in
   * magnet_configuration.groovy definition file
   * @param name name of the command
   * @param aliases optional list of aliases, can be null
   * @param hidden whether it is a hidden command
   */
  ValidateCommand(String name, List<String> aliases, boolean hidden) {
    super(name, aliases, hidden)

    // command option(s):
    s(longOpt: 'short', args: 0, required: false, 'Only display errors')
  }

  @Override
  def execute(List<String> args) {

    OptionAccessor options = parse(args)
    validateOptionsOrThrow(options)

    printStealthy b(CommonMessages.validating(getMessage(VERSION) + "                        "))
    validateMabVersion()
    validateMabEnv()

    printStealthy b(CommonMessages.validating(getMessage(PLATFORM_VERSION) + "                        "))
    validateDefaultPlatformVersion()

    printStealthy b(CommonMessages.validating("Java                        "))
    validateJava()

    printStealthy b(CommonMessages.validating("Maven                        "))
    validateMaven()

    printStealthy b(CommonMessages.validating("PowerShell                        "))
    validatePowerShell()

    printStealthy b(CommonMessages.validating("MySQL                        "))
    validateMySql(sbWarning, sbInfo)

    printStealthy b(CommonMessages.validating(getMessage(PLUGINS) + "                        "))
    validatePlugins()

    printStealthy b(CommonMessages.validating(getMessage(FACTORY_URL) + "                        "))
    validateMagnetFactoryURL()

    printStealthy b(CommonMessages.validating(getMessage(MAVEN_REPOSITORY_URL) + "        "))
    validateMavenRepositoryURL()

    printStealthy b(CommonMessages.validating(languageLocale() + "                        "))
    validateLocale()

    printStealthy("                                                                        ")

    validateMagnetToolHome()
    validateMagnetDirectory()

    if (sbInfo.length() && isVerbose(options.v)) {
      info(sbInfo.toString())
    }

    boolean hasWarnings = sbWarning.length() > WARNING_HEADER.length()
    if (hasWarnings) {
      warn(sbWarning.toString())
    }

    boolean hasErrors = sbError.length() > ERROR_HEADER.length()
    if (hasErrors) {
      error(sbError.toString())
      return CoreConstants.COMMAND_UNKNOWN_ERROR_CODE
    }


    if (!(hasErrors || hasWarnings) && !options.s) {
      info(validationOk())

    }
    return CoreConstants.COMMAND_OK_CODE
  }

  private void validateMagnetToolHome() {
    addBullet(sbInfo, 1, getMessage(MAGNET_INSTALLATION_BULLET) + " " + b(shell.configuration.installationPath))
  }
  private void validateMagnetDirectory() {
    addBullet(sbInfo, 1, getMessage(MAGNET_DIRECTORY_BULLET) + " " + b(shell.getMagnetDirectory().getAbsolutePath()))
  }

  private void validatePlugins() {
    Map plugins = shell.getConfiguration().plugins
    def pluginInfo = plugins? plugins.values().collect { b(it) } : b(getMessage(NO_PLUGINS))
    addBullet(sbInfo, 1, ([getMessage(REGISTERED_PLUGINS_BULLET)] + pluginInfo) as String[])
  }

  private void validateDefaultPlatformVersion() {
    addBullet(sbInfo, 1, getMessage(DEFAULT_PLATFORM_VERSION_BULLET) + " " + b(ConfigLexicon.DEFAULT_PLATFORM_VERSION))
  }

  private void validateMagnetFactoryURL() {
    addBullet(sbInfo, 1, getMessage(MAGNET_FACTORY_URL_BULLET) + " " + b(shell.getFactoryUrl().toString()))
  }

  private void validateMavenRepositoryURL() {
    addBullet(sbInfo, 1, getMessage(MAVEN_REPOSITORY_URL) + ": " + b(shell.getMavenRepository().toString()))
  }

  private void validateLocale() {
    addBullet(sbInfo, 1, languageLocale(),
        b(currentLocaleBullet(localeToString(currentLocale))),
        b(supportedLocalesBullet(supportedLocales.collect { localeToString(it) })),
        b(effectiveLocaleBullet(localeToString(getEffectiveLocale()))))
  }


  private void validateMavenSettings() {
    String magnetMavenSettings = System.getenv(CoreConstants.MAGNET_MAVEN_SETTINGS)
    File magnetMavenSettingsFile
    addBullet(sbInfo, 2, b(CoreConstants.MAGNET_MAVEN_SETTINGS + ": " + (magnetMavenSettings ?: "NOT SET")))
    if (magnetMavenSettings) {
      magnetMavenSettingsFile = new File(magnetMavenSettings)
      if (!magnetMavenSettingsFile.exists()) {
        sbWarning.append(
            getMessage(
                CANNOT_FIND_MAGNET_MAVEN_SETTING,
                magnetMavenSettings,
                CoreConstants.MAGNET_MAVEN_SETTINGS,
                CoreConstants.LOGIN_COMMAND)).append("\n")
      }
    } else {
      magnetMavenSettingsFile = new File(getDefaultMavenSettings())
    }

    if (magnetMavenSettingsFile.exists()) {
      addBullet(sbInfo, 2, b("settings.xml: " + magnetMavenSettingsFile.toString()))
    } else {
      sbWarning.append(
          getMessage(
              CANNOT_FIND_DEFAULT_MAVEN_SETTING,
              Constants.MAGNET_MAVEN_SETTINGS ,
              defaultMavenSettings,
              Constants.LOGIN_COMMAND)).append("\n")
    }
  }

  static String getDefaultMavenSettings() {
    //TODO: this is not right, will not work e.g. on windows
    return System.getProperty("user.home") + File.separator + ".m2" + File.separator + "settings.xml"
  }

  private void validateMabVersion() {
    addBullet(sbInfo, 1, getMessage(MAB_VERSION_BULLET) + " " + b(ConfigLexicon.MAGNET_TOOLS_VERSION + " (" + ConfigLexicon.TOOLS_SCM_REVISION + "; " + ConfigLexicon.TOOLS_BUILD_TIME + ")"))
  }

  private void validateMabEnv() {
    String magnetToolHome = System.getenv(CoreConstants.MAGNET_TOOL_HOME)
    String mabHome = System.getenv(CoreConstants.MAB_HOME)
    addBullet(sbInfo, 1, CoreConstants.MAGNET_TOOL_HOME + ": " + b(magnetToolHome))
    addBullet(sbInfo, 1, CoreConstants.MAB_HOME + ": " + b(mabHome ?: notSet()))
  }

  private void validateJava() {
    //-------------------Java-------------------
    //Check JAVA_HOME
    String javaHome = System.getenv(CoreConstants.JAVA_HOME)
    if (!javaHome) {
      sbWarning.append(getMessage(NO_JAVA_HOME_VALIDATION_WARNING, CoreConstants.JAVA_HOME)).append("\n")
    }
    //Check Java version
    String javaVersion = System.getProperty('java.version')
    if (javaVersion) {
      double minJavaVersion = shell.configuration.javaVersion.major
      double currentVersion = Double.parseDouble(javaVersion.substring(0, 3))
      if (javaVersion && currentVersion < minJavaVersion ) {
        sbWarning.append(getMessage(JAVA_VERSION_VALIDATION_WARNING, "${minJavaVersion}+", "${currentVersion}")).append("\n")
      }
    }
    addBullet(sbInfo, 1, "Java:")
    addBullet(sbInfo, 2, b(CoreConstants.CMD_JAVA + ": " + EnvironmentHelper.getPath(CoreConstants.CMD_JAVA)))
    addBullet(sbInfo, 2, b(CoreConstants.JAVA_HOME + ": " + (javaHome ?: notSet())))
    //Run 'java -version'
    ProcessOutput javaCr = ProcessHelper.runReturnOutput(getShell(), CoreConstants.CMD_JAVA, ['-version'], null, null, false)
    if (!javaCr.getExitCode()) {
      addBullet(sbInfo, 2, javaCr.getOutput().split(LINE_SEP)?.collect {b(it?.trim())} as String[])
    } else {
      sbError.append(getMessage(BIN_ENVIRONMENT_VALIDATION_WARNING, "Java", CoreConstants.CMD_JAVA)).append("\n")
    }

  }

  private void validateMaven() {
    //Run 'mvn -version'
    addBullet(sbInfo, 1, "Maven:")
    String mavenHome = System.getenv(CoreConstants.M2_HOME)
    addBullet(sbInfo, 2, b(CoreConstants.CMD_MVN + ": " + EnvironmentHelper.getPath(CoreConstants.CMD_MVN)))
    addBullet(sbInfo, 2, b(CoreConstants.M2_HOME + ": " + (mavenHome ?: notSet())))
    validateMavenSettings()
    ProcessOutput mavenCr = ProcessHelper.runReturnOutput(
        getShell(), CoreConstants.CMD_MVN, ['-version'], null, null, false)
    if (!mavenCr.getExitCode()) {
      addBullet(sbInfo, 2,
          mavenCr.getOutput().split(LINE_SEP).collect { b(it)} as String[] )
    } else {
      sbError.append(getMessage(BIN_ENVIRONMENT_VALIDATION_WARNING, "Maven", CoreConstants.CMD_MVN)).append("\n")
    }
  }

  private void validatePowerShell() {
    // Powershell
    if (EnvironmentHelper.isWindowsOS()) {
      ProcessOutput psCr = ProcessHelper.runReturnOutput(
          getShell(), CoreConstants.CMD_POWERSHELL, ['-Command', '$host.version.toString()'], null, null, false);
      if (!psCr.getExitCode()) {
        addBullet(sbInfo, 1, ([getMessage(POWERSHELL_BULLET) + " "] as String[]) + psCr.getOutput().split(LINE_SEP).collect {
          b(it)
        } as String[])
      } else {
        sbError.append(getMessage(BIN_ENVIRONMENT_VALIDATION_WARNING, "Powershell", CoreConstants.CMD_POWERSHELL)).append("\n")
      }
    }
  }

  private void validateMySql(StringBuilder sbWarning, StringBuilder sbInfo) {

    addBullet(sbInfo, 1, "MySql:")
    //Run 'mysql --version'
    try {
      String mysqlHome = System.getenv(CoreConstants.MYSQL_HOME)
      addBullet(sbInfo, 2, b(CoreConstants.CMD_MYSQL + ": " + EnvironmentHelper.getPath(CoreConstants.CMD_MYSQL)))
      addBullet(sbInfo, 2, b(CoreConstants.MYSQL_HOME + ": " + (mysqlHome ?: notSet())))

      ProcessOutput mysqlCr = ProcessHelper.runReturnOutput(getShell(), CoreConstants.CMD_MYSQL, ['--version'], null, null, false)
      if (!mysqlCr.getExitCode()) {
        String output = mysqlCr.getOutput()
        String mysqlVersion = output.substring(output.indexOf(' ')).trim()
        addBullet(sbInfo, 2, b(getMessage(MYSQL_VERSION_BULLET) + " " + mysqlVersion))
      } else {
        addBullet(sbInfo, 2, b(getMessage(BIN_ENVIRONMENT_VALIDATION_WARNING, "MySQL", CoreConstants.CMD_MYSQL + ", mysqladmin")))
      }
    } catch (Exception e) {
      // MySql is optional, eat the exception
      addBullet(sbInfo, 2, b(getMessage(BIN_ENVIRONMENT_VALIDATION_WARNING, "MySQL", CoreConstants.CMD_MYSQL + ", mysqladmin")))
    }
  }

}
