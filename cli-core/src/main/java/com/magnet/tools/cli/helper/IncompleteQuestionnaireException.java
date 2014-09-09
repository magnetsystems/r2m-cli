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
package com.magnet.tools.cli.helper;

/**
 * Exception thrown when the questionnaire is incomplete after the interview process, or for an explicit abort
 * This file must be a java file in order to work around http://jira.codehaus.org/browse/GROOVY-4838
 */
public class IncompleteQuestionnaireException extends Exception {
  public IncompleteQuestionnaireException(String message) {
    super(message);
  }

  public IncompleteQuestionnaireException(String message, Exception e) {
    super(message, e);
  }
}
