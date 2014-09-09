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
package com.magnet.tools.cli.helper

/**
 * Abstract class for questionnaire wizards:
 * wizards start other questionnaire to help filling complex values for questionnaire
 * For instance they can find out the salesforce soap endpoint address
 * or print some extra help information detailing what is asked
 */
abstract class PromptHelperWizard {

  /**
   * Additional properties that may have been already computed, so we don't ask for them a second time
   */
  Map<String, String> values

  /**
   * whether to ask for confirmation of suggested value (default is true)
   */
  boolean interactive = true

  /**
   * Start the wizard
   * @return user-acknowledge suggested value, or null if user rejected it
   * @throws IncompleteQuestionnaireException in case of error
   */
  abstract String start()

}
