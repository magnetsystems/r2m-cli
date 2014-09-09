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
package com.magnet.tools.cli.r2m

import com.magnet.tools.cli.core.CoreConstants
import com.magnet.tools.cli.rest.MobileRestConstants
import com.magnet.tools.utils.MessagesSupport
import com.magnet.tools.utils.StringHelper
import groovy.util.logging.Slf4j

/**
 * Messages support for mob-related classes
 */
@Slf4j
class R2MMessages extends MessagesSupport {
  static final String SHOW_EQUIVALENT_COMMAND_LINE = "SHOW_EQUIVALENT_COMMAND_LINE"
  static final String SPECIFICATION_MUST_BE_VALID = "SPECIFICATION_MUST_BE_VALID"
  static final String OUTPUT_DIRECTORY_CANNOT_BE_EXISTING_FILE = "OUTPUT_DIRECTORY_CANNOT_BE_EXISTING_FILE"
  static final String GEN_COMMAND_DESCRIPTION = "GEN_COMMAND_DESCRIPTION"
  static final String INVALID_CONTROLLER_NAME = "INVALID_CONTROLLER_NAME"
  static final String INVALID_PACKAGE_NAME = "INVALID_PACKAGE_NAME"
  static final String INVALID_NAME_SPACE = "INVALID_NAME_SPACE"
  static final String GEN_COMMAND_GREETINGS = "GEN_COMMAND_GREETINGS"
  static final String MISSING_SPECIFICATIONS_OPTION = "MISSING_SPECIFICATIONS_OPTION"
  static final String GENERATION_SUCCESSFUL_IN_DIRECTORY = "GENERATION_SUCCESSFUL_IN_DIRECTORY"
  static final String USING_DEFAULT_PACKAGE = "USING_DEFAULT_PACKAGE"
  static final String USING_DEFAULT_OUTPUT_DIRECTORY = "USING_DEFAULT_OUTPUT_DIRECTORY"
  static final String USING_DEFAULT_CONTROLLER_NAME = "USING_DEFAULT_CONTROLLER_NAME"
  static final String CREATE_EXAMPLES_FILE = "CREATE_EXAMPLES_FILE"

  static String getMessage(String key, Object... args) {
    _getMessage(MESSAGES_PATH + R2MMessages.getSimpleName(), key, args)
  }

  static def showEquivalentCommandLine(def line) {
    getMessage(SHOW_EQUIVALENT_COMMAND_LINE, line)
  }

  static def specificationsMustBeValid(def spec) {
    getMessage(SPECIFICATION_MUST_BE_VALID, spec)
  }

  static def outputDirectoryCannotBeExistingFile(def file) {
    getMessage(OUTPUT_DIRECTORY_CANNOT_BE_EXISTING_FILE, file)
  }

  static def genCommandDescription(def cmd, def url) {
    getMessage(GEN_COMMAND_DESCRIPTION, cmd, url)
  }

  static def invalidControllerName(def c) {
    getMessage(INVALID_CONTROLLER_NAME, c)
  }

  static def invalidPackageName(def p) {
    getMessage(INVALID_PACKAGE_NAME, p)
  }

  static def invalidNameSpace(def p) {
    getMessage(INVALID_NAME_SPACE, p)
  }

  static def genCommandGreetings(def cmd) {
    getMessage(GEN_COMMAND_GREETINGS, cmd)
  }

  static def missingSpecificationsOptions() {
    getMessage(MISSING_SPECIFICATIONS_OPTION,
        StringHelper.b("${CoreConstants.HELP_COMMAND} ${R2MConstants.GEN_COMMAND} -v"),
        StringHelper.b("${R2MConstants.GEN_COMMAND} --${MobileRestConstants.OPTION_REST_SPECIFICATIONS_LOCATION} <location>"),
        StringHelper.b("${R2MConstants.GEN_COMMAND} --${MobileRestConstants.OPTION_DOWNLOAD} <example>"),
        StringHelper.b("${R2MConstants.GEN_COMMAND} --${MobileRestConstants.OPTION_LIST}"),
        StringHelper.b("${R2MConstants.GEN_COMMAND} --${MobileRestConstants.OPTION_INTERACTIVE}"))
  }

  static def usingDefaultPackage(def val) {
    getMessage(USING_DEFAULT_PACKAGE, val)
  }

  static def usingDefaultOutputDirectory(def val) {
    getMessage(USING_DEFAULT_OUTPUT_DIRECTORY, val)
  }

  static def generationSuccessful(def dir) {
    getMessage(GENERATION_SUCCESSFUL_IN_DIRECTORY, dir)
  }

  static def usingDefaultClassName(def val){
    getMessage(USING_DEFAULT_CONTROLLER_NAME, val)
  }

  static def createExamplesFile(def from, def to) {
    getMessage(CREATE_EXAMPLES_FILE, from, to)
  }
}
