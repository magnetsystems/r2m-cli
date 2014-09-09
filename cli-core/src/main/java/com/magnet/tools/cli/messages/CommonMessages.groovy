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
 * Support messages for server commands
 */
@Slf4j
class CommonMessages extends MessagesSupport {
  //
  // Resource bundle keys
  //
  public static final String USAGE = "USAGE"
  public static final String CONTINUE_QUESTION = "CONTINUE_QUESTION"
  public static final String COMMAND_ABORTED = "COMMAND_ABORTED"
  public static final String VALIDATION_FAILED = "VALIDATION_FAILED"
  public static final String CANNOT_FIND_RESOURCE = "CANNOT_FIND_RESOURCE"
  public static final String FILTERING_BY_PATTERN = "FILTERING_BY_PATTERN"
  public static final String SKIPPING_CONTENT = "SKIPPING_CONTENT"
  public static final String INVALID_FILE = "INVALID_FILE"
  public static final String FILE_IS_DIRECTORY = "FILE_IS_DIRECTORY"
  public static final String FILE_DOES_NOT_EXIST_SHOULD_CREATE = "FILE_DOES_NOT_EXIST_SHOULD_CREATE"
  public static final String CONFIRM_DELETE = "CONFIRM_DELETE"
  public static final String NOT_A_DIRECTORY = "NOT_A_DIRECTORY"
  public static final String ONLY_ONE_FILTER_ARGUMENT = "ONLY_ONE_FILTER_ARGUMENT"
  public static final String INCORRECT_ARGUMENTS = "INCORRECT_ARGUMENTS"
  public static final String INCORRECT_ARGUMENTS_FOR_COMMAND = "INCORRECT_ARGUMENTS_FOR_COMMAND"
  public static final String TOO_MANY_ARGUMENTS_TRY_HELP = "TOO_MANY_ARGUMENTS_TRY_HELP"
  public static final String ARGUMENTS_NOT_SUPPORTED_AS_PROPERTIES = "ARGUMENTS_NOT_SUPPORTED_AS_PROPERTIES"
  public static final String COPYING_FILE = "COPYING_FILE"
  public static final String CANNOT_CREATE = "CANNOT_CREATE"
  public static final String SUMMARY = "SUMMARY"
  public static final String IS_IT_CORRECT = "IS_IT_CORRECT"
  public static final String NEEDS_WIZARD = "NEEDS_WIZARD"
  public static final String USER_DEFINED_ALIAS_TO = "USER_DEFINED_ALIAS_TO"
  public static final String RUN_COMMAND_FOR_ALIASES = "RUN_COMMAND_FOR_ALIASES"
  public static final String NOT_ENOUGH_ARGUMENTS = "NOT_ENOUGH_ARGUMENTS"
  public static final String BYE = "BYE"
  public static final String UNEXPECTED_EXCEPTION = "UNEXPECTED_EXCEPTION"
  public static final String COMMAND_ALREADY_REGISTERED = "COMMAND_ALREADY_REGISTERED"
  public static final String INVALID_COMMAND_URL = "INVALID_COMMAND_URL"
  public static final String MISSING_ARGUMENT_FOR_REGISTERING_COMMAND = "MISSING_ARGUMENT_FOR_REGISTERING_COMMAND"
  public static final String INVALID_COMMAND_NAME = "INVALID_COMMAND_NAME"
  public static final String NO_REGISTERED_COMMAND_FOR_NAME = "NO_REGISTERED_COMMAND_FOR_NAME"
  public static final String COMMAND_UNREGISTED = "COMMAND_UNREGISTED"
  public static final String ALIAS_UNREGISTERED = "ALIAS_UNREGISTERED"
  public static final String CANNOT_VALIDATE_NULL_OBJECT = "CANNOT_VALIDATE_NULL_OBJECT"
  public static final String CANNOT_INSTANTIATE_COMMAND = "CANNOT_INSTANTIATE_COMMAND"
  public static final String INVALID_COMMAND_LINE = "INVALID_COMMAND_LINE"
  public static final String ERROR = "ERROR"
  public static final String WARNING = "WARNING"
  public static final String CANNOT_FIND_MAIN_FOR_ALIAS = "CANNOT_FIND_MAIN_FOR_ALIAS"
  public static final String EVENT_NOT_FOUND = "EVENT_NOT_FOUND"
  public static final String ALIAS_PARAMETER_MUST_BE_NUMBER = "ALIAS_PARAMETER_MUST_BE_NUMBER"
  public static final String ALIAS_PARAMETER_CANNOT_BE_NEGATIVE = "ALIAS_PARAMETER_CANNOT_BE_NEGATIVE"
  public static final String MISSING_INDEX_IN_LIST = "MISSING_INDEX_IN_LIST"
  static final String INVALID_RESOURCE = "INVALID_RESOURCE"
  static final String RESOURCE_IS_EMPTY = "RESOURCE_IS_EMPTY"
  public static final String EXECUTING = "EXECUTING"
  public static final String INVALID_VALUE = "INVALID_VALUE"
  public static final String INVALID_VALUE_WITH_AVAILABLE_VALUES = "INVALID_VALUE_WITH_AVAILABLE_VALUES"
  public static final String UNSUPPORTED_TYPE = "UNSUPPORTED_TYPE"
  static final String MISSING_ARGUMENT = "MISSING_ARGUMENT"
  static final String MISSING_OPTIONS = "MISSING_OPTIONS"
  static final String MISSING_OPTIONS_WITH = "MISSING_OPTIONS_WITH"
  static final String MISSING_OPTIONS_VALUE = "MISSING_OPTIONS_VALUE"
  static final String MISSING_PROPERTY = "MISSING_PROPERTY"
  static final String RESOURCE_NOT_FOUND = "RESOURCE_NOT_FOUND"
  static final String INVALID_PROJECT = "INVALID_PROJECT"
  public static final String VALIDATING = "VALIDATING"
  public static final String PROJECT_NOT_FOUND = "PROJECT_NOT_FOUND"
  public static final String NO_CURRENT_PROJECT = "NO_CURRENT_PROJECT"
  public static final String DIRECTORY_CREATED = "DIRECTORY_CREATED"
  public static final String UNABLE_TO_CREATE = "UNABLE_TO_CREATE"
  public static final String UNABLE_TO_DELETE = "UNABLE_TO_DELETE"
  public static final String LISTING_ALL_ARTIFACTS_FOR_TYPE = "LISTING_ALL_ARTIFACTS_FOR_TYPE"
  public static final String MISSING_WIZARD_FOR_TYPE_SEE_AVAILABLE = "MISSING_WIZARD_FOR_TYPE_SEE_AVAILABLE"
  public static final String UNKNOWN_WIZARD_TYPE_SEE_AVAILABLE = "UNKNOWN_WIZARD_TYPE_SEE_AVAILABLE"
  public static final String TYPE_GENERATION_ERROR = "TYPE_GENERATION_ERROR"
  public static final String MISSING_PROPERTY_FOR_TYPE = "MISSING_PROPERTY_FOR_TYPE"
  static final String MISSING_CONFIGURATION = "MISSING_CONFIGURATION"
  public static final String TYPE_ADDED = "TYPE_ADDED"
  public static final String ARTIFACT_ID_CONFLICT = "ARTIFACT_ID_CONFLICT"
  static final String CANNOT_GENERATE_COMPONENT_DUE_TO_GAV_MISMATCH = "CANNOT_GENERATE_COMPONENT_DUE_TO_GAV_MISMATCH"
  static final String COMPONENT_ALREADY_EXISTS = "COMPONENT_ALREADY_EXISTS"
  static final String CANNOT_GENERATE_IN_EXISTING_COMPONENT = "CANNOT_GENERATE_IN_EXISTING_COMPONENT"
  static final String COMPONENT_CONFLICT_WITH_PREBUILT = "COMPONENT_CONFLICT_WITH_PREBUILT"
  public static final String CANNOT_FIND_TYPE_ARTIFACT = "CANNOT_FIND_TYPE_ARTIFACT"
  public static final String TYPE_IS_REQUIRED = "TYPE_IS_REQUIRED"
  public static final String CHOOSE_TYPE = "CHOOSE_TYPE"
  public static final String LOOKING_FOR_MATCHING_TYPES = "LOOKING_FOR_MATCHING_TYPES"
  public static final String NO_MATCHING_TYPE_SHOW_AVAILABLE = "NO_MATCHING_TYPE_SHOW_AVAILABLE"
  public static final String MULTIPLE_MATCHING_TYPE = "MULTIPLE_MATCHING_TYPE"
  public static final String NO_TYPE_IN_PROJECT = "NO_TYPE_IN_PROJECT"
  public static final String CONFIRM_TYPE_DELETION = "CONFIRM_TYPE_DELETION"
  public static final String REMOVING_TYPE = "REMOVING_TYPE"
  public static final String TYPE_REMOVED = "TYPE_REMOVED"
  public static final String INCORRECT_ENTRY = "INCORRECT_ENTRY"
  public static final String ANOTHER_MAB_IS_RUNNING = "ANOTHER_MAB_IS_RUNNING"
  public static final String INTRO_MESSAGE = "INTRO_MESSAGE"
  public static final String LOADING_CONFIGURATION = "LOADING_CONFIGURATION"
  public static final String NO_BACKEND_PROMPT = "NO_BACKEND_PROMPT"
  public static final String NO_PROJECT_PROMPT = "NO_PROJECT_PROMPT"
  public static final String USE_VALUE_QUESTION = "USE_VALUE_QUESTION"
  static final String DIRECTORY_DOES_NOT_EXIST = "DIRECTORY_DOES_NOT_EXIST"
  static final String FILE_DOES_NOT_EXIST = "FILE_DOES_NOT_EXIST"
  static final String SERVER_JVM_MEMORY_ARGUMENTS_QUESTION = "SERVER_JVM_MEMORY_ARGUMENTS_QUESTION"
  static final String SERVER_JVM_REMOTE_DEBUG_PORT_QUESTION = "SERVER_JVM_REMOTE_DEBUG_PORT_QUESTION"
  static final String SERVER_JMX_PROPERTIES_QUESTION = "SERVER_JMX_PROPERTIES_QUESTION"
  static final String SERVER_JMX_ENABLED_QUESTION = "SERVER_JMX_ENABLED_QUESTION"
  static final String CANNOT_FIND_DEPENDENCY = "CANNOT_FIND_DEPENDENCY"
  static final String GLOBAL_LOGGING_LEVEL_QUESTION = "GLOBAL_LOGGING_LEVEL_QUESTION"
  static final String MAGNET_LOGGING_LEVEL_QUESTION = "MAGNET_LOGGING_LEVEL_QUESTION"
  static final String ENABLE_REST_LOGGING_QUESTION = "ENABLE_REST_LOGGING_QUESTION"
  static final String KEYSTORE_PATH_QUESTION = "KEYSTORE_PATH_QUESTION"
  static final String KEYSTORE_PASSWORD_QUESTION = "KEYSTORE_PASSWORD_QUESTION"
  static final String KEY_ALIAS_QUESTION = "KEY_ALIAS_QUESTION"
  static final String KEY_PASSWORD_QUESTION = "KEY_PASSWORD_QUESTION"
  static final String TRUST_ALL_CERTS_APP_ADVICE = "TRUST_ALL_CERTS_APP_ADVICE"
  static final String ARTIFACT_IS_INVALID_OR_ALREADY_EXISTS = "ARTIFACT_IS_INVALID_OR_ALREADY_EXISTS"
  static final String PROJECT_PACKAGE_QUESTION = "PROJECT_PACKAGE_QUESTION"
  static final String PROJECT_PREFIX_QUESTION = "PROJECT_PREFIX_QUESTION"
  static final String PROJECT_VERSION_QUESTION = "PROJECT_VERSION_QUESTION"
  static final String PROJECT_ARTIFACT_ID_QUESTION = "PROJECT_ARTIFACT_ID_QUESTION"
  static final String PROJECT_GROUP_ID_QUESTION = "PROJECT_GROUP_ID_QUESTION"
  static final String LISTENING_PORT_QUESTION = "LISTENING_PORT_QUESTION"
  static final String TARGET_PLATFORM_VERSION_QUESTION = "TARGET_PLATFORM_VERSION_QUESTION"
  static final String APPLICATION_DB_NAME_QUESTION = "APPLICATION_DB_NAME_QUESTION"
  static final String SYSTEM_DB_PASSWORD_QUESTION = "SYSTEM_DB_PASSWORD_QUESTION"
  static final String SYSTEM_DB_USER_NAME_QUESTION = "SYSTEM_DB_USER_NAME_QUESTION"
  static final String SYSTEM_DB_NAME_QUESTION = "SYSTEM_DB_NAME_QUESTION"
  static final String APPLICATION_DB_USER_NAME_QUESTION = "APPLICATION_DB_USER_NAME_QUESTION"
  static final String APPLICATION_DB_PASSWORD_QUESTION = "APPLICATION_DB_PASSWORD_QUESTION"
  static final String CHOOSE_CONFIGURATION_TYPE = "CHOOSE_CONFIGURATION_TYPE"
  static final String PROTOCOL_SHOULD_BE = "PROTOCOL_SHOULD_BE"
  static final String DB_QUESTION = "DB_QUESTION"
  static final String H2_WEB_APP_QUESTION = "H2_WEB_APP_QUESTION"
  static final String H2_LOCATION_QUESTION = "H2_LOCATION_QUESTION"
  static final String CANNOT_DELETE_PRE_EXISTING_RESOURCE = "CANNOT_DELETE_PRE_EXISTING_RESOURCE"
  static final String CHOOSE_PACKAGE_NAME = "CHOOSE_PACKAGE_NAME"
  static final String CHOOSE_PREFIX = "CHOOSE_PREFIX"
  static final String SERVER_ARGUMENTS_QUESTION = "SERVER_ARGUMENTS_QUESTION"
  static final String SERVER_ARGUMENTS_ENABLED_QUESTION = "SERVER_ARGUMENTS_ENABLED_QUESTION"
  static final String CHOOSE_TYPE_TO_ADD = "CHOOSE_TYPE_TO_ADD"
  static final String CHOOSE_TYPE_TO_CONFIG = "CHOOSE_TYPE_TO_CONFIG"
  static final String CONFIGURING = "CONFIGURING"
  static final String DB_SCRIPTS_FAILED = "DB_SCRIPTS_FAILED"
  static final String ENABLE_SECURITY_QUESTION = "ENABLE_SECURITY_QUESTION"
  static final String DUPLICATED_ARTIFACT_ID = "DUPLICATED_ARTIFACT_ID"
  static final String BACKING_UP_RESOURCE_TO = "BACKING_UP_RESOURCE_TO"
  static final String INVALID_PROXY_URL = "INVALID_PROXY_URL"
  static final String INVALID_URL = "INVALID_URL"
  static final String UNKNOWN_POM_PROPERTY = "UNKNOWN_POM_PROPERTY"
  static final String DB_PORT_QUESTION = "DB_PORT_QUESTION"
  static final String DB_HOST_QUESTION = "DB_HOST_QUESTION"
  static final String CONSTANT_OR_UNUSED = "CONSTANT_OR_UNUSED"
  static final String DB_NOT_INITIALIZED = "DB_NOT_INITIALIZED"
  static final String INVALID_SSH_PROXY = "INVALID_SSH_PROXY"
  static final String MISSING_EXTENSION = "MISSING_EXTENSION"
  static final String CANNOT_FIND_EXECUTABLE_FOR_URL = "CANNOT_FIND_EXECUTABLE_FOR_URL"
  static final String CANNOT_FIND_EXECUTABLE = "CANNOT_FIND_EXECUTABLE"



  /**
   * Construct an internationalized and localized message
   * Expecting messages under messages/ in classpath
   * @param key key in resource bundle
   * @param args arguments for string
   * @return message
   */
  static String getMessage(String key, Object... args) {
    _getMessage(MESSAGES_PATH + CommonMessages.getSimpleName(), key, args)
  }


  static String directoryDoesNotExist(def dir) {
    getMessage(DIRECTORY_DOES_NOT_EXIST, dir)
  }

  static String fileDoesNotExist(def file) {
    getMessage(FILE_DOES_NOT_EXIST, file ?: "null");
  }

  //
  // Helper methods for well-known messages
  //
  static String incorrectArguments() {
    getMessage(INCORRECT_ARGUMENTS)
  }

  static String incorrectArguments(String commandName) {
    getMessage(INCORRECT_ARGUMENTS_FOR_COMMAND, commandName)
  }

  static String tooManyArguments(def command) {
    getMessage(TOO_MANY_ARGUMENTS_TRY_HELP, command, CoreConstants.HELP_COMMAND)
  }

  static String continueQuestion() {
    getMessage(CONTINUE_QUESTION)
  }

  static String commandAborted() {
    getMessage(COMMAND_ABORTED)
  }

  static def copyingFile(def from, def to) {
    getMessage(COPYING_FILE, from, to)

  }

  static String cannotCreate(def resource) {
    getMessage(CANNOT_CREATE, resource)
  }

  static String summary() {
    getMessage(SUMMARY)
  }

  static String isItCorrect() {
    getMessage(IS_IT_CORRECT)
  }

  static String needsWizard() {
    getMessage(NEEDS_WIZARD)
  }

  static String bye() {
    getMessage(BYE)
  }

  static String unexpectedException(def message, def logFile) {
    getMessage(UNEXPECTED_EXCEPTION, message, logFile, CoreConstants.OPEN_COMMAND, "--" + OpenCommand.MAB_LOG_OPTION)
  }

  static String unexpectedShellException(def shell, def message) {
    def logFile = new File(shell.getMagnetDirectory(), CoreConstants.CURRENT_LOG_FILE_NAME).getAbsolutePath()
    unexpectedException(message, logFile)

  }
  static String eventNotFound(def event) {
    getMessage(EVENT_NOT_FOUND, event)
  }

  static String invalidResource(def resource) {
    getMessage(INVALID_RESOURCE, resource)
  }

  static String resourceIsEmpty(def resource) {
    getMessage(RESOURCE_IS_EMPTY, resource)
  }

  static String executing(def script) {
    getMessage(EXECUTING, script)
  }

  static String invalidValue(def value, def allowedValues = null) {
    if (allowedValues) {
      getMessage(INVALID_VALUE_WITH_AVAILABLE_VALUES, value, allowedValues)
    } else {
      getMessage(INVALID_VALUE, value)
    }
  }

  static String unsupportedType(def type) {
    getMessage(UNSUPPORTED_TYPE, type)
  }

  static String missingArgument() {
    getMessage(MISSING_ARGUMENT)
  }

  static String missingProperty(def prop) {
    getMessage(MISSING_PROPERTY, prop)
  }

  static String validating(String s) {
    getMessage(VALIDATING, s)
  }

  static String errorMessage(String s) {
    getMessage(ERROR, s)
  }

  static String warningMessage(String s) {
    getMessage(WARNING, s)
  }


  static String projectNotFound(def name) {
    getMessage(PROJECT_NOT_FOUND, name)
  }

  static String noCurrentProject() {
    getMessage(NO_CURRENT_PROJECT)
  }

  static String directoryCreated(def dir) {
    getMessage(DIRECTORY_CREATED, dir)
  }

  static String unableToCreate(def file) {
    getMessage(UNABLE_TO_CREATE, file)
  }

  static String unableToDelete(def file) {
    getMessage(UNABLE_TO_DELETE, file)
  }

  static String validationFailed(def error) {
    getMessage(VALIDATION_FAILED, error)
  }

  static String artifactIdConflict(def id) {
    getMessage(ARTIFACT_ID_CONFLICT, id)
  }

  static String componentGavMismatch(def type, def dir, def originalGav, def generatedGav) {
    getMessage(CANNOT_GENERATE_COMPONENT_DUE_TO_GAV_MISMATCH, type, dir, originalGav, generatedGav)

  }

  static String componentAlreadyExists(def type, dir, def gav) {
    getMessage(COMPONENT_ALREADY_EXISTS, type, dir, gav)
  }

  static String introMessage() {
    getMessage(INTRO_MESSAGE, StringHelper.b(CoreConstants.HELP_COMMAND_ALIAS), "<${StringHelper.b('TAB')}>", "<${StringHelper.b('Ctrl-D')}>")
  }

  static String loadingConfiguration() {
    getMessage(LOADING_CONFIGURATION)
  }

  static String filteringByPattern(def pattern) {
    getMessage(FILTERING_BY_PATTERN, pattern)
  }

  static String useValueQuestion() {
    getMessage(USE_VALUE_QUESTION)
  }

  static String missingConfiguration(def config) {
    getMessage(MISSING_CONFIGURATION)
  }

  static String missingPropertyForType(def property, def type) {
    getMessage(MISSING_PROPERTY_FOR_TYPE, property, type)
  }

  static String serverJvmMemoryArguments() {
    getMessage(SERVER_JVM_MEMORY_ARGUMENTS_QUESTION)
  }

  static String serverJvmRemoteDebugPort() {
    getMessage(SERVER_JVM_REMOTE_DEBUG_PORT_QUESTION)
  }

  static String serverJmxEnabled() {
    getMessage(SERVER_JMX_ENABLED_QUESTION)
  }

  static String serverJmxProperties() {
    getMessage(SERVER_JMX_PROPERTIES_QUESTION)
  }

  static String cannotFindDependency(def dep) {
    getMessage(CANNOT_FIND_DEPENDENCY, dep)
  }

  static String globalLoggingLevel() {
    getMessage(GLOBAL_LOGGING_LEVEL_QUESTION)
  }

  static String magnetLoggingLevel() {
    getMessage(MAGNET_LOGGING_LEVEL_QUESTION)
  }

  static String enableRestLoggingQuestion() {
    getMessage(ENABLE_REST_LOGGING_QUESTION)
  }

  static String keyStorePathQuestion() {
    getMessage(KEYSTORE_PATH_QUESTION)
  }

  static String keystorePasswordQuestion() {
    getMessage(KEYSTORE_PASSWORD_QUESTION)
  }

  static String keyPasswordQuestion() {
    getMessage(KEY_PASSWORD_QUESTION)
  }

  static String keyAliasQuestion() {
    getMessage(KEY_ALIAS_QUESTION)
  }

  static String trustAllCertsAppAdvice() {
    getMessage(TRUST_ALL_CERTS_APP_ADVICE)
  }

  static String artifactIdInvalidOrAlreadyExists(def dir) {
    getMessage(ARTIFACT_IS_INVALID_OR_ALREADY_EXISTS, dir)
  }

  static String projectGroupIdQuestion() {
    getMessage(PROJECT_GROUP_ID_QUESTION)
  }

  static String projectArtifactIdQuestion() {
    getMessage(PROJECT_ARTIFACT_ID_QUESTION)
  }

  static String projectVersionQuestion() {
    getMessage(PROJECT_VERSION_QUESTION)
  }

  static String projectPackageQuestion() {
    getMessage(PROJECT_PACKAGE_QUESTION)
  }

  static String projectPrefixQuestion() {
    getMessage(PROJECT_PREFIX_QUESTION)
  }

  static String listeningPortQuestion() {
    getMessage(LISTENING_PORT_QUESTION)

  }

  static String targetPlatformVersionQuestion() {
    getMessage(TARGET_PLATFORM_VERSION_QUESTION)
  }

  static String systemDbNameQuestion() {
    getMessage(SYSTEM_DB_USER_NAME_QUESTION)
  }

  static String systemDbUserNameQuestion() {
    getMessage(SYSTEM_DB_NAME_QUESTION)
  }

  static String systemDbPasswordQuestion() {
    getMessage(SYSTEM_DB_PASSWORD_QUESTION)
  }

  static String applicationDbNameQuestion() {
    getMessage(APPLICATION_DB_NAME_QUESTION)
  }

  static String applicationDbPasswordQuestion() {
    getMessage(APPLICATION_DB_PASSWORD_QUESTION)
  }

  static String applicationDbUserNameQuestion() {
    getMessage(APPLICATION_DB_USER_NAME_QUESTION)
  }

  static String chooseConfigurationType() {
    getMessage(CHOOSE_CONFIGURATION_TYPE)
  }

  static String protocolShouldBe(def protocol) {
    getMessage(PROTOCOL_SHOULD_BE, protocol)
  }

  static String dbQuestion() {
    getMessage(DB_QUESTION)
  }

  static String h2WebAppQuestion() {
    getMessage(H2_WEB_APP_QUESTION)
  }

  static String h2LocationQuestion() {
    getMessage(H2_LOCATION_QUESTION)
  }
  static def cannotDeletePreExistingResource(File file) {
    getMessage(CANNOT_DELETE_PRE_EXISTING_RESOURCE, file)
  }

  static String choosePackageName() {
    getMessage(CHOOSE_PACKAGE_NAME)
  }

  static String serverArgumentsEnabledQuestion() {
    getMessage(SERVER_ARGUMENTS_ENABLED_QUESTION)
  }

  static String serverArgumentsQuestion() {
    getMessage(SERVER_ARGUMENTS_QUESTION)
  }

  static String chooseTypeToAdd(def type) {
    getMessage(CHOOSE_TYPE_TO_ADD, type)
  }

  static String chooseTypeToConfig(def type) {
    getMessage(CHOOSE_TYPE_TO_CONFIG, type)
  }

  static String configuring(def resource) {
    getMessage(CONFIGURING, resource)
  }

  static String cannotFindResource(def resource) {
    getMessage(CANNOT_FIND_RESOURCE, resource)
  }

  static String dbScriptsFailed(def script) {
    getMessage(DB_SCRIPTS_FAILED, script)
  }

  static String enableSecurityQuestion() {
    getMessage(ENABLE_SECURITY_QUESTION)
  }

  static String duplicatedArtifactId(def artifactId) {
    getMessage(DUPLICATED_ARTIFACT_ID, artifactId)
  }

  static def backingUpResourceTo(def resource, def destination) {
    getMessage(BACKING_UP_RESOURCE_TO,resource, destination)
  }

  static def invalidProxyUrl(def s) {
    getMessage(INVALID_PROXY_URL, s, "${CoreConstants.SET_COMMAND} $SetCommand.PROJECT_EDITOR_OPTION http(s)://username:password@host:port")

  }

  static def invalidUrl(def s) {
    getMessage(INVALID_URL, s)
  }

  static def unknownPomProperty(def var) {
    getMessage(UNKNOWN_POM_PROPERTY, var)
  }
  
  static def dbPortQuestion() {
    getMessage(DB_PORT_QUESTION)
  }

  static def dbHostQuestion() {
    getMessage(DB_HOST_QUESTION)
  }

  static def constantOrUnused() {
    getMessage(CONSTANT_OR_UNUSED)
  }

  static String missingOptions(def list) {
    getMessage(MISSING_OPTIONS, list)
  }

  static String missingOptionsWith(def withOptions, def missingOptions) {
    getMessage(MISSING_OPTIONS_WITH, withOptions, missingOptions)
  }

  static String resourceNotFound(def resource) {
    getMessage(RESOURCE_NOT_FOUND, resource)
  }

  static String projectIsInvalid(def name) {
    getMessage(INVALID_PROJECT, name)
  }

  static String dbNotInitialized() {
    getMessage(DB_NOT_INITIALIZED)
  }

  static String missingOptionValue(String optionName) {
    getMessage(MISSING_OPTIONS_VALUE, optionName)
  }


  static String cannotGenerateInExistingModule(def type, def dir, def originalGav, def generatedGav) {
    getMessage(CANNOT_GENERATE_IN_EXISTING_COMPONENT, type, dir, originalGav, generatedGav)
  }

  static String componentConflictWithPrebuilt(def type, def originalGav, def generatedGav) {
    getMessage(COMPONENT_CONFLICT_WITH_PREBUILT, type, "prebuilt", originalGav, generatedGav)
  }
  static String invalidSshProxy(String proxy) {
    getMessage(INVALID_SSH_PROXY, proxy)
  }

  static String missingExtension(def extension) {
    getMessage(MISSING_EXTENSION, extension)
  }

  static String cannotFindExecutableForUrl(def url) {
    getMessage(CANNOT_FIND_EXECUTABLE_FOR_URL, url, "${CoreConstants.SET_COMMAND} ${SetCommand.EDITOR_OPTION}")
  }

  static String cannotFindEditorExecutable(def exec) {
    getMessage(CANNOT_FIND_EXECUTABLE, exec)
  }


}
