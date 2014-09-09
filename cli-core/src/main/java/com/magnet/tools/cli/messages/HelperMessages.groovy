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

import com.magnet.tools.utils.MessagesSupport
import groovy.util.logging.Slf4j

/**
 * Messages support for helper classes
 */
@Slf4j
class HelperMessages extends MessagesSupport {
  public static final String COMMAND_NOT_SUPPORTED = "COMMAND_NOT_SUPPORTED"
  public static final String COMMAND_NOT_FOUND = "COMMAND_NOT_FOUND"
  public static final String COMMAND_NO_LONGER_EXISTS = "COMMAND_NO_LONGER_EXISTS"
  public static final String UNABLE_TO_RETRIEVE_USER_DATA_FROM_URL= "UNABLE_TO_RETRIEVE_USER_DATA_FROM_URL"
  public static final String COMMAND_SUCCEEDED = "COMMAND_SUCCEEDED"
  public static final String COMMAND_FAILED = "COMMAND_FAILED"
  public static final String INVALID_ENVIRONMENT_FORMAT = "INVALID_ENVIRONMENT_FORMAT"
  public static final String EXECUTING_COMMAND = "EXECUTING_COMMAND"
  public static final String COMMAND_OUTPUT_AT = "COMMAND_OUTPUT_AT"
  public static final String MISSING_REQUIRED_PROPERTIES = "MISSING_REQUIRED_PROPERTIES"
  public static final String INVALID_ENTRY_FOR_KEY = "INVALID_ENTRY_FOR_KEY"
  public static final String EXITING_QUESTIONNAIRE_DO_RETRY = "EXITING_QUESTIONNAIRE_DO_RETRY"
  public static final String MISMATCH_ANSWER_DO_RETRY = "MISMATCH_ANSWER_DO_RETRY"
  public static final String UNKNOWN_TYPE_FOR_QUESTION = "UNKNOWN_TYPE_FOR_QUESTION"
  public static final String CONSTANT_TYPE_REQUIRES_DEFAULT = "CONSTANT_TYPE_REQUIRES_DEFAULT"
  public static final String PLEASE_CONFIRM_PASSWORD = "PLEASE_CONFIRM_PASSWORD"
  public static final String INVALID_ENTRY_DO_RETRY = "INVALID_ENTRY_DO_RETRY"
  public static final String ABORTING_QUESTION_WITH_ERROR = "ABORTING_QUESTION_WITH_ERROR"
  public static final String SELECT_AN_OPTION = "SELECT_AN_OPTION"
  public static final String NO_OPTION_PROVIDED = "NO_OPTION_PROVIDED"
  public static final String INVALID_OPTION = "INVALID_OPTION"
  public static final String INVALID_OPTION_WITH_ERROR = "INVALID_OPTION_WITH_ERROR"
  public static final String SELECT_ALL_ABOVE = "SELECT_ALL_ABOVE"
  public static final String SELECT_OPTIONS = "SELECT_OPTIONS"
  public static final String Y_OR_N = "Y_OR_N"
  public static final String Y = "Y"
  public static final String YES = "YES"
  public static final String N = "N"
  public static final String NO = "NO"
  public static final String ENTER_VALUE_BELOW = "ENTER_VALUE_BELOW"
  public static final String MUST_BE_VALID_PORT = "MUST_BE_VALID_PORT"
  public static final String ENTRY_CANNOT_BE_EMPTY = "ENTRY_CANNOT_BE_EMPTY"
  public static final String INVALID_ENTRY = "INVALID_ENTRY"
  public static final String URL_INVALID = "URL_INVALID"
  public static final String URL_NOT_ACCESSIBLE = "URL_NOT_ACCESSIBLE"
  public static final String NOT_A_RECOGNIZED_RESOURCE = "NOT_A_RECOGNIZED_RESOURCE"
  public static final String VALUE_SHOULD_MATCH_PATTERN = "VALUE_SHOULD_MATCH_PATTERN"
  public static final String VALUE_SHOULD_NOT_BELONG_TO_LIST = "VALUE_SHOULD_NOT_BELONG_TO_LIST"
  public static final String ILLEGAL_REST_PATH = "ILLEGAL_REST_PATH"
  public static final String MISSING_RESOURCE = "MISSING_RESOURCE"
  static final String ENTER_YOUR_CHOICE = "ENTER_YOUR_CHOICE"


  /**
   * Get a message
   * @param key
   * @param args
   * @return
   */
  static String getMessage(String key, Object... args) {
    _getMessage(MESSAGES_PATH + HelperMessages.getSimpleName(), key, args)
  }

  static String yOrN() {
    getMessage(Y_OR_N)
  }

  static String y() {
    getMessage(Y)
  }

  static String n() {
    getMessage(N)
  }

  static String no() {
    getMessage(NO)
  }
  static String yes() {
    getMessage(YES)
  }

  static String enterYourChoice() {
    getMessage(ENTER_YOUR_CHOICE)
  }
}
