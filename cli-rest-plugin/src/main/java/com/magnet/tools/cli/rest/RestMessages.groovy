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

package com.magnet.tools.cli.rest

import com.magnet.tools.utils.MessagesSupport
import com.magnet.tools.utils.StringHelper
import groovy.util.logging.Slf4j

/**
 * Messages support for rest-controller generation related classes
 */
@Slf4j
class RestMessages extends MessagesSupport {
  static final String REST_CONTROLLER_GENERATION_FAILURE = "REST_CONTROLLER_GENERATION_FAILURE"
  static final String TOKEN_CANNOT_BE_FOUND_AFTER = "TOKEN_CANNOT_BE_FOUND_AFTER"
  static final String TOO_MANY_TOKEN_STATEMENTS = "TOO_MANY_TOKEN_STATEMENTS"
  static final String MISSING_REQUEST_IN_MODEL = "MISSING_REQUEST_IN_MODEL"
  static final String MISSING_RESPONSE_IN_MODEL = "MISSING_RESPONSE_IN_MODEL"
  static final String MISSING_COMPONENT_IN_MODEL = "MISSING_COMPONENT_IN_MODEL"
  static final String REST_FILE_LOCATION = "REST_FILE_LOCATION"
  static final String GENERATING_ARTIFACT = "GENERATING_ARTIFACT"
  static final String REST_WIZARD_DESCRIPTION = "REST_WIZARD_DESCRIPTION"
  static final String FAILED_TO_PARSE_EXAMPLE = "FAILED_TO_PARSE_EXAMPLE"
  static final String PARSING_RESOURCE = "PARSING_RESOURCE"

  static String getMessage(String key, Object... args) {
    _getMessage(MESSAGES_PATH + RestMessages.getSimpleName(), key, args)
  }

  static String restControllerGenerationFailure(def error) {
    getMessage(REST_CONTROLLER_GENERATION_FAILURE, error)
  }

  static String tokenCannotBeFoundAfter(def token1, def token2) {
    getMessage(TOKEN_CANNOT_BE_FOUND_AFTER, token1, token2)
  }
  static String tooManyTokenStatements(def token) {
    getMessage(TOO_MANY_TOKEN_STATEMENTS, token)
  }

  static String missingComponentInModel(String componentName, String content) {
    getMessage(MISSING_COMPONENT_IN_MODEL, componentName, content)
  }

  static String enterRestFileLocation() {
    getMessage(REST_FILE_LOCATION)
  }

  static String generatingArtifact(String artifactId, String path) {
    getMessage(GENERATING_ARTIFACT, artifactId, path)
  }

  static String restWizardDescription() {
    getMessage(REST_WIZARD_DESCRIPTION)
  }

  static String failedToParseExample(def file, def message) {
    getMessage(FAILED_TO_PARSE_EXAMPLE, file, message)
  }

  static String parsingResource(def resource) {
    getMessage(PARSING_RESOURCE, StringHelper.b(resource))
  }
}
