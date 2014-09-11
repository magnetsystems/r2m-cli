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
package com.magnet.tools.cli.messages

import com.magnet.tools.cli.base.OpenCommand
import com.magnet.tools.cli.base.SetCommand
import com.magnet.tools.cli.core.CoreConstants
import com.magnet.tools.utils.MessagesSupport
import com.magnet.tools.utils.StringHelper
import groovy.util.logging.Slf4j


/**
 * Support for base commands
 */
@Slf4j
class BaseMessages extends MessagesSupport {
  //
  // AliasCommand
  //
  public static final String CONFLICTING_ALIAS = "CONFLICTING_ALIAS"
  public static final String NO_ALIAS_FOR = "NO_ALIAS_FOR"
  public static final String INVALID_ALIAS_ARGUMENTS = "INVALID_ALIAS_ARGUMENTS"
  public static final String ASSOCIATING_USER_ALIAS = "ASSOCIATING_USER_ALIAS"
  public static final String INVALID_ALIAS_NAME = "INVALID_ALIAS_NAME"
  //
  // DiagnosticsCommand
  //
  public static final String DIAGNOSTICS_SAVED_IN_FILE = "DIAGNOSTICS_SAVED_IN_FILE"
  public static final String MAVEN_SETTINGS_NOT_FOUND = "MAVEN_SETTINGS_NOT_FOUND"
  //
  // ExecCommand
  //
  public static final String NO_SHELL_COMMAND_TO_RUN = "NO_SHELL_COMMAND_TO_RUN"
  public static final String PROCESS_FAILED_WITH_MESSAGE = "PROCESS_FAILED_WITH_MESSAGE"

  //
  // HelpCommand
  //
  public static final String LIST_AVAILABLE_COMMANDS = "LIST_AVAILABLE_COMMANDS"
  public static final String UNKNOWN_COMMAND_TRY_HELP = "UNKNOWN_COMMAND_TRY_HELP"
  public static final String USE_COMMAND_HELP_FOR_MORE = "USE_COMMAND_HELP_FOR_MORE"

  //
  // LsCommand
  //
  public static final String LISTING_FILES_RELATIVE_TO_PROJECT = "LISTING_FILES_RELATIVE_TO_PROJECT"
  public static final String CANNOT_LIST_RELATIVE_PATH = "CANNOT_LIST_RELATIVE_PATH"
  public static final String RUNNING_BUILTIN_SCRIPT = "RUNNING_BUILTIN_SCRIPT"

  //
  // RegisterCommand
  //
  public static final String MISSING_REGISTER_COMMAND_OPTIONS = "MISSING_REGISTER_COMMAND_OPTIONS"

  //
  // RunCommand
  //
  public static final String EXECUTING_LINE = "EXECUTING_LINE"
  public static final String AVAILABLE_SCRIPTS = "AVAILABLE_SCRIPTS"
  public static final String PREVIOUSLY_INVOKED_SCRIPTS = "PREVIOUSLY_INVOKED_SCRIPTS"
  public static final String LAST_INVOKED_SCRIPT_AT = "LAST_INVOKED_SCRIPT_AT"
  //
  // SetCommand
  //

  public static final String CURRENT_SETTINGS = "CURRENT_SETTINGS"
  public static final String UNKNOWN_SETTING_SHOW_AVAILABLE_SETTINGS = "UNKNOWN_SETTING_SHOW_AVAILABLE_SETTINGS"
  public static final String MISSING_VALUE_FOR_KEY = "MISSING_VALUE_FOR_KEY"
  public static final String SETTING_FOR_KEY = "SETTING_FOR_KEY"

  //
  // Topic command
  //
  public static final String LIST_AVAILABLE_TOPICS = "LIST_AVAILABLE_TOPICS"
  public static final String UNKNOWN_TOPIC = "UNKNOWN_TOPIC"
  public static final String CANNOT_LOAD_TOPIC = "CANNOT_LOAD_TOPIC"
  //
  // UnaliasCommand
  //
  public static final String ALIAS_REMOVED = "ALIAS_REMOVED"
  public static final String ALIAS_NOT_FOUND = "ALIAS_NOT_FOUND"

  //
  // ValidateCommand
  //
  public static final String VERSION = "VERSION"
  public static final String PLATFORM_VERSION = "PLATFORM_VERSION"
  public static final String DEFAULT_PLATFORM_VERSION_BULLET = "DEFAULT_PLATFORM_VERSION_BULLET"
  public static final String MAGNET_FACTORY_URL_BULLET = "MAGNET_FACTORY_URL_BULLET"
  static final String CURRENT_LOCALE_BULLET = "CURRENT_LOCALE_BULLET"
  static final String SUPPORTED_LOCALES_BULLET = "SUPPORTED_LOCALES_BULLET"
  static final String EFFECTIVE_LOCALE_BULLET = "EFFECTIVE_LOCALE_BULLET"
  public static final String LANGUAGE_LOCALE = "LANGUAGE_LOCALE"
  public static final String PLUGINS = "PLUGINS"
  public static final String NO_PLUGINS = "NO_PLUGINS"
  public static final String REGISTERED_PLUGINS_BULLET = "REGISTERED_PLUGINS_BULLET"
  public static final String FACTORY_URL = "FACTORY_URL"
  public static final String MAVEN_REPOSITORY_URL = "MAVEN_REPOSITORY_URL"
  public static final String CANNOT_FIND_MAGNET_MAVEN_SETTING = "CANNOT_FIND_MAGNET_MAVEN_SETTING"
  public static final String CANNOT_FIND_DEFAULT_MAVEN_SETTING = "CANNOT_FIND_DEFAULT_MAVEN_SETTING"
  public static final String MAB_VERSION_BULLET = "MAB_VERSION_BULLET"
  public static final String NO_JAVA_HOME_VALIDATION_WARNING = "NO_JAVA_HOME_VALIDATION_WARNING"
  public static final String JAVA_VERSION_VALIDATION_WARNING = "JAVA_VERSION_VALIDATION_WARNING"
  static final String NOT_SET = "NOT_SET"
  public static final String POWERSHELL_BULLET = "POWERSHELL_BULLET"
  public static final String MYSQL_VERSION_BULLET = "MYSQL_VERSION_BULLET"
  public static final String BIN_ENVIRONMENT_VALIDATION_WARNING = "BIN_ENVIRONMENT_VALIDATION_WARNING"
  static final String VALIDATION_OK = "VALIDATION_OK"
  static final String CANNOT_OPEN_REMOTE_LOG = "CANNOT_OPEN_REMOTE_LOG"
  static final String NO_PROJECT_EDITOR_SET = "NO_PROJECT_EDITOR_SET"
  static final String USE_OPEN_LOG = "USE_OPEN_LOG"
  public static final String MAGNET_INSTALLATION_BULLET = "MAGNET_INSTALLATION_BULLET"
  public static final String MAGNET_DIRECTORY_BULLET = "MAGNET_DIRECTORY_BULLET"
  public static final String HELP_FOR_SPECIFIC_COMMAND = "HELP_FOR_SPECIFIC_COMMAND"

  static String getMessage(String key, Object... args) {
    _getMessage(MESSAGES_PATH + BaseMessages.getSimpleName(), key, args)
  }

  //
  // Convenience methods (shortcuts)
  //
  static String languageLocale() {
    getMessage(LANGUAGE_LOCALE)
  }
  static String currentLocaleBullet(def locale) {
    getMessage(CURRENT_LOCALE_BULLET, locale)
  }

  static String supportedLocalesBullet(def list) {
    getMessage(SUPPORTED_LOCALES_BULLET, list)
  }

  static String effectiveLocaleBullet(def locale) {
    getMessage(EFFECTIVE_LOCALE_BULLET, locale)
  }

  static String listingFilesRelativeToProject(def project, def path) {
    getMessage(LISTING_FILES_RELATIVE_TO_PROJECT, project, path)
  }

  static String validationOk() {
    getMessage(VALIDATION_OK, CoreConstants.VALIDATE_COMMAND)
  }

  static String cannotOpenRemoteLog() {
    getMessage(CANNOT_OPEN_REMOTE_LOG)
  }

  static String noProjectEditorSet(def defaultValue) {
    getMessage(NO_PROJECT_EDITOR_SET, CoreConstants.SET_COMMAND, SetCommand.PROJECT_EDITOR_OPTION, defaultValue)
  }

  static String useOpenMabLog() {
    getMessage(USE_OPEN_LOG, CoreConstants.OPEN_COMMAND, "--" + OpenCommand.MAB_LOG_OPTION)
  }

  static String notSet() {
    getMessage(NOT_SET)
  }

  static String availableScripts() {
    getMessage(AVAILABLE_SCRIPTS, StringHelper.b(CoreConstants.RUN_COMMAND))
  }

}
