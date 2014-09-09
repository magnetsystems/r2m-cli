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
import com.magnet.tools.utils.ClientProxyConfiguration
import com.magnet.tools.cli.helper.CommandLineHelper
import com.magnet.tools.cli.messages.CommonMessages
import com.magnet.tools.utils.ClientProxy
import com.magnet.tools.utils.MessagesSupport

import static com.magnet.tools.utils.StringHelper.b
import static com.magnet.tools.cli.messages.BaseMessages.*

/**
 * Set command
 */
class SetCommand extends AbstractCommand {
  public static final String TRUE = Boolean.TRUE.toString()
  public static final String FALSE = Boolean.FALSE.toString()

  public static final String LOCALE = ShellSettings.KEY_LOCALE
  public static final String VERBOSE_OPTION = ShellSettings.KEY_VERBOSE
  public static final String EDITOR_OPTION = ShellSettings.KEY_EDITOR
  public static final String DEBUG_OPTION = ShellSettings.KEY_DEBUG
  public static final String TRACING_OPTION = ShellSettings.KEY_TRACING
  public static final String MAVEN_CMD_OPTION = ShellSettings.KEY_MVN_CMD
  public static final String MAVEN_EXTRA_ARGS_OPTION = ShellSettings.KEY_MVN_EXTRA_ARGS
  public static final String WORKSPACE = ShellSettings.KEY_WORKSPACE
  public static final String PROJECT_EDITOR_OPTION = ShellSettings.KEY_PROJECT_EDITOR
  public static final String HTTP_PROXY_OPTION = ShellSettings.KEY_HTTP_PROXY
  public static final String HTTPS_PROXY_OPTION = ShellSettings.KEY_HTTPS_PROXY
  public static final String SSH_PROXY_OPTION = ShellSettings.KEY_SSH_PROXY
  public static final String NO_SET_VALUE = ShellSettings.NO_SET_VALUE
  public static final String FACTORY_URL_OPTION = ShellSettings.KEY_FACTORY_URL
  public static final String MAVEN_REPOSITORY_OPTION = ShellSettings.KEY_MAVEN_REPOSITORY


  /**
   * A helper class for capturing setting specification
   */
  private static class Setting {
    String name // mandatory
    Class type // mandatory
    def initialValue // mandatory, the initial value associated with key if not set yet in user session
    List values  // optional
    def defaultValue // optional: the default value when no value for set key is specified
    Closure callback  // optional , closure called with the value after setting
  }

  /**
   * Available settings: should be re-evaluated everytime
   */
  public static final List<Setting> getSettings() {
    [
        // verbose
        new Setting(
            name: VERBOSE_OPTION,
            values: [TRUE, FALSE],
            defaultValue: TRUE,
            type: Boolean,
            initialValue: FALSE),
        new Setting(
            name: PROJECT_EDITOR_OPTION,
            type: String,
            defaultValue: OpenCommand.getDefaultProjectEditor(),
            initialValue: OpenCommand.getDefaultProjectEditor(),
        ),
        // debug
        new Setting(
            name: EDITOR_OPTION,
            defaultValue: OpenCommand.getDefaultEditor(),
            type: String,
            initialValue: OpenCommand.getDefaultEditor()),
        // tracing
        new Setting(
            name: TRACING_OPTION,
            values: [TRUE, FALSE],
            defaultValue: TRUE,
            type: Boolean,
            initialValue: FALSE),
        // debug
        new Setting(
            name: DEBUG_OPTION,
            values: [TRUE, FALSE],
            defaultValue: TRUE,
            type: Boolean,
            initialValue: FALSE),
        // mvn
        new Setting(
            name: MAVEN_CMD_OPTION,
            type: String,
            defaultValue: '',
            initialValue: ''),
        // mvnArgs
        new Setting(
            name: MAVEN_EXTRA_ARGS_OPTION,
            defaultValue: "",
            type: String,
            initialValue: ""),
        // workspace
        new Setting(
            name: WORKSPACE,
            defaultValue: ShellSettings.DEFAULT_WORKSPACE_PATH,
            type: File,
            initialValue: ShellSettings.DEFAULT_WORKSPACE_PATH),
        // locale
        new Setting(
            name: LOCALE,
            defaultValue: localeToString(Locale.getDefault()),
            values: ((supportedLocales.collect {localeToString(it)} + localeToString(Locale.getDefault())) as Set).toList() ,
            type: String,
            initialValue: localeToString(currentLocale),
            callback: {
              Set validLocaleNames = (supportedLocales.collect {localeToString(it)} + localeToString(Locale.getDefault())) as Set

              if (!(it in validLocaleNames)) {
                throw new CommandException(
                    CoreConstants.COMMAND_INVALID_OPTION_VALUE,
                    CommonMessages.getMessage(CommonMessages.INVALID_VALUE_WITH_AVAILABLE_VALUES,
                        it,
                        validLocaleNames.join(', ')))
              }
              currentLocale = stringToLocale(it)
            }),
        // HTTP Proxy option
        new Setting(
            name: HTTP_PROXY_OPTION,
            defaultValue: NO_SET_VALUE,
            type: URL,
            initialValue: NO_SET_VALUE),
        // HTTPS Proxy option
        new Setting(
            name: HTTPS_PROXY_OPTION,
            defaultValue: NO_SET_VALUE,
            type: URL,
            initialValue: NO_SET_VALUE),
        // Proxy for SSH connection
        new Setting(
            name: SSH_PROXY_OPTION,
            defaultValue: NO_SET_VALUE,
            type: ClientProxy,
            initialValue: NO_SET_VALUE),
        // factory url
        new Setting(
            name: FACTORY_URL_OPTION,
            defaultValue: NO_SET_VALUE,
            type: URL,
            initialValue: NO_SET_VALUE),
        // maven repository url
        new Setting(
            name: MAVEN_REPOSITORY_OPTION,
            defaultValue: NO_SET_VALUE,
            type: URL,
            initialValue: NO_SET_VALUE)


    ]
  }

  /**
   * Constructor: called dynamically by the Shell and injected with name, aliases, and hidden flag defined in
   * magnet_configuration.groovy definition file
   * @param name name of the command
   * @param aliases optional list of aliases, can be null
   * @param hidden whether it is a hidden command
   */
  SetCommand(String name, List<String> aliases, boolean hidden) {
    super(name, aliases, hidden)
  }

  @Override
  def execute(List<String> arguments) {

    if (!arguments) {
      // print list of options
      boldInfo(getMessage(CURRENT_SETTINGS))
      for (option in getSettings()) {
        def value = shell.getSettings()?.get(option.name)
        if (value == null) {
          value = option.initialValue
        }
        value = value?.toString()
        if (value) {
          writeBullet(1, "${option.name}: ${b(value)}")
        }
      }
      return CoreConstants.COMMAND_OK_CODE
    }

    String key = arguments[0].trim()

    Setting setting = getSettings().find { it.name == key }
    if (!setting) {
      throw new CommandException(
          CoreConstants.COMMAND_PARSING_ERROR_CODE,
          getMessage(UNKNOWN_SETTING_SHOW_AVAILABLE_SETTINGS, key, getSettings()*.name.join(', ')))
    }
    String value
    if (arguments.size() == 1) {
      if (setting.defaultValue != null) {
        value = setting.defaultValue
      } else {
        throw new CommandException(CoreConstants.COMMAND_PARSING_ERROR_CODE, getMessage(MISSING_VALUE_FOR_KEY, key))
      }
    } else {
      value = arguments.drop(1).join(' ')
    }

    shell.getSettings().set(key, convertValue(setting, value))
    setting.callback?.call(value)

    info(getMessage(SETTING_FOR_KEY, key, value))
    return CoreConstants.COMMAND_OK_CODE

  }

  private static def convertValue(Setting setting, String value) {

    List<String> validValues = setting.values
    if (validValues && !validValues.contains(value)) {
      throw new CommandException(
          CoreConstants.COMMAND_PARSING_ERROR_CODE,
          CommonMessages.invalidValue(value, validValues.join(', ')))
    }

    def type = setting.type
    switch (type) {
      case Boolean:
        return value == TRUE
      case ClientProxy:
        if (value == NO_SET_VALUE) {
          return value
        }
        try {
          new ClientProxyConfiguration(value)
        } catch (Exception e) {
          throw new CommandException(CoreConstants.COMMAND_PARSING_ERROR_CODE, CommonMessages.invalidSshProxy(value))
        }
        return value.trim()
      case URL:
        if (value == NO_SET_VALUE) {
          return value
        }
        try {
          new URL(value)
        } catch (Exception e) {
          throw new CommandException(CoreConstants.COMMAND_PARSING_ERROR_CODE, CommonMessages.invalidUrl(value))
        }
        return value.trim()
      case String:
        return value.trim()
      case File:
        //Check if it's a valid path (directory)
        try {
          File f = new File(CommandLineHelper.expandUserHome(value))
          if (!f.exists()) {
            f.mkdirs()
          } else {
            if (!f.isDirectory()) {
              throw new CommandException(CoreConstants.COMMAND_INVALID_PATH_CODE, CommonMessages.invalidValue(value))
            }
          }
          return f.getCanonicalPath()
        } catch (IOException e) {
          throw new CommandException(
              CoreConstants.COMMAND_INVALID_PATH_CODE,
              CommonMessages.invalidValue(value))
        } catch (CommandException e) {
          throw e
        }
      default:
        throw new UnsupportedOperationException(CommonMessages.unsupportedType(type))
    }
  }


}
