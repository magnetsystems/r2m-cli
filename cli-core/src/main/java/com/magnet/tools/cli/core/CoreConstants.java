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
package com.magnet.tools.cli.core;

/**
 * Constants for core shell
 */
public interface CoreConstants {
  String MAGNET_SERVER_MAIN_CLASS = "com.magnet.Main";
  String TOOL_NAME = "mab";
  String PROMPT = "mab> ";
  String MAGNET_TOOL_HOME = "MAGNET_TOOL_HOME";
  String UNKOWN_TIME = "unknown";
  String MAB_HOME = "MAB_HOME";
  String QUIT_COMMAND = "quit";
  String EXIT_COMMAND = "exit";
  String CLEAR_COMMAND = "clear";
  String EXIT_COMMAND_ALIAS = "q";
  String HELP_COMMAND = "help";
  String HELP_COMMAND_ALIAS = "?";
  String TOPIC_COMMAND = "topic";
  String TOPIC_COMMAND_ALIAS = "howto";
  String SET_COMMAND = "set";
  String UNALIAS_COMMAND = "unalias";
  String EXEC_COMMAND = "exec";
  String RUN_COMMAND = "run";
  String RUN_COMMAND_ALIAS = ".";
  String REGISTER_COMMAND = "register";
  String OPTION_FORCE = "force";
  String MAGNET_CONFIGURATION_FILE = "magnet_configuration.groovy";
  String MAGNET_CONFIGURATION_OVERRIDE_FILE = "magnet_configuration_override.groovy";
  int COMMAND_UNKNOWN_ERROR_CODE = -1;
  int COMMAND_PARSING_ERROR_CODE = -2;
  int COMMAND_ABORT_CODE = -3;
  int COMMAND_INVALID_PATH_CODE = -11;
  int COMMAND_INVALID_OPTION_VALUE = -12;
  int COMMAND_UNSUPPORTED = -14;
  int COMMAND_UNKNOWN_COMMAND = -15;
  int COMMAND_MISSING_OPTION_VALUE = -18;
  int COMMAND_MISSING_EXTENSION = -19;
  /**
   * return code when an executable from cannot be found.
   */
  int COMMAND_UNKNOWN_EXECUTABLE_CODE = -20;
  int COMMAND_OK_CODE = 0;
  String MAB_LOCK_FILE = "mab.lock";
  String CMD_MVN = "mvn";
  String CMD_JAVA = "java";
  String CMD_JPS = "jps";
  String CMD_MYSQL = "mysql";
  String CMD_POWERSHELL = "powershell";
  String HEADLESS_JAVA = "-Djava.awt.headless=true";
  String M2_HOME = "M2_HOME";
  String JAVA_HOME = "JAVA_HOME";
  String MYSQL_HOME = "MYSQL_HOME";
  String CURRENT_LOG_FILE_NAME = "magnet.log.0";
  String LOG_FILE_NAME_PATTERN = "magnet.log";
  String HISTORY_COMMAND = "history";
  String HISTORY_COMMAND_ALIAS = "h";
  String ALIAS_COMMAND = "alias";
  String EXEC_COMMAND_ALIAS = "x";
  String DIAGNOSTICS_COMMAND = "diagnostics";
  int COMMAND_PROCESS_ERROR_CODE = -5;
  String ABORT_ON_VALIDATION_FAILURE_FLAG = "abortIfValidationFail";
  String SKIP_VALIDATION = "skipValidation";
  String VALIDATE_COMMAND = "validate";
  String MAGNET_MAVEN_SETTINGS = "MAGNET_MAVEN_SETTINGS";
  String MAGNET_MAVEN_SETTINGS_XML = "magnet-maven-settings.xml";
  String OPEN_COMMAND = "open";
  String PROJECT_MANAGER_EXTENSION = "projectManager";
  String PROMPT_EXTENSION = "promptClosure";
  int COMMAND_INVALID_PROJECT_CODE = -7;
  String LOGIN_COMMAND = "login";
}
